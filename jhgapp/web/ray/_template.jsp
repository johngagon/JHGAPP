<%@ page import="jhg.*,java.util.*,jhg.appman.ApplicationManager,jhg.appman.control.*,jhg.appman.run.*" %>

<%!
public final void plog(HttpServletRequest request, String msg){
	String page = request.getPathTranslated();
	System.out.println("Page:"+page+"{"+msg+"}");
}

%>

<%

final String MESSAGES = "MESSAGES";
final String APPLICATION_MANAGER = "APPLICATION_MANAGER";
final String APPLICATION_ENGINE = "APPLICATION_ENGINE";
final String CURRENT_SCREEN = "CURRENT_SCREEN";//TODO fix

String name = "";
String commandOut = "";
 

Messages messages = new Messages();
ApplicationManager manager = null;
ApplicationEngine engine = null;
Screen screen = null;
String title = "Not Set";

//APPLICATION
if(application.getAttribute(APPLICATION_MANAGER)!=null){
	manager = (ApplicationManager) application.getAttribute(APPLICATION_MANAGER);
	name = manager.getApplicationName();
}else{
	plog(request,"application manager not found.");
}
if(application.getAttribute(APPLICATION_ENGINE)!=null){
	engine = (ApplicationEngine) application.getAttribute(APPLICATION_ENGINE);
}else{
	plog(request,"application engine not found.");
}

//SESSION
if(session.getAttribute(MESSAGES)!=null){
   messages = (Messages) session.getAttribute(MESSAGES);
   commandOut = messages.htmlOut();
}else{
	plog(request,"messages not found.");
}
boolean haveScreen = session.getAttribute(CURRENT_SCREEN)!=null; 
if(haveScreen){
	screen = (Screen) session.getAttribute(CURRENT_SCREEN);
	title = screen.getHere().getTitle();
}else{
	plog(request,"screen instance not found.");
}

if(haveScreen){
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
<p align="center">Under construction!</p>

<br/>
</body>
</html>
<% 
}else{

%>
<html><body>Screen not set.</body></html>
<%
}
%>