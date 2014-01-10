package jhg.appman.control;

import java.io.IOException;
import java.util.Map;

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
import jhg.User;
import jhg.appman.ApplicationManager;
import jhg.appman.Config;
import jhg.appman.Screen;
import jhg.appman.Screen.Button;
import jhg.appman.Screen.Code;
import jhg.appman.SimpleApplicationManager;

import org.apache.commons.lang3.StringUtils;



/**
 * Loads the application manager to the application scope.
 *
 * @author JGagon
 *
 */
public final class ApplicationManagerController extends HttpServlet{


	public static final String APPLICATION_MANAGER = "APPLICATION_MANAGER";
	private static final String APPFOLDER = "/jhgapp";
	public static final String APPMAN_CURRSCREEN = "APPMAN_CURRSCREEN";
	public static final String BASE_FOLDER = "/app/";
	public static final String PAGE_DATA = "PAGE_DATA";


	private static final String USER = "USER";

	/*

	*/

	private static final String LOGGED_IN = "LOGGED_IN";

	private static final String MESSAGES = "MESSAGES";

	//private static final Log log	= JmcSupport.getLog(LoadOnStartupServlet.class);
	private static final long		serialVersionUID	= 1L;

	private static final String		VERSION				= "0.0.1";
	private static final String     CONFIG_NAME         = "config";

	private ApplicationManager manager;

	/*
	 * Use system out to splash the console.
	 *
	 */
	private static void splash(Object o)
	{
		System.out.println(o);
	}


	/**
	 * Constructor. Required to be public as a load on startup servlet called via web xml.
	 */
	public ApplicationManagerController()
	{
		super();
		//log.debug("Constructed.");
	}

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		Screen.Code.ADMINHOME.setPage(BASE_FOLDER+"adminHome.jsp").setTitle("Admin Home");
		Screen.Code.AUTHENTICATE.setPage(BASE_FOLDER+"adminLogin.jsp").setTitle("Admin Login");
		Screen.Code.USERTABLE.setPage(BASE_FOLDER+"manageUsers.jsp").setTitle("Manage Users");
		Screen.Code.VIEWUSER.setPage(BASE_FOLDER+"viewUser.jsp").setTitle("View User");
		Screen.Code.CREATEUSER.setPage(BASE_FOLDER+"createUser.jsp").setTitle("Create User");
		Screen.Code.EDITUSER.setPage(BASE_FOLDER+"editUser.jsp").setTitle("Edit User");
		Screen.Code.ROLETABLE.setPage(BASE_FOLDER+"manageRoles.jsp").setTitle("Manage Roles");
		Screen.Code.VIEWROLE.setPage(BASE_FOLDER+"viewRole.jsp").setTitle("View Role");
		Screen.Code.CREATEROLE.setPage(BASE_FOLDER+"createRole.jsp").setTitle("Create Role");
		Screen.Code.ENTITYTABLE.setPage(BASE_FOLDER+"manageEntities.jsp").setTitle("Manage Entities");
		Screen.Code.VIEWENTITY.setPage(BASE_FOLDER+"viewEntity.jsp").setTitle("View Entity");

		Screen.Button.BACK.setLabel("Back");
		Screen.Button.DELETEROLE.setLabel("Delete Role");
		Screen.Button.DELETEUSER.setLabel("Delete User");
		Screen.Button.GOCREATEROLE.setLabel("Create a Role");
		Screen.Button.GOCREATEUSER.setLabel("Create a User");
		Screen.Button.GOEDITUSER.setLabel("Edit This User");
		Screen.Button.GOHOME.setLabel("Home");
		Screen.Button.GOVIEWUSER.setLabel("View User");
		Screen.Button.LOGIN.setLabel("Login");
		Screen.Button.LOGOUT.setLabel("Logout");
		Screen.Button.MANAGEROLES.setLabel("Manage Roles");
		Screen.Button.MANAGEUSERS.setLabel("Manage Users");
		Screen.Button.MANAGEENTITIES.setLabel("Manage Entities");
		Screen.Button.VIEWENTITY.setLabel("View Entity");
		Screen.Button.UNASSIGNROLE.setLabel("Unassign Role");
		Screen.Button.ASSIGNROLE.setLabel("Assign Role");
		Screen.Button.CREATEROLE.setLabel("Create Role");
		Screen.Button.CREATEUSER.setLabel("Create User");
		Screen.Button.EDITUSER.setLabel("Edit User");
		Screen.Button.VIEWROLE.setLabel("View Role");
		Screen.Button.GRANTPRIV.setLabel("Grant Privilege");
		Screen.Button.UNGRANTPRIV.setLabel("Un-grant Privilege");




		try
		{
			manager = new SimpleApplicationManager(  //TODO, this could get softcoded by the servlet config in the web xml parameters.
					new Config(config.getInitParameter(CONFIG_NAME))
					);
			manager.init();
			splash("#********************************************************************** ");
			splash("                     " + manager.getApplicationName() + " ");
			splash("#*                                                                    * ");
			splash("#*                          Developer: John Gagon                     * ");
			splash("#*                          Version   : " + VERSION
					+ "                         * ");
			splash("#********************************************************************** ");

			ServletContext context = getServletContext();
			context.setAttribute(APPLICATION_MANAGER, manager);
			/*
			Application app = container.getApplication();
			List<ActiveService> activeServices = app.getActiveServices();
			HttpService service = null;
			for (ActiveService as : activeServices)
			{
				if (as instanceof HttpService)
				{
					service = (HttpService) as;
					context.setAttribute(WebConstants.SERVICE, service);
					break;
				}
			}
			service = (HttpService) context.getAttribute(WebConstants.SERVICE);
			if (service == null)
				throw new ServletException(
						"Couln't find HttpService for this application.");
			*/
			splash("#**********************************************************************\n");
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

		splash("#********************************************************************** ");
		splash("#                         STOPPING " + manager.getApplicationName()
				+ "                                ");
		splash("#********************************************************************** ");

		manager.shutdown();
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

	public static String getLink(){
		return "/jhgapp/man";
	}

	/**
	 * DOC
	 * @param button
	 * @return
	 */
	public static String getLink(Screen.Button button){
		return "/jhgapp/man?command="+button.name();
	}

	public static String getLink(Screen.Button button, String id){
		return "/jhgapp/man?command="+button.name()+"&"+ApplicationManager.ID+"="+id;
	}

	public static String getLink(Screen.Button button, String id1, String id2){//make a var arg version?
		return "/jhgapp/man?command="+button.name()+"&"+ApplicationManager.ID1+"="+id1+"&"+ApplicationManager.ID2+"="+id2;
	}

	public static String getLink(Screen.Button button, String id1, String id2, String id3){//make a var arg version?
		return "/jhgapp/man?command="+button.name()
				+"&"+ApplicationManager.ID1+"="+id1
				+"&"+ApplicationManager.ID2+"="+id2
				+"&"+ApplicationManager.ID3+"="+id3;
	}



	public void closeSession(HttpSession session){
		session.removeAttribute(LOGGED_IN);
		session.removeAttribute(MESSAGES);
		session.removeAttribute(APPMAN_CURRSCREEN);
		session.invalidate();
	}

	/*
	 * Handle any requests by denying them.
	 */
	@SuppressWarnings("unchecked")
	private void handleRequests(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{

		String command = request.getParameter("command");


		splash("\nCOMMAND:'"+command+"'.");
		splash("\nQS:'"+request.getQueryString()+"'");

		HttpSession session = request.getSession();

		Screen currentScreen = new Screen(this.manager,Screen.Code.AUTHENTICATE);
		if(session.getAttribute(APPMAN_CURRSCREEN)!=null){
			currentScreen = (Screen)session.getAttribute(APPMAN_CURRSCREEN);
		}else{
			session.setAttribute(APPMAN_CURRSCREEN, currentScreen);
		}

		Messages messages = new Messages();
		if(session.getAttribute(MESSAGES)!=null){
			messages = (Messages) session.getAttribute(MESSAGES);
			splash("  Loading messages.");
		}else{
			messages.add("Ready.");
			session.setAttribute(MESSAGES,messages);
			splash("  Setting messages.");
		}

		if(command!=null){
			Result result = new Result();

			if(command.toLowerCase().startsWith("login")){
				splash("Login.");
				if(session.getAttribute(LOGGED_IN)!=null){
					splash("Have LOGGED_IN.");
					Boolean loggedin = (Boolean)session.getAttribute(LOGGED_IN);
					if(loggedin){
						String msg = "Already logged in.";
						splash(msg);
						messages.clear();
						messages.add(msg);

						session.setAttribute(APPMAN_CURRSCREEN, new Screen(this.manager,Screen.Code.ADMINHOME));
						//session.setAttribute(MESSAGES,messages);
					}else{

						splash("Login session still open.");
						//note, these would just get wiped by the session closing.
						//messages.clear();
						//messages.add("You have been logged out.");

						closeSession(session);

						Screen authenticateScreen = new Screen(this.manager,Code.AUTHENTICATE);
						String redirectTo = APPFOLDER+authenticateScreen.getHere().getPage();
						splash("Redirecting to "+redirectTo);
						response.sendRedirect(redirectTo);
						return;
					}
				}else{
					splash("No session value of login. Logging in.");
					String username = request.getParameter("username");
					String password = request.getParameter("password");
					result = manager.login(username,password);//words[1],words[2]);
					if(result.isSuccessful()){
						User user = (User)result.objectValue();
						String msg = "Logged in user "+user.getName();
						session.setAttribute(LOGGED_IN, Boolean.TRUE);
						session.setAttribute(USER, user);
						messages.clear();
						messages.add(msg);
						session.setAttribute(APPMAN_CURRSCREEN, new Screen(this.manager,Screen.Code.ADMINHOME));
						splash(msg);
					}else{
						String msg = "Login unsuccessful.";
						splash(msg);
						messages.add(msg);
						//messages.add(result.getMessages());attempting to login: message not appropriate here: says user is not logged in to perform operation (but the operation is logging in!)
						//closeSession(session); Don't close session or user won't get feedback.
						Screen authenticateScreen = new Screen(this.manager,Code.AUTHENTICATE);
						String redirectTo = APPFOLDER+authenticateScreen.getHere().getPage();
						splash("Redirecting to "+redirectTo);
						response.sendRedirect(redirectTo);
						return;
					}
				}


			//TODO not working. do we even need back screens in screens?
			}
			else if(command.equals(Button.BACK)){
				Screen.Code backCode = currentScreen.getBack();
				splash("Back page code:"+backCode);
				//TODO NOTE, this should be a back command that works or implement a Screen stack object
				Screen backAScreen = new Screen(this.manager,backCode);  //the view id is not always passed.
				String id=request.getParameter(ApplicationManager.ID);
				if(id!=null){
					backAScreen.setId(Integer.valueOf(id));
				}
				session.setAttribute(APPMAN_CURRSCREEN, backAScreen);
				splash("Going back (setting current screen) to "+backAScreen.getHere().name()+" from "+currentScreen.getHere().name());
				//return;



			}else if(command.toLowerCase().startsWith("logout")){
				//TODO check if we should use the logout command equals button like BACK above here.
				splash("Logout.");
				this.manager.logout();
				if(session.getAttribute(LOGGED_IN)==null){
					messages.add("No login session found.");
				}else{
					Boolean loggedin = (Boolean)session.getAttribute(LOGGED_IN);
					session.removeAttribute(LOGGED_IN);
					session.removeAttribute(APPMAN_CURRSCREEN);
					if(!loggedin){
						messages.add("You have already been logged out.");
					}else{
						//session.setAttribute(LOGGED_IN, Boolean.FALSE);
						String _username = "unknown";
						User user = null;
						if(session.getAttribute(USER)!=null){
							user = (User)session.getAttribute(USER);
							if(user!=null){
								_username = user.getName();
							}else{
								messages.add("User is null.");
							}
							session.removeAttribute(USER);
						}
						//session.invalidate();
						messages.clear();
						messages.add("User: "+_username+" is now logged out.");
					}
				}
				Screen authenticateScreen = new Screen(this.manager,Code.AUTHENTICATE);
				String redirectTo = APPFOLDER+authenticateScreen.getHere().getPage();
				splash("Redirecting to "+redirectTo);
				//RequestDispatcher rd = getServletContext().getRequestDispatcher(forwardto);
				//rd.forward(request, response);
				response.sendRedirect(redirectTo);
				return;


			}else if(command.startsWith("clear")){
				splash("Clear.");
				messages.clear();
				messages.add("Ready.");



			}else{
				splash("Service being done for "+command);
				if(session.getAttribute(LOGGED_IN)!=null && session.getAttribute(USER)!=null){
					Boolean loggedin = (Boolean)session.getAttribute(LOGGED_IN);
					if(loggedin){
						Button button = Button.valueOf(command);
						Result r = manager.service(button, request.getParameterMap());//manager.service(command)
						if(r.isSuccessful()){
							messages.clear();
							messages.add(button.getLabel()+" action successful.");
							session.setAttribute(APPMAN_CURRSCREEN,r.objectValue());
						}else{
							messages.add(button.getLabel()+" action unsuccessful, reason and messages follows:"+r.name().toLowerCase());
							messages.add(r.getMessages());
						}
					}else{
						messages.add("You are not logged in (command:"+command+")");
					}
				}else{
					messages.add("User not logged in.:(loggedin:"+session.getAttribute(LOGGED_IN)+",user:"+session.getAttribute(USER)+")");
				}



			}
		}else{
			splash("Command is null.");
		}
		//next = "/xxx/adminHome.jsp";//"/appman/application_manager.jsp";
		forwardScreen(request, response);
		/*
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("This servlet does not service requests or responses!");
		out.close();
		*/
	}


	private void forwardScreen(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();

		Screen currentScreen = new Screen(this.manager,Screen.Code.AUTHENTICATE);//TODO, use a screen not a code. use the screen's data holders.
		if(session.getAttribute(APPMAN_CURRSCREEN)!=null){
			currentScreen = (Screen)session.getAttribute(APPMAN_CURRSCREEN);
		}else{
			session.setAttribute(APPMAN_CURRSCREEN, currentScreen);
		}
		if(currentScreen.doingRedirect()){
			splash("Finished. Redirecting to screen :'"+APPFOLDER+currentScreen.getHere().name()+"' Title:'"+currentScreen.getHere().getTitle()+"' Page:'"+currentScreen.getHere().getPage()+"'");
			response.sendRedirect(APPFOLDER+currentScreen.getHere().getPage());
		}else{
			splash("Finished. Forwarding to screen :'"+currentScreen.getHere().name()+"' Title:'"+currentScreen.getHere().getTitle()+"' Page:'"+currentScreen.getHere().getPage()+"'");
			RequestDispatcher rd = getServletContext().getRequestDispatcher(currentScreen.getHere().getPage());
			rd.forward(request, response);
		}
	}







}

