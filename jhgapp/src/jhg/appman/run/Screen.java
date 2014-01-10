package jhg.appman.run;

import jhg.*;
import jhg.appman.run.Screen.Button;
import jhg.model.*;
import java.util.*;


public class Screen {

	public static final String AVAILABLE_BUTTONS = "AVAILABLE_BUTTONS";
	public static final String USER_ROLES = "USER_ROLES";
	public static final String ENTITES = "ENTITES";
	public static final String MANAGER = "MANAGER";
	public static final String MODEL = "MODEL";
	public static final String MODEL_LIST = "MODEL_LIST";
	
	public static enum Code{
		NOCHANGE(null),
		SPLASH(Code.NOCHANGE),//back.
		REGISTRATION(Code.SPLASH),
		AUTHENTICATE(Code.SPLASH),
		ROLESELECTION(Code.AUTHENTICATE),
		ENTITYLIST(Code.NOCHANGE),
		MODELTABLE(Code.ENTITYLIST),
		IMPORTFORM(Code.MODELTABLE),
		IMPORTTABLE(Code.IMPORTFORM),
		SEARCHFORM(Code.MODELTABLE),
		CREATEFORM(Code.MODELTABLE),
		VIEWFORM(Code.MODELTABLE),
		EDITFORM(Code.VIEWFORM),
		HISTORYTABLE(Code.VIEWFORM),
		EDITTABLE(Code.MODELTABLE),
		;
		private Code back;
		private String page;
		private String title;
		private List<Button> buttons;
		private Code(Code cancel){
			this.back = cancel;
			buttons = new ArrayList<Button>();
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
		
		public List<Button> getButtons(){
			return this.buttons;
		}
		public void addButton(Button aButton){
			this.buttons.add(aButton);
		}
		public boolean hasButton(Button button) {
			return this.buttons.contains(button);
		}	
	
		//TODO refactor this with the other Screen.
	}	
	
	public static enum Button{
		BACK(null,Action.Code.NONE),
		GOREGISTER(Code.REGISTRATION,Action.Code.NONE),
		REGISTER(Code.AUTHENTICATE,Action.Code.NONE),
		LOGIN(Code.ROLESELECTION,Action.Code.NONE),
		LOGOUT(Code.AUTHENTICATE,Action.Code.NONE),
		SELECTROLE(Code.ENTITYLIST,Action.Code.NONE),
		HOME(Code.ENTITYLIST,Action.Code.NONE),
		LOOKUP(Code.MODELTABLE,Action.Code.LOOKUP),     //aka search all
		VIEW(Code.VIEWFORM,Action.Code.VIEW),
		GOIMPORT(Code.IMPORTFORM,Action.Code.IMPORT),
		IMPORT(Code.IMPORTTABLE,Action.Code.IMPORT),
		GOSEARCH(Code.SEARCHFORM,Action.Code.SEARCH),
		SEARCH(Code.MODELTABLE,Action.Code.SEARCH),    //or search table
		GOCREATE(Code.CREATEFORM,Action.Code.CREATE),
		CREATE(Code.VIEWFORM,Action.Code.CREATE),
		OPEN(Code.EDITFORM,Action.Code.OPEN),
		CLOSE(Code.VIEWFORM,Action.Code.CLOSE),
		UPDATE(Code.VIEWFORM,Action.Code.UPDATE),
		HISTORY(Code.HISTORYTABLE,Action.Code.VIEW_HISTORY),
		DELETE(Code.MODELTABLE,Action.Code.DELETE),
		ARCHIVE(Code.VIEWFORM,Action.Code.ARCHIVE),
		PUBLISH(Code.VIEWFORM,Action.Code.PUBLISH),
		UNPUBLISH(Code.VIEWFORM,Action.Code.UNPUBLISH),
		SHARE(Code.VIEWFORM,Action.Code.SHARE),
		UNSHARE(Code.VIEWFORM,Action.Code.UNSHARE),
		SUBMIT(Code.VIEWFORM,Action.Code.SUBMIT),
		UNSUBMIT(Code.VIEWFORM,Action.Code.UNSUBMIT),
		APPROVE(Code.VIEWFORM,Action.Code.APPROVE),
		UNAPPROVE(Code.VIEWFORM,Action.Code.UNAPPROVE),
		REJECT(Code.VIEWFORM,Action.Code.REJECT),
		UNREJECT(Code.VIEWFORM,Action.Code.UNREJECT),
		ASSIGN(Code.VIEWFORM,Action.Code.ASSIGN_APPROVER),
		TRANSFER(Code.VIEWFORM,Action.Code.TRANSFER_OWNER),
		EXPORT(Code.MODELTABLE,Action.Code.EXPORT)
		;//Action codes not used: LISTENTITIES,
		
		private Code nextScreen;
		private String label;
		private Action.Code actionCode;
		private Button(Code success, Action.Code _actionCode){
			this.nextScreen = success;
			this.actionCode = _actionCode;
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
		public Action.Code getActionCode(){
			return this.actionCode;
		}
		public boolean isForSingle() {
			// TODO Auto-generated method stub
			return false;
		}
	}



	private ApplicationEngine engine;
	private Code code;
	
	//Optional objects
	private Manager manager;
	private Model model;
	private Map<Field,Model> parents;
	private Map<String,Object> data;
	private boolean redirect;
	
	
	public Screen(ApplicationEngine _engine, Code _code){
		this.engine = _engine;
		this.code = _code;
		this.data = new HashMap<String,Object>();
		this.redirect = false;
	}
	public String toString(){
		return "|"+this.code.name()+"::(redirect:"+redirect+",data:"+data.size()+")|";
	}
	public Manager getManager(){
		return this.manager;
	}
	public Model getModel(){
		return this.model;
	}
	public void setManager(Manager _manager){
		this.manager = _manager;
	}
	public void setModel(Model _model){
		this.model = _model;
	}
	public void makeRedirect(){
		this.redirect = true;
	}
	public boolean doingRedirect(){
		return this.redirect;
	}	

	/*
	public void handleButton(Button b){
		//TODO impl handle button
	}
	*/
	public Code getHere(){
		return this.code;
	}	
	
	public Code getBack(){
		return this.code.getBack();
	}
	
	public boolean hasValue(String key){
		return this.data.containsKey(key) && this.data.get(key)!=null;
	}
	
	public void setValue(String key, Object value){
		this.data.put(key, value);
	}

	public Object getValue(String key){
		return this.data.get(key);
	}
	public boolean hasButton(Button button) {
		return this.code.hasButton(button);
	}	


	
}
