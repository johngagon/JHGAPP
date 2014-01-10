<%@ page import="java.util.*, jhg.account.*, jhg.*, jhg.appman.*, jhg.appman.control.*, jhg.appman.run.*, jhg.model.*" %>
<%--
probably should not use jhg.model instances too much. jhg.model.*,
--%>


<%!
//TODO all these need to have good error handling.
public static final String APPLICATION_ENGINE = "APPLICATION_ENGINE";
public static final String LOGGED_IN = "LOGGED_IN";
public static final String USER_SESSION = "USER_SESSION";


public final void pstartload(HttpServletRequest request, HttpSession session, HttpServletResponse response){
	plog("Loading!!!");
	ServletContext application = session.getServletContext();
	ApplicationEngine appengine = (ApplicationEngine)application.getAttribute(APPLICATION_ENGINE);

	/* */
	if(isLoggedIn(session)){
		logout(session,appengine);
	}
	/* */

	if(!isLoggedIn(session)){
		plog("Not logged in. Logging in.");
		loginAndSelectRole(session,appengine);
	}else{
		plog("Logged in.");
	}

}

public final Application getApplication(HttpSession session){
	ServletContext application = session.getServletContext();
	ApplicationEngine appengine = (ApplicationEngine)application.getAttribute(APPLICATION_ENGINE);
	Application accounting = appengine.getApplication();
	return accounting;
}

public final Balance getBalanceModel(HttpSession session){
	ServletContext application = session.getServletContext();
	ApplicationEngine appengine = (ApplicationEngine)application.getAttribute(APPLICATION_ENGINE);
	String entityName = Balance.BALANCE;//HARDCODE
	String balanceId = "2";//HARDCODE for the one desired. 1 for Ray, 2 for John
	UserSession usersession = (UserSession)session.getAttribute(USER_SESSION);
	ModelResult modelResult = appengine.viewModel(usersession,entityName,balanceId);
	Balance balanceModel = (Balance)modelResult.getModel();
	return balanceModel;
}

private final boolean isLoggedIn(HttpSession session){
	return session.getAttribute(LOGGED_IN)!=null && Boolean.TRUE.equals((Boolean)session.getAttribute(LOGGED_IN));
}

private final void loginAndSelectRole(HttpSession session, ApplicationEngine engine){
	UserSession usersession = UserSession.createInstance();
 	Result result = engine.login("rlavalley","password",usersession);//HARDCODE
 	if(result.isSuccessful()){
 		User user = (User)result.objectValue();
 		String msg = "Logged in user "+user.getName();
 		session.setAttribute(LOGGED_IN, Boolean.TRUE);
 		session.setAttribute(USER_SESSION, usersession);
 		plog("Login successful.");
 		//"cpa"
 		selectRole(usersession,engine);
	}else{
		plog("Login unsuccessful.");
	}
}

private final void selectRole(UserSession usersession, ApplicationEngine engine){
	String roleId = "1";//HARDCODE
	Result roleResult = engine.findRole(roleId);
	Role selectingRole = (Role)roleResult.objectValue();//depends on successful.
	engine.selectRole(selectingRole,usersession);
	plog("Selected role:"+selectingRole.getName());
}

private final void logout(HttpSession session, ApplicationEngine engine){
	engine.logout((UserSession)session.getAttribute(USER_SESSION));
	session.removeAttribute(LOGGED_IN);
	session.removeAttribute(USER_SESSION);
	plog("Logout successful.");
}



public final void plog(String msg){
	Date d = new Date();
	System.out.println("Java Page["+d+"]:'"+msg+"'");
}

public final void pendload(){
	plog("End loading!!!");
}


	/*
	login ray or john, get session and role (hard code here for now or grab parameter, redirect PRG)

	get the balances for particular owner. (see that code)
	get the last page of transactions sorted in reverse order then add some extra rows.

	pagination actions
	list accounts.
	enter transaction (s).
	go to import screen
	handle import actions.
	go to account list screen.
	go back to home screen.     (use various engine methods to do these or make calls to them or link out to parts of the admin prog)
	*/

%>
