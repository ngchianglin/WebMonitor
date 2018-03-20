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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;




public class UserDAO
{
    
    private static final Logger log = Logger.getLogger(UserDAO.class.getName());
    
    
    /**
     * Retrieve a String property value from a user entity
     * @param userid id of user
     * @param key the key of the property value
     * @return Property value. null is returned if the entity doesn't exist, the attribute doesn't exist or 
     * the attribute value is not a String 
     * 
     */
    public static String getUserProperty(String userid, String key)
    {
        
        if(userid  == null || key == null)
        {
            log.warning("Error: userid or key is null");
            return null;
        }
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key k1 = KeyFactory.createKey(AppConstants.USER_KIND, userid);
        
        try
        {

            Entity user = datastore.get(k1);
            Object value = user.getProperty(key);
            
            if( value.getClass().equals(String.class) )
            {//Make sure that attribute is a String
                return (String) value;
            }
            else
            {
                log.warning("Error: Attribute is not a String type");
                return null; 
            }
        }
        catch (EntityNotFoundException e)
        {
            log.warning("Error: user entity not found : " + userid);
            return null;
        }
        
    }
    
    
    /**
     * Set a Property attribute for a user entity
     * 
     * @param userid
     * @param key  Property key
     * @param value  Property value
     * @return true if successful, false otherwise
     */
    public static boolean setUserProperty(String userid, String key, String value)
    {
        
        if(userid  == null || key == null || value == null)
        {
            log.warning("Error: Null arguments");
            return false;
        }
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key k1 = KeyFactory.createKey(AppConstants.USER_KIND, userid);
        
        try
        {

            Entity user = datastore.get(k1);
            user.setProperty(key, value);
            datastore.put(user);
            
        }
        catch (EntityNotFoundException e)
        {
            log.warning("Error: user entity not found : " + userid);
            return false;
        }
        
        return true;
    }
    
    
    /**
     * Update the fail login count of the user entity 
     * If the fail login exceeds a threshold, the user entity will be locked
     * 
     * @param userid
     * @return true if successful, false otherwise
     */
    public static boolean updateFailLogin(String userid)
    {
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key k1 = KeyFactory.createKey(AppConstants.USER_KIND, userid);
        Transaction txn = datastore.beginTransaction();
        
        
        if(userid == null)
        {
            log.warning("Error: userid is null");
            return false;
        }
        
        try
        {
            Entity user = null;
            try
            {
                user = datastore.get(k1);
            }
            catch (EntityNotFoundException e)
            {
                log.warning("Error: user entity not found : " + userid );
                return false;
            }
            
            
            boolean acctlock = (Boolean) user.getProperty("AccountLock");
            if(acctlock)
            {
                log.warning("Error: Unable to update fail login count user account is locked : " + userid);
                return false;
            }
            
            
            long faillogin = (Long) user.getProperty("FailLogin");
            faillogin++;
            user.setProperty("FailLogin", faillogin);
            if (faillogin >= AppConstants.MAX_FAIL_LOGIN)
            {
                log.warning("Error: Too many fail logins Account is locked : " + userid );
                user.setProperty("AccountLock", true);
            }
            datastore.put(txn, user);
            txn.commit();

            return true; 

        }
        finally
        {
            if (txn.isActive())
            {
                txn.rollback();
            }
        }
        
    }
    
    /**
     * Reset the fail login count of a user entity 
     * 
     * @param userid
     * @return true if successful, false otherwise
     */
    public static boolean resetFailLogin(String userid)
    {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key k1 = KeyFactory.createKey(AppConstants.USER_KIND, userid);
        Transaction txn = datastore.beginTransaction();
        
        if(userid == null)
        {
            log.warning("Error: userid is null");
            return false;
        }
        
        
        try
        {
            Entity user = null;
            try
            {
                user = datastore.get(k1);
            }
            catch (EntityNotFoundException e)
            {
                log.warning("Error: user entity not found : " + userid );
                return false;
            }
            
            boolean acctlock = (Boolean) user.getProperty("AccountLock");
            if(acctlock)
            {
                log.warning("Error: Unable to reset fail login user account is already locked : " + userid);
                return false;
            }
            
            user.setProperty("FailLogin", 0);
            datastore.put(txn, user);
            txn.commit();

            return true; 

        }
        finally
        {
            if (txn.isActive())
            {
                txn.rollback();
            }
        }
        
        
    }
    
    
    /**
     * Checks if a user entity exists 
     * @param userid
     * @return true if entity exists, false otherwise
     */
    public static boolean userExists(String userid)
    {
        if(userid == null)
        {
            log.warning("Error: userid is null");
            return false;
        }
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key k1 = KeyFactory.createKey(AppConstants.USER_KIND, userid);
        
        try
        {
            datastore.get(k1);
            return true; 
        }
        catch (EntityNotFoundException e)
        {
            return false;
        }
    }
    
    
    
    /**
     * Check if a user entity is locked
     * 
     * @param userid
     * @return true if entity is locked , return false otherwise
     */
    public static boolean isAccountLocked(String userid)
    {
        if (userid == null)
        {
            log.warning("Error: userid is null");
            return false;
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key k1 = KeyFactory.createKey(AppConstants.USER_KIND, userid);

        try
        {

            Entity user = datastore.get(k1);
            boolean acctlock = (Boolean) user.getProperty("AccountLock");
            return acctlock;

        }
        catch (EntityNotFoundException e)
        {
            log.warning("Error: user entity not found : " + userid);
            return false;
        }

    }

    
    /**
     * Updates the application mode for the user entity For capture mode, update the
     * capture remote ip address as well.
     * Only Requests coming from this remote ip will be
     * considered valid capture data
     * 
     * @param userid
     * @param mode
     * @param remoteip
     * @return true if successful, false otherwise
     * 
     */
    public static boolean updateCaptureMode(String userid, String mode, String remoteip)
    {

        if (userid == null || mode == null || remoteip == null)
        {
            log.warning("Error: userid, mode or remoteip is null");
            return false;
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key userkey = KeyFactory.createKey(AppConstants.USER_KIND, userid);

        Transaction txn = datastore.beginTransaction();
        try
        {
            Entity user = null;
            
            try
            {
                user = datastore.get(userkey);
            }
            catch(EntityNotFoundException e)
            {
                log.warning("Error: userid entity not found : " + userid);
                return false;
            }
             
            user.setProperty("Mode", mode);
            user.setProperty("CaptureModeIPAddress", remoteip);
            datastore.put(txn, user);
            txn.commit();
            return true;
        }
        finally
        {
            if (txn.isActive())
            {
                txn.rollback();
            }
        }

    }
    
    
    /**
     * Retrieves the current application mode from the user entity
     * 
     * @param userid
     * @return A string containing the mode setting or null if there are errors.
     */
    public static String getCurrentMode(String userid)
    {
        if (userid == null)
        {
            log.warning("Error: userid is null");
            return "Error: userid is null";
        }
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key userkey = KeyFactory.createKey(AppConstants.USER_KIND, userid);
        Entity user;
        String result = null;
        Transaction txn = datastore.beginTransaction();
        
        try
        {
            
            try
            {
                user = datastore.get(userkey);
            }
            catch (EntityNotFoundException e)
            {
                log.warning("Error: User entity not found : " + userid);
                return "Error: User Entity Not Found";
            }
            
            result = (String) user.getProperty("Mode");
            String captureip = (String) user.getProperty("CaptureModeIPAddress");
            txn.commit();
            
            if(result.equals("Capture"))
            {
                result = result + " | Allowed IP Addresss : " + captureip;
            }
            
            return HtmlEscape.escapeHTML(result);
            
        }
        finally
        {
            if (txn.isActive())
            {
                txn.rollback();
            }
        }
        
        
    }
    
    
    /**
     * Set the action to both alert and redirect to a specified url for a user
     * entity. Default action is to alert only
     * 
     * @param userid
     * @param url
     * @return true if successful, false otherwise
     */
    public static boolean enableRedirection(String userid, String url)
    {
        if (userid == null || url == null)
        {
            log.warning("Error: userid or url is null");
            return false;
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key userkey = KeyFactory.createKey(AppConstants.USER_KIND, userid);

        Transaction txn = datastore.beginTransaction();
        try
        {

            Entity user = null;
                    
            try
            {
                user = datastore.get(userkey);
            }
            catch(EntityNotFoundException e)
            {
                log.warning("Error: User entity not found : " + userid);
                return false;
            }
                          
            user.setProperty("Action", "AlertRedirection");
            user.setProperty("RedirectionURL", url);
            datastore.put(txn, user);
            txn.commit();
            return true; 

        }
        finally
        {
            if (txn.isActive())
            {
                txn.rollback();
            }
        }

    }
    
    
    
    /**
     * Disable url redirection and set the action to the default alert only for
     * a user entity
     * 
     * @param userid
     * @return true if successful, false otherwise
     */
    public static boolean disableRedirection(String userid) 
    {
        if (userid == null)
        {
            log.warning("Error: userid is null");
            return false; 
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key userkey = KeyFactory.createKey(AppConstants.USER_KIND, userid);

        Transaction txn = datastore.beginTransaction();
        try
        {
            Entity user = null;
            
            try
            {
                user =   datastore.get(userkey);
            }
            catch(EntityNotFoundException e)
            {
                log.warning("Error: User entity not found : " + userid);
                return false;
            }
            
            user.setProperty("Action", "Alert");
            user.setProperty("RedirectionURL", "");
            datastore.put(txn, user);
            txn.commit();
            return true; 

        }
        finally
        {
            if (txn.isActive())
            {
                txn.rollback();
            }
        }

    }
    
    
    /**
     * Retrives the redirectional url from the user entity
     * 
     * @param userid
     * @return String containing the url or an error message
     */
    public static String getRedirectionURL(String userid)
    {
        if (userid == null)
        {
            log.warning("Error: userid is null");
            return "Error: userid is null";
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key userkey = KeyFactory.createKey(AppConstants.USER_KIND, userid);
        Entity user;
        String result = null;

        try
        {
            user = datastore.get(userkey);
            result = (String) user.getProperty("RedirectionURL");
            return HtmlEscape.escapeHTML(result); // Html escape the redirection url

        }
        catch (EntityNotFoundException e)
        {
            log.warning("Error: user entity not found");
            return "Error: Cannot retrieve redirection url";
        }

    }


    

}
