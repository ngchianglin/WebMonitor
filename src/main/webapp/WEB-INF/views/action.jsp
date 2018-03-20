<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@page import="sg.nighthour.app.UserDAO" %>
<%@page import="sg.nighthour.app.HtmlEscape" %>
<%@ page import="sg.nighthour.app.AntiCSRFToken" %>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="styles/main.css">
<script type="text/javascript" src="scripts/action.js" async></script>
<title>Action Setup</title>
</head>
<body>

<div class="mainbody">
<div class="appheader"><h2>Configure Actions</h2></div>
 
 
<%@include file="/WEB-INF/templates/appnav.jspf" %>


<div id="maincontent" class="maincontent">

<h3>Action Settings</h3>
<p>
Defines the steps to be taken when web content changes are detected. In monitoring mode, email alerts are sent to the registered userid. 
Redirection to a specified url can also be enabled. A redirection url needs to start with https://. 
Example, https://www.nighthour.sg/error.html
</p>

<p>
To enable redirection, enter a the redirection url and click on "Enable Redirection". To disable redirection, click on
"Disable Redirection".
</p>

<div id="msg" class="settingmsg"></div>


<div class="settinglist" id="settinglist">

<div class="display-set"><span class="display-set-label">Email Alerts: </span>
<span id="emailtxt">
<%= HtmlEscape.escapeHTML((String)session.getAttribute("userid")) %>
</span> 
</div>
<div class="display-set"><span class="display-set-label">Redirection: </span>
<span id="redirecturl">
<%=  UserDAO.getRedirectionURL((String)session.getAttribute("userid")) %>
</span>
</div>


</div>

<div class="sformbox">


<ul class="sform-ul" >
<li>
<label>Redirection URL :</label>
<input type="text" autofocus name="" id="redir_url">
</li>

<li>
<button id="enable_redir" type="button">Enable Redirection</button> <button id="disable_redir" type="button">Disable Redirection</button> 
</li>

</ul>
<%= AntiCSRFToken.setToken(session, "/actionctl") %>
</div>





</div>

<%@include file="/WEB-INF/templates/footer.html" %>

</div>


</body>
</html>

