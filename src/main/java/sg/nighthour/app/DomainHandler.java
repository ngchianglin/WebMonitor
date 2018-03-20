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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ConcurrentModificationException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DomainHandler extends GenericFunctionHandler
{

    private static final Logger log = Logger.getLogger(DomainHandler.class.getName());
    
    public static void handleRequest(HttpServletRequest request, HttpServletResponse response, String userid, PrintWriter out) 
            throws ServletException, IOException
    {
        
        if(userid == null)
        {
            log.warning("Error: userid is null");
            return; 
        }
        
        String command = request.getParameter("command");
        String domain = request.getParameter("domain");
        
        
        if (domain != null)
        {
            domain = domain.toLowerCase();
        }
        
        
        if(validateDomain(domain))
        {
            processCommand(userid, command, domain, request.getRemoteAddr(), out); 
        }
        else
        {
            log.warning("Error: Invalid domain name format : " + domain + " : " +
                         userid + " : " + request.getRemoteAddr());
            out.println("Error: Invalid Domain name format");
            return;

        }
        
    }
    
    
    
    private static void processCommand(String userid, String command, String domain, String remoteip, PrintWriter out)
           throws ServletException
    {
        
        
        if (userid == null || command == null || domain == null || remoteip == null)
        {
            log.warning("Error: null arguments : " + domain + " : " + command + " : " 
                        + userid + " : " +  remoteip);
            out.println("Error: Invalid request");
            return;
        }
        
        if (command.equals("add"))
        {
            try
            {
                DomainDAO.addDomain(userid, domain);
                out.println("Success");
            }
            catch (DomainDAO.DomainExistsException e)
            {
                
                log.warning("Error: Domain already exists cannot add : " + domain + " : " + command + " : " 
                            + userid + " : " + remoteip);
                out.println("Error: Domain exists");

            }
            catch (DomainDAO.MaxDomainsException e)
            {
                log.warning("Error: Exceeded Maximum Domains : " + domain + " : " + command + " : " 
                        + userid + " : " + remoteip);
                out.println("Error: Exceeded Maximum Domains");

            }
            catch (ConcurrentModificationException e)
            {
                log.warning("Error: Concurrent addition of domain : " + domain + " : " + command + " : " 
                        + userid + " : " + remoteip);
                out.println("Error: Concurrent addition of domain retry again");
            }

        }
        else if (command.equals("delete"))
        {

            try
            {
                DomainDAO.deleteDomain(userid, domain);
                out.println("Success");
            }
            catch (DomainDAO.DomainNotExistsException e)
            {
                log.warning("Error: Domain does not exist cannot delete : " + domain + " : " + command + " : " 
                        + userid + " : " + remoteip);
               
                out.println("Error: Domain does not exist");
            }
            catch (ConcurrentModificationException e)
            {
                log.warning("Error: Concurrent deletion of domain : " + domain + " : " + command + " : " 
                        + userid + " : " + remoteip);
              
                out.println("Error: Concurrent deletion of domain retry again");
            }

        }
        else
        {
            out.println("Error: Invalid command type");
            log.warning("Error: Invalid command type : " + domain + " : " + command + " : " 
                    + userid + " : " + remoteip);

        }
        
        
    }
    
    
    private static boolean validateDomain(String domain)
    {
        if (domain == null || domain.length() > AppConstants.MAX_DOMAIN_LEN)
        {
            return false;
        }
        
        domain = domain.toLowerCase();

        String arr[] = domain.split("\\.");

        String regex;
        if (arr.length == 2)
        {// regex for single tld part domain example .sg
            regex = "^[a-z0-9][a-z0-9][a-z0-9-]{0,62}\\.[a-z]{2,20}$";
        }
        else if (arr.length >= 3)
        {// regex for 2 tld parts domain example .com.sg

            regex = "^[a-z0-9][a-z0-9][a-z0-9-]{0,62}\\.[a-z]{2,20}\\.[a-z]{2,20}$";
        }
        else
        {
            return false;
        }

        Pattern re = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = re.matcher(domain);

        if (matcher.find())
        {
            return true;
        }

        return false;

    }

    
    
}
