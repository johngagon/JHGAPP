<%@ page import="jhg.*,jhg.model.*,jhg.model.field.*,java.util.*,jhg.appman.ApplicationManager,jhg.appman.control.*,jhg.appman.run.*,jhg.appman.run.Screen.Button" %>
<%@ include file="inclHead.jsp" %>

<%
Manager man = null;
Model model = null;
List<Button> availableButtons = new ArrayList<Button>();
if(haveScreen){
	man = (Manager)screen.getValue(Screen.MANAGER);
	model = (Model)screen.getValue(Screen.MODEL);
	availableButtons = (List<Button>)screen.getValue(Screen.AVAILABLE_BUTTONS);
}
boolean haveManager = manager!=null;

if(haveManager){

	List<Field> fieldList = man.getFields();
	
	
%>

<hr/>
<br/>
<form action="<%=ApplicationController.getFormAction()%>">
<input type="hidden" name="<%=ApplicationEngine.ID%>" value="<%=model.getId()%>"/>
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr><td colspan="2"><%= man.getLabel() %>[<%=model.getId()%>]</td></tr>
	
<%
for(Field field:fieldList){
	Value value = model.getValue(field);
	System.out.println("Value class:"+value.getClass());
	System.out.println("value instanceof Text:"+(value instanceof Text));
	DisplayField df = DisplayField.make(field,value);
	//df.setReadonly();
%>	
	
	<tr>
	  <td align="right"><label for="<%=field.getName()%>"><%= field.getLabel() %></label></td>
	  <td> <%= df.render() %></td>
	</tr>
	
<%
}
%>	
	
	<tr><td colspan="2">
<%
for(Button button:availableButtons){

%>	
	 
	<input type="submit" name="command" value="<%=button.name()%>"/> &nbsp;&nbsp;
	
<% 
}	
%>	
	&nbsp;</td></tr>

</table>
</form>

<%
}else{
%>
<br/>
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr>
		<td>Error: Manager not found. Report this and go back for now.</td>
		</td>
	</tr>
</table>
<br/>		
<%
}
%>
<br/>
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr>
		<td>
			<a href="<%=ApplicationController.getLink(Screen.Button.LOGOUT)%>"><%=Screen.Button.LOGOUT.getLabel()%></a> 
		</td>
	</tr>
</table>

<%@ include file="inclFoot.jsp" %>