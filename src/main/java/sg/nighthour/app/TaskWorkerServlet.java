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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Servlet implementation class TaskWorkerServlet
 */
@WebServlet("/worker")
public class TaskWorkerServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(TaskWorkerServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TaskWorkerServlet()
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
        PrintWriter out = response.getWriter();
        response.setContentType("text/plain;charset=UTF-8");

        // Retrieve the Alert entity key
        String key = request.getParameter("key");
        // log.warning("Got : " + key);

        long numerickey = Long.parseLong(key);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Key k1 = KeyFactory.createKey("Alert", numerickey);

        String userid = null;
        String content = null;
        String senderip = null;
        String sha256 = null;
        String url = null;

        try
        { // Retrieve the alert entity
            Entity alert = datastore.get(k1);
            userid = (String) alert.getProperty("userid");
            content = ((Text) alert.getProperty("content")).getValue();
            senderip = (String) alert.getProperty("senderip");
            sha256 = (String) alert.getProperty("sha256");
            url = (String) alert.getProperty("url");
        }
        catch (EntityNotFoundException e)
        {
            log.warning("Unable to retrieve alert entity : " + key);
            throw new ServletException("Unable to retrieve alert entity");
        }

        // Get the email domain
        String arr[] = userid.split("@");
        if (arr.length != 2)
        {
            log.warning("Invalid email domain");
            throw new ServletException("Invalid email domain");
        }
        String emaildomain = arr[1];

        // Look up MX records for email domain
        ArrayList<String> mxrecords = new ArrayList<String>();
        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
        env.put(Context.PROVIDER_URL, "dns://8.8.8.8 dns://8.8.4.4");

        try
        {
            DirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes(emaildomain, new String[] { "MX" });

            NamingEnumeration<?> results = attrs.getAll();
            while (results.hasMore())
            {
                Attribute attr = (Attribute) results.next();
                NamingEnumeration<?> mxresults = attr.getAll();
                while (mxresults.hasMore())
                {
                    String mx = (String) mxresults.next();
                    mxrecords.add(mx);
                }
            }

        }
        catch (NamingException e)
        {
            log.warning("NamingException unable to get MX : " + e);
            throw new ServletException("NamingException unable to get MX : " + e);
        }

        Collections.sort(mxrecords);
        if (mxrecords.isEmpty())
        {
            log.warning("Empty MX records");
            throw new ServletException("Empty MX records");
        }

        // Send email alert
        boolean done = false;
        int index = 0;

        while (!done && (index < mxrecords.size()))
        {

            String smtphost = mxrecords.get(index);
            try
            {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "false");
                props.put("mail.smtp.starttls.enable", "true");
                // props.put("mail.smtp.localhost", "nighthour.sg");
                props.put("mail.smtp.host", smtphost);
                props.put("mail.smtp.port", "25");
                Session session = Session.getInstance(props);
                String msg = "Web Content has changed \n" + url + "\n" + "Sender Address : " + senderip + "\n"
                        + "Sha256 : " + sha256 + "\n\n\n" + content;

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress("noreply.nighthour1@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userid));
                message.setSubject("Alert message " + url);
                message.setText(msg, "UTF-8");
                Transport.send(message);
                done = true;
            }
            catch (MessagingException e)
            {
                log.warning("Error sending email " + index + " " + smtphost);
                if (index == (mxrecords.size() - 1))
                {
                    throw new ServletException("Error sending email");
                }
            }

            index++;
        }

        // delete the alert entity
        datastore.delete(k1);

        out.println("");

    }

}
