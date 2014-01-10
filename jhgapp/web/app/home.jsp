<%@ page import="jhg.*,jhg.model.Manager,java.util.*,jhg.appman.ApplicationManager,jhg.appman.control.*,jhg.appman.run.*" %>
<%@ include file="inclHead.jsp" %>

<%
List<Manager> managers = new ArrayList<Manager>();
if(haveScreen){
	managers = (List<Manager>)screen.getValue(Screen.ENTITES);
}
%>

<table width="800" align="center" >
	<tr><th align="center">Manage</th></tr>
<%
for(Manager man:managers){
%>
<tr><td><a href="<%=ApplicationController.getLink(Screen.Button.LOOKUP,man.getManagerId())%>"><%=man.getLabel()%></a></td></tr>
	
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


<!-- 
<table width="800" align="center" cellpadding="10" cellspacing="10">
	<tr><td><b>Alerts</b></td></tr>
	<tr><td><font color="red">Warning! It has been over 10 days since you last made an entry.</font>
	 <input type="button" value="Dismiss"/></td></tr>
</table>

 
<table width="800" align="center" >
	<tr><td><a href="balances.jsp">Balances</a></td></tr>
	<tr><td><a href="accounts.jsp">Accounts</a></td></tr>
	<tr><td><a href="journal.jsp">This Month Journal</a></td></tr>
	<tr><td><a href="pastJournals.jsp">Past Months Journals</a></td></tr>
	<tr><td><a href="login.jsp">Logout</a></td></tr>
</table>
 -->


