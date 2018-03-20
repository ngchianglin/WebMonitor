/*
* MIT License
*
*Copyright (c) 2018 Ng Chiang Lin
*
*Permission is hereby granted, free of charge, to any person obtaining a copy
*of this software and associated documentation files (the "Software"), to deal
*in the Software without restriction, including without limitation the rights
*to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*copies of the Software, and to permit persons to whom the Software is
*furnished to do so, subject to the following conditions:
*
*The above copyright notice and this permission notice shall be included in all
*copies or substantial portions of the Software.
*
*THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
*SOFTWARE.
*
*/

package sg.nighthour.app;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;

import com.google.appengine.api.datastore.FetchOptions;

import javax.servlet.ServletException;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.logging.Logger;

public class DomainDAO
{

    private static final Logger log = Logger.getLogger(DomainDAO.class.getName());
    private final static int BUFSIZE = 1024;
    private static final int MAXDOMAIN = AppConstants.MAX_DOMAIN;

    /**
     * Returns the current domains under a user
     * 
     * @param userid
     * @return Html string containing ordered list of domains
     */
    public static String getCurrentDomains(String userid)
    {
        if (userid == null)
        {
            log.warning("Error: userid is null");
            return "Error: Userid is null";
        }

        StringBuilder ret = new StringBuilder(BUFSIZE);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key k1 = KeyFactory.createKey(AppConstants.USER_KIND, userid);
        Query q = new Query(AppConstants.DOMAIN_KIND).setAncestor(k1);

        List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        if (results.size() == 0)
            return "";

        ret.append("<ol>");
        for (int i = 0; i < results.size(); i++)
        {
            Entity domain = results.get(i);
            String domainname = (String) domain.getProperty("domainname");
            domainname = HtmlEscape.escapeHTML(domainname); // Html escape each domain
            ret.append("<li>");
            ret.append(domainname);
            ret.append("</li>");
        }

        ret.append("</ol>");

        return ret.toString();
    }

    /**
     * Deletes a domain under a user entity
     * 
     * @param userid
     * @param domain
     * @throws DomainNotExistsException
     * @throws ConcurrentModificationException
     */
    public static void deleteDomain(String userid, String domain)
            throws DomainNotExistsException, ConcurrentModificationException, ServletException
    {
        if (userid == null || domain == null)
        {
            log.warning("Error: userid or domain is null");
            throw new ServletException("Invalid null arguments !");

        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key globalk = new KeyFactory.Builder(AppConstants.GLOBAL_DOMAIN_TABLE_KIND,
                AppConstants.GLOBAL_DOMAIN_TABLE_ENTITY).addChild(AppConstants.GLOBAL_DOMAIN_ENTRY_KIND, domain)
                        .getKey();

        Key userdk = new KeyFactory.Builder(AppConstants.USER_KIND, userid).addChild(AppConstants.DOMAIN_KIND, domain)
                .getKey();

        Key userkey = KeyFactory.createKey(AppConstants.USER_KIND, userid);

        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        try
        {
            try
            {
                // Delete the global domain entry
                datastore.get(globalk);
                datastore.delete(txn, globalk);

            }
            catch (EntityNotFoundException e)
            {
                log.warning("Error: Global Domain does not exist " + userid + " " + domain);
                throw new DomainNotExistsException("Global Domain does not exist");
            }

            try
            {
                datastore.get(userdk);
                // Delete all the URLs under the domain
                Query q = new Query(AppConstants.URL_KIND).setAncestor(userdk);
                q.setKeysOnly();
                List<Entity> results = datastore.prepare(txn, q).asList(FetchOptions.Builder.withDefaults());

                for (int i = 0; i < results.size(); i++)
                {
                    Entity url = results.get(i);
                    Key urlk = url.getKey();
                    datastore.delete(txn, urlk);

                }
                // Delete the domain itself
                datastore.delete(txn, userdk);

            }
            catch (EntityNotFoundException e)
            {
                log.warning("Error: User Domain does not exist " + userid + " " + domain);
                throw new DomainNotExistsException("User Domain does not exist");
            }

            try
            {
                // Update the user entity domain count
                Entity userentity = datastore.get(userkey);
                long domaincount = (Long) userentity.getProperty("Domaincount");
                domaincount--;
                userentity.setProperty("Domaincount", domaincount);
                datastore.put(txn, userentity);

            }
            catch (EntityNotFoundException e)
            {
                log.warning("Error: userid is invalid " + userid);
                throw new ServletException("Invalid userid !");
            }

            txn.commit();

        }
        finally
        {
            if (txn.isActive())
            {
                txn.rollback();
            }

        }

    }

    /**
     * Adds a new domain to a user. All domains need to be unique in the whole
     * system. The GlobalDomainsTable track a list of unique domains and is used to
     * ensure that there are no duplicate domains.
     * 
     * @param userid
     * @param domain
     * @throws DomainExistsException
     * @throws ConcurrentModificationException
     */
    public static void addDomain(String userid, String domain)
            throws DomainExistsException, ConcurrentModificationException, MaxDomainsException, ServletException
    {

        if (userid == null || domain == null)
        {
            log.warning("Error: userid or url is null");
            throw new ServletException("Invalid null arguments !");

        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key k = new KeyFactory.Builder(AppConstants.GLOBAL_DOMAIN_TABLE_KIND, AppConstants.GLOBAL_DOMAIN_TABLE_ENTITY)
                .addChild(AppConstants.GLOBAL_DOMAIN_ENTRY_KIND, domain).getKey();
        Key global_domain_parentkey = KeyFactory.createKey(AppConstants.GLOBAL_DOMAIN_TABLE_KIND,
                AppConstants.GLOBAL_DOMAIN_TABLE_ENTITY);

        Key userkey = KeyFactory.createKey(AppConstants.USER_KIND, userid);

        Entity globaldomainentry, userdomainentity, userentity;

        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        try
        {

            try
            {
                globaldomainentry = datastore.get(k);
                throw new DomainExistsException("Global Domain exists");
            }
            catch (EntityNotFoundException e)
            {// domain doesn't exists create it

                try
                {
                    // Check of domain count for User entity is still within limit
                    userentity = datastore.get(userkey);
                    long domaincount = (Long) userentity.getProperty("Domaincount");

                    if (domaincount >= MAXDOMAIN)
                    {// Exceeds max domains
                        throw new MaxDomainsException("Exceeded Maximum Number of Domains");
                    }
                    else
                    {// Within max domains limit

                        // Create global entry
                        globaldomainentry = new Entity(AppConstants.GLOBAL_DOMAIN_ENTRY_KIND, domain,
                                global_domain_parentkey);
                        datastore.put(txn, globaldomainentry);

                        // Create user domain entry
                        userdomainentity = new Entity("Domain", domain, userkey);
                        userdomainentity.setProperty("domainname", domain);
                        userdomainentity.setProperty("urlcount", 0);
                        datastore.put(txn, userdomainentity);

                        // Update user domain count
                        domaincount++;
                        userentity.setProperty("Domaincount", domaincount);
                        datastore.put(txn, userentity);

                        txn.commit();

                    }

                }
                catch (EntityNotFoundException ue)
                {
                    log.warning("Error: userid is invalid " + userid);
                    throw new ServletException("Invalid userid !");

                }

            }

        }
        finally
        {
            if (txn.isActive())
            {
                txn.rollback();
            }

        }

    }

    /**
     * Exception thrown when maximum number of allowable domains has been exceeded
     *
     */
    public static class MaxDomainsException extends Exception
    {
        private static final long serialVersionUID = 1L;

        public MaxDomainsException(String message)
        {
            super(message);
        }

    }

    /**
     * Exception thrown when attempting to add a domain that already exists
     *
     */

    public static class DomainExistsException extends Exception
    {
        private static final long serialVersionUID = 1L;

        public DomainExistsException(String message)
        {
            super(message);
        }

    }

    /**
     * Exception thrown when trying to delete a domain that does not exist
     *
     */
    public static class DomainNotExistsException extends Exception
    {
        private static final long serialVersionUID = 1L;

        public DomainNotExistsException(String message)
        {
            super(message);
        }

    }

}
