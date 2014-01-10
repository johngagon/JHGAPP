/*
		NONE(Privilege.NONE,State.EXISTING,State.NOCHANGE),
		x LISTENTITIES(Privilege.LOOKUP,State.EXISTING,State.NOCHANGE),
		x CREATE(Privilege.OWN,State.BLANK,State.SAVED),
		x IMPORT(Privilege.OWN,State.BLANK,State.SAVED),
		x LOOKUP(Privilege.LOOKUP,State.PUBLISHED,State.NOCHANGE),//up to exported 
		x OLOOKUP(Privilege.OWN,State.SAVED,State.NOCHANGE),		
		x SEARCH ""
		x OSEARCH ""
		x VIEW(Privilege.READ,State.PUBLISHED,State.NOCHANGE),       
		x OVIEW(Privilege.OWN,State.SAVED,State.NOCHANGE),		
		x OPEN(Privilege.WRITE,State.SHARED,State.NOCHANGE),		//up to shared or maybe even submitted 
		x OOPEN(Privilege.OWN,State.SAVED,State.NOCHANGE),		 
		x CLOSE(Privilege.WRITE,State.SHARED,State.NOCHANGE),		
		x OCLOSE(Privilege.OWN,State.SAVED,State.NOCHANGE),
		x UPDATE(Privilege.WRITE,State.SHARED,State.NOCHANGE),
		x OUPDATE(Privilege.OWN,State.SAVED,State.NOCHANGE),
		x PUBLISH(Privilege.OWN,State.SAVED,State.PUBLISHED),
		x UNPUBLISH(Privilege.OWN,State.PUBLISHED,State.SAVED),
		x SHARE(Privilege.OWN,State.PUBLISHED,State.PUBLISHED),
		x UNSHARE(Privilege.OWN,State.SHARED,State.PUBLISHED),
		x SUBMIT(Privilege.OWN,State.SAVED,State.SUBMITTED),      //locks the record. (no update
		x UNSUBMIT(Privilege.OWN,State.SUBMITTED,State.SAVED),    //unlocks
		x APPROVE(Privilege.APPROVE,State.SUBMITTED,State.APPROVED),
		x UNAPPROVE(Privilege.APPROVE,State.APPROVED,State.SUBMITTED),
		x REJECT(Privilege.APPROVE,State.SUBMITTED,State.REJECTED),
		x UNREJECT(Privilege.APPROVE,State.REJECTED,State.SUBMITTED),
		x TRANSFER_OWNER(Privilege.APPROVE,State.EXISTING,State.NOCHANGE),
		x ASSIGN_APPROVER(Privilege.APPROVE,State.EXISTING,State.NOCHANGE),
		x EXPORT(Privilege.APPROVE,State.REJECTED,State.EXPORTED),
		//EXPORT_REJECTED(Privilege.APPROVE,State.REJECTED,State.EXPORTED),
		//EXPORT_APPROVED(Privilege.APPROVE,State.APPROVED,State.EXPORTED),
		x DELETE(Privilege.OWN,State.SAVED,State.DELETED),                   //record gone
		x ARCHIVE(Privilege.APPROVE,State.EXPORTED,State.ARCHIVED);          //record gone
		x VIEW_HISTORY(Privilege.APPROVE,State.SAVED,State.NOCHANGE),
		//ROLLBACK(Privilege.APPROVE,State.SAVED,State.NOCHANGE),
		//ROLLFORWARD(Privilege.APPROVE,State.SAVED,State.NOCHANGE),

 */
package jhg.appman.run;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.Box.Filler;

import jhg.*;
import jhg.Action.Code;
import jhg.Result.Reason;
import jhg.account.Account;
import jhg.appman.ApplicationManager;
import jhg.appman.Factory;
import jhg.appman.SqlTranslator;

import jhg.appman.control.ApplicationManagerController;
import jhg.appman.run.Screen;
import jhg.appman.run.Screen.Button;

import jhg.db.Database;

import jhg.model.Application;
import jhg.model.Field;
import jhg.model.Manager;
import jhg.model.Model;
import jhg.model.ModelHistory;
import jhg.model.field.DateTime;
import jhg.model.field.DateTime.DateTimeField;
import jhg.model.field.Reference.ReferenceField;

/**
 * DOC
 * aka script application engine
 * @author John
 *
 */
@SuppressWarnings({"rawtypes","unused"})
public class SimpleApplicationEngine extends Base implements ApplicationEngine {

	public static final String HTML5_DATEFORMAT = "yyy-MM-dd'T'HH:mm";

	private static final Boolean LOG_SQL = Boolean.TRUE;
	
	private ApplicationManager appman;
	private Database db;
	private Application application;
	private int status;
	
	public SimpleApplicationEngine(){
		super();
		status=ApplicationEngine.OFFLINE;
	}
	
	@Override
	public int status() {
		return this.status;
	}
	
	@Override	
	public void init(ApplicationManager _appman) throws ApplicationException{
		log("Initializing.");
		this.appman = _appman;
		//TODO add a switch so this can be turned on in standalone mode in eclipse or off in web mode.
		//appman = Factory.createApplicationManager();
		
		
		db = appman.getDatabase();
		application = appman.getApplication();
		//TODO this all should move to the application manager
		//when granting ownership, ensure the existing model table has the owner updated.
		//add a state to the model table for share,approval/lock,publish,etc
		//(custom states requires extra feature)
		
		//compare the fields in the actual entity from table definition (table jdbc metadata)
		//  with the fields declared, 
		//    if a field has been removed, throw an error
		//    if a field has beed added, ensure records default when necessary.
		//    tables may need a "blank" row to indicate a non-reference.
		//compare the model table with the models in the tables themselves.
		//Also add a migration tool.
		
		//TODO use check like method elsewhere
		if(!check("CreateApplicationTables",createApplicationTables()))return;
		if(!check("CompareFields",compareFields()))return;
		if(!check("LoadModels",loadModels()))return;
		//if(!check("LoadModelHistory",loadModelHistories()))return; probably takes up to much room. TODO remove this if not used.
		log("Finished initializing.");
		this.status = ApplicationEngine.ONLINE;
	}	
	
	@Override
	public Application getApplication(){
		return this.application;
	}
	
	@Override
	public void shutdown() {
		this.status = ApplicationEngine.OFFLINE;
	}	
	
	@Override
	public ApplicationManager getApplicationManager(){
		return this.appman;
	}
	
	@Override
	public Result login(String username, String passwd, UserSession blankSession) {
		log("Logging in user "+username);
		Result result = new Result();
		String selectUserSql = "SELECT * FROM "+User.AUX_USER+" WHERE "
				+User.USERFLD+" = '"+username+"' AND "
				+User.PASSFLD+" = '"+passwd+"' ";//TODO prepared statement
		logsql(selectUserSql);
		User user = new User();
		result = db.executeSingleSelectQuery(selectUserSql,user);
		if(!user.isValid()){
			result.error("User object not valid.");
		}else{
			if(result.isSuccessful()){
				blankSession.start(user);
				result.objectValue(user);
			}
		}
		return result;
		//TODO do we need the manager to have a login?
	}

	@Override
	public Result logout(UserSession userSession) {
		log("Logging out user "+userSession.getUser().getName());
		Result r = new Result();//TODO in the future, we may need to unset any in use items.
		userSession.invalidate();
		r.success();
		return r;
	}	

	public static String getSingleParameter(Map parameterMap, String key){
		if(key==null)throw new IllegalArgumentException("key is null.");
		String rv = null;
		String[] values = (String[])parameterMap.get(key);
		if(values!=null){
			if(values.length==1){
				rv = values[0];
			}else if(values.length<1){
				//System.out.println("WARNING: SimpleApplicationEngine.getSingleParameter ")
			}else{
				System.out.println("WARNING: SimpleApplicationEngine.getSingleParameter had more than one value.");
			}
		}
		return rv;//TODO add validation.
	}

	@Override
	public Result service(UserSession usersession, Screen currentScreen, Button button, Map parameterMap) {
		log("service(String,Map)");
		Result result = new Result();
		log(button.getLabel());
		Screen next = new Screen(this,button.destination());
		
		//TODO check login (but the role may not be selected yet so we may have to soft validate through each branch's methods
		
		switch(button){
			case SELECTROLE:
				String roleId = getSingleParameter(parameterMap, ApplicationManager.ID);
				Result roleResult = findRole(roleId);
				if(roleResult.isSuccessful()){
					Role selectingRole = (Role)roleResult.objectValue();
					log("Selected role:"+selectingRole.getName());
					Result doSelectResult = selectRole(selectingRole,usersession);
					if(doSelectResult.isSuccessful()){
						List<Manager> managers = new ArrayList<Manager>();
						result = listManagers(usersession, managers);
						next.setValue(Screen.ENTITES, managers);
						result.objectValue(next);
						result.success();
					}else{
						result.noResult();
						result.addMessages(doSelectResult.getMessages());						
					}
				}else{
					result.noResult();
					result.addMessages(roleResult.getMessages());
				}
				break;		
			case HOME:
				List<Manager> managers = new ArrayList<Manager>();
				result = listManagers(usersession, managers);
				next.setValue(Screen.ENTITES, managers);
				if(result.isSuccessful()){
					result.objectValue(next);
				}
				break;		
			case LOOKUP:
				String entityId = getSingleParameter(parameterMap, ApplicationEngine.MID);
				List<Model> models = new ArrayList<Model>();
				result = lookupModels(usersession, entityId, models);
				Manager lookupManager = application.getManagerById(Integer.valueOf(entityId));
				next.setValue(Screen.MODEL_LIST, models);
				next.setValue(Screen.MANAGER,lookupManager);
				result.objectValue(next);
				result.success();				
				break;		
			case GOCREATE:
				String goCreateEntityId = getSingleParameter(parameterMap, ApplicationEngine.MID);
				Manager goCreateManager = application.getManagerById(Integer.valueOf(goCreateEntityId));
				next.setValue(Screen.MANAGER,goCreateManager);
				result.objectValue(next);
				result.success();
				break;				
			case CREATE:         //autopublish, autoshare, autosubmit, autoapprove, (define on entity)
				Manager createManager = (Manager)currentScreen.getValue(Screen.MANAGER);
				List<Field> fieldList = createManager.getFields();
				Map<String,String> insertValues = new HashMap<String,String>();
				for(Field field:fieldList){
					String fieldName = field.getName();
					String fieldValue = getSingleParameter(parameterMap, fieldName);
					if(fieldValue!=null){
						insertValues.put(fieldName,fieldValue);
					}
				}
				String entityName = createManager.getName();
				ModelResult mr = this.createModel(usersession, entityName ,insertValues);

				
				if(mr.isSuccessful()){
					Model newModel = mr.getModel();

					List<Button> availableButtons = new ArrayList<Button>();
					List<Action.Code> availableCodes = new ArrayList<Action.Code>();
					Result availableActionsResult = listAvailableActions(usersession, entityName, newModel.getIdString(),availableCodes);
					if(availableActionsResult.isSuccessful()){
						log("Available Actions size:"+availableCodes.size());
						for(Action.Code c:availableCodes){
							log("Code:"+c.name());
						}						
						availableButtons = listAvailableButtonsFromAvailableActions(next,availableCodes);
						log("Available Buttons size:"+availableButtons.size());
						for(Button b:availableButtons){
							log("Button:"+b.name());
						}						
						
					}else{
						log("Available Buttons failed.");
					}
					next.setValue(Screen.MODEL,newModel);
					next.setValue(Screen.MANAGER,createManager);
					next.setValue(Screen.AVAILABLE_BUTTONS, availableButtons);
					result.objectValue(next);
					result.success();				
				}else{
					result.addMessages(mr.getMessages());
					log("Create [ModelResult]:"+mr.getReason().name());
					log("Create [ModelResult] messages:"+mr.getMessages().size());
				}
				break;
			case VIEW:
				Manager viewManager = (Manager)currentScreen.getValue(Screen.MANAGER);//TODO nullcheck
				String viewModelId = getSingleParameter(parameterMap, ApplicationEngine.ID);//TODO nullcheck
				ModelResult viewModelResult = viewModel(usersession,viewManager.getName(),viewModelId);

				if(viewModelResult.isSuccessful()){
					Model viewingModel = viewModelResult.getModel();
					List<Button> availableButtons = new ArrayList<Button>();
					List<Action.Code> availableCodes = new ArrayList<Action.Code>();
					Result availableActionsResult = listAvailableActions(usersession, viewManager.getName(), viewingModel.getIdString(),availableCodes);
					if(availableActionsResult.isSuccessful()){
						availableButtons = listAvailableButtonsFromAvailableActions(next,availableCodes);				
					}//might be unsuccessful but may be due to no available actions.
					next.setValue(Screen.MODEL,viewingModel);
					next.setValue(Screen.MANAGER,viewManager);
					next.setValue(Screen.AVAILABLE_BUTTONS, availableButtons);
					result.objectValue(next);
					result.success();				
				}else{
					result.addMessages(viewModelResult.getMessages());
				}				
				break;
			case OPEN:
				Manager openManager = (Manager)currentScreen.getValue(Screen.MANAGER);//TODO nullcheck
				Model openModel = (Model)currentScreen.getValue(Screen.MODEL);
				//ModelResult openModelResult = viewModel(usersession,openManager.getName(),openModelId);
				Result openModelResult = openModel(usersession,openModel);
				if(openModelResult.isSuccessful()){
					List<Button> availableButtons = new ArrayList<Button>();
					List<Action.Code> availableCodes = new ArrayList<Action.Code>();
					Result availableActionsResult = listAvailableActions(usersession, openManager.getName(), openModel.getIdString(),availableCodes);
					if(availableActionsResult.isSuccessful()){
						availableButtons = listAvailableButtonsFromAvailableActions(next,availableCodes);				
					}//might be unsuccessful but may be due to no available actions.
					next.setValue(Screen.MODEL,openModel);
					next.setValue(Screen.MANAGER,openManager);
					next.setValue(Screen.AVAILABLE_BUTTONS, availableButtons);
					
					result.objectValue(next);
					result.success();
				}else{
					result.addMessages(openModelResult.getMessages());
				}
				break;
			case CLOSE:
				Manager closeManager = (Manager)currentScreen.getValue(Screen.MANAGER);//TODO nullcheck
				Model closeModel = (Model)currentScreen.getValue(Screen.MODEL);
				//ModelResult openModelResult = viewModel(usersession,openManager.getName(),openModelId);
				Result closeModelResult = closeModel(usersession,closeModel);
				if(closeModelResult.isSuccessful()){
					List<Button> availableButtons = new ArrayList<Button>();
					List<Action.Code> availableCodes = new ArrayList<Action.Code>();
					Result availableActionsResult = listAvailableActions(usersession, closeManager.getName(), closeModel.getIdString(),availableCodes);
					if(availableActionsResult.isSuccessful()){
						availableButtons = listAvailableButtonsFromAvailableActions(next,availableCodes);				
					}//might be unsuccessful but may be due to no available actions.
					next.setValue(Screen.MODEL,closeModel);
					next.setValue(Screen.MANAGER,closeManager);
					next.setValue(Screen.AVAILABLE_BUTTONS, availableButtons);
					result.objectValue(next);
					result.success();
				}else{
					result.addMessages(closeModelResult.getMessages());
				}
				break;
			case UPDATE:
				Manager updateManager = (Manager)currentScreen.getValue(Screen.MANAGER);//TODO nullcheck
				Model updateModel = (Model)currentScreen.getValue(Screen.MODEL);
				//ModelResult openModelResult = viewModel(usersession,openManager.getName(),openModelId);
				List<Field> updateFieldList = updateManager.getFields();
				Map<String,String> updateValues = new HashMap<String,String>();
				ModelResult updateModelResult = new ModelResult();
				for(Field field:updateFieldList){
					String fieldName = field.getName();
					String fieldValue = getSingleParameter(parameterMap, fieldName);
					if(fieldValue!=null){
						Result fieldUpdateResult = update(usersession,updateModel,fieldName,fieldValue);
						updateModelResult.addResult(fieldUpdateResult);
					}
				}			
				if(updateModelResult.isSuccessful()){
					ModelResult saveModelResult = save(usersession,updateModel);
					if(saveModelResult.isSuccessful()){
						List<Button> availableButtons = new ArrayList<Button>();
						List<Action.Code> availableCodes = new ArrayList<Action.Code>();
						Result availableActionsResult = listAvailableActions(usersession, updateManager.getName(), updateModel.getIdString(),availableCodes);
						if(availableActionsResult.isSuccessful()){
							availableButtons = listAvailableButtonsFromAvailableActions(next,availableCodes);				
						}//might be unsuccessful but may be due to no available actions.
						next.setValue(Screen.MODEL,updateModel);
						next.setValue(Screen.MANAGER,updateManager);
						next.setValue(Screen.AVAILABLE_BUTTONS, availableButtons);
						result.objectValue(next);
						result.success();
					}else{
						result.addMessages(saveModelResult.getMessages());
					}
				}else{
					result.addMessages(updateModelResult.getMessages());
				}
				break;
			case DELETE:       
				//FIXME excessive copy and paste
				Manager deleteManager = (Manager)currentScreen.getValue(Screen.MANAGER);//TODO nullcheck
				Model deleteModel = (Model)currentScreen.getValue(Screen.MODEL);
				String deleteEntityId = deleteManager.getManagerIdString();
				if(deleteModel==null){
					log("deleteModel null.");
				}else{
					Result deleteModelResult = delete(usersession,deleteModel,"User deleting model id:"+deleteModel.getIdString());
					if(deleteModelResult.isSuccessful()){
						
						List<Model> postDeleteLookupModels = new ArrayList<Model>();
						result = lookupModels(usersession, deleteEntityId, postDeleteLookupModels);
						next.setValue(Screen.MODEL_LIST, postDeleteLookupModels);
						next.setValue(Screen.MANAGER,deleteManager);
						result.objectValue(next);
						
						//List<Button> availableButtons = new ArrayList<Button>();
						//List<Action.Code> availableCodes = new ArrayList<Action.Code>();
						//Result availableActionsResult = listAvailableActions(usersession, deleteManager.getName(), deleteModel.getIdString(),availableCodes);
						//if(availableActionsResult.isSuccessful()){
						//	availableButtons = listAvailableButtonsFromAvailableActions(next,availableCodes);				
						//}//might be unsuccessful but may be due to no available actions.
						//next.setValue(Screen.AVAILABLE_BUTTONS, availableButtons);
						result.success();
					}else{
						result.addMessages(deleteModelResult.getMessages());
					}
				}
				break;
			case GOIMPORT:
				break;
			case IMPORT:
				break;
			case ARCHIVE:
				break;
			case GOSEARCH:
				break;
			case HISTORY:
				break;

			case SEARCH:
				break;
			case EXPORT:
				break;
			/*
			case PUBLISH:
				break;
			case SHARE:
				break;
			case SUBMIT:
				break;
			case APPROVE:
				break;	
			 * 
			 * 
			//automatic in some cases or not allowed
			case UNPUBLISH:
				break;
			case UNSHARE:
				break;
			case UNSUBMIT:
				break;
			case UNAPPROVE:
				break;
			case REJECT:
				break;
			case UNREJECT:
				break;
			//automatic or unallowed.	
			case ASSIGN:
				break;
			case TRANSFER:
				break;
			case GOREGISTER:
				break;
			case REGISTER:
				break;
			*/
		    /*
			case BACK:
				break; 
			case LOGIN:
				break;
			case LOGOUT:
				break;
			*/					
			default:
				result.invalidInput("Command not found.");
				break;		
				
		}
		return result;
	}	
	
	@Override
	public Result findRole(String roleId) {
		Result r = new Result();
		Role role = new Role();
		String selectRoleSql = "SELECT * FROM "+Role.AUX_ROLE+" WHERE "
				+"ID = "+roleId+"  ";
		
		r = db.executeSingleSelectQuery(selectRoleSql, role);
		if(!role.isValid()){
			r.error("User object not valid.");
		}else{
			if(r.isSuccessful()){
				r.objectValue(role);
			}
		}
		return r;		
	}

	@Override
	public Result listRoles(UserSession userSession, List<Role> roleList) {//TODO see if this style of just returning desired object is good or not since most return result.
		log("listing roles.");
		Result r = new Result();
		if(userSession.isValid(r)){
			User user = userSession.getUser();
			StringBuffer sb = new StringBuffer(); //TODO put this in sql translator
			sb.append("SELECT R.ID, R.").append(Role.ROLEFLD);
			sb.append(" FROM ").append(Role.AUX_ROLE).append(" R ");
			sb.append(" INNER JOIN ").append(User.AUX_USER_ROLE+" UR ");
			sb.append(" ON UR.").append(Role.ROLE_ID).append("=R.ID ");
			sb.append(" WHERE UR.").append(Role.USER_ID);
			sb.append(" = ");
			sb.append(user.getId());
			sb.append(" ");
			String selectRolesSql = sb.toString();
			logsql(selectRolesSql);
			r = db.executeSelectRoles(selectRolesSql,roleList);
		}
		log("list size:"+roleList.size());
		return r;
	}	
	
	@Override
	public Result selectRole(Role role, UserSession loggedInSession) {
		log("selectRole");
		Result r = new Result();
		
		/*
		String selectRolesSql = "SELECT "+Role.MANAGER_ID+", "+Role.PRIV_ID
				+" FROM "+Role.AUX_ROLE_PRIV
				+" WHERE "+Role.ROLE_ID+" = "+role.getId()+" "; 
		logsql(selectRolesSql);
		
		//r = db.executeSelectRole(selectRolesSql,role);
		if(r.isSuccessful()){
		}
		*/
		String selectRolePrivsSql = "SELECT "+Role.MANAGER_ID+", "+Role.PRIV_ID
				+" FROM "+Role.AUX_ROLE_PRIV
				+" WHERE "+Role.ROLE_ID+" = "+role.getId()+" "; 
		logsql(selectRolePrivsSql);
		r = db.executeSelectRolePrivileges(application,selectRolePrivsSql,role);
		if(r.isSuccessful()){
			loggedInSession.setRole(role);
		}
		return r;
	}
	

	
	/*
	 * NOTE: tested
	 * 
	 * model state & action
	 *   &&
	 * priv & action
	 */
	@Override
	public Result listAvailableActions(UserSession session, String entity, List<Action.Code> fillList) {
		Result r = new Result();
		Manager manager = validateAndRetrieveManager(entity);
		Action.Code[] actionCodes = Action.Code.values();
		Privilege priv = session.getPrivilege(manager);
		for(Action.Code code:actionCodes){
			if( priv.has(code.getPrivilege()) ){
				fillList.add(code);
			}
		}
		r.success();
		return r;
	}
	
	private List<Screen.Button> listAvailableButtonsFromAvailableActions(Screen screen, List<Action.Code> actionCodes){
		//log("listAvailableButtonsFromAvailableActions("+screen+",List["+actionCodes.size()+"])");
		//log("Screen button values size:"+Screen.Button.values().length);
		
		List<Screen.Button> buttons = new ArrayList<Screen.Button>();
		for(Screen.Button button:Screen.Button.values()){
			Action.Code buttonCode = button.getActionCode();
			//log("  Button's action code "+buttonCode.name()+" in list :"+(actionCodes.contains(buttonCode)));
			//log("  Screen has the button:" + (screen.hasButton(button)) );
			if(actionCodes.contains(buttonCode) && screen.hasButton(button)){
				buttons.add(button);
			}
		}
		//log("Buttons size:"+buttons.size());
		return buttons;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * 1. first get the actions they can do with the entity.
	 * 
	 * 2. Next, look at the model's state.
	 * 
	 * 3. Filter this code list taking out codes where the model is not in a state that applies to the code's required state.
	 *   if the code is one of the read or writes, look at the maximum as well passing in the privilege
	 * 
	 * 
	 * 
	 * @see jhg.appman.run.ApplicationEngine#listAvailableActions(jhg.appman.run.UserSession, java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public Result listAvailableActions(UserSession session, String entity,String modelId, List<Action.Code> fillList) {
		log("listAvailableActions(session,entity:"+entity+",modelID:"+modelId+",List)");
		Result r = new Result();
		Manager manager = validateAndRetrieveManager(entity);
		Action.Code[] actionCodes = Action.Code.values();
		Privilege priv = session.getPrivilege(manager);
		Model foundModel = manager.getModel(Integer.valueOf(modelId));
		if(foundModel!=null){
			State modelState = foundModel.getState();
			//log("Model State("+entity+":"+modelId+"):"+modelState.name());
			for(Action.Code code:actionCodes){
				//TODO move this logic right into Code.
				if( priv.has(code.getPrivilege()) ){
					//log("  Code:"+code.name());
					//log("  User's Priv:"+priv.name());
					//log("  Model's State:"+modelState);
					if(priv.ordinal()>=Privilege.OWN.ordinal()){ //users privilege is 
						State _state = code.getOwnerRequired();
						//log("  Super's user's state required for action code:"+_state.name());
						//log("  code isRead:"+code.isRead());
						//log("  code isWrite:"+code.isWrite());
						//log("  code can lookup/read model state:"+code.canLookupRead(modelState, true));
						//log("  code can open/write model state:"+code.canWrite(modelState, true));
						if(State.EXISTING.equals(_state)){        //any model can have this done if the user has the priv to.
							fillList.add(code);
							//log("  FILLED for existing.");
						}else if(_state.equals(modelState)){
							fillList.add(code);
							//log("  FILLED for same code.");
						}else if(code.isRead() && code.canLookupRead(modelState,true)){
							fillList.add(code);
							//log("  FILLED for can read.");
						}else if(code.isWrite() && code.canWrite(modelState,true)){
							fillList.add(code);
							//log("  FILLED for can write.");
						}					
					}else{
						State _state = code.getDefaultRequired();
						//log("  User's state required for action code:"+_state.name());
						//log("  code isRead:"+code.isRead());
						//log("  code isWrite:"+code.isWrite());
						//log("  code can lookup/read model state:"+code.canLookupRead(modelState, true));
						//log("  code can open/write model state:"+code.canWrite(modelState, true));
						
						if(State.EXISTING.equals(_state)){
							fillList.add(code);
							//log("  FILLED for existing.");
						}else if(_state.equals(modelState)){
							fillList.add(code);
							//log("  FILLED for same code");
						}else if(code.isRead() && code.canLookupRead(modelState,false)){
							fillList.add(code);
							//log("  FILLED for can read");
						}else if(code.isWrite() && code.canWrite(modelState,false)){
							fillList.add(code);
							//log("  FILLED for can write");
						}//state conditions					
					}//if owner
				}//if have priv
			}//for
			r.success();
		}else{
			r.noResult("Model not found.");
		}
		return r;
	}		
	
	@Override
	public Result listEntities(UserSession session, List<Manager> managers) {
		String M = "listEntities";
		log(M);
		Result r = new Result();//NOT_SET
		Action action = new Action(Code.LISTENTITIES); //Note, make methods consistent.
		noNullParams(M,session,managers);
		   
		if(session.isValid(r)){
			List<Manager> appManagers = application.getManagers();
			for(Manager m:appManagers){
				Privilege priv = session.getPrivilege(m);
				if(priv.has(action.getPrivilege())){
					managers.add(m);
				}
			}
			r.success();
		}
		noNullReturns(M,r);
		return r;
	}	

	@SuppressWarnings("unchecked")
	@Override
	public ModelResult createModel(UserSession session, String entity, Map<String,String> reqFieldValues) {
		String M = "createModel";
		log(M);
		ModelResult mr = new ModelResult();//NOT_SET
		Action action = new Action(Code.CREATE);
		noNullParams(M,session,entity,reqFieldValues);
		minLength(M,1,reqFieldValues);
		checkStatus();
		
		try{
			validateSession(mr,session);                             //session
			Manager manager = validateAndRetrieveManager(entity);    //entity
			checkPrivileges(mr, action, session, manager);//the user can cause this so we use the mr
			//log("ModelResult successful:"+mr.isSuccessful());
			
			Model newModel = manager.makeModel();
			List<Field> _fields = manager.getFields();
			for(Field _f:_fields){
				String _fld = _f.getName();
				if(reqFieldValues.containsKey(_fld)){
					
					//for(String _fld:reqFieldValues.keySet()){// this has to be done in the order the manager uses
					//Field _f = manager.getField(_fld);  	notNull(M, "_f", _f);
					String _v = reqFieldValues.get(_fld);	notNull(M, "_v", _v);
					log("Setting field:"+_fld+" value:'"+_v+"'");
					if(_f instanceof ReferenceField){
						log("Reference field");
						ReferenceField _rf = (ReferenceField)_f;
						log("Reference field name:"+_rf.getLabel());
						Manager _rm = _rf.getReferencedManager();
						log("Reference field referenced manager:"+_rm.getLabel());
						log("Reference field ref string:"+_v);
						Integer _ref = Integer.valueOf(_v);//TODO double check this.
						log("Reference field ref integer:"+_ref);
						Model _rd = _rm.getModel(_ref);//TODO double check this too.
						log("Reference field found ref model identifying value:"+_rd.getIdentifyingValue());
						mr.addResult(newModel.setReference(_f,_rd));
					}else if(_f instanceof DateTimeField){
						log("DateTimeField");
						_v = DateTime.Util.convertDateFormat(HTML5_DATEFORMAT, DateTime.DEFAULT_TIMESTAMP_FORMAT, _v);
						mr.addResult(newModel.setValue(_f,_v));
					}else{
						mr.addResult(newModel.setValue(_f,_v));
					}
					 /*  FIXME fix the date
					 *  2013-11-08T19:09
					 *  VS my format used in testing: 2013-07-27 05:26:33.001-0400					
					 */
					checkSuccess(M,mr,"unable to set field "+_fld);
				}
			}

			manager.verifyUnique(newModel,mr);
			checkSuccess(M,mr,"model not unique.");
			boolean committed = false;
			User owner = session.getUser();
			Connection conn = db.openTransactionalConnection();
			try{
				//insert the record.
				newModel.setOwner(owner.getId());
				newModel.incrementVersion();
				newModel.setState(State.SAVED);
				String insertSql = SqlTranslator.createInsert(newModel);
				String identitySql = SqlTranslator.callIdentity();
				logsql(insertSql);
				logsql(identitySql);
				Result dbInsertResult = new Result();
				Result dbModelHistoryResult = new Result();
				dbInsertResult = db.executeInsert(conn, insertSql,identitySql);
				Role userRole = session.getCurrentRole();		         //checked
				if(dbInsertResult.isSuccessful()){
					Integer managerId = manager.getManagerId();
					Integer modelId = (Integer)dbInsertResult.objectValue();
					Integer changeId = newModel.getVersionId();
					Integer ownerId = owner.getId();	
					Integer roleId = userRole.getId();//TODO guard code
					
					action.init(managerId, modelId, changeId, ownerId, roleId);
					newModel.setId(modelId);
					//insert into model history.
					String modelHistorySql = SqlTranslator.createModelHistoryInsert(action);
					logsql(modelHistorySql);
					dbModelHistoryResult = db.executeInsert(conn,modelHistorySql,identitySql);
				}
				//commit or rollback.
				if(dbInsertResult.isSuccessful() && dbModelHistoryResult.isSuccessful()){
					Result commitResult = db.commit(conn);
					if(commitResult.notSuccessful()){
						mr.fail(ModelResult.Reason.STORE_FAILURE,"Commit failure.");
					}else{
						committed = true;//mr is already successful after verifying unique
					}
					log("Committing.");
				}else{
					mr.fail(ModelResult.Reason.STORE_FAILURE, "Insert and history transaction failed.");
					if(dbInsertResult.notSuccessful()){
						mr.addMessage("Insert failed. ");
						if(dbInsertResult.hasException()){
							mr.error(dbInsertResult.exception());
						}
					}
					if(dbModelHistoryResult.notSuccessful()){
						mr.addMessage("Model history insert failed. ");
						if(dbModelHistoryResult.hasException()){
							mr.error(dbModelHistoryResult.exception());
						}							
					}
					
					Result rollbackResult = db.rollback(conn);
					if(rollbackResult.notSuccessful()){
						mr.addMessage("Rollback unsuccessful. You may have to manually edit the database.");
						mr.error(rollbackResult.exception());
					}
					log("Rolling back.");
				}
			}catch(Exception e){
				e.printStackTrace();
				mr.error(e);
			}finally{
				db.closeTransactionalConnection(conn);
			}
			
			if(committed){
				newModel.setState(State.SAVED);
				mr.setModel(newModel);
				manager.addModel(newModel);
			}else{
				mr.fail(ModelResult.Reason.STORE_FAILURE, "Was unable to commit history or record.");
			}			
		}catch(ApplicationException ae){
			ae.printStackTrace();
			log(ae.getMessage());
		}
		return mr;
	}
	


	@SuppressWarnings("unchecked")
	@Override
	public ImportResults importModels(UserSession session, String entity,  List<String> csvDataLines) {
		log("importModels");
		ImportResults ir = new ImportResults();
		Action action = new Action(Code.IMPORT);
		ModelsResult msr = new ModelsResult();//NOT_SET
		try{
			validateSession(msr,session); 
			User user = session.getUser();
			Role role = session.getCurrentRole();
			Manager manager = validateAndRetrieveManager(entity);    //entity
			checkPrivileges(msr, action, session, manager);//the user can cause this so we use the mr
			Privilege priv = session.getPrivilege(manager);
			msr = manager.performImport(csvDataLines);
			List<ModelResult> modelResults = msr.getResults();
			for(ModelResult mr:modelResults){
				Model m = mr.getModel();
			
				if(mr.isSuccessful()){
					boolean committed = false;
					Connection conn = db.openTransactionalConnection();
					try{
						m.setOwner(user.getId());
						m.incrementVersion();
						m.setState(State.SAVED);						
						String insertSql = SqlTranslator.createInsert(m);
						String identitySql = SqlTranslator.callIdentity();
						logsql(insertSql);
						logsql(identitySql);
						Result dbInsertResult = new Result();
						Result dbModelHistoryResult = new Result();
						dbInsertResult = db.executeInsert(conn, insertSql,identitySql);
						Role userRole = session.getCurrentRole();		         //checked
						if(dbInsertResult.isSuccessful()){
							Integer managerId = manager.getManagerId();
							Integer modelId = (Integer)dbInsertResult.objectValue();
							Integer changeId = m.getVersionId();
							Integer ownerId = user.getId();	
							Integer roleId = userRole.getId();//TODO guard code
							
							action.init(managerId, modelId, changeId, ownerId, roleId);
							m.setId(modelId);
							//insert into model history.
							String modelHistorySql = SqlTranslator.createModelHistoryInsert(action);
							logsql(modelHistorySql);
							dbModelHistoryResult = db.executeInsert(conn,modelHistorySql,identitySql);
						}
						//commit or rollback.
						if(dbInsertResult.isSuccessful() && dbModelHistoryResult.isSuccessful()){
							Result commitResult = db.commit(conn);
							if(commitResult.notSuccessful()){
								mr.fail(ModelResult.Reason.STORE_FAILURE,"Commit failure.");
							}else{
								committed = true;//mr is already successful after verifying unique
							}
							log("Committing.");
						}else{
							mr.fail(ModelResult.Reason.STORE_FAILURE, "Insert and history transaction failed.");
							if(dbInsertResult.notSuccessful()){
								mr.addMessage("Insert failed. ");
								if(dbInsertResult.hasException()){
									mr.error(dbInsertResult.exception());
								}
							}
							if(dbModelHistoryResult.notSuccessful()){
								mr.addMessage("Model history insert failed. ");
								if(dbModelHistoryResult.hasException()){
									mr.error(dbModelHistoryResult.exception());
								}							
							}
							Result rollbackResult = db.rollback(conn);
							if(rollbackResult.notSuccessful()){
								mr.addMessage("Rollback unsuccessful. You may have to manually edit the database.");
								mr.error(rollbackResult.exception());
							}
							log("Rolling back.");
						}
					
					}catch(Exception e){
						mr.error(e);
					}finally{
						db.closeTransactionalConnection(conn);
					}
					
					if(committed){
						m.setState(State.SAVED);
						mr.setModel(m);
						manager.addModel(m);
						ir.addValidModel(m);
					}else{
						mr.fail(ModelResult.Reason.STORE_FAILURE, "Was unable to commit history or record.");
					}						
					
					
				}else{
					ir.addInvalidModelResult(mr);
				}
			}
		}catch(ApplicationException ae){
			//ae.printStackTrace();
			log(ae.getMessage());
		}
		return ir;
	}
	
	@Override
	public Result lookupModels(UserSession session, String entityIdS, List<Model> models) {
		String M = "lookupModels";
		log(M);
		Result r = new Result();//NOT_SET
		Action action = new Action(Code.LOOKUP);
		noNullParams(M,session,entityIdS,models);
		verifyInteger(M,entityIdS);
		Integer entityId = Integer.parseInt(entityIdS);
		try{
			validateSession(r,session);
			Manager manager = validateAndRetrieveManager(entityId);
			checkPrivileges(r, action, session, manager);
			Map<Integer,Model> managerModels = manager.getModels();
			boolean isOwnerOrHigher = isOwnerOrHigher(session,manager);
			for(Model model:managerModels.values()){
				State modelState = model.getState();
				Code code = action.getCode();
				if(code.canLookupRead(modelState,isOwnerOrHigher)){
					models.add(model);
				}
			}
			r.success();
		}catch(ApplicationException ae){
			log(ae.getMessage());
		}
		noNullReturns(M,r);
		return r;
	}	
	
	//RESUME implement sort and filter.(page result)
	//RESUME implement a memory count to database count verifier.


	@Override
	public ModelsResult search(UserSession session, String entity, String findMethodName, String[] params){
			log("search");
			//RESUME implement function search
			Action action = new Action(Code.SEARCH);
			ModelsResult mr = new ModelsResult();//NOT_SET
			//TODO impl
			return mr;
	}	
	
	
	@Override
	public ModelResult viewModel(UserSession session, String entity, String id) {
		String M = "viewModel";
		log(M);
		Action action = new Action(Code.VIEW);
		ModelResult mr = new ModelResult();//starts out successful.
		noNullParams(M,session,entity,id);
		verifyInteger(M,id);
		try{
			validateSession(mr,session);
			Manager manager = validateAndRetrieveManager(entity);
			checkPrivileges(mr, action, session, manager);
			Model model = manager.getModel(Integer.parseInt(id));
			if(model==null){
				mr.fail(ModelResult.Reason.NOT_FOUND, "Model with id:"+id+" was not found.");
			}
			boolean isOwnerOrHigher = isOwnerOrHigher(session,manager);
			State modelState = model.getState();
			Code code = action.getCode();
			if(code.canLookupRead(modelState,isOwnerOrHigher)){
				mr.setModel(model);
			}else{
				mr.fail(ModelResult.Reason.NO_ACCESS,"Model "+id+" cannot be seen.");
			}
		}catch(ApplicationException ae){
			log(ae.getMessage());
		}
		noNullReturns(M,mr);
		return mr;		
	}
	
	
	@Override
	public Result openModel(UserSession session, Model model) {
		String M = "openModel";
		log(M);
		Action action = new Action(Code.OPEN);
		Result r = new Result();//NOT_SET
		noNullParams(M,session,model);
		log("openModel:no null params");
		try{
			validateSession(r,session);
			log("openModel:validated session");
			Manager manager = model.getManager();
			checkPrivileges(r, action, session, manager);
			boolean isOwnerOrHigher = isOwnerOrHigher(session,manager);
			State modelState = model.getState();
			Code code = action.getCode();
			log("openModel:checked privs. got code. isOwnerOrHigher:"+isOwnerOrHigher);
			if(code.canWrite(modelState,isOwnerOrHigher)){
				log("openModel:code can write");
				if(!model.isReserved()){//TODO have this recheck the status.
					log("openModel:model not reserved but session user is not the model owner and model state is less than shared");
					log(M+":session.getUser().getId():"+session.getUser().getId());
					log(M+":model.getOwnerId():"+model.getOwnerId());
					log(M+":modelState.ordinal():"+modelState.ordinal());
					log(M+":State.SHARED.ordinal():"+State.SHARED.ordinal());
							
					if(!session.getUser().getId().equals(model.getOwnerId()) && modelState.ordinal()<State.SHARED.ordinal()){
						log("openModel:someone else has opened");
						r.noResult("This model is owned by another and not shared yet.");
					}else{
						log("openModel:reserving.");
						model.reserve(session.getUser().getId());//TODO add guard code?
						r.success();
					}
					
				}else{
					log("openModel:model is reserved.");
					Date inUseSince = model.getInUseSinceDate();
					checkNull(M, "inUseDate", inUseSince);
					r.noResult("This model is in use by user id:"+model.getInUseBy()+" until "+model.lockExpires()+"");
				}
			}else{
				log("openModel:code "+code.name()+" cannot write model in state "+modelState);
				r.noResult("You do not have access to open this model in it's current state");
			}
		}catch(ApplicationException ae){
			log("openModel:Application exception caught.");
			if(ae.getMessage()!=null){
				log(ae.getMessage());
			}else{
				log("openModel:printing stacktrace.");
				ae.printStackTrace();
			}
		}
		noNullReturns(M,r);
		log("openModel:no null returns");
		return r;
	}	

	@Override
	public Result closeModel(UserSession session, Model model) {
		String M = "closeModel";
		log(M);
		Action action = new Action(Code.CLOSE);
		Result r = new Result();//NOT_SET
		noNullParams(M,session,model);
		try{
			validateSession(r,session);
			Manager manager = model.getManager();
			checkPrivileges(r, action, session, manager);
			boolean isOwnerOrHigher = isOwnerOrHigher(session,manager);
			State modelState = model.getState();
			Code code = action.getCode();
			if(code.canWrite(modelState,isOwnerOrHigher)){
				if(model.isReserved()){
					model.rollBack();
					model.unreserve();//TODO add
					r.success();
				}else{
					r.noResult("This model is not reserved.");
				}
			}else{
				r.noResult("You do not have access to close this model.");//NOTE should never happen
			}
		}catch(ApplicationException ae){
			if(ae.getMessage()!=null){
				log(ae.getMessage());
			}else{
				ae.printStackTrace();
			}
		}
		noNullReturns(M,r);
		return r;
	}	


	@SuppressWarnings("unchecked")
	@Override
	public Result update(UserSession session, Model model, String fieldName, String value) {
		
		String M = "update";
		log(M);
		Action action = new Action(Code.UPDATE);
		Result r = new Result();//NOT_SET
		noNullParams(M,session,model);
		try{
			validateSession(r,session);
			Manager manager = model.getManager();
			Privilege priv = session.getPrivilege(manager);
			checkPrivileges(r,action,session,manager);
			boolean isOwnerOrHigher = isOwnerOrHigher(session,manager);
			State modelState = model.getState();
			Code code = action.getCode();
			if(code.canWrite(modelState,isOwnerOrHigher)){
				if(model.isReserved()){
					r = model.setValue(fieldName,value);
					r.success();
					//manager.verifyUnique(model,r);
				}else{
					r.fail(Reason.NOT_RESERVED, "This model is not reserved.");
				}
			}else{
				r.fail(Reason.NO_ACCESS, "Cannot update model "+model.getId()+" in it's current state:"+model.getState().name()+" with privilege "+priv.name());
				//r.notAuthorized(session.getUser(),);
				//TODO Add this kind of no access feature, have model and state be params
			}
		}catch(ApplicationException ae){
			if(ae.getMessage()!=null){
				log(ae.getMessage());
			}else{
				ae.printStackTrace();
			}
		}
		noNullReturns(M,r);
		return r;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ModelResult save(UserSession session, Model model) {
		String M = "save";
		log(M);
		Action action = new Action(Code.UPDATE);
		ModelResult mr = new ModelResult();//NOT_SET
		noNullParams(M,session,model);
		try{
			validateSession(mr,session);
			Manager manager = model.getManager();
			Privilege priv = session.getPrivilege(manager);
			checkPrivileges(mr, action, session, manager);
			boolean isOwnerOrHigher = isOwnerOrHigher(session,manager);
			State modelState = model.getState();
			Code code = action.getCode();//TODO use the new checkState methods (all this the the methods above this)
			if(code.canWrite(modelState,isOwnerOrHigher)){//should also check getDefault/owner
				if(model.isReserved()){
					manager.verifyUnique(model,mr);
					checkSuccess(M,mr,"model not unique.");
					boolean committed = false;
					User writer = session.getUser();//User owner = session.getUser();
					Connection conn = db.openTransactionalConnection();
					try{
						
						//update the record
						model.incrementVersion();
						String updateSql = SqlTranslator.createUpdate(model);
						logsql(updateSql);
						Result dbUpdateResult = new Result();
						Result dbModelHistoryResult = new Result();
						dbUpdateResult = db.executeUpdate(conn, updateSql);
						Role userRole = session.getCurrentRole();		         //checked
						if(dbUpdateResult.isSuccessful()){
							
							Integer managerId = manager.getManagerId();
							Integer modelId = model.getId();
							Integer changeId = model.getVersionId();
							Integer writerId = writer.getId();	
							Integer roleId = userRole.getId();//TODO guard code
							action.init(managerId, modelId, changeId, writerId, roleId);
							//insert into model history.
							String modelHistorySql = SqlTranslator.createModelHistoryInsert(action);
							String identitySql = SqlTranslator.callIdentity();
							logsql(modelHistorySql);
							dbModelHistoryResult = db.executeInsert(conn,modelHistorySql,identitySql);
						}
						//commit or rollback.
						if(dbUpdateResult.isSuccessful() && dbModelHistoryResult.isSuccessful()){
							Result commitResult = db.commit(conn);
							if(commitResult.notSuccessful()){
								mr.fail(ModelResult.Reason.STORE_FAILURE,"Commit failure.");
							}else{
								committed = true;//mr is already successful after verifying unique
							}
							log("Committing.");
						}else{
							mr.fail(ModelResult.Reason.STORE_FAILURE, "Update and/or history transaction failed.");
							if(dbUpdateResult.notSuccessful()){
								mr.addMessage("Update failed. ");
								if(dbUpdateResult.hasException()){
									mr.error(dbUpdateResult.exception());
								}
							}
							if(dbModelHistoryResult.notSuccessful()){
								mr.addMessage("Model history insert failed. ");
								if(dbModelHistoryResult.hasException()){
									mr.error(dbModelHistoryResult.exception());
								}							
							}
							
							Result rollbackResult = db.rollback(conn);
							if(rollbackResult.notSuccessful()){
								mr.addMessage("Rollback unsuccessful. You may have to manually edit the database.");
								mr.error(rollbackResult.exception());
							}
							log("Rolling back.");
						}						
					}catch(Exception e){
						model.decrementVersion();
						mr.error(e);//NOTE: we keep open and do not roll back.
					}finally{
						db.closeTransactionalConnection(conn);
					}
					
					if(committed){
						//model.setState(State.SAVED); no change
						mr.setModel(model);
						model.save();
						model.unreserve();
						//manager.addModel(newModel);
					}else{
						mr.fail(ModelResult.Reason.STORE_FAILURE, "Was unable to commit history or record.");
					}							
					
				}else{
					mr.fail(ModelResult.Reason.NOT_RESERVED,"This model is not reserved.");
				}
			}else{
				mr.fail(ModelResult.Reason.NO_ACCESS, "Cannot update model "+model.getId()+" in it's current state:"+model.getState().name()+" with privilege "+priv.name());
			}
		}catch(ApplicationException ae){
			if(ae.getMessage()!=null){
				log(ae.getMessage());
			}else{
				ae.printStackTrace();
			}
		}
		noNullReturns(M,mr);
		return mr;
	}
	
	@Override
	public Result publish(UserSession session, Model model) {
		String M = "publish";
		log(M);
		Action action = new Action(Code.PUBLISH);
		Result r = new Result();//NOT_SET
		noNullParams(M,session,model);
		updateState(session, model, action, r, null, null);		
		noNullReturns(M,r);
		return r;
	}

	@Override
	public Result unpublish(UserSession session, Model model) {
		String M = "unpublish";//$1
		log(M);
		Action action = new Action(Code.UNPUBLISH);//$2
		Result r = new Result();
		noNullParams(M,session,model);
		updateState(session, model, action, r, null, null);		
		noNullReturns(M,r);
		return r;
	}

	@Override
	public Result share(UserSession session, Model model) {
		String M = "share";//$1
		log(M);
		Action action = new Action(Code.SHARE);//$2
		Result r = new Result();
		noNullParams(M,session,model);
		updateState(session, model, action, r, null, null);		
		noNullReturns(M,r);
		return r;
	}	

	@Override
	public Result unshare(UserSession session, Model model) {
		String M = "unshare";//$1
		log(M);
		Action action = new Action(Code.UNSHARE);//$2
		Result r = new Result();
		noNullParams(M,session,model);
		updateState(session, model, action, r, null, null);		
		noNullReturns(M,r);
		return r;
	}

	@Override
	public Result submit(UserSession session, Model model,	String comment) {
		String M = "submit";//$1
		log(M);
		Action action = new Action(Code.SUBMIT);//$2
		Result r = new Result();
		noNullParams(M,session,model);
		updateState(session, model, action, r, comment, null);		
		noNullReturns(M,r);
		return r;
	}

	@Override
	public Result unsubmit(UserSession session, Model model, String comment) {
		String M = "unsubmit";//$1
		log(M);
		Action action = new Action(Code.UNSUBMIT);//$2
		Result r = new Result();
		noNullParams(M,session,model);
		updateState(session, model, action, r, comment, null);		
		noNullReturns(M,r);
		return r;
	}
	
	@Override
	public Result approve(UserSession session, Model model, String signature, String comment){
		String M = "approve";//$1
		log(M);
		Action action = new Action(Code.APPROVE);//$2
		Result r = new Result();
		noNullParams(M,session,model);
		updateState(session, model, action, r, comment, signature);		
		noNullReturns(M,r);
		return r;
	}
	
	@Override
	public Result unapprove(UserSession session, Model model, String signature, String comment){
		String M = "unapprove";//$1
		log(M);
		Action action = new Action(Code.UNAPPROVE);//$2
		Result r = new Result();
		noNullParams(M,session,model);
		updateState(session, model, action, r, comment, signature);		
		noNullReturns(M,r);
		return r;
	}	
	
	@Override
	public Result reject(UserSession session, Model model, String signature, String comment){
		String M = "reject";//$1
		log(M);
		Action action = new Action(Code.REJECT);//$2
		Result r = new Result();
		noNullParams(M,session,model);
		updateState(session, model, action, r, comment, signature);		
		noNullReturns(M,r);
		return r;
	}
	
	@Override
	public Result unreject(UserSession session, Model model, String signature, String comment){
		String M = "unreject";//$1
		log(M);
		Action action = new Action(Code.UNREJECT);//$2
		Result r = new Result();
		noNullParams(M,session,model);
		updateState(session, model, action, r, comment, signature);		
		noNullReturns(M,r);
		return r;
	}
		
	@Override
	public Result transferOwner(UserSession session, User newOwner, Model model){
		String M = "transferOwner";
		log(M);
		Action action = new Action(Code.TRANSFER_OWNER);//$2
		Result r = new Result();
		noNullParams(M,session,model);
		try{
			validateSession(r,session);
			User user = session.getUser();
			Role role = session.getCurrentRole();
			Manager manager = model.getManager();
			Privilege priv = session.getPrivilege(manager);
			//State existingState = model.getState();
			checkPrivileges(r, action, session, manager);
			checkState(r,user,role,priv,action,model);
			checkModelNotReserved(r,model);
			boolean committed = false;
			Connection conn = db.openTransactionalConnection();
			try{
				model.incrementVersion();//during fail, decrement
				//model.setState(action.getCode().getCompletedState());
				String updateSql = SqlTranslator.createOwnerUpdate(model);
				logsql(updateSql);
				Result dbUpdateResult = new Result();
				Result dbModelHistoryResult = new Result();				
				dbUpdateResult = db.executeUpdate(conn, updateSql);
				if(dbUpdateResult.isSuccessful()){
					Integer managerId = manager.getManagerId();
					Integer modelId = model.getId();
					Integer changeId = model.getVersionId();
					Integer userId = user.getId();	
					Integer roleId = role.getId();
					action.init(managerId, modelId, changeId, userId, roleId);
					String modelHistorySql = SqlTranslator.createModelHistoryInsert(action);
					String identitySql = SqlTranslator.callIdentity();
					logsql(modelHistorySql);
					dbModelHistoryResult = db.executeInsert(conn,modelHistorySql,identitySql);
				}
				//commit or rollback.
				if(dbUpdateResult.isSuccessful() && dbModelHistoryResult.isSuccessful()){
					Result commitResult = db.commit(conn);
					if(commitResult.notSuccessful()){
						r.noResult(action.getCode().name()+" could not commit.");
						//rare but should we then try to rollback or what should happen if we don't commit? find out.
					}else{
						committed = true;
						log("Committing.");
					}
				}else{
					r.noResult(action.getCode().name()+" update and or history were unable to save to data storage.");
					if(dbUpdateResult.notSuccessful()){
						r.addMessage("Update of model failed. ");//TODO add actions/more details to all these.
						if(dbUpdateResult.hasException()){
							r.error(dbUpdateResult.exception());
						}
					}
					if(dbModelHistoryResult.notSuccessful()){
						r.addMessage("Model history insert failed. ");
						if(dbModelHistoryResult.hasException()){
							r.error(dbModelHistoryResult.exception());
						}							
					}
					Result rollbackResult = db.rollback(conn);
					if(rollbackResult.notSuccessful()){
						r.addMessage("Rollback unsuccessful. You may have to manually edit the database.");
						r.error(rollbackResult.exception());
					}else{
						log("Rolling back.");
					}
				}						
			}catch(Exception e){
				model.decrementVersion();
				//model.setState(existingState);
				r.error(e);
			}finally{
				db.closeTransactionalConnection(conn);
			}
			if(committed){
				r.success();
			}							
		}catch(ApplicationException ae){//we only throw these when results have been updated.
			//we can also add more detail to logging.
			if(ae.getMessage()!=null){
				log(ae.getMessage());
			}else{
				ae.printStackTrace();
			}
		}	
		noNullReturns(M,r);
		return r;
	}
	
	@Override
	public Result assignApprover(UserSession session, User approver, Model model) {
		String M = "assignApprover";//$1
		log(M);
		Action action = new Action(Code.ASSIGN_APPROVER);//$2
		Result r = new Result();
		noNullParams(M,session,model);
		try{
			validateSession(r,session);
			User user = session.getUser();
			Role role = session.getCurrentRole();
			Manager manager = model.getManager();
			Privilege priv = session.getPrivilege(manager);
			//State existingState = model.getState();
			checkPrivileges(r, action, session, manager);
			checkState(r,user,role,priv,action,model);
			checkModelNotReserved(r,model);
			boolean committed = false;
			Connection conn = db.openTransactionalConnection();
			try{
				model.incrementVersion();//during fail, decrement
				//model.setState(action.getCode().getCompletedState());
				String updateSql = SqlTranslator.createApproverUpdate(model);//$3
				logsql(updateSql);
				Result dbUpdateResult = new Result();
				Result dbModelHistoryResult = new Result();				
				dbUpdateResult = db.executeUpdate(conn, updateSql);
				if(dbUpdateResult.isSuccessful()){
					Integer managerId = manager.getManagerId();
					Integer modelId = model.getId();
					Integer changeId = model.getVersionId();
					Integer userId = user.getId();	
					Integer roleId = role.getId();
					action.init(managerId, modelId, changeId, userId, roleId);
					String modelHistorySql = SqlTranslator.createModelHistoryInsert(action);
					String identitySql = SqlTranslator.callIdentity();
					logsql(modelHistorySql);
					dbModelHistoryResult = db.executeInsert(conn,modelHistorySql,identitySql);
				}
				//commit or rollback.
				if(dbUpdateResult.isSuccessful() && dbModelHistoryResult.isSuccessful()){
					Result commitResult = db.commit(conn);
					if(commitResult.notSuccessful()){
						r.noResult(action.getCode().name()+" could not commit.");
						//rare but should we then try to rollback or what should happen if we don't commit? find out.
					}else{
						committed = true;
						log("Committing.");
					}
				}else{
					r.noResult(action.getCode().name()+" update and or history were unable to save to data storage.");
					if(dbUpdateResult.notSuccessful()){
						r.addMessage("Update of model failed. ");//TODO add actions/more details to all these.
						if(dbUpdateResult.hasException()){
							r.error(dbUpdateResult.exception());
						}
					}
					if(dbModelHistoryResult.notSuccessful()){
						r.addMessage("Model history insert failed. ");
						if(dbModelHistoryResult.hasException()){
							r.error(dbModelHistoryResult.exception());
						}							
					}
					Result rollbackResult = db.rollback(conn);
					if(rollbackResult.notSuccessful()){
						r.addMessage("Rollback unsuccessful. You may have to manually edit the database.");
						r.error(rollbackResult.exception());
					}else{
						log("Rolling back.");
					}
				}						
			}catch(Exception e){
				model.decrementVersion();
				//model.setState(existingState);
				r.error(e);
			}finally{
				db.closeTransactionalConnection(conn);
			}
			if(committed){
				r.success();
			}							
		}catch(ApplicationException ae){//we only throw these when results have been updated.
			//we can also add more detail to logging.
			if(ae.getMessage()!=null){
				log(ae.getMessage());
			}else{
				ae.printStackTrace();
			}
		}	
		noNullReturns(M,r);
		return r;
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	public Result export(UserSession session, String entity, StringBuffer buffer) {
		String M = "export";//$1
		log(M);
		Action action = new Action(Code.EXPORT);//$2
		Result r = new Result();
		noNullParams(M,session,entity);
		Manager manager = validateAndRetrieveManager(entity);
		buffer.append(manager.toCsvHeader());//TODO consider a real csv library (the one I downloaded)
		Collection<Model> exportable = manager.getModels().values();
		for(Model model : exportable){
			if(model.getState().equals(State.APPROVED) || model.getState().equals(State.REJECTED)){
				buffer.append(model.toCsv());
				try{
					validateSession(r,session);
					User user = session.getUser();
					Role role = session.getCurrentRole();
					Privilege priv = session.getPrivilege(manager);
					State existingState = model.getState();      
					checkPrivileges(r, action, session, manager);
					checkState(r,user,role,priv,action,model);
					checkModelNotReserved(r,model);
					boolean committed = false;
					Connection conn = db.openTransactionalConnection();
					try{
						model.incrementVersion();
						model.setState(action.getCode().getCompletedState());
						String updateSql = SqlTranslator.createStateVersionUpdate(model);
						logsql(updateSql);
						Result dbUpdateResult = new Result();
						Result dbModelHistoryResult = new Result();				
						dbUpdateResult = db.executeUpdate(conn, updateSql);
						if(dbUpdateResult.isSuccessful()){
							Integer managerId = manager.getManagerId();
							Integer modelId = model.getId();
							Integer changeId = model.getVersionId();
							Integer userId = user.getId();	
							Integer roleId = role.getId();
							action.init(managerId, modelId, changeId, userId, roleId);
							String modelHistorySql = SqlTranslator.createModelHistoryInsert(action);
							String identitySql = SqlTranslator.callIdentity();
							logsql(modelHistorySql);
							dbModelHistoryResult = db.executeInsert(conn,modelHistorySql,identitySql);
						}
						//commit or rollback.
						if(dbUpdateResult.isSuccessful() && dbModelHistoryResult.isSuccessful()){
							Result commitResult = db.commit(conn);
							if(commitResult.notSuccessful()){
								r.noResult(action.getCode().name()+" could not commit.");
								//rare but should we then try to rollback or what should happen if we don't commit? find out.
							}else{
								committed = true;
								log("Committing.");
							}
						}else{
							r.noResult(action.getCode().name()+" update and or history were unable to save to data storage.");
							if(dbUpdateResult.notSuccessful()){
								r.addMessage("Update of model failed. ");//TODO add actions/more details to all these.
								if(dbUpdateResult.hasException()){
									r.error(dbUpdateResult.exception());
								}
							}
							if(dbModelHistoryResult.notSuccessful()){
								r.addMessage("Model history insert failed. ");
								if(dbModelHistoryResult.hasException()){
									r.error(dbModelHistoryResult.exception());
								}							
							}
							Result rollbackResult = db.rollback(conn);
							if(rollbackResult.notSuccessful()){
								r.addMessage("Rollback unsuccessful. You may have to manually edit the database.");
								r.error(rollbackResult.exception());
							}else{
								log("Rolling back.");
							}
						}						
					}catch(Exception e){
						model.decrementVersion();
						model.setState(existingState);
						r.error(e);
					}finally{
						db.closeTransactionalConnection(conn);
					}
					if(committed){
						r.success();
					}							
				}catch(ApplicationException ae){//we only throw these when results have been updated.
					//we can also add more detail to logging.
					if(ae.getMessage()!=null){
						log(ae.getMessage());
					}else{
						ae.printStackTrace();
					}
				}
				//main logic.
			}//if rejected or approved
		}//for each model.
		
		noNullReturns(M,r);
		return r;
	}

	@Override
	public Result delete(UserSession session, Model model, String comment) {
		String M = "delete";//$1
		log(M);
		Action action = new Action(Code.DELETE);//$2
		Result r = new Result();
		noNullParams(M,session,model,comment);
		try{
			validateSession(r,session);
			User user = session.getUser();
			Role role = session.getCurrentRole();
			Manager manager = model.getManager();
			Privilege priv = session.getPrivilege(manager);
			State existingState = model.getState();      
			checkPrivileges(r, action, session, manager);
			checkState(r,user,role,priv,action,model);
			checkModelNotReserved(r,model);
			boolean committed = false;
			Connection conn = db.openTransactionalConnection();
			try{
				//still necessary during deletion in case of failures and for the model history.
				model.incrementVersion();//during fail, decrement
				model.setState(action.getCode().getCompletedState());
				
				String updateSql = SqlTranslator.createDelete(model);//vacuum? NOTE
				
				logsql(updateSql);
				Result dbUpdateResult = new Result();
				Result dbModelHistoryResult = new Result();				
				dbUpdateResult = db.executeUpdate(conn, updateSql);
				if(dbUpdateResult.isSuccessful()){
					Integer managerId = manager.getManagerId();
					Integer modelId = model.getId();
					Integer changeId = model.getVersionId();
					Integer userId = user.getId();	
					Integer roleId = role.getId();
					action.init(managerId, modelId, changeId, userId, roleId);
					if(comment!=null){
						action.comment(comment);
					}
					String modelHistorySql = SqlTranslator.createModelHistoryInsert(action);
					String identitySql = SqlTranslator.callIdentity();
					logsql(modelHistorySql);
					dbModelHistoryResult = db.executeInsert(conn,modelHistorySql,identitySql);
				}
				//commit or rollback.
				if(dbUpdateResult.isSuccessful() && dbModelHistoryResult.isSuccessful()){
					Result commitResult = db.commit(conn);
					if(commitResult.notSuccessful()){
						r.noResult(action.getCode().name()+" could not commit.");
						//rare but should we then try to rollback or what should happen if we don't commit? find out.
					}else{
						committed = true;
						log("Committing.");
					}
				}else{
					r.noResult(action.getCode().name()+" update and or history were unable to save to data storage.");
					if(dbUpdateResult.notSuccessful()){
						r.addMessage("Update of model failed. ");//TODO add actions/more details to all these.
						if(dbUpdateResult.hasException()){
							r.error(dbUpdateResult.exception());
						}
					}
					if(dbModelHistoryResult.notSuccessful()){
						r.addMessage("Model history insert failed. ");
						if(dbModelHistoryResult.hasException()){
							r.error(dbModelHistoryResult.exception());
						}							
					}
					Result rollbackResult = db.rollback(conn);
					if(rollbackResult.notSuccessful()){
						r.addMessage("Rollback unsuccessful. You may have to manually edit the database.");
						r.error(rollbackResult.exception());
					}else{
						log("Rolling back.");
					}
				}						
			}catch(Exception e){
				model.decrementVersion();
				model.setState(existingState);
				r.error(e);
			}finally{
				db.closeTransactionalConnection(conn);
			}
			if(committed){
				manager.removeModel(model);
				r.success();
			}							
		}catch(ApplicationException ae){//we only throw these when results have been updated.
			//we can also add more detail to logging.
			if(ae.getMessage()!=null){
				log(ae.getMessage());
			}else{
				ae.printStackTrace();
			}
		}
		
		noNullReturns(M,r);
		return r;
	}

	@Override
	public Result archive(UserSession session, Model model, String comment) {
		String M = "archive";//$1
		log(M);
		Action action = new Action(Code.ARCHIVE);//$2
		Result r = new Result();
		noNullParams(M,session,model,comment);
		try{
			validateSession(r,session);
			User user = session.getUser();
			Role role = session.getCurrentRole();
			Manager manager = model.getManager();
			Privilege priv = session.getPrivilege(manager);
			State existingState = model.getState();      
			checkPrivileges(r, action, session, manager);
			checkState(r,user,role,priv,action,model);
			checkModelNotReserved(r,model);
			boolean committed = false;
			Connection conn = db.openTransactionalConnection();
			try{
				//still necessary during deletion in case of failures and for the model history.
				model.incrementVersion();//during fail, decrement
				model.setState(action.getCode().getCompletedState());
				
				String updateSql = SqlTranslator.createDelete(model);//vacuum? NOTE
				
				logsql(updateSql);
				Result dbUpdateResult = new Result();
				Result dbModelHistoryResult = new Result();				
				dbUpdateResult = db.executeUpdate(conn, updateSql);
				if(dbUpdateResult.isSuccessful()){
					Integer managerId = manager.getManagerId();
					Integer modelId = model.getId();
					Integer changeId = model.getVersionId();
					Integer userId = user.getId();	
					Integer roleId = role.getId();
					action.init(managerId, modelId, changeId, userId, roleId);
					if(comment!=null){
						action.comment(comment);
					}
					String modelHistorySql = SqlTranslator.createModelHistoryInsert(action);
					String identitySql = SqlTranslator.callIdentity();
					logsql(modelHistorySql);
					dbModelHistoryResult = db.executeInsert(conn,modelHistorySql,identitySql);
				}
				//commit or rollback.
				if(dbUpdateResult.isSuccessful() && dbModelHistoryResult.isSuccessful()){
					Result commitResult = db.commit(conn);
					if(commitResult.notSuccessful()){
						r.noResult(action.getCode().name()+" could not commit.");
						//rare but should we then try to rollback or what should happen if we don't commit? find out.
					}else{
						committed = true;
						log("Committing.");
					}
				}else{
					r.noResult(action.getCode().name()+" update and or history were unable to save to data storage.");
					if(dbUpdateResult.notSuccessful()){
						r.addMessage("Update of model failed. ");//TODO add actions/more details to all these.
						if(dbUpdateResult.hasException()){
							r.error(dbUpdateResult.exception());
						}
					}
					if(dbModelHistoryResult.notSuccessful()){
						r.addMessage("Model history insert failed. ");
						if(dbModelHistoryResult.hasException()){
							r.error(dbModelHistoryResult.exception());
						}							
					}
					Result rollbackResult = db.rollback(conn);
					if(rollbackResult.notSuccessful()){
						r.addMessage("Rollback unsuccessful. You may have to manually edit the database.");
						r.error(rollbackResult.exception());
					}else{
						log("Rolling back.");
					}
				}						
			}catch(Exception e){
				model.decrementVersion();
				model.setState(existingState);
				r.error(e);
			}finally{
				db.closeTransactionalConnection(conn);
			}
			if(committed){
				manager.removeModel(model);
				r.success();
			}							
		}catch(ApplicationException ae){//we only throw these when results have been updated.
			//we can also add more detail to logging.
			if(ae.getMessage()!=null){
				log(ae.getMessage());
			}else{
				ae.printStackTrace();
			}
		}
		
		noNullReturns(M,r);
		return r;
	}	
	
	@Override
	public Result getHistory(UserSession session, Model model, List<ModelHistory> historyToFill) {
		String M = "getHistory";//$1
		log(M);
		Action action = new Action(Code.VIEW_HISTORY);//$2
		Result r = new Result();
		noNullParams(M,session,model);
		try{
			validateSession(r,session);
			User user = session.getUser();
			Role role = session.getCurrentRole();
			Manager manager = model.getManager();
			Privilege priv = session.getPrivilege(manager);
			checkPrivileges(r, action, session, manager);
			String selectModelHistorySql = SqlTranslator.createSelectModelHistory(model);
			logsql(selectModelHistorySql);
			r = db.executeSelectModelHistories(selectModelHistorySql,model,historyToFill);
			r.success();
		}catch(ApplicationException ae){//we only throw these when results have been updated.
			//we can also add more detail to logging.
			if(ae.getMessage()!=null){  //TODO review all these for consistent handling.
				log(ae.getMessage());
			}else{
				ae.printStackTrace();
			}
		}		
		noNullReturns(M,r);
		return r;
	}

	@Override
	public Result customOperation(UserSession session, String entity, String id, String findMethodName, String[] params) {
		log("customOperation");
		//Action action = new Action(Code.???);
		Result r = new Result();//NOT_SET
		//TODO impl
		return r;
	}		
	
	@Override
	public void log(String s){
		String msg = "ApplicationEngine: "+s;
		super.log(msg);
	}
	
	@Override
	public Result register(String user, String passwd, String email){
		// TODO Auto-generated method stub
		return null;		
	}

	@Override
	public Result listManagers(UserSession session, List<Manager> managers){
		log("listManagers");
		Result r = new Result();
		try{
			validateSession(r,session);
			User user = session.getUser();
			Role role = session.getCurrentRole();
			managers.addAll(application.getManagers());
			r.success();
			log("Managers found:"+managers.size());
		}catch(ApplicationException ae){//we only throw these when results have been updated.
			//we can also add more detail to logging.
			if(ae.getMessage()!=null){
				log(ae.getMessage());
			}else{
				ae.printStackTrace();
			}
			log("Failed to get managers:"+r.getReason().name());
		}	
		return r;
	}

	
	@Override
	public Result listAllRoles(UserSession session, List<Role> roleList) {
		// TODO Auto-generated method stub
		return null;
	}	

	@Override
	public List<UserSession> openSessions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void log(UserSession session, Model model, Action event, Map<String, String> args) {
		// TODO Auto-generated method stub
		/*
			x sb.append("MANAGER_ID INTEGER, ");
			x sb.append("MODEL_ID INTEGER, ");
			x sb.append("CHANGE_ID INTEGER, ");
			x sb.append("EVENT_ID INTEGER, ");
			x sb.append("EVENT_STAMP TIMESTAMP, ");
			x sb.append("USER_ID INTEGER, ");
			x sb.append("TXFR_USER_ID INTEGER, ");
			sb.append("CLIENT VARCHAR(200), ");
			sb.append("ROLE_ID INTEGER, ");
			sb.append("SIGNATURE VARCHAR(500), ");
			//sb.append("CHANGES VARCHAR(8000), ");
			sb.append("COMMENTS VARCHAR(500) ");
		 */
	}	
	

	@Override
	public Integer countModels(String entity) {
		Manager m = validateAndRetrieveManager(entity);
		return m.countModels();//TODO add more parameters or base one on user priv.
	}	

	protected void updateState(UserSession session, Model model, Action action,
			Result r, String comment, String signature) {
		try{
			validateSession(r,session);
			User user = session.getUser();
			Role role = session.getCurrentRole();
			Manager manager = model.getManager();
			Privilege priv = session.getPrivilege(manager);
			State existingState = model.getState();
			checkPrivileges(r, action, session, manager);
			checkState(r,user,role,priv,action,model);
			checkModelNotReserved(r,model);
			boolean committed = false;
			Connection conn = db.openTransactionalConnection();
			try{
				model.incrementVersion();//during fail, decrement
				model.setState(action.getCode().getCompletedState());
				String updateSql = SqlTranslator.createStateVersionUpdate(model);
				logsql(updateSql);
				Result dbUpdateResult = new Result();
				Result dbModelHistoryResult = new Result();				
				dbUpdateResult = db.executeUpdate(conn, updateSql);
				if(dbUpdateResult.isSuccessful()){
					Integer managerId = manager.getManagerId();
					Integer modelId = model.getId();
					Integer changeId = model.getVersionId();
					Integer userId = user.getId();	
					Integer roleId = role.getId();
					action.init(managerId, modelId, changeId, userId, roleId);
					if(comment!=null){
						action.comment(comment);
					}
					if(signature!=null){
						action.sign(signature);
					}
					String modelHistorySql = SqlTranslator.createModelHistoryInsert(action);
					String identitySql = SqlTranslator.callIdentity();
					logsql(modelHistorySql);
					dbModelHistoryResult = db.executeInsert(conn,modelHistorySql,identitySql);
				}
				//commit or rollback.
				if(dbUpdateResult.isSuccessful() && dbModelHistoryResult.isSuccessful()){
					Result commitResult = db.commit(conn);
					if(commitResult.notSuccessful()){
						r.noResult(action.getCode().name()+" could not commit.");
						//rare but should we then try to rollback or what should happen if we don't commit? find out.
					}else{
						committed = true;
						log("Committing.");
					}
				}else{
					r.noResult(action.getCode().name()+" update and or history were unable to save to data storage.");
					if(dbUpdateResult.notSuccessful()){
						r.addMessage("Update of model failed. ");//TODO add actions/more details to all these.
						if(dbUpdateResult.hasException()){
							r.error(dbUpdateResult.exception());
						}
					}
					if(dbModelHistoryResult.notSuccessful()){
						r.addMessage("Model history insert failed. ");
						if(dbModelHistoryResult.hasException()){
							r.error(dbModelHistoryResult.exception());
						}							
					}
					Result rollbackResult = db.rollback(conn);
					if(rollbackResult.notSuccessful()){
						r.addMessage("Rollback unsuccessful. You may have to manually edit the database.");
						r.error(rollbackResult.exception());
					}else{
						log("Rolling back.");
					}
				}						
			}catch(Exception e){
				model.decrementVersion();
				model.setState(existingState);
				r.error(e);
			}finally{
				db.closeTransactionalConnection(conn);
			}
			if(committed){
				r.success();
			}							
		}catch(ApplicationException ae){//we only throw these when results have been updated.
			//we can also add more detail to logging.
			if(ae.getMessage()!=null){
				log(ae.getMessage());
			}else{
				ae.printStackTrace();
			}
		}
	}	
	
	/**
	 * DOC
	 * 
	 * @param q
	 */
	protected void logsql(String q){
		if(LOG_SQL){
			String msg = "ApplicationEngine: SQL:'"+q+"'";
			super.log(msg);
		}
	}	
	
	
	
	/*
	 * DOC
	 */
	private void reviewLocks(){
		//TODO implement!
	}
		
	/*
	 * DOC
	 */
	private PageResult paginate(ModelsResult mr, int _pageSize) {
		// TODO Auto-generated method stub
		return null;
	}
	

	/*
	 * DOC
	 */
	private ModelsResult subset(UserSession session, String entity,
			List<Integer> ids) {
		// TODO Auto-generated method stub
		return null;
	}	
	
	/*
	 * DOC
	 */
	private void checkModelNotReserved(Result r, Model model) throws ApplicationException {
		if(model.isReserved()){
			r.reserved(model);
			throw new ApplicationException();
		}
	}	
	
	/*
	 * DOC
	 */
	private boolean isOwnerOrHigher(UserSession session, Manager manager) {
		Privilege priv = session.getPrivilege(manager);
		return priv.ordinal() >= Privilege.OWN.ordinal();
	}
	
	/*
	 * DOC
	 */
	private void checkState(Result result, User u, Role r, Privilege p, Action a, Model m) throws ApplicationException {
		State s = m.getState();
		Code code = a.getCode();
		if(!code.canWorkOn(s,p)){
			result.noAccess(u,r,p,a,m);
			throw new ApplicationException();
		}
	}
	
	
	/*
	 * DOC
	 */
	private void checkPrivileges(Result r, Action action, UserSession session,
			Manager manager) throws ApplicationException {
		Privilege priv = session.getPrivilege(manager);          //checked
		//log("Privilege found: "+priv.name());
		if(!priv.has(action.getPrivilege())){//OWN minimum required privilege. this means approver can too.
			String failmsg = "The privilege:"+priv.name()+" cannot "+action.getCode().name();
			r.notAuthorized(action.getCode().name());
			throw new ApplicationException();
		}
	}	
	
	/*
	 * DOC
	 */
	private void checkPrivileges(ModelResult mr, Action action, UserSession session, Manager manager)
			throws ApplicationException {
		Privilege priv = session.getPrivilege(manager);          //checked
		//log("Privilege found: "+priv.name());
		if(!priv.has(action.getPrivilege())){//OWN minimum required privilege. this means approver can too.
			String failmsg = "The privilege:"+priv.name()+action.getCode().name();
			mr.fail(ModelResult.Reason.UNAUTHORIZED,failmsg);
			throw new ApplicationException();
		}
	}
	
	
	/*
	 * DOC
	 */
	private void checkPrivileges(ModelsResult mr, Action action, UserSession session, Manager manager)
			throws ApplicationException {
		Privilege priv = session.getPrivilege(manager);          //checked
		//log("Privilege found: "+priv.name());
		if(!priv.has(action.getPrivilege())){//OWN minimum required privilege. this means approver can too.
			String failmsg = "The privilege:"+priv.name()+action.getCode().name();
			mr.fail(ModelsResult.Reason.UNAUTHORIZED,failmsg);
			throw new ApplicationException();
		}
	}	

	private void validateSession(Result r, UserSession session) throws ApplicationException {
		if(!session.isValid(r)){
			throw new ApplicationException();
		}
	}		
	
	/*
	 * DOC
	 */
	private void validateSession(ModelResult mr, UserSession session) throws ApplicationException {
		Result sessionResult = new Result();
		if(!session.isValid(sessionResult)){
			mr.fail(ModelResult.Reason.SESSION,sessionResult.allMessages());
			throw new ApplicationException();
		}else{
			sessionResult.success();
		}
	}	
	
	/*
	 * DOC
	 */
	private void validateSession(ModelsResult mr, UserSession session) throws ApplicationException {
		Result sessionResult = new Result();
		if(!session.isValid(sessionResult)){
			mr.fail(ModelsResult.Reason.SESSION,sessionResult.allMessages());
			throw new ApplicationException();
		}else{
			sessionResult.success();
		}
	}		
	

	/*
	 * DOC
	 */
	private Manager validateAndRetrieveManager(Integer entityId) {
		Manager manager = application.getManagerById(entityId);
		if(manager==null)throw new IllegalArgumentException("Manager for entity with ID :"+entityId+" could not be found.");
		Integer managerId = manager.getManagerId();//NOTE replaced     getManagerId(entity);
		if(managerId==null)throw new IllegalStateException("Cannot find manager in manager table for entity:"+entityId);
		return manager;
	}		
	
	/*
	 * DOC
	 */
	private Manager validateAndRetrieveManager(String entity) {
		Manager manager = application.getManager(entity);
		if(manager==null)throw new IllegalArgumentException("Manager for model entity named:"+entity+" could not be found.");
		Integer managerId = manager.getManagerId();//NOTE replaced     getManagerId(entity);
		if(managerId==null)throw new IllegalStateException("Cannot find manager in manager table for entity:"+entity);
		return manager;
	}	
	
	
	/*
	 * DOC
	 */
	private Result compareFields() {
		Result r = new Result();
		List<Manager> managers = application.getManagers();
		for(Manager m:managers){
			if(!db.exists(m.getName())){
				String createAuxSql = SqlTranslator.createTable(m);
				r = db.executeDDL(createAuxSql.toString());
				if(r.notSuccessful()){
					return r;
				}
			}
		}
		r.success();
		return r;
	}	

	/*
	 * DOC
	 */
	private List<ModelsResult> loadModels() throws ApplicationException {
		List<ModelsResult> modelsResultList = new ArrayList<ModelsResult>();
		List<Manager> managers = application.getManagers();
		for(Manager m:managers){
			String selectAllSQL = SqlTranslator.createSelectAll(m);
			ModelsResult result = db.executeSelectAll(selectAllSQL, new ModelResultSetAdaptor(m));
			modelsResultList.add(result);
		}
		return modelsResultList;
	}


	/*
	 * DOC
	 */
	private boolean check(String _op, Result _result) {
		boolean rv = false;
		if(_result.notSuccessful()){
			log("Error:"+_op+":"+_result.name());
		}else{
			log("Success:"+_op);
			rv = true;
		}
		return rv;
	}
	
	/*
	 * DOC
	 */
	private boolean check(String _op, List<ModelsResult> _results) {
		boolean rv = true;
		for(ModelsResult modelsResult:_results){
			if(!modelsResult.isSuccessful()){
				ModelsResult.Reason r = modelsResult.getReason();
				log("Error:"+_op+":"+r.name());
				return false;
			}
		}
		log("Success:"+_op);
		return rv;
	}	

	/*
	 * DOC
	 */
	private Result createApplicationTables() {
		Result r = new Result();
		List<Manager> managers = application.getManagers();
		for(Manager m:managers){
			if(!db.exists(m.getName())){
				String createAuxSql = SqlTranslator.createTable(m);
				r = db.executeDDL(createAuxSql.toString());
				if(r.notSuccessful()){
					return r;
				}
			}
		}
		r.success();
		return r;
	}
	
	/*
	 * DOC
	 */
	private boolean hasCount(Result executeCount) {
		Integer count = (Integer)executeCount.objectValue();//TODO use guard code.
		log("Count:"+count);
		return count>0;
	}


	/*
	 * DOC
	 */
	private void checkStatus() {
		if(ApplicationEngine.OFFLINE==this.status)throw new IllegalStateException("ApplicationEngine is offline. check status first.");
	}

	@Override
	public Result listRolesUser(UserSession session, String user,
			List<Role> roleList) {
		// TODO Duplicate???
		return null;
	}



	
}

/*
	 * D OC
	 * not necessary anymore. 
	private Integer getManagerId(String entity) {
		Integer rv = null;
		String selectSql = "SELECT ID FROM "+Manager.AUX_MANAGER+" WHERE manager='"+entity+"' ";//TO DO pstmt, extract from this class
		Result r = db.executeQuerySingleInteger(selectSql);
		if(r.hasValue()){
			rv = (Integer)r.objectValue();
		}
		return rv;
	}

 * 
 * 
 * 
 */





/*
 * Commands:
 * 
 * same as below:
 * 
 */

/* login
 * logout
 * clear
 * 
 * public Result x(User user, Role role)
 * lookup(String entity)                                         LOOKUP
 * search(Map<String,String>params)                              READ    (must have access to all tables)
 * read(String entity, Integer id)                               READ
 * update(String entity, Integer id, String field, String value) WRITE
 * update(String entity, Integer id, Map<String,String> values)  WRITE
 * create(String entity, Map<String,String> reqFieldValues);     OWN
 * import(String entity, Map<String,String> fieldValues);        OWN
 * export(String entity)                                         EXPORT
 * lock(String entity, Integer id)                               OWN
 * unlock(String entity, Integer id)                             APPROVE
 * approve(String entity, Integer id)                            APPROVE
 * reject(String entity, Integer id)                             APPROVE
 * 
 * grant(String role, String entity,String priv)                 OWN,APPROVE,SUPER,ADMIN
 * remove(String entity, Integer id)                             OWN
 * archive(String entity, Integer id)                            APPROVE
 * purgeExport(String entity)                                    APPROVE
 * shareRead(String entity, Integer id)                          OWN, (until this is done, only the owner can see/lookup,etc)
 * shareWrite(String entity, Integer id)                         OWN  (until this is done, other owners and writers cannot write and only the owner can write.)
 * customMethods?
 */



/*
	NONE,					//this user can do nothing. the user won't see it in count or lookup.
	LOOKUP,                 //this user can see this record counted and can see it in lookup.
	READ,					//this user can see the current record in detail
	WRITE,					//this user can contribute to the record, update and do above
	EXPORT,                 //this user can export all records 
	OWN,					//this user can import, create, or remove (soft-delete) a record and add/remove/update lesser roles and all the above
	APPROVE,                //this user can add approval, reject, unlock, transfer owner to another in same role, look at reports and do all the above
	SUPER,                  //this user can do all the above plus whatever migrations may be needed and adding users to roles, grant approve
 */

/* (allow these functions to work like a load on startup servlet)
 * 
 * Load configuration settings.
 * 
 * Load the application (to do some book keeping)
 * 
 * Load the application manager with configuration settings and set up the database
 * 
 * Feed the application into the manager. (it will set up records in the database)
 * 
 * Instruct the manager policies on the models. (it will set up more records in the appman database)
 * 
 * Load the application records from database.
 *  
 * 
 */

/*
* Validate (regex, range), message, per field, javascript & ajax generator
* Format out 
* Format in (gen javascript)
* Autocomplete AJAX/Uniqueness check
* Validate AJAX
* Calculation formula (or just use pre and post set logic), Map entries
* navigate relationships to other fields
* Aggregate total calculation (to a parent record)
* Summary/grouping stats table
* Field groupings
* Value object can do conversions to other data types as needed with convert methods.



See Deploy Tool for examples of the robustness.
See Java Practices 
See Hsql
See rules engines
See a project which does where/order/etc

 */
/*
 * functions:
 * 
 * Offline.
 * inject persistence.
 * online a record by id if offline.
 * "is in sync"
 * "is saved"
 * consider r/w and transactions
 * "lock" by user, session unlock.
 * 
 * define Model
 * 
 * count of models.
 * get all models with all fields.
 * get all models but only load certain fields.
 * (if matching, match in memory)filter that list by 
 *   a field matching criteria.
 * do summaries, summaries by repeated value (group)
 * sort
 * look up using id and business key, autoseek
 * view a single model, a page of models, a set
 * check out a model for edit
 * update and check in the model.
 * delete a model.
 * create a new model.
 * enter a new or update on a field in a model and validate
 * and provide any messages while updating.
 * see if a model's field is unique.
 * 
 * LATER
 * set these models to persist and unload after change or read
 * set models to only partially unload.
 * set up a search and have which fields will be seen
 * 
 * do calculated fields
 * do validation
 * 
 * last save time./read time. (make the database 
 * save async?)
 * 
 * 
 *   
 * 
 */




/*
 * id, 
 * manager_id, 
 * model_id, 
 * event, 
 * stamp, 
 * user_by, 
 * role, 
 * signature, 
 * changes, 
 * comments
 * 
 * OPS
 * create,
 * import
 * write,
 * transfer  (owner)
 * publish R
 * publish W
 * submit
 * approve
 * reject
 * unsubmit (a third option)
 * delete
 * archive
 * 
 * 
 * Not included:
 * Read,Export,Lookup,Search,Open
 *  
 *  
 */

/*
private Result compareModels() {
	Result r = new Result();
	// TO DO NOTE: we won't need to compare if the data is in the table. we won't need to join either.
	// aux fields: 
	//   created: date
	//   create by  : (first owner)
	//   last mod date:
	//   last mod by:
	//   delete date:                       isDeleted
	//   delete by:
	//   publish read:	                    visible to EXPORT,READ,LOOKUP,WRITE,OWN,APPROVE (always to SUPER/OWN)
	//   publish write:                     writable to WRITE,OWN						    (always to SUPER/OWN)
	//   approve/lock date:                 isLocked
	//   approve/locked by:
	//   mark arhive:           Y/N
	//   current owner:         user
	//   current approver:      user
	//   inUseBy                (concurrency lock)  isStale (could be stale)   isCommitted (not open/saved)
	//   inUseStarted           date                allows expirations if system down and unable to reopen.
	//      The concurrency lock means the read is valid.
	//   
	 *
	 *  Rules:
	 *  1. The super can access everything.
	 *  2. If the owner is no longer in a role, 
	 *  		it's good to transfer ownerships to other users in that role
	 *  3. If the role no longer has the owner privilege, 
	 *  		it's good to create a new role with the privilege and transfer ownersthips to a user in that role.
	 *  4. If a user is no longer active, it's ownership should be transferred.
	 *  5. If the approver is no longer in a role, 
	 *  		any other approver can take over (transfer to self).
	 *  
	 *  Be able to show 
	 *  	1. deserted ownerships (where the owner can't access because: 
	 *  		a) not active, 
	 *  		b) not in role 
	 *  		c) role no longer has privilege
	 *  	2. deserted approverships (where the approver can't access because:
	 *  		a) not active
	 *  		b) not in role
	 *  		c) role no longer has approvership
	 *  
	 *  No "shared" role.  Anyone in a role who is read write or just read is essentially shared with.
	 *    An owner might restrict publication.
	 *  
	 *
	
	List<Manager> managers = application.getManagers();
	for(Manager m:managers){
		Integer countRecords = 0;
		String countRecordsQuery = SqlTranslator.createCountQuery(m);
		logsql(countRecordsQuery);
		Result c = db.executeCountQuery(countRecordsQuery);
		countRecords = (Integer)c.objectValue();//TO DO use guard code.
		
		Integer countModels = 0;
		String countModelsQuery = "SELECT COUNT(*) FROM "+ApplicationManager.AUX_MODEL+" WHERE "+
		//TO DO START HERE  Should we create an aux table for each entity or just add fields or just one giant table?
	}
	r.success();
	return r;
}*/

/*
 * 
 
private Result loadModelHistories() {
	// TO DO Auto-generated method stub
	/*
		sb.append("CREATE TABLE "+AUX_MODEL+" ("+SqlTranslator.AUTO_PK+", ");
		sb.append("MANAGER_ID INTEGER, ");
		sb.append("MODEL_ID INTEGER, ");
		sb.append("CHANGE_ID INTEGER, ");
		sb.append("EVENT_ID INTEGER, ");
		sb.append("EVENT_STAMP TIMESTAMP, ");
		sb.append("USER_ID INTEGER, ");
		sb.append("TXFR_USER_ID INTEGER, ");
		sb.append("CLIENT VARCHAR(200), ");
		sb.append("ROLE_ID INTEGER, ");
		sb.append("SIGNATURE VARCHAR(500), ");
		//sb.append("CHANGES VARCHAR(8000), ");
		sb.append("COMMENTS VARCHAR(500) ");
		sb.append(")");
	 
	return null;
}
*/
/*
 * x assuming checks all pass:
 * x create a new model using fields in map and ensure it passes.  i.e.: validation
 * 
 * save the model and populate the id for the model 
 * make entry in model history that is create event. (all create,update,delete,share,publish,approve,submit,reject,etc).
 * 
 * x both the above must pass, then add the model to the manager object.
 * 
 * messages back to the user of the success or fail.
 * 
 */
/* checks:
* x no argument is null.
* x the reqFieldValues has size
* x application is started 
* x usersession valid and user is logged in, not expired.
* x entity found
* x usersession has a role
* x the role has a privilege for this entity.
* x that privilege can do createModel. (own,approver,super)
*/