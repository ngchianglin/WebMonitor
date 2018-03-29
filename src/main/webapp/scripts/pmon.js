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

(function ()
{

  "use strict";
	
  var crypto; 	
  var subtle; 
  var supportMsg ="";
  var seq = 1; 
  var elementvalues="";
  var cframes=[];
  var JSON;
  var XMLHttpRequest; 
 
  window.addEventListener("load", 
   function(event)
   {
     runmon(); 
   }, 
  false
  );
  
  
  /*
  WebRpt object holding information for current url/page
  */
  function WebRpt(url, cksum, seq, smsg, cdate, content, clen)
  {
     this.url = url;
     this.cksum = cksum;
     this.seq = seq;
     this.smsg = smsg;
     this.cdate = cdate;
     this.content = content;
     this.clen = clen;
  }
  
  
  /*
   Return a child iframe
  */
  
  function getCframe()
  {
      var ifr = document.createElement('iframe'); 
      ifr.src='about:blank';
      ifr.width = 0;
      ifr.height = 0;
      ifr.style.display = 'none';
      document.body.appendChild(ifr);
      cframes.push(ifr);
      return ifr;
  }
  
  /*
  Remove child frame
  */
  function removeCframe(ifr)
  {
     try
     {
       document.body.removeChild(ifr);
     }
     catch(err)
     {
     
     }
  }
  
  
  /*
  Clean up all child frames
  */
  function cleanCframes()
  {
     var len = cframes.length;
     
     for(var i=0;i<len ;i++)
     {
         removeCframe(cframes[i]);
     }
     cframes = []; 
  }
  
  /*
  Tight loop
  */
  function tightLoop(randrange, basecycle)
  {
      var ifr = getCframe();
      
      if(window.crypto)
      {
         crypto = ifr.contentWindow.crypto;
         crypto.getRandomValues = ifr.contentWindow.crypto.getRandomValues;
      }
      else if(window.msCrypto)
      {
         crypto = ifr.contentWindow.msCrypto;
         crypto.getRandomValues = ifr.contentWindow.msCrypto.getRandomValues;
      }
      
      var rint = new Uint32Array(1);
      crypto.getRandomValues(rint);
      
      var interval = (rint[0] % randrange) + basecycle;
      rint =0;
      
      for(var i = 0 ; i < interval ; i++)
      {//do tight loop
         rint = rint + 1; 
      }
      
      removeCframe(ifr);
  }
  
  
  /*
  Try to prevent javascript tampering
  Obtain objects and functions from 
  child frame, to guard against javascript
  tampering
  */
  
 function guardObjects()
 {
     var ifr = getCframe();
     if(window.msCrypto)
     {
         crypto = ifr.contentWindow.msCrypto;
         subtle = ifr.contentWindow.msCrypto.subtle;
     }
     else if(window.crypto)
     {
         crypto = ifr.contentWindow.crypto;
         subtle = ifr.contentWindow.crypto.subtle;
     }
     
     if(window.JSON)
     {
         JSON = ifr.contentWindow.JSON;
         JSON.stringify = ifr.contentWindow.JSON.stringify;
     }
 
  }
  
  /*
  Obtain XMLHttpRequest from child frame
  to try prevent javascript tampering
  */
  function guardXMLHttpRequest()
  {
     var ifr = getCframe();
     XMLHttpRequest = ifr.contentWindow['XMLHttpRequest'];
      
  }
  
  /*
  Function to check that webcrypto is supported
  and setup the subtle object. 
  */
  function initCrypto()
  { 
  
    crypto = window.crypto || window.msCrypto; 
	if(!crypto)
	{
		return false;
	}
    else if(crypto.subtle)
    { //IE 11, Chrome, firefox
		 subtle = crypto.subtle;  
		 return true;
	}
	else if(crypto.webkitSubtle)
	{ //Safari browser
		 subtle =  crypto.webkitSubtle;
		 return true;
	}
	else
	{
		 return false; 
	}
  }
  
  
  /*
  Function to check if native promise is supported
  */
  function checkPromise()
  {	  
  
     if(window.Promise)
     {
         if(window.Promise.toString().indexOf("[native code]") !== -1)
         {
             supportMsg = supportMsg + " : Promise Supported";	
	         return true;
         
         }
     }
   
    supportMsg = supportMsg + " : Promise Unsupported";	
    return false;
    
  }
  
  
  
  /* Function to retrieve the html content using innerHTML */
  function getContent()
  {
	  var content = document.documentElement.innerHTML;
	  content = content.replace(/\s+/g, ' ');
      content = content.trim();
	  return content; 
	  
  }
  
  /*Function to retrieve content using dom traversal */
  function getProcessContent()
  {
	 var root = document.documentElement;
	 elementvalues = "";
     traverse(root); 

	 var ret =  elementvalues  ; 
	 ret = ret.replace(/\s+/g, ' ');
     ret = ret.trim();
	 return ret;
  }
  
  
  /* Recursive function to traverse the dom tree and extracting the content of the DOM 
   * The extracted content is saved into elementvalues 
   * */
  function traverse(element)
  {
   if(element)
   {
       elementvalues = elementvalues + " " + element.tagName + " "; 
	  
       
       //Extract attributes of html element
	   var attributes_array = [];
	   
	   if(element.hasAttributes())
	   {
	      var attributes = element.attributes;
		  for(var i=0; i< attributes.length;i++)
		  {
		     attributes_array.push(attributes[i].name);
			 attributes_array.push(attributes[i].value);
		  }
		  
		 attributes_array.sort();
	     elementvalues = elementvalues + attributes_array.join(); 
	   
	   }
	   
	   //Extract script content if it is script element
	   if(element.tagName === "SCRIPT")
	   {
	      if(element.innerText)
		  {
		    elementvalues = elementvalues + element.innerText + " ";
		  }
	   }
	   
	   //Extract any text or comment from element 
	   for (var i=0; i < element.childNodes.length; i++)
       {
           var childnode = element.childNodes[i];
          
           if(childnode.nodeType === 3 ||  childnode.nodeType === 8 )
           {//text or comment node
               elementvalues = elementvalues + childnode.nodeValue + " "; 
           }	  
       }
	   
	   //Depth first recursion to process child elements
	   if(element.children)
	   {
           for(var i=0;i< element.children.length;i++)
	       { 
	          traverse(element.children[i]);
	       }
	   }
       
   }
   else
   {
      return "";
   }

  }
  
  
  
  /*Function to send web report to server using json and ajax */
  function sendRpt(rpt)
  {
      
      guardXMLHttpRequest();
     
	  var endpoint = "https://demo2-nighthour.appspot.com/webrpt";
	  var xhttp = new XMLHttpRequest();
	  
	  var data = JSON.stringify({"WebRpt" : rpt });
	  var timeout_timer;
	  
	  xhttp.onreadystatechange = procResponse(xhttp, timeout_timer);
      xhttp.open("POST", endpoint);
      xhttp.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
      xhttp.send(data);
	  
	  timeout_timer = setTimeout(
			  function(xhttp)
			  {
				  return function()
				  {
					  
					  xhttp.abort();
					  console.log("ajax timeout"); 
				  }
				  
			  }
			  , 60000
			);
	  
	  seq = seq + 1; 
	  
  }
  
  /*Function to process the response from the server */
  function procResponse(xhttp, timeout_timer)
  {
    return function()
    {
       try 
       {
          if (xhttp.readyState === XMLHttpRequest.DONE) 
          {
        	  clearTimeout(timeout_timer);
        	  timeout_timer = null;
        	  
              if (xhttp.status === 200) 
              { 
            	 var resp = xhttp.responseText;
            	 resp = resp.trim();
            	  
                 if(resp.indexOf("600") !== -1)
				 {
					 //alert("Bad response");
					 var arr = resp.split(" ");
					 if(arr[1])
					 {
					    window.location.replace(arr[1]); 	
					 }
					 
                     cleanCframes();
					 return;
				 }
                 else if(resp === "Ok")
                 {
                	 //alert("Good response");
                 }
                 else
                 {// Other responses ignore and do nothing
                	 
                 }
                 
                 cleanCframes();
				 
              } 
              else 
              {
                  console.log("Http status error");
              }
          }
       }
       catch(e) 
       {
    	   if(timeout_timer)
    	   {
    		   clearTimeout(timeout_timer);
    	   }
    	  
           console.log(e); 
           cleanCframes();
       }
    };
  
 }
  
  
  /* Converts a Arraybuffer into hexadecimal string */
  function toHex(buf)
  {
	  var dview = new DataView(buf); //Use DataView to prevent platform endianness issue. 
	  var hexstring="";
	  var i;
	  
	  for(i=0; i < dview.byteLength ; i++)
	  {
		  var byteval = dview.getUint8(i);
		  
		  if(byteval < 16)
		  {
              hexstring = hexstring + "0" + byteval.toString(16);
		  }	  
		  else
		  {
			  hexstring = hexstring + byteval.toString(16);
		  }
	  }
	  
	  return hexstring;
  }
  
   
  
  /* Async Sha256 function using promise */
  function async_sha256(str)
  { 
	  var i;
	  var utf8str = unescape(encodeURIComponent(str));
	  var utf8buf = new Uint8Array(utf8str.length);
      for(i=0; i < utf8str.length;i++)
		  utf8buf[i] = utf8str.charCodeAt(i);
	  
	  
	 return subtle.digest("SHA-256", utf8buf).then(
	           function(hex) { return toHex(hex); }
	           ); 
				
  }
  
  
  /*
  Function to process content using Promise
  */
  
  function pproc()
  {
	 var url=window.location.href;	  
	 var c = getContent();
	 
	 /* 
	  * Process the content for sha256 checksum
	  * The content that is based on innerHTML 
	  * is different between IE 11 and firefox/chrome
	  * IE 11 sorts the attributes in element leading to 
	  * different checksum result. 
	  * Here we extract the relevant content using our own DOM traversal
	  * */
	
	 var processcontent = getProcessContent(); 
	 
	 async_sha256(processcontent).then(
	   function(hexcode)
	   { 
	      var cdate = Date();
	      var rpt = new WebRpt(url, hexcode, seq, supportMsg, cdate, c, c.length);
		  sendRpt(rpt); 
	   }
	  );
  }
  
  
  /* Asynchronous sha256 using callbacks without promise IE 11 */
  function cb_sha256(str, func)
  {
	  //Additional check to make sure msCrypto is available IE 11.
	  //MS edge is more standard complaint and support the normal crypto
	  if (!window.msCrypto)  
	  {
		  func("00000000000000000000000000000000");
		  return; 
	  }
	   
	  var i;
	  var utf8str = unescape(encodeURIComponent(str));
	  var utf8buf = new Uint8Array(utf8str.length);
      for(i=0; i < utf8str.length;i++)
		  utf8buf[i] = utf8str.charCodeAt(i);
	  
	  
	  var op = subtle.digest("SHA-256", utf8buf);
	  
	  op.oncomplete= function (e)
	  {
		 var hexstring = toHex(e.target.result);
		 func(hexstring); 
	  }
	  
  }
  
  
  /*
  Function to process content without Promise
  IE 11
  */
  function proc()
  {
	  
     var url=window.location.href;	 
	 var c = getContent();
	
	 /* 
	  * Process the content for sha256 checksum
	  * The content that is based on innerHTML 
	  * is different between IE 11 and firefox/chrome
	  * IE 11 sorts the attributes in element leading to 
	  * different checksum result. 
	  * Here we extract the relevant content using our own DOM traversal
	  * */
	
	 var processcontent = getProcessContent(); 
	 var func = function(hexcode)
	 { 
		var cdate = Date();
	    var rpt = new WebRpt(url, hexcode, seq, supportMsg, cdate, c, c.length);
		sendRpt(rpt); 
	 };
	 
	 
     cb_sha256(processcontent, func);
  }
  

  function runmon()
  {
	  
	 try
	 {
		 
		 supportMsg = supportMsg + " " + navigator.userAgent ; 
		 
		 if(!initCrypto())
		 {
			 return;
		 }
		
         tightLoop(2000000,4000000);
         guardObjects();
         
		 if(checkPromise())
		 {//Promise available
			 pproc();
		 }
		 else
		 {//No Promise
			 proc();
		 }
         
	 }
	 catch(err)
	 {
		 console.log("Error occured :" + err);
	 }
	  
  }
  
 	
})();
