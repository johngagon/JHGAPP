<%@ page import="jhg.*,java.util.*,jhg.appman.*,jhg.appman.control.*,java.util.*" %>
<%

final String MESSAGES = "MESSAGES";

final String APPLICATION_MANAGER = "APPLICATION_MANAGER";
String name = "";
String commandOut = "";

Screen currentScreen = (Screen)session.getAttribute(ApplicationManagerController.APPMAN_CURRSCREEN);
List<User> userList = (List<User>)currentScreen.getValue(Screen.USERS_LIST);
 

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
<h1 align="center">Manage Users</h1>
<hr/>

<table border="1" width="50%" align="center"><tr><td>
<br/>
<%=commandOut%>
<br/>
</td></tr></table>

<hr/>



<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr>
		<td>
			<a href="<%=ApplicationManagerController.getLink(Screen.Button.GOHOME)%>"><%=Screen.Button.GOHOME.getLabel() %></a> | 
			<a href="<%=ApplicationManagerController.getLink(Screen.Button.GOCREATEUSER)%>"><%=Screen.Button.GOCREATEUSER.getLabel() %></a>
		</td>
	</tr>
</table>
<br/>
<hr/>
<br/>
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr>
		<th>ID</th><th>Username</th>
	</tr>

<%
if(userList.size()<1){
%>
	<tr><td colspan="2">NO USERS TO DISPLAY</td></tr>
<%
}else{
%>
<%	
	for(User u:userList){
		Integer userId = u.getId();
		String userName = u.getName();
%>		

	<tr>
	<td><%=userId%></td>
	<td><a href="<%=ApplicationManagerController.getLink(Screen.Button.GOVIEWUSER,userId.toString())%>"><%=userName%></a></td>
	</tr>
<%
	}
%>

<%
}
%>

	
</table>


</body>
</html>

