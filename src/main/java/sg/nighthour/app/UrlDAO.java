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

import javax.servlet.ServletException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.FetchOptions;

import java.util.List;
import java.util.logging.Logger;

public class UrlDAO
{

    private static final Logger log = Logger.getLogger(UrlDAO.class.getName());
    private final static int BUFSIZE = 1024;

    /**
     * Retrieves the domains under a user entity
     * 
     * @param userid
     * @return A html select element containing the domains or an error string if
     *         there are errors
     */
    public static String getDomainOptions(String userid)
    {

        if (userid == null)
        {
            log.warning("Error: UrlDAO userid is null");
            return "Error: userid is null";
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key k1 = KeyFactory.createKey(AppConstants.USER_KIND, userid);

        Query q = new Query(AppConstants.DOMAIN_KIND).setAncestor(k1);

        List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        if (results.size() == 0)
            return "";

        StringBuffer ret = new StringBuffer(BUFSIZE);

        ret.append("<select id=\"domainsel\">");
        ret.append("<option value=\"blank\"></option>");

        for (int i = 0; i < results.size(); i++)
        {

            Entity domain = results.get(i);
            String domainname = (String) domain.getProperty("domainname");
            domainname = HtmlEscape.escapeHTML(domainname); // Html escape the domainname

            ret.append("<option value=\"");
            ret.append("domain");
            ret.append(i);
            ret.append("\">");
            ret.append(domainname);
            ret.append("</option>");

        }

        ret.append("</select>");

        return ret.toString();

    }

    /**
     * Retrieves a json array of URLs that belong to a domain
     * 
     * @param userid
     * @param domain
     * @return a json array of url
     * @throws ServletException
     */
    public static String getURL(String userid, String domain) throws ServletException
    {

        if (userid == null || domain == null)
        {
            log.warning("Error: userid or domain is null");
            throw new ServletException("Invalid null arguments !");
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Key ancestorkey = new KeyFactory.Builder(AppConstants.USER_KIND, userid)
                .addChild(AppConstants.DOMAIN_KIND, domain).getKey();

        Query q = new Query(AppConstants.URL_KIND).setAncestor(ancestorkey);
        q.setKeysOnly();
        List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        StringBuffer buf = new StringBuffer();
        int size = results.size();

        buf.append("[");

        for (int i = 0; i < size; i++)
        {
            Entity url = results.get(i);
            buf.append("\"");
            buf.append(HtmlEscape.escapeHTML(url.getKey().getName())); // Html escape the url
            buf.append("\"");
            if (i < (size - 1))
            {
                buf.append(",");
            }

        }

        buf.append("]");

        return buf.toString();

    }

}
