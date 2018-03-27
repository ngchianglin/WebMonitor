<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@page import="sg.nighthour.app.UrlDAO" %>    
<%@ page import="sg.nighthour.app.AntiCSRFToken" %>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="styles/main.css">
<script type="text/javascript" src="scripts/url.js" async></script>
<title>Monitored WebPages</title>
</head>
<body>

<div class="mainbody">
<div class="appheader"><h2>Monitored WebPages</h2></div>
 
 
<%@include file="/WEB-INF/templates/appnav.jspf" %>

<div id="maincontent" class="maincontent">

<h3>Monitored WebPages</h3>
<p>
The list of webpages/urls that are monitored. Each domain can have up to 300 monitored webpages/urls. 
</p>
<div id="msg" class="settingmsg"></div>


<div class="settinglist" id="settinglist">

<div class="display-set">
<span class="display-set-label">Select Domain </span>

<%=  UrlDAO.getDomainOptions((String)session.getAttribute("userid")) %>

</div>

<div class="display-set">
<span class="display-set-label">Search WebPage/URL</span>
<input type="text" name="url" id="urlsearch"  placeholder="search text">
<button id="searchbtn" type="button">Submit</button> 
</div>

<div class="display-set">
<button id="reloadbtn" type="button">Reload</button> 
</div>

<%= AntiCSRFToken.setToken(session, "/urlctl") %>

</div>



<div class="urlbox" id="urlbox">




</div>





</div>

<%@include file="/WEB-INF/templates/footer.html" %>

</div>


</body>
</html>


