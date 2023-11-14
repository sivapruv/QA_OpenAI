<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>File Upload result</title>
</head>
<style>
.button {
	background-color: #4CAF50;
	color: white;
	padding: 10px 20px;
	margin-top: 20px;
	border: none;
	border-radius: 4px;
	cursor: pointer;
}
</style>
<body>
	<div align="center">
		<h3>${message}</h3>
		<a href="download?filePath=${message2}">Download File</a>
	</div>
	<%
    String referrer = request.getHeader("referer");
%>

	<button class="button" onclick="goBack()">Go Back</button>

	<script>
function goBack() {
  <% if (referrer != null) { %>
    window.location.href = '<%= referrer %>';
	<% } else { %>
		alert('Sorry, we could not determine the previous page you were on.');
	<% } %>
		}
	</script>
	
</body>
</html>