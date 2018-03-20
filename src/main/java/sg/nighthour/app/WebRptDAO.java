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

import java.util.logging.Logger;
import javax.servlet.ServletException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Transaction;

public class WebRptDAO
{

    private static final Logger log = Logger.getLogger(WebRptDAO.class.getName());

    /**
     * Check if a domain is a valid monitored domain in the application datastore
     * 
     * @param domain
     * @return true if domain is valid otherwise return false
     */
    public static boolean isValidDomain(String domain)
    {
        if (domain == null)
            return false;

        Key domainkey = new KeyFactory.Builder(AppConstants.GLOBAL_DOMAIN_TABLE_KIND,
                AppConstants.GLOBAL_DOMAIN_TABLE_ENTITY).addChild(AppConstants.GLOBAL_DOMAIN_ENTRY_KIND, domain)
                        .getKey();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        try
        {
            datastore.get(domainkey);
            return true;

        }
        catch (EntityNotFoundException e)
        {
            return false;
        }

    }

    /**
     * Obtains the userid that owns the domain
     * 
     * @param domain
     * @return userid or null if there is an error
     */

    public static String getUserIdFromDomain(String domain)
    {

        if (domain == null)
        {
            log.warning("Error: WebRptDAO domain is null");
            return null;
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query q = new Query(AppConstants.DOMAIN_KIND)
                .setFilter(new FilterPredicate("domainname", FilterOperator.EQUAL, domain));

        Entity domain_entity = datastore.prepare(q).asSingleEntity();

        if (domain_entity == null)
        {
            log.warning("Error: Domain entity not found " + domain);
            return null;
        }

        Key userkey = domain_entity.getParent();

        return userkey.getName();

    }

    /**
     * Gets the current application mode from a user enitty
     * 
     * @param userid
     * @return mode or null if there is an error
     */

    public static String getModeFromUserid(String userid)
    {

        if (userid == null)
        {
            log.warning("Error: userid is null");
            return null;
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity user = null;
        Key userkey = KeyFactory.createKey(AppConstants.USER_KIND, userid);
        try
        {
            user = datastore.get(userkey);
        }
        catch (EntityNotFoundException e)
        {
            log.warning("Error: Cannot get user entity " + userid);
            return null;
        }

        String mode = (String) user.getProperty("Mode");
        return mode;

    }

    /**
     * Get the remote ip address that is allowed for capturing URL entities from a
     * user entity
     * 
     * @param userid
     * @return the allowed remote capture ip or null if there is an error
     * 
     */
    public static String getAllowCaptureIP(String userid)
    {
        if (userid == null)
        {
            log.warning("Error: userid is null");
            return null;
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity user = null;
        Key userkey = KeyFactory.createKey(AppConstants.USER_KIND, userid);
        try
        {
            user = datastore.get(userkey);
        }
        catch (EntityNotFoundException e)
        {
            log.warning("Error: Cannot get user entity " + userid);
            return null;
        }

        String captureip = (String) user.getProperty("CaptureModeIPAddress");
        return captureip;
    }

    /**
     * Create a new or update an existing URL entity from a WebReport object. Used
     * by capture mode. The remoteip must match the client ip address that is used
     * to activate capture mode in the application admin console, otherwise the
     * creation or update will be ignored.
     * 
     * @param rpt
     *            WebReport object
     * @param domain
     * @param userid
     * @param remoteip
     *            remote client ip address making the request
     * @throws ServletException
     */
    public static void createURL(WebReport rpt, String domain, String userid, String remoteip) throws ServletException
    {
        if (rpt == null || domain == null || userid == null || remoteip == null)
        {
            log.warning("Error: Webreport object, domain, userid or remoteip is null");
            throw new ServletException("Invalid null arguments !");
        }

        if (rpt.getURL() == null || rpt.getCheckSum() == null || rpt.getContent() == null)
        {
            log.warning("Error: Invalid webreport object");
            throw new ServletException("Invalid webreport object argument !");
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Key parentKey = new KeyFactory.Builder(AppConstants.USER_KIND, userid)
                .addChild(AppConstants.DOMAIN_KIND, domain).getKey();

        Key urlkey = new KeyFactory.Builder(AppConstants.USER_KIND, userid).addChild(AppConstants.DOMAIN_KIND, domain)
                .addChild(AppConstants.URL_KIND, rpt.getURL()).getKey();

        Entity url = new Entity("URL", rpt.getURL(), parentKey);

        Transaction txn = datastore.beginTransaction();

        boolean urlexists = false;

        try
        {
            try
            {
                datastore.get(urlkey);
                urlexists = true;
            }
            catch (EntityNotFoundException e)
            {
                urlexists = false;
            }

            if (!urlexists)
            {// New url check that urlcount does not exceed maximum allowed urls per domain

                try
                {
                    Entity domain_entity = datastore.get(parentKey);
                    long urlcount = (Long) domain_entity.getProperty("urlcount");
                    urlcount++;

                    if (urlcount > AppConstants.MAX_URL_PER_DOMAIN)
                    {
                        log.warning("Error: Maximum url count exceeded  for " + domain);
                        throw new ServletException("Maximum url count exceeded  for " + domain);
                    }

                    domain_entity.setProperty("urlcount", urlcount);
                    datastore.put(txn, domain_entity);

                }
                catch (EntityNotFoundException e)
                {
                    log.warning("Error: domain does not exists " + domain);
                    return;
                }

            }

            // Update an existing url or create a new one if it doesn't exist and urlcount
            // does not exceed the maximum allowed urls per domain
            url.setProperty("sha256", rpt.getCheckSum());
            url.setProperty("location", rpt.getURL());
            url.setUnindexedProperty("content", new Text(rpt.getContent()));
            datastore.put(txn, url);
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
     * Check if redirection is enabled
     * 
     * @param userid
     * @return true if redirection is enabled, false if redirection is disabled or
     *         if there is an error
     */
    public static boolean isRedirectionEnabled(String userid)
    {
        if (userid == null)
        {
            log.warning("Error: userid is null");
            return false;
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity user = null;
        Key userkey = KeyFactory.createKey(AppConstants.USER_KIND, userid);
        try
        {
            user = datastore.get(userkey);
        }
        catch (EntityNotFoundException e)
        {
            log.warning("Error: Cannot get user entity " + userid);
            return false;
        }

        String action = (String) user.getProperty("Action");
        if (action.equals("AlertRedirection"))
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    
    /**
     * Retrieve the SHA-256 checksum of a url entity
     * @param userid
     * @param domain
     * @param url
     * @return Sha-256 checkum if successful, null if url is not found or error occurs. 
     */
    public static String getURLChecksum(String userid, String domain, String url)
    {
        
        if(userid == null || domain == null || url == null)
        {
            log.warning("Error: null arguments");
            return null;
        }
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key urlkey = new KeyFactory.Builder(AppConstants.USER_KIND, userid).addChild(AppConstants.DOMAIN_KIND, domain)
                .addChild(AppConstants.URL_KIND, url).getKey();
        
        Entity weburl = null;

        try
        {
            weburl = datastore.get(urlkey);
        }
        catch (EntityNotFoundException e)
        {
            log.warning("Error: URL  entity not found : " + userid + " : " + domain + " : " + url);
            return null;
        }
        
        String checkSum = (String) weburl.getProperty("sha256");
        
        return checkSum;
    }
    
    
    /**
     * Retrieves the redirection url of an user entity
     * @param userid
     * @return Redirection url or null on error
     */
    public static String getRedirectionURL(String userid)
    {
        
        if(userid == null)
        {
            log.warning("Error: userid is null");
            return null;
        }
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        String redirectionurl = null;
        Key userkey = KeyFactory.createKey(AppConstants.USER_KIND, userid);
        try
        {
            Entity user_entity = datastore.get(userkey);
            redirectionurl = (String) user_entity.getProperty("RedirectionURL");
        }
        catch (EntityNotFoundException e)
        {
            log.warning("Error: user entity not found : " + userid);
            redirectionurl = null;
        }
        
        return redirectionurl;   
        
    }
    
    
   

    /**
     * Create a alert entity that can be processed by a task queue worker. The task
     * queue worker will send an email to the userid using the alert entity and then
     * it will delete the alert entity.
     * 
     * @param rpt
     *            WebReport
     * @param userid
     * @param remoteip
     * @return the alert entity key
     * @throws ServletException
     */
    public static String createAlert(WebReport rpt, String userid, String remoteip) throws ServletException
    {

        if (rpt == null || userid == null || remoteip == null)
        {
            log.warning("Error: WebReport object, userid or remoteip is null");
            throw new ServletException("Invalid null arguments !");
        }

        if (rpt.getURL() == null || rpt.getCheckSum() == null || rpt.getContent() == null)
        {
            log.warning("Error: Invalid webreport object");
            throw new ServletException("Invalid webreport object argument !");

        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity alert = new Entity(AppConstants.ALERT_KIND);
        alert.setUnindexedProperty("content", new Text(rpt.getContent()));
        alert.setProperty("senderip", remoteip);
        alert.setUnindexedProperty("sha256", rpt.getCheckSum());
        alert.setProperty("userid", userid);
        alert.setProperty("url", rpt.getURL());

        datastore.put(alert);
        Key alertkey = alert.getKey();
        return (Long.toString(alertkey.getId()));

    }

}
