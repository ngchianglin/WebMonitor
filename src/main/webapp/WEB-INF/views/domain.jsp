<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="sg.nighthour.app.DomainDAO" %>
<%@ page import="sg.nighthour.app.AntiCSRFToken" %>




<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="styles/main.css">
<script type="text/javascript" src="scripts/domain.js" async></script>
<title>Domain Setup</title>
</head>
<body>

<div class="mainbody">
<div class="appheader"><h2>Domain Setup</h2></div>
 
<%@include file="/WEB-INF/templates/appnav.jspf" %>


<div id="maincontent" class="maincontent">

<h3>Current Domains</h3>
<p>Valid domain names need to be in alphanumeric and each part separated by dots has to be at least 2 characters.<br>  
Examples. nighthour.sg, mywebsite.com.sg
</p>
<div id="msg" class="settingmsg"></div>
<div class="settinglist" id="domainlist">

<%=   DomainDAO.getCurrentDomains((String)session.getAttribute("userid")) %>

</div>



<div class="sformbox">


<ul class="sform-ul" >
<li>
<label>Enter Domain :</label>
<input type="text" autofocus name="domain" id="domain">
</li>

<li>
<button id="add" type="button">Add Domain</button> <button id="delete" type="button">Delete Domain</button> 
</li>

</ul>

<%= AntiCSRFToken.setToken(session, "/domainctl") %>

</div>



</div>

<%@include file="/WEB-INF/templates/footer.html" %>


</div>




</body>
</html>

