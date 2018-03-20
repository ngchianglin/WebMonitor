<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@page import="sg.nighthour.app.UserDAO" %>
<%@ page import="sg.nighthour.app.AntiCSRFToken" %>
    

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="styles/main.css">
<script type="text/javascript" src="scripts/mode.js" async></script>
<title>Action Setup</title>
</head>
<body>

<div class="mainbody">
<div class="appheader"><h2>Configure Mode</h2></div>
 
 
<%@include file="/WEB-INF/templates/appnav.jspf" %>

<div id="maincontent" class="maincontent">

<h3>Mode Settings</h3>
<p>
Set the current mode of the application. Capture mode enables the application to capture and populate the local datastore
with the webpages/urls to be monitored. Monitor mode starts monitoring the captured pages/urls. Email alerts will be sent and redirection of clients can be enabled.
The default mode of Disable is a neutral state where both capturing and monitoring are not active.   
</p>
<div id="msg" class="settingmsg"></div>


<div class="settinglist" id="settinglist">

<div class="display-set">
<span class="display-set-label">Current: </span>
<span id="modetxt">
<%= UserDAO.getCurrentMode((String)session.getAttribute("userid")) %>
</span> 
</div>

</div>

<div class="sformbox">


<ul class="sform-ul" >
<li>
<button id="disable" type="button">Disable Mode</button> <button id="capture" type="button">Capture Mode</button> 
<button id="monitor" type="button">Monitor Mode</button> 
</li>

</ul>


<%= AntiCSRFToken.setToken(session, "/modectl") %>

</div>





</div>

<%@include file="/WEB-INF/templates/footer.html" %>

</div>


</body>
</html>

