<!--http://localhost:8080/jhgapp/app/login.jsp  NOTE: TODO not secure, create an index -->
<%@ page import="jhg.Messages,java.util.*,jhg.appman.*,jhg.appman.control.*" %>
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
<h1 align="center">Admin Home</h1>
<hr/>

<table border="1" width="800" align="center"><tr><td>
<br/>
<%=commandOut%>
<br/>
<a href="/jhgapp/man?command=clear">Clear</a>
</td></tr></table>


<hr/>
<table width="800" align="center" >
	<tr><td><a href="<%=ApplicationManagerController.getLink(Screen.Button.MANAGEUSERS)%>"><%=Screen.Button.MANAGEUSERS.getLabel()%></a></td></tr>
	<tr><td><a href="<%=ApplicationManagerController.getLink(Screen.Button.MANAGEROLES)%>"><%=Screen.Button.MANAGEROLES.getLabel()%></a></td></tr>
	<!-- maybe not needed 
	<tr><td><a href="<%=ApplicationManagerController.getLink(Screen.Button.MANAGEENTITIES)%>"><%=Screen.Button.MANAGEENTITIES.getLabel()%></a></td></tr>
	-->
	<tr><td><a href="<%=ApplicationManagerController.getLink(Screen.Button.LOGOUT)%>"><%=Screen.Button.LOGOUT.getLabel()%></a></td></tr>
	
	<!-- 
	<tr><td><a href="manageRoles.jsp">Manage Roles</a></td></tr>
	<tr><td><a href="manageEntities.jsp">Manage Entities</a></td></tr>
	<tr><td><a href="/jhgapp/man?command=logout">Logout</a></td></tr>
	 -->
</table>

<hr/>
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr>
		<td>
			<a href="/jhgapp/app/index.jsp">Splash</a> 
		</td>
	</tr>
</table>

</body>
</html>

