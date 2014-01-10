<%@ include file="java.jsp" %>
<%
pstartload(request,session,response);
Balance balance = getBalanceModel(session);
Accounting accounting = (Accounting)getApplication(session);
Manager accountTypeManager = accounting.getManager(Accounting.ACCOUNT_TYPE);
AccountType asset = (AccountType)accountTypeManager.lookup(AccountType.BK,new String[]{"Asset"}).getModel();
AccountType liability = (AccountType)accountTypeManager.lookup(AccountType.BK,new String[]{"Liability"}).getModel();
AccountType capital = (AccountType)accountTypeManager.lookup(AccountType.BK,new String[]{"Worth"}).getModel();



//FINISHED SECTION
String username = balance.getValue(Balance.OWNER).format();//"RAY L. LAVALLEY";
String assetTotal = accounting.getAccountTotal(balance.getId(), asset);//"$ 10,000.00";
String liabilityTotal  = accounting.getAccountTotal(balance.getId(), liability);//"$  8,500.00";
String capitalTotal = accounting.getAccountTotal(balance.getId(), capital);//"$  1,500.00";

//TESTING SECTION
int pageSize = 10;
/*
Integer balanceId, int pageSize
PageResult<Entry> entryPages = accounting.getEntries(balanceId, pageSize);
entryPages.last()
List<Entry> lastPageEntries = entryPages.getPage();  entryPages.from() entryPages.to() 
  ....see other methods for PageResult.
  ...figure out list size, subtract from the pagesize and see how many blank rows to make
  
	
}
engine open Model 
   locks  for editing (ensure it's temporary - navigating away unlocks, session timeout unlocks
   the model has a natural timeout of 30 minutes hard coded (not configured) in.


 
		//by default, get the last page of entries only. 
		//sorted by date filtered by balance
		//RESUME implement here.
*/

//SOFTCODE SECTION


%>

<%
String title = "Bank Accounts";
String tablewidth="950";
String optionInstr = "--pick--";

%>

<html>
<head>
<title><%=title%></title>
<link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
<h1>&nbsp;<%=title%></h1>
<hr/>
<table border="1" width="<%=tablewidth%>">
	<tr><td align="right"><a href="main.jsp" style="color:red;">Home</a> <a href="#">Import</a> <a href="#">Accounts</a> <a href="#">Options</a> <a href="#">Admin</a> </td></tr>
</table><br/>
<!--TODO: Pie Chart or bars with red and green to show percentage. -->
<table border="1" width="<%=tablewidth%>">
	<tr><td>
	<table align="center" width="100%">
	<tr><th class="layout" align="center" colspan="5">SUMMARY FOR:   <%=username %></th></tr>
	<tr><td class="layout" colspan="5">&nbsp;</td></tr>
	<tr><td class="layout" colspan="5">&nbsp;</td></tr>
	<tr><td class="layout">&nbsp;</td>
		<td class="layout">Assets:</td><td class="layout"><%=assetTotal%></td>
		<td class="layout">Liability:</td><td class="layout"><%=liabilityTotal%></td></tr>
	<tr><td class="layout">&nbsp;</td>
		<td class="layout">&nbsp;</td><td class="layout">&nbsp;</td>
		<td class="layout">Net Worth:</td><td class="layout"><%=capitalTotal%></td></tr>
	</table>

	</td></tr>
</table><br/>
<!--
	<pre>

	                              SUMMARY FOR:   $$username

	Assets :$ 10,000.00  Net    :$  8,500.00  Liab :$  1,500.00   85% [See Balance Sheet]
	Income :$  1,150.00  Remain :$    500.00  Save :$    150.00   50% [See Budget]
	Savings:$  2,500.00  Period :$    150.00                     GOOD [See Savings Plan]
	</pre>

-->

<form method="post" action="main.jsp">
<table width="<%=tablewidth%>">
	<tr>
		<th colspan="8">ENTER RECEIPTS</th>
	</tr><!-- # Date Desc DocNo Amt DR CR Post-->
	<tr class="labelrow"><td width="20">#</td>
		<td>Date</td><td>Description</td><td>Doc#</td>
		<td>Amt($) <small><i>999999.99</i></small></td>
		<td>DR</td><td>CR</td><td>Posted</td>
	</tr>
	<!--TODO: show partly filled page and page controls-->

	<tr><td width="20"><%=1%></td>   <!--1 #-->

		<td><input type="datetime-local"
			readonly="true"
			value="2013-05-31T17:30"
			required="true"/></td><!--2 date-->

		<td><input type="text" size="20" maxlength="20"
			readonly="true"
			value="Giant - Groceries"
			required="true"/></td><!--3 desc-->

		<td><input type="text" size="5" maxlength="15"
			readonly="true"
			value="385323"
			/></td><!--4 doc-->

		<td><input class="currency" type="number" pattern="(\d{6})([\.])(\d{2})"
			id="amount1" name="amount1"
		    min="0" max="999999.99" step="0.01"
			size="9" maxlength="9" align="right"
			readonly="true"
			value="129.93"
			required="true" />
			</td><!--5 amt-->

		<td><select disabled><option><%=optionInstr%></option>
		    <option selected="true">Food</option>
			<option>Gas</option>
			<option>Bus</option>
			<option>Rent</option>
			</select></td><!-- 6 DR -->

		<td><select disabled><option><%=optionInstr%></option>
			<option selected="true">Checking ABC</option>
			<option>Checking XYZ</option>
		</select></td><!-- 7 CR -->

		<td><input type="checkbox" checked="true"
			disabled="true" /></td><!-- 8 posted -->

	</tr>
	<tr><td width="20"><%=2%></td>   <!--1 #-->

		<td><input type="datetime-local"
			value="2013-11-15T19:55"
			required="true"/></td><!--2 date-->

		<td><input type="text" size="20" maxlength="20"
			required="true"
			value="Shell Dundalk - gas only"/></td><!--3 desc-->

		<td><input type="text" size="5" maxlength="15"
			value="R3252" /></td><!--4 doc-->

		<td><input class="currency" type="number" pattern="(\d{6})([\.])(\d{2})"
		    id="amount2" name="amount2"
		    min="0" max="999999.99" step="0.01"
			size="9" maxlength="9"
			value="39.48" required="true"
			/></td><!--5 amt-->

		<td><select ><option><%=optionInstr%></option>
		    <option>Food</option>
			<option selected="true">Gas</option>
			<option>Bus</option>
			<option>Rent</option>
			</select></td><!-- 6 DR -->

		<td><select ><option><%=optionInstr%></option>
			<option>Checking ABC</option>
			<option selected="true">Checking XYZ</option>
		</select></td><!-- 7 CR -->

		<td><input type="checkbox" readonly="true" /></td><!-- 8 posted -->

	</tr>

<%
for(int i=3;i<=10;i++){
%>
	<tr><td width="20"><%=i%></td>   <!--1 #-->
		<td><input type="datetime-local" required="true"/></td><!--2 date-->
		<td><input type="text" size="20" maxlength="20"
			required="true"/></td><!--3 desc-->
		<td><input type="text" size="5" maxlength="15"
			required="true"
			/></td><!--4 doc-->
		<td><input type="number" pattern="(\d{6})([\.])(\d{2})"
		    id="amount<%=i%>" name="amount<%=i%>"
		    min="0" max="999999.99" step="0.01"
			size="9" maxlength="9"
			required="true"
			align="right"/></td><!--5 amt-->
		<td><select required="true"><option><%=optionInstr%></option>
		    <option>Food</option>
			<option>Gas</option>
			<option>Bus</option>
			<option>Rent</option>
			</select></td><!-- 6 DR -->
		<td><select required="true"><option><%=optionInstr%></option>
			<option>Checking ABC</option>
			<option>Checking XYZ</option>
		</select></td><!-- 7 CR -->
		<td><input type="checkbox" /></td><!-- 8 posted -->
	</tr>
<%
}
%>
<tr class="labelrow"><td colspan="8"><input type="submit" name="command"
	value="Save"/></td></tr>
<tr><th colspan="8">&nbsp;</th></tr>
</table>
</form>
<br/>

<table border="1" width="<%=tablewidth%>">
	<tr><td>
	Messages <br/>
	&nbsp;
	</td></tr>
</table><br/>
<hr/>
</body>
</html>

<%
pendload();
%>

<%--
//-----------------------------------------------------------------------------
//optional protection instead of autologin.

automatic login

if(not logged in){
  login ray
  logged in
else{

  show home.

  TOOLTIP/RESULT AREA/MESSAGES

  WHAT: Rays checkbook.

  SUMMARY DATA
  Savings (which account) total, Savings this month.
  Assets: (total worth). Liability, Worth,           % Worth     (click balance) (time warp function)
  Spent/Remaining (from income) &                    % Remaining (click budget)

  MAIN FUNCTION
  Receipt Entry (most common feature is home)

  LINK BAR TO LESS USED FUNCTIONS
   Import and Reconcile Bank,
   Accounts
   Past Cycles  (total income, total spent), balance at that time?
   Export/Report/Archive etc.

   (lockup)

  3. View Remaining Budget


  show the admin login.
  a configuration (for protecting the accounting)



}



--%>

<!--
A floating-point number consists of the following parts, in exactly the following order:
Optionally, the first character may be a "-" character.
One or more characters in the range "0-9".
Optionally, the following parts, in exactly the following order:
a "." character
one or more characters in the range "0-9"
Optionally, the following parts, in exactly the following order:
a "e" character or "E" character
optionally, a "-" character or "+" character
One or more characters in the range "0-9".
-->