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

(function()
{
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
		
		var addbtn = document.getElementById("add");
		addbtn.addEventListener("click", 
				   function(event) 
				   {
				       adddomain();    
				  
				   }
		);
		
		var delbtn = document.getElementById("delete");
		delbtn.addEventListener("click", 
				function(event)
				{
			       deletedomain();
				}
		);
		
		
	}
	
	/*
	 * Function to check that a domain is of valid format
	 */
	function validateField()
	{
		var name = document.getElementById("domain").value;
		name = name.toLowerCase();
		
		if(name.length > 128)
		{
		    return false; 	
		}
		
		var arr = name.split(".");
		var re; 
		if(arr.length === 2)
		{//regex for single part tld domain .sg
			re=/^[a-z0-9][a-z0-9][a-z0-9-]{0,62}\.[a-z]{2,20}$/i;
		}
		else if(arr.length >= 3)
		{//regex for 2 part tld domain .com.sg
			re=/^[a-z0-9][a-z0-9][a-z0-9-]{0,62}\.[a-z]{2,20}\.[a-z]{2,20}$/i;
		}
		else
		{
			return false; 
		}
	
		
		if(re.test(name) )
	    {
		    return true;	
		}
		else
		{
		     return false;	
		}	
	}
	
	/*
	 * Function to count the number of dots in a string
	 */
	function countDot(str)
	{
		var count = 0;
		var c;
		
		for(var i=0 ; i < str.length; i++)
	    {
			
		}
	}
	
	/*
	 * Add a new domain
	 */
	function adddomain()
	{
		
		if(validateField())
		{
		   showMsg("");
		   var domain = document.getElementById("domain").value;
		   domain = domain.toLowerCase();
		   sendRequest("add", domain);
			
		}
		else
		{
			showMsg("Invalid Domain name");
		}
	}
	
	/*
	 * Delete a domain
	 */
	function deletedomain()
	{
		if(validateField())
		{
			showMsg("");
			var domain = document.getElementById("domain").value;
			domain = domain.toLowerCase();
			sendRequest("delete", domain);
		}
		else
		{
			showMsg("Invalid Domain name");
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
	 * Send the command and domain data request to the server using ajax
	 */
	function sendRequest(cmd, domain)
	{
		var target = "https://" + window.location.hostname + "/domainctl"; 
		
		var data = "command=" + encodeURIComponent(cmd) + "&domain=" + encodeURIComponent(domain) + 
		           "&csrf=" + getCSRFToken() ;
		
		var req = new XMLHttpRequest();
		req.onreadystatechange = handleResponse(req, cmd, domain);
		
		req.open("POST", target);
		req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded;charset=UTF-8');
		req.send(data);
		
	}
	
	/*
	 * Function to process the ajax server response
	 */
	function handleResponse(req, cmd, domain)
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
		                  {//domain successfully added or deleted, update the domain listing
		                	  updateContent(cmd, domain);
		                	  
		                  }
		                  else if(result.startsWith("Error"))
		                  {
		                	  showMsg(result) ;
		                  }
		                  else
		                  {//Some other content such as redirection back to main login 
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
	 * Function to update the domain listing using DOM when a new domain is added or a domain is deleted
	 */
	function updateContent(cmd, domain)
	{
		
		var ol = document.getElementById("domainlist").children[0];
		if(ol)
		{
			
			if(cmd === "add")
			{
				var li = document.createElement("li");
				li.innerText = domain;
				ol.appendChild(li);
				document.getElementById("domain").value = "";
			}
			else
			{
				var child_element = null;
				for(var i=0 ; i < ol.children.length;i++)
				{
					child_element = ol.children[i];
					if(child_element.innerText === domain)
					{
					   break;	
					}
					
				}
				
				if(child_element)
				{
					ol.removeChild(child_element);
					document.getElementById("domain").value = "";
				}
				else
				{//Something is wrong reload the page
					window.location.reload(true);
				}
				
				
			}
			
		}
		else
		{//Something is wrong reload the page
			window.location.reload(true);
		}
		
	}
	
	
})();