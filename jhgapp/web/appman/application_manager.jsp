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
</head>

<body>
<h1 align="center">Application Manager <%=name %></h1><br/>

<hr/>

<table border="1" width="50%" align="center"><tr><td>
<br/>
<%=commandOut%>
<br/>
</td></tr></table>

<hr/>

<table border="1" width="50%" align="center"><tr><td>
<form name="command" action="/jhgapp/man" method="post">
	Command:<input type="text" size="150" name="command"/><br/>
	<input type="submit" name="action" value="Send"/>
</form>
</td></tr></table>

</body>
</html>