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


public class ModeHandler extends GenericFunctionHandler
{
    private static final Logger log = Logger.getLogger(ModeHandler.class.getName());
    
    public static void handleRequest(HttpServletRequest request, HttpServletResponse response, String userid, PrintWriter out) 
            throws ServletException, IOException
    {
        
        if(userid == null)
        {
            log.warning("Error: userid is null");
            return; 
        }
        
        String command = request.getParameter("command");
        String remoteip = request.getRemoteAddr();
        
        if (command == null)
        {
            log.warning("Error: null arguments : "  + userid + " : " +  remoteip);
            out.println("Error: Invalid request");
            return;
        }
        else if (command.equals("disable"))
        {// Set disable mode
            
           if(UserDAO.setUserProperty(userid, "Mode", AppConstants.MODEDISABLE ))
           {
               out.println("Success");
           }
           else
           {
               log.warning("Error: Unable to set disable mode check if user entity exists : "  + userid + " : " +  remoteip);
               out.println("Error: Cannot set disable mode");
           }
           return;
        }
        else if (command.equals("capture"))
        {// Set capture mode
            
            if(UserDAO.updateCaptureMode(userid, AppConstants.MODECAPTURE, remoteip))
            {
                out.println("SuccessCapture:" + request.getRemoteAddr());
            }
            else
            {
                log.warning("Error: Unable to set capture mode check if user entity exists : "  + userid + " : " +  remoteip);
                out.println("Error: Cannot set capture mode");
            }
            return;
        }
        else if (command.equals("monitor"))
        {// Set monitor mode
               
            if(UserDAO.setUserProperty(userid, "Mode", AppConstants.MODEMONITOR))
            {
                out.println("Success");
            }
            else
            {
                log.warning("Error: Unable to set monitor mode check if user entity exists : "  + userid + " : " +  remoteip);
                out.println("Error: Cannot set monitor mode");
            }
            return; 
        }
        else
        {// Invalid mode
            out.println("Error: Invalid command type");
            log.warning("Error: Invalid command : "  + command + " : " + userid + " : " +  remoteip);
            return; 
        }
        
    
    }

}
