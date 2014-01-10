<!--http://localhost:8080/jhgapp/xxx/login.jsp  NOTE: TODO not secure-->
<%@ page import="jhg.*,java.util.*,jhg.appman.*" %>
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
<h1 align="center">User Login</h1>
<hr/>

<table border="1" width="800" align="center"><tr><td>
<br/>
<%=commandOut%>
<br/>
<a href="/jhgapp/app?command=clear">Clear</a>
</td></tr></table>


<hr/>
<form action="/jhgapp/app" name="jhgappUserLogin" method="POST">
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr><td colspan="2">Enter your user name and password below.</td></tr>
	<tr><td align="right">User name:</td><td><input type="text" name="<%=User.USERFLD%>"/></td></tr>
	<tr><td align="right">Password:</td><td><input type="password" name="<%=User.PASSFLD%>"/></td></tr>
	<tr><td colspan="2"><input type="submit" name="command" value="Login"/></td></tr>
</table>
</form>
<hr/>
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr>
		<td>
			<a href="forgotUsernameOrPassword.jsp">Forgot Username/Password?</a> |
			<a href="/jhgapp/app/index.jsp">Splash</a>
		</td>
	</tr>
</table>

</body>
</html>



