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

import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import sg.nighthour.json.JsonType;
import sg.nighthour.json.SimpleJsonObject;
import sg.nighthour.json.SimpleJsonParser;
import sg.nighthour.json.SimpleJsonValue;
import sg.nighthour.json.SimpleParseException;
import sg.nighthour.json.SimpleTokenException;

public class WebRptHandler extends GenericFunctionHandler
{

    private static final Logger log = Logger.getLogger(WebRptHandler.class.getName());
    private static final int MAX_URL_LEN = 2048;
    private static final String COOKIE_DOMAIN = AppConstants.WEBRPT_COOKIE_DOMAIN;

    /**
     * Sets a webreport cookie
     * 
     * @param response
     *            HttpServletResponse
     */
    public static void setCookie(HttpServletResponse response)
    {
        Cookie wrptcookie = new Cookie(AppConstants.WEBRPT_COOKIE_NAME, UUID.randomUUID().toString());
        wrptcookie.setHttpOnly(true);
        wrptcookie.setSecure(true);
        wrptcookie.setDomain(COOKIE_DOMAIN);
        response.addCookie(wrptcookie);

    }

    /**
     * Gets a webreport cookie value
     * 
     * @param request
     *            HttpServletRequest
     * @return the cookie value or null if cookie does not exist
     */
    public static String getWebReportCookie(HttpServletRequest request)
    {
        Cookie[] cookies = request.getCookies();

        if (cookies == null)
            return null;

        for (int i = 0; i < cookies.length; i++)
        {
            String name = cookies[i].getName();
            if (name.equals(AppConstants.WEBRPT_COOKIE_NAME))
            {
                return cookies[i].getValue();
            }

        }

        return null;

    }
    
    
    /**
     * Check if a domain is in the application datastore
     * @param domain
     * @return true if the domain is present, false otherwise
     */
    public static boolean isValidDomain(String domain)
    {
        return WebRptDAO.isValidDomain(domain);
    }

    /**
     * Parses a json input string into a WebReport object
     * 
     * @param jsonstr
     *            json input string
     * @param remoteip
     *            remote ip address
     * @return WebReport object or null if an error occur when processing the json
     *         input
     * 
     */
    public static WebReport processJson(String jsonstr, String remoteip)
    {

        SimpleJsonParser parser = new SimpleJsonParser(jsonstr);
        SimpleJsonValue val = null;

        try
        {
            val = parser.parse();
        }
        catch (SimpleTokenException e)
        {
            log.warning("Error: " + remoteip + "  Json parser exception " + e);
            return null;
        }
        catch (SimpleParseException e)
        {
            log.warning("Error: " + remoteip + " Json parser exception " + e);
            return null;
        }

        SimpleJsonObject jsonObj = val.getJsonObject();
        if (jsonObj == null || jsonObj.isEmpty())
        {
            log.warning("Error: " + remoteip + "  Invalid json object");
            return null;
        }

        val = jsonObj.getValue("WebRpt");

        if (val != null && val.getType() == JsonType.OBJECT)
        {

            SimpleJsonObject rpt = val.getJsonObject();

            SimpleJsonValue urlval = rpt.getValue("url");
            SimpleJsonValue cksumval = rpt.getValue("cksum");
            SimpleJsonValue contentval = rpt.getValue("content");

            if (urlval == null || cksumval == null || contentval == null)
            {
                log.warning("Error: " + remoteip + "  Invalid json properties");
                return null;
            }

            String url = urlval.getJsonString();
            String cksum = cksumval.getJsonString();
            String content = contentval.getJsonString();

            if (url == null || cksum == null || content == null)
            {
                log.warning("Error: " + remoteip + " Invalid json property value");
                return null;
            }

            WebReport wreport = new WebReport(url, cksum, content, jsonstr);
            return wreport;

        }
        else
        {
            log.warning("Error: " + remoteip + "  Invalid webrpt json object");
            return null;
        }

    }

    /**
     * Extracts the domain from a url string The method expects urls to start with
     * either https:// or http:// scheme. The domain and the tld portion must be at
     * 2 characters, eg. N.N or N.NN is not valid.
     * 
     * @param url
     *            string
     * @return domain or null if there are errors in parsing the domain
     * 
     */
    public static String extractDomain(String url)
    {

        if (url == null)
        {
            log.warning("Error: url is null");
            return null;
        }

        int urllen = url.length();

        if (urllen > MAX_URL_LEN)
        {
            log.warning("Error: Url exceeded MAX URL LEN " + url);
            return null;
        }

        StringBuilder schemebuf = new StringBuilder(urllen); // stores the scheme
        StringBuilder authbuf = new StringBuilder(urllen); // store the authority part

        int i = 0;
        boolean scheme = false;

        while (i < urllen)
        {
            char c = url.charAt(i);

            if (scheme != true)
            {// Get the scheme
                if (c != ':')
                {
                    schemebuf.append(c);
                }
                else if (c == ':')
                {
                    schemebuf.append(c);
                    if (i + 2 < urllen)
                    {
                        schemebuf.append(url.charAt(++i));
                        schemebuf.append(url.charAt(++i));
                        scheme = true;
                    }
                    else
                    {// Invalid scheme
                        log.warning("Error: Invalid scheme insufficient length " + url);
                        return null;
                    }

                }
            }
            else
            {// Get the authority part
                if (c != '/')
                {
                    authbuf.append(c);
                }
                else if (c == '/')
                { // handles case of multiple /// after scheme
                    if (authbuf.length() > 0)
                    { // already got authority part
                        break;
                    }
                }
            }

            i++;
        }

        String schemepart = schemebuf.toString();
        String authpart = authbuf.toString();

        if (!(schemepart.equalsIgnoreCase("http://") || schemepart.equalsIgnoreCase("https://")))
        {
            log.warning("Error: Invalid scheme " + url);
            return null;
        }

        if (authpart.length() == 0)
        {
            log.warning("Error: Invalid auth part " + url);
            return null;
        }

        String hostportpart = null;
        String hostpart = null;

        int index = 0;

        if ((index = authpart.indexOf('@')) != -1)
        {// clear away the user password portion
            if ((index + 1) < authpart.length())
            {
                hostportpart = authpart.substring(index + 1);
            }
            else
            {// empty hostname after user and password portion
                log.warning("Error: Empty hostname after user and password " + url);
                return null;
            }
        }
        else
        {
            hostportpart = authpart;
        }

        if ((index = hostportpart.indexOf(':')) != -1)
        {

            hostpart = hostportpart.substring(0, index);

        }
        else
        {
            hostpart = hostportpart;
        }

        String[] arr = hostpart.split("\\.");
        int len = arr.length;

        String domainpart = null;

        if (len == 2)
        {// single tld part domain .sg
            String tld = arr[len - 1];
            String namepart = arr[len - 2];

            if (tld.length() < 2 || namepart.length() < 2)
            {
                log.warning("Error: Invalid tld or domain name part length " + url);
                return null;
            }

            domainpart = arr[len - 2] + "." + arr[len - 1];
        }
        else if (len >= 3)
        { // 2 parts tld domain .com.sg
            String tld1 = arr[len - 1];
            String tld2 = arr[len - 2];
            String namepart = arr[len - 3];

            if (tld1.length() < 2 || tld2.length() < 2 || namepart.length() < 2)
            {
                log.warning("Error: Invalid tld or domain name part length " + url);
                return null;
            }

            domainpart = arr[len - 3] + "." + arr[len - 2] + "." + arr[len - 1];

        }
        else
        {// invalid domain
            log.warning("Error: Invalid domain part " + url);
            return null;
        }

        return domainpart;

    }
    
    
    /**
     * Handle and process incoming webreports from clients
     * 
     * @param request
     * @param response
     * @param userid
     * @param out
     * @throws ServletException
     * @throws IOException
     */
    public static void handleRequest(HttpServletRequest request, HttpServletResponse response, String userid, PrintWriter out)
            throws ServletException, IOException
    {
        
        BufferedReader in = request.getReader();
        
        String line = null;
        StringBuffer strbuf = new StringBuffer();
        while ((line = in.readLine()) != null)
        {
            strbuf.append(line);
        }

        String jsoninput = strbuf.toString();
        // Obtain WebReport object from the Json input
        WebReport wreport = processJson(jsoninput, request.getRemoteAddr());
        
        if(wreport == null)
        {
            log.warning("Error: Cannot parse json : " + jsoninput + " : " + request.getRemoteAddr());
            return;
        }

       
        String domain = extractDomain(wreport.getURL());
        if (domain != null)
        {
            wreport.setDomain(domain);
        }
        else
        {
            log.warning("Error: Cannot extract domain from JSON webreport : " + 
                wreport.getURL() + " : " + request.getRemoteAddr());
            return;
        }
        
        String origin = request.getHeader("Origin");
        origin = extractDomain(origin); 
        if(origin == null)
        {
            log.warning("Error: Cannot extract domain from origin header : " + 
                    request.getHeader("Origin") + " : " + request.getRemoteAddr());
             return;
        }
        
        //Check to make sure that domain in the origin header is the same
        //as the domain in the webreport
        if(!domain.equals(origin))
        {
            log.warning("Error: Origin domain does not match report domain : " + 
                    origin + " : " + domain + " : " + request.getRemoteAddr());
            return; 
        }

        userid = WebRptDAO.getUserIdFromDomain(domain);
        if (userid == null)
        {
            log.warning("Error: Cannot extract user from domain in JSON webreport : " + 
                    domain + " : " + request.getRemoteAddr());
           return;
        }

        String mode = WebRptDAO.getModeFromUserid(userid);
        String allowip = WebRptDAO.getAllowCaptureIP(userid);
        
        if(mode == null || allowip == null)
        {
            log.warning("Error: " + " null mode or null capture ip " + userid 
                    + request.getRemoteAddr());
            return; 
        }

       
        if (mode.equals(AppConstants.MODEMONITOR))
        {// monitor mode
             
             String checksum = WebRptDAO.getURLChecksum(userid, domain, wreport.getURL());
             
             if(checksum == null)
             {//url not found or error getting checksum behave as if monitoring is disabled
                 log.warning("Error: Monitor mode url is not found or error : "  + userid + " : " +  domain +
                         " : " + wreport.getURL() + " : " + request.getRemoteAddr());
                 return;
             }
             
             if(checksum.equals(wreport.getCheckSum()))
             {//checksum matches
                 out.println("Ok");
                 return;
             }
             else if( WebRptDAO.isRedirectionEnabled(userid))
             {// checksum does not match alert and redirect to error page
                 
                 String redirecturl = WebRptDAO.getRedirectionURL(userid);
                 
                 if(redirecturl != null)
                 {
                     out.println("600 " + redirecturl); 
                 }
                 else
                 {
                     log.warning("Error: Monitor mode cannot get redirection url : "  + userid + " : " +  domain +
                             " : " + wreport.getURL() + " : " + request.getRemoteAddr());
                 }
                 
                 String alertkey = WebRptDAO.createAlert(wreport, userid, request.getRemoteAddr());
                 Queue queue = QueueFactory.getQueue("alert-queue");
                 queue.add(TaskOptions.Builder.withUrl("/worker").param("key", alertkey));
                 
                 return; 
             }
             else 
             {//checksum does not match redirection is not enabled
                 String alertkey = WebRptDAO.createAlert(wreport, userid, request.getRemoteAddr());
                 Queue queue = QueueFactory.getQueue("alert-queue");
                 queue.add(TaskOptions.Builder.withUrl("/worker").param("key", alertkey));
                 return; 
             }
             

          }
          else if (mode.equals(AppConstants.MODECAPTURE) && allowip.equals(request.getRemoteAddr()))
          {// capture mode
               WebRptDAO.createURL(wreport, domain, userid, request.getRemoteAddr());
               return; 
          }
          else
          {// disable mode
               return;
          }
            
        
    }

}
