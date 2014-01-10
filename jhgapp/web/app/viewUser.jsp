
<!-- 

editUser (reset their password for them).

deleteUser - can only delete if does not own anything, cascade deletes owned roles.
	(not referenced anywhere). users probably can't be deleted, only inactivated.
	inactivated means not assigned any roles usually. (if no roles assigned, can't login?)
	//or the password set to something random.



assignRole
unassignRole

 -->
 <%@ page import="jhg.*,java.util.*,jhg.appman.*,jhg.appman.control.*,java.util.*" %>
<%

final String MESSAGES = "MESSAGES";

final String APPLICATION_MANAGER = "APPLICATION_MANAGER";
String name = "";
String commandOut = "";

Screen currentScreen = (Screen)session.getAttribute(ApplicationManagerController.APPMAN_CURRSCREEN);

User user = (User)currentScreen.getValue(Screen.VIEW_USER);
List<Role> roles = (List<Role>)currentScreen.getValue(Screen.USER_ROLES);
List<Role> notAssignedRoles = (List<Role>)currentScreen.getValue(Screen.AVAILABLE_ROLES);

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
<h1 align="center"><%=currentScreen.getHere().getTitle() %></h1>
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
		</td>
	</tr>
</table>
<br/>
<hr/>
<br/>

<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr><td colspan="2">Viewing User</td></tr>
	<tr><td align="right"><b>ID:</b></td><td><%=user.getId()%></td></tr>
	<tr><td align="right"><b>User name:</b></td><td><%=user.getName()%></td></tr>
	<tr><td align="right"><b>Password:</b></td><td><%=user.getMaskedPassword() %></td></tr>
	<tr><td align="right" colspan="2"><b>Roles</b></td></tr>
<%
for(Role role:roles){
%>
	<tr><td><a href="<%=ApplicationManagerController.getLink(Screen.Button.VIEWROLE,role.getId().toString())%>"><%=role.getName()%></a></td>
	<td>
		<a href="<%=ApplicationManagerController.getLink(Screen.Button.UNASSIGNROLE,user.getId().toString(),role.getId().toString())%>">
		<%=Screen.Button.UNASSIGNROLE.getLabel() %></a>
	</td></tr>
<%
}
%>

	<tr><td align="right" colspan="2"><b>Roles Not Assigned</b></td></tr>
<%
for(Role role:notAssignedRoles){
%>
	<tr><td><a href="<%=ApplicationManagerController.getLink(Screen.Button.VIEWROLE,role.getId().toString())%>"><%=role.getName()%></a></td>
	<td>
		<a href="<%=ApplicationManagerController.getLink(Screen.Button.ASSIGNROLE,user.getId().toString(),role.getId().toString())%>">
		<%=Screen.Button.ASSIGNROLE.getLabel() %></a>
	</td></tr>
<%
}
%>

	<tr><td colspan="2" align="center">
		<a href="<%=ApplicationManagerController.getLink(Screen.Button.GOEDITUSER,user.getId().toString())%>">
		<%=Screen.Button.GOEDITUSER.getLabel() %></a>
		<!-- 
		<a href="<%=ApplicationManagerController.getLink(Screen.Button.DELETEUSER,user.getId().toString())%>">
		<%=Screen.Button.DELETEUSER.getLabel() %></a>
 		-->
	</td></tr>	 
</table>


</body>
</html>

 