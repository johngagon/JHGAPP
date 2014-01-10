<%
final String CURRENT_SCREENF = "CURRENT_SCREEN";//TODO fix
boolean haveScreenF = session.getAttribute(CURRENT_SCREENF)!=null; 
if(haveScreenF){
%>
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