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

import sg.nighthour.crypto.CryptoUtil;

public class LoginHandler
{

    
    private static final Logger log = Logger.getLogger(LoginHandler.class.getName());
    
    /**
     * Authenticates a user
     * 
     * @param userid
     * @param password
     * @return true if successful , false otherwise
     */
    public static boolean doLogin(String userid, String password)
    {
        
        if(userid == null || password == null)
        {
            log.warning("Error: userid or password is null" );
            return false;
        }
        
        
        if(UserDAO.isAccountLocked(userid))
        {
            password = null; 
            log.warning("Error: User enitty is locked : " + userid);
            return false; 
        }
        
        
        String stored_salt = UserDAO.getUserProperty(userid, "Salt");
        String stored_password = UserDAO.getUserProperty(userid,"Password");
        
        if(stored_salt == null || stored_password == null)
        {
            log.warning("Error: Unable to retrieve entity salt and password");
            return false; 
        }
        
        //Derive the PBKDF2 hash of the user supplied password
        byte[] stored_salt_bytes = CryptoUtil.hexStringToByteArray(stored_salt);
        char[] user_password_char = password.toCharArray();
        byte[] user_derivekey = CryptoUtil.getPasswordKey(user_password_char, stored_salt_bytes,
                CryptoUtil.PBE_ITERATION);
        
        CryptoUtil.zeroCharArray(user_password_char);
        password = null;
        
        if (user_derivekey == null)
        {
            log.warning("Error: Unable to derive PBKDF2 password using CryptoUtil  : " + userid);
            return false;
        }
        
       
        String user_derivekey_string = CryptoUtil.byteArrayToHexString(user_derivekey);
        CryptoUtil.zeroByteArray(user_derivekey);
        
        if (user_derivekey_string.equals(stored_password))
        {//Successful authentication
            user_derivekey_string = null;
            stored_password = null;
            
            /*
             * resetFailLogin count uses transaction and will check if userid exists and if it is locked 
             * It returns true if account is not locked and the fail login count is successfully set to 0
             * Using it here will help to prevent potential time of check/time of use issue 
             */
            return UserDAO.resetFailLogin(userid);
        }
        else
        {//Unsuccessful authentication 
            user_derivekey_string = null;
            stored_password = null;
            UserDAO.updateFailLogin(userid);
        }
        
        
        return false;
        
        
    }
    
    
}
