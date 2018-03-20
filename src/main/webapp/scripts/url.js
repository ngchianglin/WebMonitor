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
	
	var urlcontent=""; 
	
	window.addEventListener("load",
			function(event)
			{
		        hookform();
			},
			false
	);
	
	/*
	 * Hook up the event listeners for url input controls
	 */
	function hookform()
	{
		
		var domain_selection = document.getElementById("domainsel");
		
		domain_selection.addEventListener("change",
				function(event)
				{
			          if(domain_selection.value !== "blank")
			          {
			        	 var domain = getDomainFromAttribute(domain_selection.value);
			             sendRequest("get", domain);
			          }
				}
		);
		
		
		var reloadbtn = document.getElementById("reloadbtn");
		
		reloadbtn.addEventListener("click", 
				function(event)
				{
			            var domainvalue = document.getElementById("domainsel").value;
			            if(domainvalue === "blank")
			            {
			            	var urlbox = document.getElementById("urlbox");
			    			urlbox.innerText = ""; 
			            }
			            else
			            {
			            	var domain = getDomainFromAttribute(domainvalue);
			                sendRequest("get", domain);
			            }
			            
				}
				
		);
		
		var searchbtn = document.getElementById("searchbtn");
		
		searchbtn.addEventListener("click",
		        function(event)
		        {
			 
			          search();
		        }
		
		);
		
		
	}
	
	
	/*
	 * Search through urls array listing and 
	 * display the ones that matches the search 
	 */
	function search()
	{
		var searchstring = document.getElementById("urlsearch").value; 
		var textresult = "";
		
		if(searchstring)
		{
			if(Array.isArray(urlcontent))
			{
				
				for(var i =0 ; i< urlcontent.length ; i++)
				{
					if(urlcontent[i].includes(searchstring))
					{
						textresult = textresult + (i + 1) + ".  " + urlcontent[i] + "\n";
					}
				}
				
				var urlbox = document.getElementById("urlbox");
				 
				
				if(textresult !== "")
				{
					urlbox.innerText = textresult;
				}
				else
				{
					urlbox.innerText ="No matches";
				}
				
			}
		}
		
		
	}
	
	/*
	 * Set a message on the msg div
	 * Can be used to display error messages
	 */
	function showMsg(msg)
	{
		
		var msgdiv = document.getElementById("msg");
		msgdiv.innerText = msg; 
		
	}
	
	/*
	 * Retrieves the corresponding domain from the value attribute in option tag
	 */
	function getDomainFromAttribute(value)
	{
		 var ret="";
		 var sel = document.getElementById("domainsel");
		 
		 for(var i=0;i<sel.children.length;i++)
		 {
			 var opt = sel.children[i];
			 
			 if(opt.getAttribute("value") === value)
			 {
				 ret = opt.innerHTML; 
			 }
			 
		  }
		 
		 return ret; 
		
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
	 *  Send the command request to the server using ajax
	 */
	
	function sendRequest(cmd, domain)
	{
		var target = "https://" + window.location.hostname + "/urlctl"; 
		
		var data = "command=" + encodeURIComponent(cmd) + "&domain=" + encodeURIComponent(domain) 
		            + "&csrf=" + getCSRFToken();
		
		var req = new XMLHttpRequest();
		req.onreadystatechange = handleResponse(req);
		
		req.open("POST", target);
		req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded;charset=UTF-8');
		req.send(data);
		
	}
	
	
	/* 
	 * Handles the server json response
	 */
	function handleResponse(req)
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
		                 
		                  var jobject=JSON.parse(result);
		                  
		                  if(jobject.Status)
		                  {
		                	  
		                	  if(jobject.Status === "Success")
		                	  {
		                		  //Update url content
		                		  updateContent(jobject); 
		                	  }
		                	  else
		                	  {
		                		  showMsg("Error invalid response from server ") ;
		                	  }
		                	  
		                	  
		                  }
		                  else
		                  {
		                	  showMsg("Error invalid response from server ") ;
		                	  
		                  }
		                  
		              
		            } 
		            else 
		            {
		            	showMsg("Error sending request to server  ") ;
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
	 * Update the url content from the server json reply
	 */
	function updateContent(jobject)
	{
		
		var arr = jobject.URLArray;
		
		if(Array.isArray(arr))
		{
			urlcontent = arr; 
			
			var textcontent = "";
			
			for (var i = 0 ; i < urlcontent.length ; i++)
			{
			     textcontent = textcontent + (i + 1) + ".  " + urlcontent[i] + "\n"; 	
			}
			
			var urlbox = document.getElementById("urlbox");
			urlbox.innerText = textcontent; 
			
		}
		else
		{
			 
			 showMsg("Error invalid response from server ") ;
			 var urlbox = document.getElementById("urlbox");
			 urlbox.innerText = ""; 
		}
		
	}
	
	
	
})();