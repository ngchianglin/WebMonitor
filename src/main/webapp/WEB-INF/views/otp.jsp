<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="sg.nighthour.app.OTPHandler" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="styles/main.css">
<title>2 Factor Authentication Page</title>
</head>
<body>



<div class="webformdiv">

<div class="formheader">
<h3>2 Factor Authentication</h3>
</div>


<form method="POST" autocomplete="off" accept-charset="utf-8" action="/otpctl">
<ul class="form-ul" >

<li>
<label>Enter OTP</label>
</li>

<li>
<div id="msg" class="settingmsg">

<%=   OTPHandler.getOTPErrorMessage(session) %>

</div>
<input type="text" required name="totp" size="25" >
</li>
<li>
<input type="submit" value="Submit" >
</li>

</ul>
</form>

</div>

<%@include file="/WEB-INF/templates/footer.html" %>


</body>
</html>