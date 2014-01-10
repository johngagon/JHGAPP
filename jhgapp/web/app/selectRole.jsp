<%@ page import="jhg.*,java.util.*,jhg.appman.ApplicationManager,jhg.appman.control.*,jhg.appman.run.*" %>
<%

final String MESSAGES = "MESSAGES";
final String APPLICATION_MANAGER = "APPLICATION_MANAGER";
final String APPLICATION_ENGINE = "APPLICATION_ENGINE";
final String CURRENT_SCREEN = ApplicationController.CURRENT_SCREEN;//TODO fix

String name = "";
String commandOut = "";
 

Messages messages = new Messages();
ApplicationManager manager = null;
ApplicationEngine engine = null;
Screen screen = null;
String title = "Not Set";
if(session.getAttribute(MESSAGES)!=null){
   messages = (Messages) session.getAttribute(MESSAGES);
   commandOut = messages.htmlOut();
}

if(application.getAttribute(APPLICATION_MANAGER)!=null){
	manager = (ApplicationManager) application.getAttribute(APPLICATION_MANAGER);
	name = manager.getApplicationName();
}
if(application.getAttribute(APPLICATION_ENGINE)!=null){
	engine = (ApplicationEngine) application.getAttribute(APPLICATION_ENGINE);
}
if(session.getAttribute(CURRENT_SCREEN)!=null){
	System.out.println("have screen.");
	screen = (Screen) session.getAttribute(CURRENT_SCREEN);
	title = screen.getHere().getTitle();
}else{
	System.out.println("no screen.");
}

List<Role> roleList = new ArrayList<Role>();
if(screen!=null){
	roleList = (List<Role>)screen.getValue(Screen.USER_ROLES);
}
%>

<html>
<head>
<title><%=title%></title>
<style>
table
{
	border:3px solid black;
	border-collapse:collapse;
}
</style>

</head>
<body>
<h1 align="center"><%=title%></h1>
<hr/>

<table border="1" width="800" align="center"><tr><td>
<br/>
<%=commandOut%>
<br/>
<a href="/jhgapp/man?command=clear">Clear</a>
</td></tr></table>

<br/>


<form action="<%=ApplicationController.getFormAction()%>" name="jhgappSelectRole" method="POST"><!-- /jhgapp/app -->
<table width="800" align="center"  cellpadding="10" cellspacing="10">
	<tr><td colspan="2">Select the role you will use during this session.</td></tr>
	<tr><td align="right"></td>
		<td>

			<select name="<%=ApplicationEngine.ID%>">
			    <option value="">Select a Role</option>
<%
for(Role role:roleList){ 
%>
				<option value="<%=role.getId()%>"><%=role.getName()%></option>
<%
}
%>
	    	</select>
	  	</td>
	</tr>
	<tr><td colspan="2"><input type="submit" name="command" value="<%=Screen.Button.SELECTROLE.name()%>"/></td></tr>
</table>

<br/>
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr>
		<td>
			<a href="<%=ApplicationController.getLink(Screen.Button.LOGOUT)%>"><%=Screen.Button.LOGOUT.getLabel()%></a> 
		</td>
	</tr>
</table>


</form>
</body>
</html>

