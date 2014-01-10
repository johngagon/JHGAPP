<%@ page import="jhg.*,jhg.model.*,java.util.*,jhg.appman.ApplicationManager,jhg.appman.control.*,jhg.appman.run.*" %>
<%@ include file="inclHead.jsp" %>

<%
Manager man = null;
if(haveScreen){
	man = (Manager)screen.getValue(Screen.MANAGER);
}
boolean haveManager = manager!=null;

if(haveManager){

	List<Field> fieldList = man.getFields();
	
	
%>

<hr/>
<br/>
<form action="<%=ApplicationController.getFormAction()%>">

<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr><td colspan="2">Enter values for each field below:</td></tr>
	
<%
for(Field field:fieldList){
	DisplayField df = DisplayField.make(field);
	if(!field.isAggregateCalculated() && !field.isCalculated()){
%>	
	
	<tr>
	  <td align="right"><label for="<%=field.getName()%>"><%= field.getLabel() %></label></td>
	  <td> <%= df.render() %></td>
	</tr>
	
<%
	}else{
		//do not render		
	}
}
%>	
	
	<tr><td colspan="2"><input type="submit" name="command" value="<%=Screen.Button.CREATE.name()%>"/></td></tr>

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
