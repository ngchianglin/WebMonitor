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

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import sg.nighthour.crypto.CryptoUtil;
import sg.nighthour.crypto.TimeBaseOTP;

public class OTPHandler
{
    
    private static final Logger log = Logger.getLogger(OTPHandler.class.getName());

    /**
     * Validates the OTP value for a user entity 
     * 
     * @param userid
     * @param otpvalue
     * @return true if OTP is validated, false otherwise
     */
    public static boolean checkOTP(String userid, String otpvalue)
    {
        
        if(userid == null || otpvalue == null)
        {
            log.warning("Error: userid or otpvalue is null" );
            return false;
        }
        
        if(UserDAO.isAccountLocked(userid))
        {
            log.warning("Error: User enitty is locked : " + userid);
            return false; 
        }
        
        String otpsecret = UserDAO.getUserProperty(userid, "TOTP");
        
        if(otpsecret == null)
        {
            log.warning("Error: Unable to retrieve otp secret : " + userid);
            return false; 
        }
        
        String otpresult = TimeBaseOTP.generateOTP(CryptoUtil.hexStringToByteArray(otpsecret));
        otpsecret = null;
        
        if(otpresult == null)
        {
            log.warning("Error: Unable to generate TOTP value");
            return false;
        }
        
        
        if(otpresult.equals(otpvalue))
        {// OTP validation successful
        
            /*
             * resetFailLogin count uses transaction and will check if userid exists and if it is locked 
             * It returns true if account is not locked and the fail login count is successfully set to 0
             * Using it here will help to prevent potential time of check/time of use issue 
             */
            return UserDAO.resetFailLogin(userid);
        }
        else
        {
            UserDAO.updateFailLogin(userid);
            
        }
        
        return false;
    }
    
    /**
     * Check if otperror session attribute is present. 
     * The session attribute is set by OTPControllerServlet
     * if OTP validation fails. otp.jsp calls this method
     * to display an error message.  
     * 
     * @param session
     * @return error message if otperror session attribute is present
     * @throws ServletException
     */
    public static String getOTPErrorMessage(HttpSession session) throws ServletException
    {
        if (session == null)
        {
            log.warning("Error: null arguments");
            throw new ServletException("null arguments");
        }

        String otperror = (String) session.getAttribute("otperror");

        if (otperror == null)
        {
            return "";
        }
        else
        {
            session.removeAttribute("otperror");
            return "Invalid OTP";
        }

    }

    
}
