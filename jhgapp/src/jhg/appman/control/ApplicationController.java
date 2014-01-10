package jhg.appman.control;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jhg.Messages;
import jhg.Result;
import jhg.Role;
import jhg.User;
import jhg.appman.ApplicationManager;
import jhg.appman.Config;
import jhg.appman.Factory;
import jhg.appman.run.Screen;
import jhg.appman.run.UserSession;
import jhg.appman.SimpleApplicationManager;
import jhg.appman.run.Screen.Button;
import jhg.appman.run.Screen.Code;
import jhg.appman.run.ApplicationEngine;
import jhg.model.Application;

/**
 * DOC
 * @author John
 *
 */
public class ApplicationController  extends HttpServlet{

	public static final String APPLICATION_ENGINE = "APPLICATION_ENGINE";

	//SERIALIZATION
	private static final long serialVersionUID = -2095388236695380672L;

	//FOLDER PATHS
	public static final String BASE_FOLDER = "/app/";//for jsps
	public static final String APPFOLDER = "/jhgapp";

	//KEYS
	public static final String USER_SESSION = "USER_SESSION";
	public static final String LOGGED_IN = "LOGGED_IN";
	public static final String MESSAGES = "MESSAGES";
	public static final String CURRENT_SCREEN = "CURRENT_SCREEN";
	public static final String VERSION				= "0.0.1";


	private ApplicationManager manager;
	private ApplicationEngine  engine;
	private Application app;

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		Screen.Code.AUTHENTICATE.setPage(BASE_FOLDER+"login.jsp").setTitle("User Login");
		Screen.Code.ROLESELECTION.setPage(BASE_FOLDER+"selectRole.jsp").setTitle("Select Role");
		Screen.Code.ENTITYLIST.setPage(BASE_FOLDER+"home.jsp").setTitle("Home");
		Screen.Code.MODELTABLE.setPage(BASE_FOLDER+"listModels.jsp").setTitle("List");
		Screen.Code.VIEWFORM.setPage(BASE_FOLDER+"viewForm.jsp").setTitle("View");
		Screen.Code.CREATEFORM.setPage(BASE_FOLDER+"createForm.jsp").setTitle("Create");
		Screen.Code.EDITFORM.setPage(BASE_FOLDER+"editForm.jsp").setTitle("Edit");
		Screen.Code.SEARCHFORM.setPage(BASE_FOLDER+"searchForm.jsp").setTitle("Search");
		Screen.Code.REGISTRATION.setPage(BASE_FOLDER+"register.jsp").setTitle("Register");
		Screen.Code.HISTORYTABLE.setPage(BASE_FOLDER+"history.jsp").setTitle("History");
		Screen.Code.IMPORTFORM.setPage(BASE_FOLDER+"importForm.jsp").setTitle("Import");
		Screen.Code.IMPORTTABLE.setPage(BASE_FOLDER+"importTable.jsp").setTitle("Import Results");
		Screen.Code.EDITTABLE.setPage(BASE_FOLDER+"editTable.jsp").setTitle("Edit Table");//RESUME opens all rows at once so no one else can edit.

		Screen.Code.AUTHENTICATE.addButton(Screen.Button.LOGIN);
		Screen.Code.AUTHENTICATE.addButton(Screen.Button.GOREGISTER);
		Screen.Code.REGISTRATION.addButton(Screen.Button.REGISTER);

		Screen.Code.ROLESELECTION.addButton(Screen.Button.SELECTROLE);
		Screen.Code.ROLESELECTION.addButton(Screen.Button.LOGOUT);

		//Screen.Code.ENTITYLIST.addButton(Screen.Button.LOOKUP); link not button.
		Screen.Code.ENTITYLIST.addButton(Screen.Button.LOGOUT);

		//VIEW is by link
		Screen.Code.MODELTABLE.addButton(Screen.Button.GOCREATE);
		Screen.Code.MODELTABLE.addButton(Screen.Button.GOIMPORT);
		Screen.Code.MODELTABLE.addButton(Screen.Button.GOSEARCH);
		Screen.Code.MODELTABLE.addButton(Screen.Button.EXPORT);
		Screen.Code.MODELTABLE.addButton(Screen.Button.HOME);
		Screen.Code.MODELTABLE.addButton(Screen.Button.LOGOUT);



		//Screen.Code.VIEWFORM.addButton(Screen.Button.HISTORY);
		Screen.Code.VIEWFORM.addButton(Screen.Button.OPEN);
		Screen.Code.VIEWFORM.addButton(Screen.Button.DELETE);
		Screen.Code.VIEWFORM.addButton(Screen.Button.HOME);
		Screen.Code.VIEWFORM.addButton(Screen.Button.LOGOUT);
		/*
		Screen.Code.VIEWFORM.addButton(Screen.Button.ARCHIVE);
		Screen.Code.VIEWFORM.addButton(Screen.Button.SUBMIT);
		Screen.Code.VIEWFORM.addButton(Screen.Button.PUBLISH);
		Screen.Code.VIEWFORM.addButton(Screen.Button.SHARE);
		Screen.Code.VIEWFORM.addButton(Screen.Button.APPROVE);
		Screen.Code.VIEWFORM.addButton(Screen.Button.REJECT);
		Screen.Code.VIEWFORM.addButton(Screen.Button.UNSUBMIT);
		Screen.Code.VIEWFORM.addButton(Screen.Button.UNPUBLISH);
		Screen.Code.VIEWFORM.addButton(Screen.Button.UNSHARE);
		Screen.Code.VIEWFORM.addButton(Screen.Button.UNAPPROVE);
		Screen.Code.VIEWFORM.addButton(Screen.Button.UNREJECT);

		Screen.Code.VIEWFORM.addButton(Screen.Button.ASSIGN);
		Screen.Code.VIEWFORM.addButton(Screen.Button.TRANSFER);
		*/


		Screen.Code.EDITFORM.addButton(Screen.Button.CLOSE);
		Screen.Code.EDITFORM.addButton(Screen.Button.UPDATE);
		Screen.Code.EDITFORM.addButton(Screen.Button.HOME);
		Screen.Code.EDITFORM.addButton(Screen.Button.LOGOUT);

		Screen.Code.SEARCHFORM.addButton(Screen.Button.SEARCH);
		Screen.Code.SEARCHFORM.addButton(Screen.Button.HOME);
		Screen.Code.SEARCHFORM.addButton(Screen.Button.LOGOUT);

		Screen.Code.IMPORTFORM.addButton(Screen.Button.IMPORT);
		Screen.Code.IMPORTFORM.addButton(Screen.Button.HOME);
		Screen.Code.IMPORTFORM.addButton(Screen.Button.LOGOUT);


		Screen.Code.HISTORYTABLE.addButton(Screen.Button.HOME);
		Screen.Code.HISTORYTABLE.addButton(Screen.Button.LOGOUT);

		//TODO finish screen buttons
		//Screen.Code.EDITTABLE

		//Screen.Code.NOCHANGE


		Screen.Button.APPROVE.setLabel("Approve");
		Screen.Button.ARCHIVE.setLabel("Archive");
		Screen.Button.ASSIGN.setLabel("Assign Role");
		Screen.Button.BACK.setLabel("Back");
		Screen.Button.CLOSE.setLabel("Close");
		Screen.Button.CREATE.setLabel("Create");
		Screen.Button.DELETE.setLabel("Delete");
		Screen.Button.GOCREATE.setLabel("Create New");
		Screen.Button.GOIMPORT.setLabel("Import");
		Screen.Button.GOREGISTER.setLabel("Register New User");
		Screen.Button.GOSEARCH.setLabel("Start Search");
		Screen.Button.HISTORY.setLabel("History");
		Screen.Button.HOME.setLabel("Home");
		Screen.Button.IMPORT.setLabel("Import");
		Screen.Button.LOOKUP.setLabel("Lookup");//Skipped: Login, Logout
		Screen.Button.OPEN.setLabel("Open");
		Screen.Button.PUBLISH.setLabel("Publish");
		Screen.Button.REGISTER.setLabel("Register");
		Screen.Button.REJECT.setLabel("Reject");
		Screen.Button.SEARCH.setLabel("Search");
		Screen.Button.SELECTROLE.setLabel("Select Role");
		Screen.Button.SHARE.setLabel("Share");
		Screen.Button.SUBMIT.setLabel("Submit");
		Screen.Button.TRANSFER.setLabel("Transfer");
		Screen.Button.UPDATE.setLabel("Update");
		Screen.Button.VIEW.setLabel("View");
		Screen.Button.UNAPPROVE.setLabel("Unapprove");
		Screen.Button.UNPUBLISH.setLabel("Unpublish");
		Screen.Button.UNREJECT.setLabel("Unreject");
		Screen.Button.UNSHARE.setLabel("Unshare");
		Screen.Button.UNSUBMIT.setLabel("Unsubmit");
		Screen.Button.LOGIN.setLabel("Login");
		Screen.Button.LOGOUT.setLabel("Logout");
		Screen.Button.EXPORT.setLabel("Export");
		try
		{
			ServletContext context = getServletContext();

			engine = Factory.createApplicationEngine();
			ApplicationManager appman = (ApplicationManager)context.getAttribute(ApplicationManagerController.APPLICATION_MANAGER);
			if(appman!=null){
				engine.init(appman);
				app = engine.getApplicationManager().getApplication();
				Screen.Code.SPLASH.setPage(BASE_FOLDER+"index.jsp").setTitle(app.getName());
				out("#********************************************************************** ");
				out("#*                     Loaded  Engine                                 * ");
				out("#********************************************************************** ");

				context.setAttribute(APPLICATION_ENGINE, engine);//TODO softcode

			}else{
				out("!!! ERROR: Application Manager not found in context.");
			}

		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void destroy()
	{
		ServletContext context = getServletContext();

		out("#********************************************************************** ");
		out("#                         STOPPING ENGINE                               ");
		out("#********************************************************************** ");

		engine.shutdown();

		/*
		HttpService service = (HttpService) context
				.getAttribute(WebConstants.SERVICE);
		if(container!=null)
		{
			container.stop();
		}
		context.removeAttribute(WebConstants.SERVICE);
		if (service != null)
		{
			service = null;
		}

		container = null;
		*/
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		handleRequests(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		handleRequests(request, response);
	}

	@Override
	public String getServletInfo()
	{
		return this.getClass().getName();
	}

	public void closeSession(HttpSession session){
		session.removeAttribute(LOGGED_IN);
		session.removeAttribute(MESSAGES);
		session.removeAttribute(CURRENT_SCREEN);
		session.removeAttribute(USER_SESSION);
		session.invalidate();
	}

	/*
	 * Handle any requests by denying them.
	 */
	private void handleRequests(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{

		String command = request.getParameter("command");

		System.out.println("");
		out("COMMAND:'"+command+"'.");
		out("QS:'"+request.getQueryString()+"'");

		HttpSession session = request.getSession();

		Screen currentScreen = new Screen(engine,Screen.Code.AUTHENTICATE);//this.manager,Screen.Code.AUTHENTICATE);
		if(session.getAttribute(CURRENT_SCREEN)!=null){
			currentScreen = (Screen)session.getAttribute(CURRENT_SCREEN);
		}else{
			session.setAttribute(CURRENT_SCREEN, currentScreen);
		}

		Messages messages = new Messages();
		if(session.getAttribute(MESSAGES)!=null){
			messages = (Messages) session.getAttribute(MESSAGES);
			out("  Loading messages.");
		}else{
			messages.add("Ready.");
			session.setAttribute(MESSAGES,messages);
			out("  Setting messages.");
		}

		UserSession usersession = null;
		if(session.getAttribute(USER_SESSION)!=null){
			usersession = (UserSession)session.getAttribute(USER_SESSION);
			out("Have usersession.");
		}

		if(command!=null){
			Result result = new Result();

			//TODO NOTE: if servicing a command and the role was not selected, log back out, start over.

			if(command.toLowerCase().startsWith("login")){
				out("Login.");
				if(session.getAttribute(LOGGED_IN)!=null){
					out("Have LOGGED_IN.");
					Boolean loggedin = (Boolean)session.getAttribute(LOGGED_IN);
					if(loggedin){
						String msg = "Already logged in.";
						out(msg);
						messages.clear();
						messages.add(msg);

						session.setAttribute(CURRENT_SCREEN, new Screen(this.engine,Screen.Code.ENTITYLIST));
					}else{
						out("Login session still open.");
						closeSession(session);
						Screen authenticateScreen = new Screen(this.engine,Code.AUTHENTICATE);
						Code authenticateScreenCode = authenticateScreen.getHere();
						String redirectTo = APPFOLDER+authenticateScreenCode.getPage();
						if(authenticateScreenCode.equals(Screen.Code.NOCHANGE)){
							out("ERROR: redirecting not available for authenticate screen.");
						}
						out("Redirecting to "+redirectTo);
						response.sendRedirect(redirectTo);
						return;
					}
				}else{
					out("No session value of login. Logging in.");
					String username = request.getParameter(User.USERFLD);
					String password = request.getParameter(User.PASSFLD);
					usersession = UserSession.createInstance();
					result = engine.login(username,password,usersession);//words[1],words[2]);
					if(result.isSuccessful()){
						User user = (User)result.objectValue();
						usersession.start(user);
						String msg = "Logged in user "+user.getName();
						session.setAttribute(LOGGED_IN, Boolean.TRUE);
						session.setAttribute(USER_SESSION, usersession);
						messages.clear();
						messages.add(msg);
						Screen screen = new Screen(this.engine,Screen.Code.ROLESELECTION);
						session.setAttribute(CURRENT_SCREEN, screen );
						List<Role> roleList = new ArrayList<Role>();
						result = engine.listRoles(usersession, roleList);
						screen.setValue(Screen.USER_ROLES, roleList);
						out(msg);
					}else{
						String msg = "Login unsuccessful.";
						out(msg);
						messages.add(msg);
						//messages.add(result.getMessages());attempting to login: message not appropriate here: says user is not logged in to perform operation (but the operation is logging in!)
						//closeSession(session); Don't close session or user won't get feedback.
						Screen authenticateScreen = new Screen(this.engine,Code.AUTHENTICATE);
						String redirectTo = APPFOLDER+authenticateScreen.getHere().getPage();
						out("Redirecting to "+redirectTo);
						response.sendRedirect(redirectTo);
						return;
					}
				}

			}else if(command.toLowerCase().startsWith("logout")){
				//TODO check if we should use the logout command equals button like BACK above here.
				out("Logout.");
				this.manager.logout();//TODO should this be manager logout or engine logout?
				if(session.getAttribute(LOGGED_IN)==null){
					messages.add("No login session found.");
				}else{
					Boolean loggedin = (Boolean)session.getAttribute(LOGGED_IN);
					session.removeAttribute(LOGGED_IN);
					session.removeAttribute(CURRENT_SCREEN);
					session.removeAttribute(USER_SESSION);
					if(!loggedin){
						messages.add("You have already been logged out.");
					}else{
						//session.setAttribute(LOGGED_IN, Boolean.FALSE);
						String _username = "unknown";
						usersession = null;
						User user = null;
						if(session.getAttribute(USER_SESSION)!=null){
							usersession = (UserSession)session.getAttribute(USER_SESSION);
							user = usersession.getUser();
							if(user!=null){
								_username = user.getName();
							}else{
								messages.add("User is null.");
							}
							session.removeAttribute(USER_SESSION);
						}
						//session.invalidate();
						messages.clear();
						messages.add("User: "+_username+" is now logged out.");
					}
				}
				Screen authenticateScreen = new Screen(this.engine,Code.AUTHENTICATE);
				String redirectTo = APPFOLDER+authenticateScreen.getHere().getPage();
				out("Redirecting to "+redirectTo);
				response.sendRedirect(redirectTo);
				return;


			}else if(command.startsWith("clear")){
				out("Clear.");
				messages.clear();
				messages.add("Ready.");

			/*
			 * SERVICE
			 */
			}else{
				out("Service being done for "+command);
				if(session.getAttribute(LOGGED_IN)!=null && session.getAttribute(USER_SESSION)!=null){
					Boolean loggedin = (Boolean)session.getAttribute(LOGGED_IN);
					if(loggedin){
						Button button = Button.valueOf(command);

						Result r = new Result();
						try{
							r = engine.service(usersession, currentScreen, button, request.getParameterMap());//manager.service(command)
						}catch(Exception e){
							r.error(e);
							e.printStackTrace();
						}
						if(r.isSuccessful()){
							String msg = button.getLabel()+" action successful.";
							messages.clear();
							messages.add(msg);
							Screen next = (Screen)r.objectValue();//TODO make a result for screen.
							out(msg);
							out("ApplicationController: SUCCESS - Setting screen:"+next);
							//FIXME should we do the screen next here in one place?
							//maybe also the available buttons.
							session.setAttribute(CURRENT_SCREEN,next);
						}else{
							String msg = button.getLabel()+" action unsuccessful, reason and messages follows:"+r.name().toLowerCase();
							out("ApplicationController: FAIL - Not forwarding:"+msg);//TODO extract this pattern to a method.
							messages.add(msg);
							messages.add(r.getMessages());
						}
					}else{
						messages.add("You are not logged in (command:"+command+")");//TODO need to splash, use the above refactoring.
					}
				}else{
					messages.add("User not logged in.:(loggedin:"+session.getAttribute(LOGGED_IN)+",useression:"+session.getAttribute(USER_SESSION)+")");//TODO need splash, use above, also apply to the other controller.
				}
			}





		}else{
			out("Command is null.");//TODO also add to messages.
		}
		//next = "/xxx/adminHome.jsp";//"/appman/application_manager.jsp";
		forwardScreen(request, response);
		/*
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("This servlet does not service requests or responses!");
		out.close();
		*/
		/*
		 * convert request into an action on the engine.
		 * perform the action on the engine and get the result
		 * the result maps to a screen update and is translated into messages for the user.
		 *
		 */

	}

	/*
	 * Use system out to splash the console.
	 *
	 */
	private static void out(Object o)
	{
		System.out.println("ApplicationController: '"+o+"'");
	}

	private void forwardScreen(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();

		Screen currentScreen = new Screen(this.engine,Screen.Code.AUTHENTICATE);//TODO, use a screen not a code. use the screen's data holders.
		if(session.getAttribute(CURRENT_SCREEN)!=null){
			currentScreen = (Screen)session.getAttribute(CURRENT_SCREEN);
		}else{
			session.setAttribute(CURRENT_SCREEN, currentScreen);
		}
		Code currentCode = currentScreen.getHere();
		if(Screen.Code.NOCHANGE.equals(currentCode)){
			out("No change, not redirecting or forwarding, back to same page.");
		}else{
			if(currentScreen.doingRedirect()){
				out("Finished. Redirecting to screen :'"+APPFOLDER+currentCode.name()+"' Title:'"+currentCode.getTitle()+"' Page:'"+currentCode.getPage()+"'");
				response.sendRedirect(APPFOLDER+currentCode.getPage());
			}else{
				out("Finished. Forwarding to screen :'"+currentCode.name()+"' Title:'"+currentCode.getTitle()+"' Page:'"+currentCode.getPage()+"'");
				RequestDispatcher rd = getServletContext().getRequestDispatcher(currentCode.getPage());
				rd.forward(request, response);
			}
		}
	}
	public static String getFormAction(){
		return "/jhgapp/app";//TODO refactor
	}

	public static String getLink(Screen.Button button){
		return "/jhgapp/app?command="+button.name();
	}

	public static String getLink(Screen.Button button, Integer managerId){
		StringBuffer sb = new StringBuffer();
		sb.append("/jhgapp/app?command="+button.name());
		sb.append("&"+ApplicationEngine.MID+"="+managerId);
		return sb.toString();
	}



	public static String getLink(Screen.Button button, String id){
		return "/jhgapp/app?command="+button.name()+"&"+ApplicationEngine.ID+"="+id;
	}

	public static String getLink(Screen.Button button, String id1, String id2){//make a var arg version?
		return "/jhgapp/appcommand="+button.name()+"&"+ApplicationEngine.ID1+"="+id1+"&"+ApplicationEngine.ID2+"="+id2;
	}

	public static String getLink(Screen.Button button, String id1, String id2, String id3){//make a var arg version?
		return "/jhgapp/app?command="+button.name()
				+"&"+ApplicationEngine.ID1+"="+id1
				+"&"+ApplicationEngine.ID2+"="+id2
				+"&"+ApplicationEngine.ID3+"="+id3;
	}


}
