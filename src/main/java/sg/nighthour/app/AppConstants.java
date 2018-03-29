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

public class AppConstants
{

    /*
     * Definitions for Datastore entity kinds
     */

    public static final String GLOBAL_DOMAIN_TABLE_KIND = "GlobalDomainsTable";
    public static final String GLOBAL_DOMAIN_ENTRY_KIND = "GlobalDomainEntry";

    public static final String DOMAIN_KIND = "Domain";
    public static final String USER_KIND = "User";
    public static final String URL_KIND = "URL";
    public static final String ALERT_KIND = "Alert";

    /*
     * Definitions for Datastore entities
     */
    public static final String GLOBAL_DOMAIN_TABLE_ENTITY = "GlobalDomains";

    /*
     * Defines the application modes
     */
    public static final String MODEDISABLE = "Disable";
    public static final String MODECAPTURE = "Capture";
    public static final String MODEMONITOR = "Monitor";

    public static final int MAX_URL_PER_DOMAIN = 300; // Maximum urls per domain

    /*
     * Cookie settings for WebRpt servlet Currently cookies are not used
     */
    public static final String WEBRPT_COOKIE_NAME = "wrpt";
    public static final String WEBRPT_COOKIE_DOMAIN = "nighthour-002-dot-abiding-bongo-179014.appspot.com";

    // Defines maximum fail logins before account is locked
    public static final int MAX_FAIL_LOGIN = 5;

    // Defines the maximum size of json input string that SimpleJsonParser will
    // accept
    public static final int MAX_JSON_PARSER_INPUT_SIZE = 1000000;

    // Session timeout of the application
    public static final long MAX_SESSION_TIMEOUT = 15 * 60 * 1000;

    // Number of random bytes for generating CSRF token
    public static final int CSRF_RANDOM_TOKEN_SIZE = 48;

    // Defines the maximum number of domains per user
    public static final int MAX_DOMAIN = 10;

    public static final int STD_BUF_SIZE = 256;

    public static final int MAX_DOMAIN_LEN = 128;
    public static final int MAX_ACTIONURL_LEN = 256;
    
    public static final int PBE_ITERATION = 10000;
    
    //The from email address for sending out email alerts
    public static String fromemail = "noreply@nighthour.sg";

}

