package jhg.appman;

import java.util.*;



public class Screen {
	
	public static final String USERS_LIST = "USER_LIST";
	public static final String VIEW_USER = "VIEW_USER";                       //User
	public static final String USER_ROLES = "USER_ROLES";                     //List<Role>
	public static final String AVAILABLE_ROLES = "AVAILABLE_ROLES";			  //List<Role>
	public static final String VIEW_ROLE = "VIEW_ROLE";						  //Role
	public static final String ROLE_USERS = "ROLE_USERS";                     //List<User>
	public static final String ROLE_PRIVILEGES = "ROLE_PRIVILEGES";			  //Map<Entity,Priv>
	public static final String AVAILABLE_ENTITIES = "AVAILABLE_ENTITIES";	  //List<Entity>
	public static final String AVAILABLE_PRIVILEGES = "PRIVILEGES";			  //Privilege[]
	
	public static enum Code{
		NOCHANGE(null),
		AUTHENTICATE(Code.NOCHANGE),
		ADMINHOME(Code.NOCHANGE),
		USERTABLE(Code.ADMINHOME),
		VIEWUSER(Code.USERTABLE),
		EDITUSER(Code.VIEWUSER),
		CREATEUSER(Code.VIEWUSER),
		ROLETABLE(Code.ADMINHOME),
		VIEWROLE(Code.USERTABLE),
		CREATEROLE(Code.VIEWROLE),
		ENTITYTABLE(Code.ADMINHOME),
		VIEWENTITY(Code.ENTITYTABLE),
		;
		private Code back;
		private String page;
		private String title;
		private Code(Code cancel){
			this.back = cancel;
		}
		public Code getBack(){
			return this.back;
		}
		public Code setPage(String _p){
			this.page = _p;
			return this;
		}
		public Code setTitle(String _t){
			this.title = _t;
			return this;
		}
		public String getPage(){
			return this.page;
		}
		public String getTitle(){
			return this.title;
		}
	}	
	
	public static enum Button{
		BACK(null),
		LOGIN(Code.ADMINHOME),
		LOGOUT(Code.AUTHENTICATE),
		GOHOME(Code.ADMINHOME),
		MANAGEUSERS(Code.USERTABLE),
		GOVIEWUSER(Code.VIEWUSER),
		GOCREATEUSER(Code.CREATEUSER),
		CREATEUSER(Code.VIEWUSER),
		GOEDITUSER(Code.EDITUSER),
		EDITUSER(Code.VIEWUSER),
		DELETEUSER(Code.USERTABLE),
		MANAGEROLES(Code.ROLETABLE),
		VIEWROLE(Code.VIEWROLE),
		GOCREATEROLE(Code.CREATEROLE),
		CREATEROLE(Code.VIEWROLE),
		DELETEROLE(Code.ROLETABLE),
		MANAGEENTITIES(Code.ENTITYTABLE),
		VIEWENTITY(Code.VIEWENTITY),
		ASSIGNROLE(Code.VIEWUSER),
		UNASSIGNROLE(Code.VIEWUSER),
		GRANTPRIV(Code.VIEWROLE),
		UNGRANTPRIV(Code.VIEWROLE),
		;
		
		private Code nextScreen;
		private String label;
		private Button(Code success){
			this.nextScreen = success;
			this.label = "";
		}
		public Code destination(){
			return this.nextScreen;
		}
		public Button setLabel(String _l){
			this.label = _l;
			return this;
		}
		public String getLabel(){
			return this.label;
		}
	}

	private ApplicationManager appmanager;
	private Code code;
	private Map<String,Object> data;
	private Integer id;
	private boolean redirect;
	//private Map<Button,Action> buttonActions;
	
	//TODO, perhaps put the stack of url on the session for back function.(or leave it out)
	
	public Screen(ApplicationManager _am, Code _code){
		this.appmanager = _am;
		this.code = _code;
		this.data = new HashMap<String,Object>();
		this.redirect = false;
	}
	
	public void makeRedirect(){
		this.redirect = true;
	}
	
	public boolean doingRedirect(){
		return this.redirect;
	}
	
	public Code getHere(){
		return this.code;
	}
	
	public Code getBack(){
		return this.code.getBack();
	}

	public ApplicationManager getApplicationManager(){
		return this.appmanager;
	}
	
	public void setValue(String key, Object value){
		this.data.put(key, value);
	}

	public Object getValue(String key){
		return this.data.get(key);
	}
	public void setId(Integer _id) {
		this.id = _id;
	}
	
}
