<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="styles/main.css">
<title>Main Application Page</title>
</head>
<body>

<div class="mainbody">

<div class="appheader">
<h2>Main Application Page</h2>
</div>


<%@include file="/WEB-INF/templates/appnav.jspf" %>


<div id="maincontent" class="maincontent">

<h3>Getting Started</h3>

<p>
This is a simple application that detect changes in the content of monitored websites using client side javascript. 
The application uses client side javascript that is embedded or injected into a webpage to check the content when a visitor 
loads the page. If changes are detected, an email alert can be sent to the site administrator and 
the visitor redirected to an error page. 

The client side javascript can be included directly in a webpage or injected through a reverse proxy like Nginx. 
No changes to the coding of the monitored webpages are required if a reverse proxy is used to add the client side javascript. 
</p>


<p>
The application has the following sections that can be accessed through the top navigation bar. 
<table class="mtable">
<tr><th>Section</th><th>Description</th> </tr>
<tr><td>Domain</td><td>Specifies the domain that is being monitored. Up to 10 domains can be added per user.</td></tr>
<tr><td>Monitored URLs</td><td>Shows the list of urls/webpages that are being monitored for changes.</td></tr>
<tr><td>Action</td><td>The action to take when changes are detected. By default an email alert will be sent to the user. A redirection url
can be added to redirect visitors when changes are detected.</td></tr>
<tr><td>Mode</td><td>Sets the application to disabled, monitoring or capturing mode. Capturing mode allows the application to
populate its database with the content of monitored urls/pages during initial setup or during updates. In monitor mode, 
the application sends email alerts and redirect visitors. Disable mode is the default mode where the application is in a neutral state
that does nothing.</td></tr>
</table>
</p>



</div>

<%@include file="/WEB-INF/templates/footer.html" %>

</div>




</body>
</html>