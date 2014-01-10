<%@ page import="jhg.*,jhg.model.*,java.util.*,jhg.appman.ApplicationManager,jhg.appman.control.*,jhg.appman.run.*" %>
<%@ include file="inclHead.jsp" %>

<%
List<Model> models = null;
Manager modelManager = (Manager)screen.getValue(Screen.MANAGER);
if(haveScreen){
	models = (List<Model>)screen.getValue(Screen.MODEL_LIST);
}
%>

<table width="800" align="center" >
	<tr><th align="center">List <%=modelManager.getLabel()%></th></tr>
	<tr><td><a href="
<%=ApplicationController.getLink(Screen.Button.GOCREATE,modelManager.getManagerId())%>
		">
		<%=Screen.Button.GOCREATE.getLabel() %>
		</a></td></tr>
	<tr><td>&nbsp;</td></tr>
<%
if(models.size()<1){
%>
	<tr><td>NO MODELS TO DISPLAY</td></tr>
<%
}else{
%>	
	
	<%
	for(Model model:models){
	%>
	<tr><td><a href="<%=ApplicationController.getLink(Screen.Button.VIEW,model.getIdString())%>"><%=model.getIdentifyingValue()%></a></td></tr>
	<%
	}
	%>

<%
}
%>
</table>
<br/>
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr>
		<td>
			<a href="<%=ApplicationController.getLink(Screen.Button.LOGOUT)%>"><%=Screen.Button.LOGOUT.getLabel()%></a> 
		</td>
	</tr>
</table>
<%@ include file="inclFoot.jsp" %>



