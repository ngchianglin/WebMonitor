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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;

import sg.nighthour.crypto.CryptoUtil;

public class AntiCSRFToken
{

    private static final Logger log = Logger.getLogger(AntiCSRFToken.class.getName());

    /**
     * Generate a random token string
     * 
     * @param size
     *            The number of random bytes to be generated by SecureRandom
     * @return random token string
     */
    public static String generateToken(int size)
    {
        SecureRandom rand = new SecureRandom();
        byte[] result = new byte[size];
        rand.nextBytes(result);

        byte[] digest;

        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            digest = md.digest(result);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.warning("Error: Unable to get digest algorithm SHA-256");
            return null;

        }

        return CryptoUtil.byteArrayToHexString(digest);
    }

    /**
     * Sets up a CSRF token for a target url and saves it into the HttpSession
     * 
     * @param session
     * @param targeturl
     *            url where the csrf token corresponds to
     * @return Hidden input String with csrf token value that can be used on a
     *         webpage
     * @throws ServletException
     */
    public static String setToken(HttpSession session, String targeturl) throws ServletException
    {
        if (session == null || targeturl == null)
        {
            log.warning("Error: null arguments");
            throw new ServletException("null arguments");
        }

        String token = generateToken(AppConstants.CSRF_RANDOM_TOKEN_SIZE);
        if (token == null)
        {
            log.warning("Error: Unable to generate token");
            throw new ServletException("Error: Unable to generate token");
        }

        session.setAttribute(targeturl, token);

        StringBuilder buf = new StringBuilder(AppConstants.STD_BUF_SIZE);
        buf.append("<input id=\"csrftoken\" name=\"csrftoken\" type=\"hidden\" value=\"");
        buf.append(token);
        buf.append("\">");

        return buf.toString();
    }
    
    
    /**
     * Sets up a CSRF token for a target url and saves it into the HttpSession
     * 
     * @param session
     * @param targeturl
     * @return csrf token
     * @throws ServletException
     */
    public static String setTokenPlain(HttpSession session, String targeturl) throws ServletException
    {
        if (session == null || targeturl == null)
        {
            log.warning("Error: null arguments");
            throw new ServletException("null arguments");
        }

        String token = generateToken(AppConstants.CSRF_RANDOM_TOKEN_SIZE);
        if (token == null)
        {
            log.warning("Error: Unable to generate token");
            throw new ServletException("Error: Unable to generate token");
        }

        session.setAttribute(targeturl, token);
        return token;
    }
    
    
    /**
     * Compares two anti csrf tokens
     * @param token1
     * @param token2
     * @return true if they are the same, false otherwise
     */
    public static boolean compareToken(String token1, String token2)
    {
        if(token1 == null || token2 == null)
        {
            return false;
        }
        
        if(token1.equals(token2))
        {
            return true; 
        }
        
        return false;
    }
    

}
