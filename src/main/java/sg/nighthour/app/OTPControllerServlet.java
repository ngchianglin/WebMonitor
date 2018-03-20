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
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class OTPControllerServlet
 */
@WebServlet("/otpctl")
public class OTPControllerServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(OTPControllerServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public OTPControllerServlet()
    {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        
        HttpSession session = request.getSession(false);

        // Make sure it has a valid 2fa session from login page
        // userid2fa session must be set
        String userid2fa = (String) session.getAttribute("userid2fa");
        if(userid2fa == null)
        {
            log.warning("Error: User credentials not validated : " + userid2fa + " : " + request.getRemoteAddr());
            response.sendRedirect("/error.html");
            return; 
        }

        String userid = (String) session.getAttribute("userid2fa");
        // Remove the userid2fa attribute to prevent multiple submission attempts
        session.removeAttribute("userid2fa");

        String otpvalue = (String) request.getParameter("totp");

        if (otpvalue == null)
        {
            session.invalidate();
            log.warning("Error: Invalid otp value : " + userid + " : " + request.getRemoteAddr());
            response.sendRedirect("/error.html");
            return; 
        }

       if (OTPHandler.checkOTP(userid, otpvalue))
       {//OTP validation successful
           
           String currenturl = (String) session.getAttribute("currenturl");
           
           /*
            * Prevent Session Fixation by setting a new session
            */
           session.invalidate();
           session = request.getSession(true);
           
           session.setAttribute("userid", userid);
           
           if(currenturl == null)
           {
               currenturl = "/home";   
           }
          
           //Enable CSRF protection when viewing pages
           session.setAttribute("currenturl", currenturl);
           String csrftoken=  AntiCSRFToken.setTokenPlain(session, currenturl);
           
          
           
           /*
            * Set a session cookie with HttpOnly, Secure and Samesite=Strict 
            * security flags
            */
           String custsession = "JSESSIONID=" + session.getId() + ";Path=/;Secure;HttpOnly;SameSite=Strict";
           response.setHeader("Set-Cookie", custsession);
           response.sendRedirect(currenturl + "?csrf=" + csrftoken);
           return; 
           
           
       }
       else
       {//OTP validation fails
           
           if(UserDAO.isAccountLocked(userid))
           {//Account is locked
               
               log.warning("Error: OTP validation failed account is locked : " + userid + " : " + request.getRemoteAddr());
               
               session.invalidate();
               response.sendRedirect("/locked.html");
               return;
           }
           else
           {
               log.warning("Error: OTP validation failed : " + userid + " : " + request.getRemoteAddr());
               
               // Send back to the otp input page again
               session.setAttribute("userid2fa", userid);
               session.setAttribute("otperror", "");
               RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/otp.jsp");
               rd.forward(request, response);
           }
           
       }
       

       

    }

}
