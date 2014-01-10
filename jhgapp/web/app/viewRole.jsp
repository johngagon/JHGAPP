<%@ page import="jhg.*,java.util.*,jhg.appman.*,jhg.appman.control.*,java.util.*,jhg.model.*" %>

<!-- 

grant privilege
ungrant privilege

 -->
<%

final String MESSAGES = "MESSAGES";

final String APPLICATION_MANAGER = "APPLICATION_MANAGER";
String name = "";
String commandOut = "";

Screen currentScreen = (Screen)session.getAttribute(ApplicationManagerController.APPMAN_CURRSCREEN);

Role role = (Role)currentScreen.getValue(Screen.VIEW_ROLE);
Map<Manager,Privilege> privs = (Map<Manager,Privilege>)role.getPrivileges();
List<Manager> availableEntities = (List<Manager>)currentScreen.getValue(Screen.AVAILABLE_ENTITIES);
Privilege[] availablePrivs = (Privilege[])currentScreen.getValue(Screen.AVAILABLE_PRIVILEGES);

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

<form action="<%=ApplicationManagerController.getLink()%>">
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr><td colspan="3">Viewing Role</td></tr>
	<tr><td align="right"><b>ID:</b></td><td><%=role.getId()%></td><td>&nbsp;</td></tr>
	<tr><td align="right"><b>Role Name:</b></td><td><%=role.getName()%></td><td>&nbsp;</td></tr>
	<tr><td align="right" colspan="3"><b>Privileges Granted</b></td></tr>
<%
for( Manager _man:privs.keySet()){
	Privilege _priv = privs.get(_man);
%>
	<tr>
		<td><%=_man.getName()%></td>
		<td><%=_priv.name()%></td>
		<td>
		<a href="<%=ApplicationManagerController.getLink(Screen.Button.UNGRANTPRIV,role.getId().toString(),_man.getManagerId().toString(),String.valueOf(_priv.ordinal()) )%>">
		<%=Screen.Button.UNGRANTPRIV.getLabel() %></a>
		</td>
	</tr>
<%
}
%>

	<tr><td align="right" colspan="3"><b>Grant Privilege to Role</b></td></tr>
	<tr>
		<td><input type="hidden" name="<%=ApplicationManager.ID1%>" value="<%=role.getId()%>"/>
			<select name="<%=ApplicationManager.ID2%>">
<%
for(Manager _man:availableEntities){
%>
				<option value="<%=_man.getManagerId() %>"><%=_man.getName() %></option>
<%
}//end for(Manager _man:availableEntities)
%>	
			</select>
		</td>
		
		<td>
			<select name="<%=ApplicationManager.ID3%>">
<%
for(Privilege _priv:availablePrivs){
%>
				<option value="<%=_priv.ordinal() %>"><%=_priv.name() %></option>
<%
}//end for(Manager _man:availableEntities)
%>	
			</select>
		</td>	
		
		<td>
			<input type="submit" name="command" value="<%=Screen.Button.GRANTPRIV.name()%>"/>					
		</td>
		
	</tr>
</table>
</form>

</body>
</html>

 
