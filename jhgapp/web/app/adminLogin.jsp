

<!--http://localhost:8080/jhgapp/xxx/login.jsp  NOTE: TODO not secure-->
<%@ page import="jhg.Messages,java.util.*,jhg.appman.*" %>
<%

final String MESSAGES = "MESSAGES";
final String APPLICATION_MANAGER = "APPLICATION_MANAGER";
String name = "";
String commandOut = "";
 

Messages messages = new Messages();
ApplicationManager manager = null;

if(session.getAttribute(MESSAGES)!=null){
   messages = (Messages) session.getAttribute(MESSAGES);
   commandOut = messages.htmlOut();
}

if(application.getAttribute(APPLICATION_MANAGER)!=null){
	manager = (ApplicationManager) application.getAttribute(APPLICATION_MANAGER);
	name = manager.getApplicationName();
}

%>
<html>
<head>

<style>
table
{
	border:3px solid black;
	border-collapse:collapse;
}
</style>

</head>
<body>
<h1 align="center">Admin Login</h1>
<hr/>

<table border="1" width="800" align="center"><tr><td>
<br/>
<%=commandOut%>
<br/>
<a href="/jhgapp/man?command=clear">Clear</a>
</td></tr></table>



<hr/>
<form action="/jhgapp/man" name="jhgappAdminLogin" method="POST">
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr><td colspan="2">Enter your username and password below.</td></tr>
	<tr><td align="right">Admin Login:</td><td><input type="text" name="username"/></td></tr>
	<tr><td align="right">Password:</td><td><input type="password" name="password"/></td></tr>
	<tr><td colspan="2"><input type="submit" name="command" value="Login"/></td></tr>
</table>
</form>
<hr/>
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr>
		<td>
			<a href="index.jsp">Splash</a> 
		</td>
	</tr>
</table>

</body>
</html>

