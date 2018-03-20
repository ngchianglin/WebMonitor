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

(function(){
	"use strict";
	
	window.addEventListener("load", 
		      function(event)
		      {
			     hookform(); 
			  }, 
			  false
			);
	
	/*
	 * Hook up the event listeners for input buttons
	 */
	function hookform()
	{
		var enablebtn = document.getElementById("enable_redir");
		enablebtn.addEventListener("click",
				function(event)
				{
			         enableRedirection();
				}
				
			);
		
		var disablebtn = document.getElementById("disable_redir");
		disablebtn.addEventListener("click",
		      function(event)
		      {
			        disableRedirection();
		      }
		 );
		
	}
	
	/*
	 * Disable redirection
	 */
	function disableRedirection()
	{
		
		showMsg("");
		sendRequest("disableurl","");
		
	}
	
	/*
	 * Enable redirection
	 */
	function enableRedirection()
	{
		var url = document.getElementById("redir_url").value;
		if(validateURL(url))
		{
			showMsg("");
			sendRequest("enableurl",url);
		}
		else
		{
			showMsg("Invalid url");
		}
		
	}
	
	/*
	 * Validate the url format
	 */
	function validateURL(url)
	{
		if(url === "" || url.length > 256)
	    {
			return false;
	    }
			
		/* Note although this is case insensitive regex, url can actually
		have paths/parts that are case sensitive unlike domain names.
		The regex only check for characters that we allow regardless of case. 
		 */
		var re=/^https:\/\/[a-z0-9][a-z0-9-.?%=&/]{1,}[^.]$/i;
		
		if(re.test(url) )
	    {
		    return true;	
		}
		else
		{
		     return false;	
		}	
		
	}
	
	/*
	 * Set a message on the msg div
	 * Can be used to display error or status messages
	 */
	function showMsg(msg)
	{
		
		var msgdiv = document.getElementById("msg");
		msgdiv.innerText = msg; 
		
	}
	
	/*
	 * Retrieve the csrf token from the hidden input 
	 */
	function getCSRFToken()
	{
		var input = document.getElementById("csrftoken");
		return input.value;
		
	}
	
	/*
	 *  Send the request to the server using ajax
	 */
	function sendRequest(cmd, url)
	{
		var target = "https://" + window.location.hostname + "/actionctl"; 
		
		var data = "command=" + encodeURIComponent(cmd) + "&url=" + encodeURIComponent(url) + 
		           "&csrf=" + getCSRFToken();
		
		var req = new XMLHttpRequest();
		req.onreadystatechange = handleResponse(req, cmd, url);
		
		req.open("POST", target);
		req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded;charset=UTF-8');
		req.send(data);
		
	}
	
	/*
	 * Handles the server ajax response
	 */
	function handleResponse(req, cmd, url)
	{
		return function()
		{
			try 
		    {
		         if (req.readyState === XMLHttpRequest.DONE) 
		         {
		            if (req.status === 200) 
		            { 
		                  var result = req.responseText;
		                  result = result.trim();
		                  
		                  if(result === "Success")
		                  {
		                	  updateContent(cmd, url);
		                	  
		                  }
		                  else if(result.startsWith("Error"))
		                  {
		                	  showMsg(result) ;
		                  }
		                  else
		                  {
		                	//Some other content such as redirection back to main login 
			                //when session expires
			                window.location.reload(true);
		                	  
		                  }
		            } 
		            else 
		            {
		            	showMsg("Error sending request to server ") ;
		            }
		          }
		     }
		     catch( e ) 
		     {
		    	 showMsg("Error sending request to server ") ;
		     }
			
		};
		
	}
	
	/*
	 * Update the display url redirection content
	 */
	function updateContent(cmd, url)
	{
		 var urlspan = document.getElementById("redirecturl");
		 document.getElementById("redir_url").value = ""; //empty the url input field
	      if(cmd === "enableurl")
	      {
	    	  urlspan.innerText=url;
	      }
	      else if(cmd === "disableurl")
	      {
	    	  urlspan.innerText="";
	      }
	      else
	      {
	    	  window.location.reload(true);  
	      }
		
	}
	
	
})();