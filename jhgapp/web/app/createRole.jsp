<%@ page import="jhg.*,java.util.*,jhg.appman.*,jhg.appman.control.*,java.util.*" %>
<%

final String MESSAGES = "MESSAGES";

final String APPLICATION_MANAGER = "APPLICATION_MANAGER";
String name = "";
String commandOut = "";

Screen currentScreen = (Screen)session.getAttribute(ApplicationManagerController.CURRENT_SCREEN);

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
			<a "<%=ApplicationManagerController.getLink(Screen.Button.GOHOME)%>"><%=Screen.Button.GOHOME.getLabel() %></a> | 
			<a href="<%=ApplicationManagerController.getLink(Screen.Button.BACK)%>"><%=Screen.Button.BACK.getLabel() %></a>
		</td>
	</tr>
</table>
<br/>
<hr/>
<br/>
<form action="<%=ApplicationManagerController.getLink()%>">
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr><td colspan="2">Enter a role name</td></tr>
	<tr><td align="right">Role Name:</td><td><input type="text" name="<%=Role.ROLEFLD%>"/></td></tr>
	<tr><td colspan="2"><input type="submit" name="command" value="<%=Screen.Button.CREATEROLE.name()%>"/></td></tr>
</table>
</form>
</body>
</html>

