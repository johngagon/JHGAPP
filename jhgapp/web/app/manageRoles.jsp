<%@ page import="jhg.*,java.util.*,jhg.appman.*,jhg.appman.control.*,java.util.*" %>
<%

final String MESSAGES = "MESSAGES";

final String APPLICATION_MANAGER = "APPLICATION_MANAGER";
String name = "";
String commandOut = "";

Screen currentScreen = (Screen)session.getAttribute(ApplicationManagerController.APPMAN_CURRSCREEN);
List<Role> roleList = (List<Role>)currentScreen.getValue(Screen.AVAILABLE_ROLES);
 

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
<h1 align="center">Manage Roles</h1>
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
			<a href="adminHome.jsp">Admin Home</a> | 
			<a href="<%=ApplicationManagerController.getLink(Screen.Button.GOCREATEROLE)%>"><%=Screen.Button.GOCREATEROLE.getLabel() %></a>
		</td>
	</tr>
</table>
<br/>
<hr/>
<br/>
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr>
		<th>ID</th><th>Role</th>
	</tr>

<%
if(roleList.size()<1){
%>
	<tr><td colspan="2">NO ROLES TO DISPLAY</td></tr>
<%
}else{
%>
<%	
	for(Role r:roleList){
		Integer roleId = r.getId();
		String roleName = r.getName();
%>		

	<tr>
	<td><%=roleId%></td>
	<td><a href="<%=ApplicationManagerController.getLink(Screen.Button.VIEWROLE,roleId.toString())%>"><%=roleName%></a></td>
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

