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
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ApplicationControllerServlet
 */

@WebServlet(name="ApplicationControllerServlet",
loadOnStartup = 1,
urlPatterns = {"/home",
               "/domain",
               "/action",
               "/mode",
               "/url",
               "/domainctl",
               "/modectl",
               "/urlctl",
               "/actionctl"})

public class ApplicationControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ApplicationControllerServlet.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ApplicationControllerServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	    request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        
	    String spath = request.getServletPath();
	    String csrf = request.getParameter("csrf");
	    String viewurl = "/WEB-INF/views"; 
	    
        HttpSession sess = request.getSession(false);
        
        if(sess == null)
        {
            log.warning("Error: Null Session : GET : " + request.getRemoteAddr());
            response.sendRedirect("/index.jsp");
            return;
        }
	    
	    if(spath.equals("/home"))
	    {
	        viewurl = viewurl +  spath + ".jsp";
	    }
	    else if(spath.equals("/domain"))
	    {
	        viewurl = viewurl +  spath + ".jsp";
	    }
	    else if(spath.equals("/action"))
	    {
	        viewurl = viewurl +  spath + ".jsp";
	    }
	    else if(spath.equals("/mode"))
	    {
	        viewurl = viewurl +  spath + ".jsp";
	    }
	    else if(spath.equals("/url"))
	    {
	        viewurl = viewurl +  spath + ".jsp";
	    }
	    else
	    {//Redirect back to index.jsp if path is not valid
	        log.warning("Error: Invalid Servlet Path: GET " + spath + " : " + request.getRemoteAddr());
	        sess.invalidate();
            response.sendRedirect("/index.jsp");
            return;   
	    }
	    
	   
	    if(sess.getAttribute("userid")== null)
	    {//Not authenticated redirect back to index.jsp
	        log.warning("Error: Unauthenticated Session : " + request.getRemoteAddr());
	        sess.invalidate();
	        response.sendRedirect("/index.jsp");
	        return;
	    }
	    else
	    {//Authenticated sessions forward to views
	        
	        sess.setAttribute("currenturl", spath);
	        String saved_csrf = (String) sess.getAttribute(spath);
	        
	        if(AntiCSRFToken.compareToken(csrf, saved_csrf))
	        {//Anti CSRF check is ok
	            sess.removeAttribute(spath);
                RequestDispatcher dp = request.getRequestDispatcher(viewurl);
                dp.forward(request, response);
                return;
	            
	        }
	        else
	        {//Invalid Anti CSRF parameters
	            String userid = (String) sess.getAttribute("userid");
                log.warning("Error: Invalid csrf : " + userid + " : " + request.getRemoteAddr());
                sess.setAttribute("userid2fa", userid);
                sess.removeAttribute("userid");
                RequestDispatcher dp = request.getRequestDispatcher("/WEB-INF/views/otp.jsp");
                dp.forward(request, response);
                return;
	        }
	       
	
	    }
	    
	    
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	    request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        
        PrintWriter out = response.getWriter();
        String spath = request.getServletPath();
        String csrf = request.getParameter("csrf");
        
        HttpSession session = request.getSession(false);
        if(session == null)
        {//no existing session
            log.warning("Error: Null Session : POST : " + spath + " : " + request.getRemoteAddr());
            response.sendRedirect("/index.jsp");
            return;
        }
        
        String userid = (String)session.getAttribute("userid");
        
        if(userid == null)
        {//Session not authenticated
            log.warning("Error: Unauthenticated Session : POST : " + spath + " : " + request.getRemoteAddr());
            session.invalidate();
            response.sendRedirect("/index.jsp");
            return;
        }
        
        String saved_csrf = (String) session.getAttribute(spath);
        
        if(spath.equals("/domainctl"))
        {
            if(AntiCSRFToken.compareToken(csrf, saved_csrf))
            {
                DomainHandler.handleRequest(request, response, userid, out);
                return; 
            }
            else
            {
                log.warning("Error: Invalid csrf : POST : " + spath + " : " +
                     userid + " : " + request.getRemoteAddr());
                out.println("Error: Invalid CSRF Token");
                return;
            }
            
        }
        else if(spath.equals("/modectl"))
        {
            
            if(AntiCSRFToken.compareToken(csrf, saved_csrf))
            {
                ModeHandler.handleRequest(request, response, userid, out);
                return; 
            }
            else
            {
                log.warning("Error: Invalid csrf : POST : " + spath + " : " +
                     userid + " : " + request.getRemoteAddr());
                out.println("Error: Invalid CSRF Token");
                return;
            }
            
        }
        else if(spath.equals("/urlctl"))
        {
           
            if(AntiCSRFToken.compareToken(csrf, saved_csrf))
            {
                UrlHandler.handleRequest(request, response, userid, out);
                return; 
            }
            else
            {
                log.warning("Error: Invalid csrf : POST : " + spath + " : " +
                     userid + " : " + request.getRemoteAddr());
                out.println("Error: Invalid CSRF Token");
                return;
            }
            
        }
        else if(spath.equals("/actionctl"))
        {
            
            if(AntiCSRFToken.compareToken(csrf, saved_csrf))
            {
                ActionHandler.handleRequest(request, response, userid, out);
                return; 
            }
            else
            {
                log.warning("Error: Invalid csrf : POST : " + spath + " : " +
                     userid + " : " + request.getRemoteAddr());
                out.println("Error: Invalid CSRF Token");
                return;
            }
            
        }
        else
        {
            
            log.warning("Error: Invalid servlet path : POST : " + spath + " : " +
                    userid + " : " + request.getRemoteAddr());
            return;
            
        }
	    
	    
	}

}
