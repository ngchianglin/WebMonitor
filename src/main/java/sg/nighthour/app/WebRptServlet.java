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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class WebRptServlet
 */
@WebServlet("/webrpt")
public class WebRptServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(WebRptServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public WebRptServlet()
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
        response.setCharacterEncoding("utf-8");

        // Check that request comes from a valid origin
        String origin = request.getHeader("Origin");
        if (origin == null)
        {
            log.warning("Error: Origin header not present : " + request.getRemoteAddr());
            return;
        }

        String domainentry = WebRptHandler.extractDomain(origin);
        if (domainentry == null)
        {
            log.warning("Error: Cannot extract domain from origin header : " + 
              origin + " : " + request.getRemoteAddr());
            return;
        }

        // Make sure that it is a valid domain under monitoring by the application
        if (!WebRptHandler.isValidDomain(domainentry))
        {
            log.warning("Error: Domain is not present in application datastore : " + 
                    domainentry + " : " + request.getRemoteAddr());
            return;
        }

        
        PrintWriter out = response.getWriter();
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Access-Control-Allow-Origin", origin);

        WebRptHandler.handleRequest(request, response, null, out);

    }

    /**
     * @see HttpServlet#doOptions(HttpServletRequest, HttpServletResponse)
     */
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        // Check that request comes from a valid origin
        String origin = request.getHeader("Origin");
        if (origin == null)
        {
            log.warning("Error: Origin header not present : " + request.getRemoteAddr());
            return;
        }

        String domain = WebRptHandler.extractDomain(origin);
        if (domain == null)
        {
            log.warning("Error: Cannot extract domain from origin header : " + 
              origin + " : " + request.getRemoteAddr());
            return;
        }
        
        if (!WebRptDAO.isValidDomain(domain))
        {
            log.warning("Error: Domain is not present in application datastore : " + 
                    domain + " : " + request.getRemoteAddr());
            return;
        }
            
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

    }

}
