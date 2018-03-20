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
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ActionHandler extends GenericFunctionHandler
{
    
    private static final Logger log = Logger.getLogger(ActionHandler.class.getName());
    
    public static void handleRequest(HttpServletRequest request, HttpServletResponse response, String userid, PrintWriter out) 
            throws ServletException, IOException
    {
        if(userid == null)
        {
            log.warning("Error: userid is null");
            return; 
        }
        
        String command = request.getParameter("command");
        String url = request.getParameter("url");
        
        if (command == null)
        {
            log.warning("Error: null command : " + userid + " : " + request.getRemoteAddr());
            out.println("Error: Invalid request");
            return; 
        }
        else if (command.equals("enableurl"))
        {
            if (validateURL(url))
            {
                
                if(UserDAO.enableRedirection(userid, url))
                {
                    out.println("Success");     
                }
                else
                {
                    log.warning("Error: Cannot enable redirection url : " + url 
                                 + " : " + userid + " : " + request.getRemoteAddr());
                    out.println("Error: Cannot enable redirection url");
                }
                
                return; 

            }
            else
            {
                log.warning("Error: Invalid url : " + url 
                        + " : " + userid + " : " + request.getRemoteAddr());
                out.println("Error: Invalid url");
                return; 
            }

        }
        else if (command.equals("disableurl"))
        {

            if(UserDAO.disableRedirection(userid))
            {
                out.println("Success");
            }
            else
            {
                log.warning("Error: Cannot disable redirection url : " +
                         " : " + userid + " : " + request.getRemoteAddr());
                out.println("Error: Cannot disable url redirection");
            }
            
            return; 
            
           
        }
        else
        {
            log.warning("Error: Invalid command type : " + command + 
                    " : " + userid + " : " + request.getRemoteAddr());
            
            out.println("Error: Invalid command type");
            return; 
        }
        
    }
    
    
    /**
     * Check the format of the url
     * 
     * @param url
     * @return true if url is valid, false otherwise
     */
    private static boolean validateURL(String url)
    {
        if (url == null || url.length() > AppConstants.MAX_ACTIONURL_LEN)
        {
            return false;
        }

        String regex = "^https:\\/\\/[a-z0-9][a-z0-9-.?%=&/]{1,}[^.]$";

        Pattern re = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = re.matcher(url);

        if (matcher.find())
        {
            return true;
        }

        return false;

    }


}
