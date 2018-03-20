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
		var disablebtn = document.getElementById("disable");
		disablebtn.addEventListener("click",
				function(event)
				{
			         disable();
				}
				
			);
		
		var capturebtn = document.getElementById("capture");
		capturebtn.addEventListener("click",
		      function(event)
		      {
			        capture();
		      }
		 );
		
		
		var monitorbtn = document.getElementById("monitor");
		monitorbtn.addEventListener("click",
		      function(event)
		      {
			      monitor();
		      }
		);
		
	}
	
	/*
	 * Set disable mode
	 */
	function disable()
	{
		
		showMsg("");
		sendRequest("disable");
	}
	
	/*
	 * Set capture mode
	 */
	function capture()
	{
		showMsg("");
		sendRequest("capture");
		
	}
	
	/*
	 * Set monitor mode
	 */
	function monitor()
	{
		showMsg("");
		sendRequest("monitor");
		
	}
	
	/*
	 * Display message to messge div
	 * Can be used to show error or status messages
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
	 * Send the command request to the server using ajax
	 */
	function sendRequest(cmd)
	{
		var target = "https://" + window.location.hostname + "/modectl"; 
		
		var data = "command=" + encodeURIComponent(cmd) + "&csrf=" + getCSRFToken() ;
		
		var req = new XMLHttpRequest();
		req.onreadystatechange = handleResponse(req, cmd);
		
		req.open("POST", target);
		req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded;charset=UTF-8');
		req.send(data);
		
	}
	
	/*
	 * Function to process the ajax server response
	 */
	function handleResponse(req, cmd)
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
		                  { //Update the mode content
		                	  updateContent(cmd);
		                	  
		                  }
		                  else if(result.startsWith("SuccessCapture:"))
		                  { //Update the mode content to capture with capture ip
		                	  updateCaptureContent(cmd, result);
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
	 * Update the mode content
	 */
	function updateContent(cmd)
	{
		 var modespan = document.getElementById("modetxt");
		 var showusermode = document.getElementById("showusermode");
	      if(cmd === "disable")
	      {
	    	  modespan.innerText="Disable";
	    	  showusermode.innerText="Disable";
	      }
	      else if( cmd === "monitor")
	      {
	    	  modespan.innerText="Monitor";   
	    	  showusermode.innerText="Monitor";
	      }
	      else
	      {
	    	  window.location.reload(true);  
	      }
		
	}
	
	/*
	 * Update the mode content with the allowed capture ip
	 */
	function updateCaptureContent(cmd, result)
	{
		
		var modespan = document.getElementById("modetxt");
		var showusermode = document.getElementById("showusermode");
		
		if(cmd === "capture")
		{
			var ipstr = result.substring(15);
			
			if(ipstr.length < 7)
			{
				window.location.reload(true);
			}
			else
			{
				modespan.innerText = "Capture |" + " Allow IP Address: " + ipstr;  
				showusermode.innerText = "Capture |" + " Allow IP Address: " + ipstr; 
			}
			
		}
		else
		{
			window.location.reload(true); 
		}
		
	}
	
	
})();