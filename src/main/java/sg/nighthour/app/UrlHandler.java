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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UrlHandler extends GenericFunctionHandler
{
    private static final Logger log = Logger.getLogger(UrlHandler.class.getName());
    
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
        String remoteip = request.getRemoteAddr();
        
        if (command == null || domain == null)
        {
            log.warning("Error: Null Parameters : " + userid + " : "  + remoteip);
            out.println("Error: Null Parameters");
            return;
        }
        
        domain = domain.toLowerCase();
        
        if (command.equals("get"))
        {
            String arr = UrlDAO.getURL(userid, domain);
            out.println("{\"Status\":\"Success\",\"URLArray\":" + arr + "}");
            return;

        }
        else
        {
            log.warning("Error: Invalid command : " + command + " : " + userid + " : " + remoteip);
            out.println("{\"Status\":\"Error: Invalid command\"}");
            return;
        }
        
     }

}
