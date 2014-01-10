package jhg.appman;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jhg.Base;
import jhg.Privilege;
import jhg.Result;
import jhg.Role;
import jhg.User;
import jhg.appman.Screen.Button;
import jhg.db.Database;
import jhg.model.Application;
import jhg.model.Manager;
import jhg.model.Model;



/**
 * DOC
 * 
 * @author John
 *
 */
public class SimpleApplicationManager extends Base implements ApplicationManager {
	
	private Application application;
	private Database db;
	private Config config;
	private boolean loggedIn=false;
	
	
	public SimpleApplicationManager(Config _config){
		config = _config;
		config.init();
		//this.users = new ArrayList<User>();
		//this.roles = new ArrayList<Role>();
	}
	

	@Override
	public String getApplicationName() {
		if(application==null){
			return "Not Loaded Yet.";
		}else{
			return application.toString();
		}
	}	
	
	@Override	
	public void init(){
		log("Initializing.");
		application = config.loadApplication();
		application.initialize();
		db = new Database("jdbc:hsqldb:file:./data/","accounting","SA","");//config.DBURL,config.DATABASE,config.ADMIN_LOGIN,config.ADMIN_PASSWORD//TODO secure.
		boolean goodConnection = db.testConnection();
		if(!goodConnection){
			throw new IllegalStateException("Database failure.");
		}
		Result r = createManagementTables();
		if(r.notSuccessful()){
			log("CreateManagementTables:"+r.name());
			return;
		}
		log("Created Management Tables.");
		r = fillAuxManagementTable();
		
		if(r.notSuccessful()){
			log("FillAuxManagementTable:"+r.name());
			return;
		}
		log("Filled Management Tables.");
	}

	@Override
	public Database getDatabase(){
		return db;
	}
	
	@Override
	public Application getApplication(){
		return application;
	}
	
	@Override	
	public void resetDatabase(){
		log("Initializing.");
		application = config.loadApplication();
		db = new Database("jdbc:hsqldb:file:./data/","accounting","SA","");//config.DBURL,config.DATABASE,config.ADMIN_LOGIN,config.ADMIN_PASSWORD//TODO secure.
		boolean goodConnection = db.testConnection();
		if(!goodConnection){
			throw new IllegalStateException("Database failure.");
		}		
		destroyManagementTables();
	}
	
	@Override
	public void shutdown() {
		log("Shutting down.");
	}	
	
	/*
	 * Commands:
	 * clear						Clears the buffer
	 * login						Logs in the admin
	 * logout						Logs out the admin
	 * 
	 * cuser <user> <pass>			Creates a user 3,2
	 * crole <role>          		Creates a role 2,1
	 * assign <user> <role>     	Assign a user a role. insert into table.
	 * grant <role> <entity> <priv> Grants a role an entity privilege.
	 * 
	 * grants <role>                List all entities and privileges for this role.
	 * grants <role> <entity>       List all the privileges for this role and entity. 
	 * assignments <user>           List all assigned roles for this user.
	 * 
	 * users                        List all users
	 * roles                        List all roles
	 * assignments                  List the users (all) and any roles
	 * grants                   List the roles (all) and any entity privileges.
	 * entities                     List all the entities/manager and their settings.
	 * models <entity>              List all models for the entity and their states.
	 * 
	 * unassign <user> <role>       Unassign (delete) for the user and this role.
	 * duser <user>                 Delete a user, cannot have any roles.
	 * drole <role>                 Delete a role, cannot have any privileges.
	 * ungrant <role><entity><priv> Ungrant (delete) an entity privilege for the role.
	 * 
	 * uentity <entity> <nvp>       Update entity with settings.
	 * uuser <user> <nvp>           Update user
	 * umodel <entity><id><nvpset>  Update model with settings, does not update the model.
	 * 
	 * Application Commands
	 * 
	 * 
	 * 
	 */

	/*
	 * RESUME create something that will create a screen based on code and results.
	 * 
	 * (non-Javadoc)
	 * @see jhg.appman.ApplicationManager#service(jhg.appman.Screen.Button, java.util.Map)
	 */
	@Override
	public Result service(Screen.Button button, Map<String, String[]> parameterMap){//, Map<String,Object> valuesMap) {
		log("service(String,Map)");
		Result result = new Result();
		Screen next = null;
		if(!loggedIn){
			return result.notAuthorized();
		}
		/*   TODO: finish the remaining cases/commands
		> BACK(null),
		x LOGIN(Code.ADMINHOME),
		x LOGOUT(Code.AUTHENTICATE),
		x GOHOME(Code.ADMINHOME),
		x MANAGEUSERS(Code.USERTABLE),
		x GOVIEWUSER(Code.VIEWUSER),
		x GOCREATEUSER(Code.CREATEUSER),
		x CREATEUSER(Code.VIEWUSER),
		x GOEDITUSER(Code.EDITUSER),
		EDITUSER(Code.VIEWUSER),
		> DELETEUSER(Code.USERTABLE),
		x MANAGEROLES(Code.ROLETABLE),
		x VIEWROLE(Code.VIEWROLE),
		x GOCREATEROLE(Code.CREATEROLE),
		x CREATEROLE(Code.VIEWROLE),
		> DELETEROLE(Code.ROLETABLE),
		> MANAGEENTITIES(Code.ENTITYTABLE),
		> VIEWENTITY(Code.VIEWENTITY),
		x ASSIGNROLE(Code.VIEWUSER),
		x UNASSIGNROLE(Code.VIEWUSER),
		x GRANTPRIV(Code.VIEWROLE),
		x UNGRANTPRIV(Code.VIEWROLE),
		 */
		//don't have to cover login or logout
		switch(button){
			case MANAGEUSERS:
				log("Manage Users.");
				result = getUserList(button);
				break;
			case MANAGEROLES:
				log("Manage Roles.");
				result = getRoleList(button);
				break;
			case MANAGEENTITIES:                //TODO check: is this necessary right now? finish role create, grant, ungrant, assign, unassign
				log("Manage Entities.");
				result = getEntityList(button);
				break;
			case GOCREATEUSER:
				log("Create User Form: "+button.destination().getPage());
				next = new Screen(this,button.destination());
				result.objectValue(next);
				result.success();
				break;
			case CREATEUSER:
				log("Create User.");
				String username = parameterMap.get(User.USERFLD)[0];//TODO validate presence
				String password = parameterMap.get(User.PASSFLD)[0];
				result = createUser(username,password);
				String createdUserId = ((Integer)result.objectValue()).toString();
				result = findUser(createdUserId);//TODO check success
				result = createViewUser(button, (User)result.objectValue(),true);					
				break;
			case GOVIEWUSER:
				log("View User.");
				String viewUserId = parameterMap.get(ApplicationManager.ID)[0];//TODO validate presence
				result = findUser(viewUserId);//TODO check success
				result = createViewUser(button, (User)result.objectValue(),false);					
				break;
			case GOEDITUSER:
				log("Edit this User.");
				String editUserId = parameterMap.get(ApplicationManager.ID)[0];//TODO validate presence
				//right now it is just change password.
				//password, email
				//result = editUser(editUserId,...);
				result = findUser(editUserId);//TODO check success
				result = createViewUser(button, (User)result.objectValue(), true);					
				break;//RESUME finish edit
			case EDITUSER:
				log("Edit User.");
				/* copied from create
				String username = parameterMap.get(User.USERFLD)[0];//TODO validate presence
				String password = parameterMap.get(User.PASSFLD)[0];
				result = createUser(username,password);
				String createdUserId = ((Integer)result.objectValue()).toString();
				result = findUser(createdUserId);//TODO check success
				result = createViewUser(button, (User)result.objectValue(),true);					
				 */
				break;
			case ASSIGNROLE:
				log("Assign Role.");
				String userId = parameterMap.get(ApplicationManager.ID1)[0];//TODO validate presence
				String roleId = parameterMap.get(ApplicationManager.ID2)[0];
				result = assign(userId, roleId);//TODO check success
				result = findUser(userId);//TODO check success
				result = createViewUser(button, (User)result.objectValue(),false);					
			case UNASSIGNROLE:
				log("Unassign Role.");
				String unassignUserId = parameterMap.get(ApplicationManager.ID1)[0];//TODO validate presence
				String unassignRoleId = parameterMap.get(ApplicationManager.ID2)[0];
				result = unassign(unassignUserId, unassignRoleId);//TODO check success
				result = findUser(unassignUserId);//TODO check success
				result = createViewUser(button, (User)result.objectValue(),false);					
				break;
			case DELETEUSER:
				log("Delete User.");
				String deleteUserId = parameterMap.get(ApplicationManager.ID)[0];//TODO validate presence
				//result = findUser(deleteUserId);//TODO check success
				result = deleteUser(deleteUserId);
				next = new Screen(this,button.destination());
				result = getUserList(button);
				result.success();					
				break;	
			case VIEWROLE:
				log("View Role.");
				String viewRoleId = parameterMap.get(ApplicationManager.ID)[0];//TODO validate presence
				result = findRole(viewRoleId);//TODO check success
				result = createViewRole(button, (Role)result.objectValue(),false);						
				break;
			case GOCREATEROLE:
				log("Create Role Form: "+button.destination().getPage());
				next = new Screen(this,button.destination());
				result.objectValue(next);
				result.success();
				break;
			case CREATEROLE:
				log("Create a Role.");
				String rolename = parameterMap.get(Role.ROLEFLD)[0];//TODO validate presence
				result = createRole(rolename);
				String createdRoleId = ((Integer)result.objectValue()).toString();
				result = findRole(createdRoleId);//TODO check success
				result = createViewRole(button, (Role)result.objectValue(),true);					
				break;
			case GRANTPRIV:
				log("Grant privilege");
				String grantRoleId = parameterMap.get(ApplicationManager.ID1)[0];//TODO validate presence
				String entityId = parameterMap.get(ApplicationManager.ID2)[0];
				String privId = parameterMap.get(ApplicationManager.ID3)[0];
				result = grant(grantRoleId, entityId, privId);//TODO check success
				result = findRole(grantRoleId);//TODO check success
				result = createViewRole(button, (Role)result.objectValue(),false);					
				break;
			case UNGRANTPRIV:
				log("Ungrant privilege");
				String ungrantRoleId = parameterMap.get(ApplicationManager.ID1)[0];//TODO validate presence
				String ungrantEentityId = parameterMap.get(ApplicationManager.ID2)[0];
				String ungrantPrivId = parameterMap.get(ApplicationManager.ID3)[0];
				result = ungrant(ungrantRoleId, ungrantEentityId, ungrantPrivId);//TODO check success
				result = findRole(ungrantRoleId);//TODO check success
				result = createViewRole(button, (Role)result.objectValue(),false);					
				break;
			//NOTE broken below	
			case BACK:
				log("Go Back.");
				next = new Screen(this,button.destination());
				result.objectValue(next);
				result.success();				
			break;
			case GOHOME:
				log("Go Home.");
				next = new Screen(this,button.destination());
				result.objectValue(next);
				result.success();				
			break;
			default:
				result.invalidInput("Command not found.");
			break;
		}
			//valuesMap.put(USERLIST,userList);


		return result;
	}





	@Override
	public Result listUsers(List<User> userList) {
		log("listing users.");
		Result r = new Result();
		
		StringBuffer sb = new StringBuffer(); //TODO put this in sql translator
		sb.append("SELECT U.ID, U.").append(User.USERFLD).append(", ");
		sb.append("U.").append(User.PASSFLD).append(" ");
		sb.append(" FROM ").append(User.AUX_USER).append(" U ");

		String selectUsersSql = sb.toString();
		logsql(selectUsersSql);
		r = db.executeSelectUsers(selectUsersSql,userList);
		log("list size:"+userList.size());
		return r;
	}	
	
	/**
	 * DOC
	
	public Result service(String commandStr){
		Result result = new Result();
		if(!loggedIn){
			return result.notAuthorized();
		}
		String[] parse = StringUtils.split(commandStr);
		String command = parse[0];
		if("cuser".equals(command)){
			if(parse.length==3){
				result = createUser(parse[1],parse[2]);
			}else{
				result.invalidInput("cuser requires 2 arguments.");
			}
		}else if("crole".equals(command)){
			if(parse.length==2){
				result = createRole(parse[1]);
			}else{
				result.invalidInput("Command crole requires 1 arguments.");
			}
		}else if("assign".equals(command)){
			if(parse.length==3){
				result = assign(parse[1],parse[2]);
			}else{
				result.invalidInput("Command assign requires 2 arguments.");
			}
		}else if("grant".equals(command)){
			if(parse.length==4){
				result = grant(parse[1],parse[2],parse[3]);
			}else{
				result.invalidInput("Command crole requires 3 arguments.");
			}
		}else{
			result.invalidInput("Command not found.");
		}
		return result;
	} */

	
	/**
	 * DOC
	 * 
	 * @param role
	 * @param manager
	 * @param privilege
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean denyAccess(Role role, Manager manager, Privilege privilege){
		boolean rv = true;
		String countSql = "SELECT count(*) FROM "+Role.AUX_ROLE_PRIV+" WHERE role_id = ? and manager_id = ? and priv_id = ? ";
		Result r = db.executeCountQuery(countSql);
		if(r.isSuccessful()){
			Integer count = (Integer)r.objectValue();
			rv = (count ==    Base.ZERO);
		}else{
			log("Deny access query:"+countSql+" failed:"+r.name());
		}
		return rv; 
	}	
	
	/**
	 * DOC
	 * @param username
	 * @param pass
	 * @return
	 */
	public Result login(String username, String pass){
		Result r = new Result();
		if(loggedIn){
			log("Already logged in.");
			r.setMessage("Already logged in.");
			r.success();
			//r.setNext("/ray/adminHome.jsp");
		}else{
			log("config.ADMIN_LOGIN:"+config.ADMIN_LOGIN);
			log("username:'"+username+"'");
			log("config.ADMIN_PASSWORD:"+config.ADMIN_PASSWORD);
			log("pass:'"+pass+"'");
			boolean valid = config.ADMIN_LOGIN.equals(username) && config.ADMIN_PASSWORD.equals(pass);
			if(valid){
				log("User found.");
				User user = new User(username,pass);
				r.success();
				r.objectValue(user);
				loggedIn = true;
			}else{
				log("User not found.");
				r = r.notAuthorized();
			}
		}
		return r;
	}
	
	/**
	 * DOC
	 */
	public Result logout(){
		Result r = new Result(); 
		if(!loggedIn){
			r.noResult();
			r.setMessage("Already logged out.");
		}
		loggedIn = false;
		r.success();;
		return r;
	}
	
	@Override
	public Result createUser(String user, String pass){
		Result r = new Result(); 
		if(!loggedIn){
			return r.notAuthorized();
		}		
		if(!existsUser(user)){
			String sInsert = "INSERT INTO "+User.AUX_USER+" ("+User.USERFLD+","+User.PASSFLD+") values ('"+user+"', '"+pass+"')";//TODO sql injection, used pstmt setString?
			String identitySql = "CALL IDENTITY();";
			r = db.executeInsert(sInsert,identitySql);				
		}else{
			r.noResult();
			r.setMessage("User already exists.");
		}
		return r;
	}
	
	@Override
	public Result findUser(String userId){
		Result r = new Result();
		if(!loggedIn){
			r = r.notAuthorized();
			return r;
		}		
		User user = new User();
		String selectUserSql = "SELECT * FROM "+User.AUX_USER+" WHERE "
				+"ID = "+userId+"  ";
		
		r = db.executeSingleSelectQuery(selectUserSql, user);
		if(!user.isValid()){
			r.error("User object not valid.");
		}else{
			if(r.isSuccessful()){
				r.objectValue(user);
			}
		}
		return r;		
	}
	
	public Result findRole(String roleId){
		Result r = new Result();
		if(!loggedIn){
			r = r.notAuthorized();
			return r;
		}		
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
	public Result listAllRoles(List<Role> roleList){
		log("listing roles.");
		Result r = new Result();
		
		if(!loggedIn){
			r = r.notAuthorized();
			return r;
		}
		StringBuffer sb = new StringBuffer(); //TODO put this in sql translator
		sb.append("SELECT R.ID, R.").append(Role.ROLEFLD);
		sb.append(" FROM ").append(Role.AUX_ROLE).append(" R ");
		
		String selectRolesSql = sb.toString();
		logsql(selectRolesSql);
		
		r = db.executeSelectRoles(selectRolesSql,roleList);
		log("list size:"+roleList.size());
		return r;
	}
	
	/**
	 * DOC
	 * @param user
	 * @param allRolesList
	 * @param rolesAssigned
	 */
	protected void subtractRolesNotAssigned(List<Role> allRolesList, List<Role> rolesAssigned){
		allRolesList.removeAll(rolesAssigned);
	}
	
	@Override
	public Result listPrivileges(Role role){
		log("listing privileges.");
		Result r = new Result();
		if(!loggedIn){
			r = r.notAuthorized();
			return r;
		}
		StringBuffer sb = new StringBuffer(); //TODO put this in sql translator
		sb.append("SELECT R."+Role.MANAGER_ID+", R.").append(Role.PRIV_ID);
		sb.append(" FROM ").append(Role.AUX_ROLE_PRIV).append(" R ");
		sb.append(" WHERE R.").append(Role.ROLE_ID);
		sb.append(" = ");
		sb.append(role.getId());
		sb.append(" ");
		
		String selectRolesSql = sb.toString();
		logsql(selectRolesSql);
		
		r = db.executeSelectRolePrivileges(application,selectRolesSql, role);//(selectRolesSql,entityPrivs);
		log("map size:"+role.getPrivileges().size());
		return r;
	}
	
	@Override
	public Result listRoles(User user, List<Role> roleList) {//TODO see if this style of just returning desired object is good or not since most return result.
		log("listing roles.");
		Result r = new Result();
		
		if(!loggedIn){
			r = r.notAuthorized();
			return r;
		}
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
		log("list size:"+roleList.size());
		return r;
	}		
	
	@Override
	public boolean existsUser(String user){
		boolean result = false;
		Result r = new Result();
		if(!loggedIn){
			r = r.notAuthorized();
			return result;
		}		
		String countSql = "SELECT COUNT(*) FROM "+User.AUX_USER+" WHERE "+User.USERFLD+" = '"+user+"'";//TODO sql injection, used pstmt setString?
		r = db.executeCountQuery(countSql);	
		if(r.hasValue()){
			Integer count = (Integer)r.objectValue();
			if(count>0){
				result = true;
			}
		}
		log("existsUser:"+result);
		return result;
	}
	
	@Override
	public Result deleteUser(String user){
		Result r = new Result(); 
		if(!loggedIn){
			return r.notAuthorized();
		}		
		String deleteSql = "DELETE FROM "+User.AUX_USER+" WHERE "+User.USERFLD+" = '"+user+"'";//TODO sql injection, used pstmt setString?
		r = db.executeDelete(deleteSql);		
		return r;
	}

	@Override
	public Result createRole(String role){
		Result r = new Result(); 
		if(!loggedIn){
			return r.notAuthorized();
		}		
		if(!existsRole(role)){
			String sInsert = "INSERT INTO "+Role.AUX_ROLE+" (role) values ('"+role+"')";//TODO sql injection, used pstmt setString?
			String identitySql = "CALL IDENTITY();";
			r = db.executeInsert(sInsert,identitySql);				
		}else{
			r.noResult();
			r.setMessage("Role already exists.");
		}
		return r;
	}
	
	@Override
	public boolean existsRole(String role){
		boolean result = false;
		Result r = new Result(); 
		if(!loggedIn){
			r = r.notAuthorized();
			return false;
		}		
		String countSql = "SELECT COUNT(*) FROM "+Role.AUX_ROLE+" WHERE "+Role.ROLEFLD+" = '"+role+"'";//TODO sql injection, used pstmt setString?
		r = db.executeCountQuery(countSql);	
		if(r.hasValue()){
			Integer count = (Integer)r.objectValue();
			if(count>0){
				result = true;
			}
		}
		log("existsRole:"+result);
		return result;		
	}
	
	@Override
	public Result deleteRole(String role){
		Result r = new Result(); 
		if(!loggedIn){
			return r.notAuthorized();
		}			
		String deleteSql = "DELETE FROM "+Role.AUX_ROLE+" WHERE "+Role.ROLEFLD+" = '"+role+"'";//TODO sql injection, used pstmt setString?
		r = db.executeDelete(deleteSql);		
		return r;		
	}
	
	@Override
	public Result assign(String userId, String roleId){
		Result r = new Result(); 
		if(!loggedIn){
			return r.notAuthorized();
		}			
		if(!hasAssignment(userId, roleId)){
			String sInsert = "INSERT INTO "+User.AUX_USER_ROLE+" (user_id, role_id) values ("+userId+","+roleId+")";//TODO sql injection, used pstmt setString?
			String identitySql = "CALL IDENTITY();";
			r = db.executeInsert(sInsert,identitySql);	
		}else{
			r.noResult();
			r.setMessage("Assignment already exists.");
		}
		return r;	
		
	}
	
	@Override
	public boolean hasAssignment(String userId, String roleId){
		boolean result = false;
		Result r = new Result(); 
		if(!loggedIn){
			r = r.notAuthorized();
			return result;
		}			
		String countSql = "SELECT COUNT(*) FROM "+User.AUX_USER_ROLE+" WHERE user_id = "+userId+" AND role_id = "+roleId+" ";//TODO sql injection, used pstmt setString?
		r = db.executeCountQuery(countSql);	
		if(r.hasValue()){
			Integer count = (Integer)r.objectValue();
			if(count>0){
				result = true;
			}
		}
		log("hasAssignment:"+result);
		return result;			
	}
	
	@Override
	public Result unassign(String userId, String roleId){
		Result r = new Result(); 
		if(!loggedIn){
			return r.notAuthorized();
		}		
		String deleteSql = "DELETE FROM "+User.AUX_USER_ROLE+" WHERE user_id = "+userId+" AND role_id = "+roleId+" ";//TODO sql injection, used pstmt setString?
		r = db.executeDelete(deleteSql);		
		return r;			
	}
	
	@Override
	public Result grant(String roleId, String entityId, String priv){
		//TODO validate priv. (roleId,entityId)
		//List<Result> rlist = new ArrayList<Result>();
		//TODO  ensure it doesn't already exist.
		Result r = new Result(); 
		if(!loggedIn){
			return r.notAuthorized();
		}		
		List<String> entityIds = new ArrayList<String>();
		if(Base.ALL.equals(entityId)){
			String sSelectIds = "SELECT ID FROM "+Manager.AUX_MANAGER+" ";
			Result selectResult = db.executeSelectAllIds(sSelectIds, entityIds);
			if(selectResult.notSuccessful()){
				return selectResult;
			}
		}else{
			entityIds.add(entityId);
		}
		
		boolean found = false;
		for(String s:entityIds){
			if(!found){
				found = true;
			}
			if(!hasBeenGranted(roleId, s, priv)){
				String sInsert = "INSERT INTO "+Role.AUX_ROLE_PRIV+" (role_id, manager_id, priv_id) values ("+roleId+","+s+","+priv+")";//TODO sql injection, used pstmt setString?
				String identitySql = "CALL IDENTITY();";
				r = db.executeInsert(sInsert,identitySql);
				if(r.notSuccessful()){
					return r;
				}
			}
		}
		if(!found){
			r.noResult();
			r.setMessage("All privileges were already granted.");
		}else{
			r.success();;//some privileges exist.
		}
		
		return r;
	}

	@Override
	public boolean hasBeenGranted(String roleId, String entityId, String priv){
		//TODO validate priv. (roleId,entityId)
		boolean result = false;
		Result r = new Result(); 
		if(!loggedIn){
			r = r.notAuthorized();
			return result;
		}		
		String countSql = "SELECT COUNT(*) FROM "+Role.AUX_ROLE_PRIV+" WHERE role_id = "+roleId+" AND manager_id = "+entityId+" AND priv_id = "+priv+"  ";//TODO sql injection, used pstmt setString?
		r = db.executeCountQuery(countSql);	
		if(r.hasValue()){
			Integer count = (Integer)r.objectValue();
			if(count>0){
				result = true;
			}
		}
		log("hasBeenGranted:"+result);
		return result;			
	}
	
	@Override
	public Result ungrant(String roleId, String entityId, String priv){
		//TODO validate priv. (roleId,entityId)
		Result r = new Result(); 
		if(!loggedIn){
			return r.notAuthorized();
		}		
		String deleteSql = "DELETE FROM "+Role.AUX_ROLE_PRIV+" WHERE role_id = "+roleId+" AND manager_id = "+entityId+" AND priv_id = "+priv+"  ";//TODO sql injection, used pstmt setString?
		r = db.executeDelete(deleteSql);		
		return r;		
	}
	
	
	/**
	 * DOC
	 * @return
	 */
	protected Result destroyManagementTables(){
		Result r = new Result();
		String[] dropTables = new String[]{Role.AUX_ROLE_PRIV,User.AUX_USER_ROLE,User.AUX_USER,Role.AUX_ROLE,Model.AUX_MODEL,Manager.AUX_MANAGER};
		String dropSql = "";
		for(String s:dropTables){
			dropSql = "DROP TABLE "+s;
			db.executeDDL(dropSql);
		}
		r.success();;
		return r;
	}
	
	/**
	 * DOC
	 * 
	 * @return
	 */
	protected Result createManagementTables(){
		Result r = new Result();
		if(!db.exists(Manager.AUX_MANAGER)){
			//TODO move some of this to SqlTranslator.
			String createAuxSql = "CREATE TABLE "+Manager.AUX_MANAGER+" ("+SqlTranslator.PLAIN_PK+", manager VARCHAR(50) )";
			r = db.executeDDL(createAuxSql);
		}else{
			String countQuery = "SELECT COUNT(*) FROM "+Manager.AUX_MANAGER+" ";
			logsql(countQuery);
			Result c = db.executeCountQuery(countQuery);
			if(hasCount(c)){
				String selectAllManagers = "SELECT * FROM "+Manager.AUX_MANAGER+" "; 
				db.debugSelectAll(selectAllManagers);
			}
		}
		
		if(!db.exists(Model.AUX_MODEL)){
			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE "+Model.AUX_MODEL+" ("+SqlTranslator.AUTO_PK+", ");
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
			r = db.executeDDL(sb.toString());
		}else{
			String countQuery = "SELECT COUNT(*) FROM "+Model.AUX_MODEL+" ";
			logsql(countQuery);
			Result c = db.executeCountQuery(countQuery);
			if(hasCount(c)){
				db.debugSelectAll("SELECT * FROM "+Model.AUX_MODEL+" ");
			}
		}
		
		
		if(!db.exists(Role.AUX_ROLE)){
			String createAuxSql = "CREATE TABLE "+Role.AUX_ROLE+" ("+SqlTranslator.AUTO_PK+",  "+Role.ROLEFLD+" VARCHAR(50) )";
			r = db.executeDDL(createAuxSql);
		}else{
			String countQuery = "SELECT COUNT(*) FROM "+Role.AUX_ROLE+" ";
			logsql(countQuery);
			Result c = db.executeCountQuery(countQuery);
			if(hasCount(c)){
				db.debugSelectAll("SELECT * FROM "+Role.AUX_ROLE+" ");
			}
		}
		 
		if(!db.exists(User.AUX_USER)){
			String createAuxSql = "CREATE TABLE "+User.AUX_USER+" ("+SqlTranslator.AUTO_PK+", "+User.USERFLD+" VARCHAR(50), "+User.PASSFLD+" VARCHAR(50) )";//TODO security
			r = db.executeDDL(createAuxSql);
		}else{
			String countQuery = "SELECT COUNT(*) FROM "+User.AUX_USER+" ";
			logsql(countQuery);
			Result c = db.executeCountQuery(countQuery);
			if(hasCount(c)){
				db.debugSelectAll("SELECT * FROM "+User.AUX_USER+" ");
			}
		}
		//one role at a time in session.
		if(!db.exists(User.AUX_USER_ROLE)){
			String createAuxSql = "CREATE TABLE "+User.AUX_USER_ROLE+" ("+SqlTranslator.AUTO_PK+", "+Role.USER_ID+" INTEGER, "+Role.ROLE_ID+" INTEGER )";
			r = db.executeDDL(createAuxSql);
		}else{
			String countQuery = "SELECT COUNT(*) FROM "+User.AUX_USER_ROLE+" ";
			logsql(countQuery);
			Result c = db.executeCountQuery(countQuery);
			if(hasCount(c)){
				db.debugSelectAll("SELECT * FROM "+User.AUX_USER_ROLE+" ");
			}
		}
		
		if(!db.exists(Role.AUX_ROLE_PRIV)){
			String createAuxSql = "CREATE TABLE "+Role.AUX_ROLE_PRIV+" ("+SqlTranslator.AUTO_PK+", "+Role.ROLE_ID+" INTEGER, "+Role.MANAGER_ID+" INTEGER, "+Role.PRIV_ID+" INTEGER )";
			r = db.executeDDL(createAuxSql);
		}else{
			String countQuery = "SELECT COUNT(*) FROM "+Role.AUX_ROLE_PRIV+" ";
			logsql(countQuery);
			Result c = db.executeCountQuery(countQuery);
			if(hasCount(c)){
				db.debugSelectAll("SELECT * FROM "+Role.AUX_ROLE_PRIV+" ");
			}
		}			
		/*
		final String AUX_PRIV = "aux_priv";
		if(!db.exists(AUX_PRIV)){
			String createAuxSql = "CREATE TABLE "+AUX_PRIV+" ("+AUTO_PK+", priv_name VARCHAR(50) )";
			check(db.executeDDL(createAuxSql));
		}			
		*/
		r.success();;
		return r;
	}


	protected boolean hasCount(Result executeCount) {
		Integer count = (Integer)executeCount.objectValue();//TODO use guard code.
		log("Count:"+count);
		return count>0;
	}


	@SuppressWarnings("rawtypes")
	protected Result fillAuxManagementTable(){
		Result r = new Result();
		if(!haveRecords(Manager.AUX_MANAGER)){
			List<Manager> managers = application.getManagers();
			int i=0;
			for(Manager m:managers){
				i++;
				String sInsert = "INSERT INTO "+Manager.AUX_MANAGER+" (id,manager) values ("+i+",'"+m.getName()+"')";
				String identitySql = "CALL IDENTITY();";
				logsql(sInsert);
				r = db.executeInsert(sInsert,identitySql);
				Integer managerId = (Integer)r.objectValue();//TODO safeguards
				m.setManagerId(managerId);
			}			
		}
		String countQuery = "SELECT COUNT(*) FROM "+Manager.AUX_MANAGER+" ";
		logsql(countQuery);
		Result c = db.executeCountQuery(countQuery);
		if(hasCount(c)){
			db.debugSelectAll("SELECT * FROM "+Manager.AUX_MANAGER+" ");
			
			List<Manager> managers = application.getManagers();
			int i=0;			
			for(Manager m:managers){
				i++;
				String sSelect = "SELECT ID FROM "+Manager.AUX_MANAGER+" WHERE manager='"+m.getName()+"' ";
				logsql(sSelect);
				r = db.executeSelect(sSelect, m);
			}	
			
		}		
		r.success();;
		return r;
	}
	
	protected boolean haveRecords(String table){
		boolean rv = false;
		String countQuery = "SELECT COUNT(*) FROM "+table;
		logsql(countQuery);
		Result dbr = db.executeCountQuery(countQuery);
		if(dbr.isSuccessful()){
			rv = ((Integer) dbr.objectValue()) >0;
		}else{
			log("haveRecords("+table+")"+dbr.name());
		}
		return rv;
	}


	
	protected void logsql(String q){
		String msg = "ApplicationManager: SQL:'"+q+"'";
		super.log(msg);
	}
	protected void log(String s){
		String msg = "ApplicationManager: "+s;
		super.log(msg);
	}

	private Result createViewUser(Screen.Button button, User user, boolean redirect) {
		Result result = new Result();
		Screen next;
		next = new Screen(this,button.destination());
		if(redirect){
			next.makeRedirect();
		}
		//Integer usrId = user.getId();
		
		next.setValue(Screen.VIEW_USER, user);
		List<Role> roleList = new ArrayList<Role>();
		result = listRoles(user, roleList);//TODO check if successful
		
		List<Role> allRoles = new ArrayList<Role>();
		result = listAllRoles(allRoles);
		
		subtractRolesNotAssigned(allRoles, roleList);
		
		next.setValue(Screen.USER_ROLES,roleList);
		next.setValue(Screen.AVAILABLE_ROLES,allRoles);
		result.objectValue(next);
		result.success();
		return result;
	}
	


	private Result createViewRole(Button button, Role role, boolean redirect) {
		Result result = new Result();
		Screen next;
		next = new Screen(this,button.destination());
		if(redirect){
			next.makeRedirect();
		}
		//Integer usrId = user.getId();
		result = listPrivileges(role); 
		next.setValue(Screen.VIEW_ROLE, role);
		next.setValue(Screen.AVAILABLE_ENTITIES,application.getManagers());
		next.setValue(Screen.AVAILABLE_PRIVILEGES,Privilege.values());

		result.objectValue(next);
		
		result.success();
		return result;
	}	
	

	private Result getUserList(Screen.Button button) {
		Result result;
		Screen next;
		List<User> userList = new ArrayList<User>();
		result = listUsers(userList);
		if(result.isSuccessful()){
			next = new Screen(this,button.destination());
			next.setValue(Screen.USERS_LIST, userList);
			result.objectValue(next);
		}else{
			log(result.getReason().name());
			log(result.allMessages());
		}
		return result;
	}	
	
	private Result getRoleList(Screen.Button button) {
		Result result;
		Screen next;
		List<Role> roleList = new ArrayList<Role>();
		result = listAllRoles(roleList);
		if(result.isSuccessful()){
			next = new Screen(this,button.destination());
			next.setValue(Screen.AVAILABLE_ROLES,roleList);
			result.objectValue(next);
		}else{
			log(result.getReason().name());
			log(result.allMessages());
		}
		return result;
	}	

	@SuppressWarnings("rawtypes")
	private Result getEntityList(Screen.Button button) {
		Result result = new Result();
		Screen next;
		List<Manager> entityList = application.getManagers();
		next = new Screen(this,button.destination());
		next.setValue(Screen.AVAILABLE_ENTITIES,entityList);
		result.objectValue(next);
		result.success();			
		return result;
	}		

	

	

}
/*
 * x create the database for the app manager if it doesn't exist.
 * look at the managers and create rows if they don't exist for the aux data on entities
 * 
 * create the database for the application if it doesn't exist and a table for each manager.
 * read the models from the entity/manager tables if they already do exist
 * create the model auxillary table in the appman database if it doesn't exist 
 * insert a model for each one in the db if they don't exist, relate to the manager-entity in appman
 *   double check the relationships if they do exist.
 *   
 * The admin user logs in and can start to add users, roles, and privileges for roles on each entity in a table.
 * The admin can put other auxillary data on models etc. (create time, state, current owner, share role, etc)
 * 
 * The application runner doesn't need to check the database tables for the application.
 * It will assume that it's there.  Therefore this will always run first.
 * 
 * This will also create the first admin user.
 *   
 */	 
/*


ACCESS / WORKFLOW(special application database)
privilege: no-access, read, read-write, read-create, read-delete/archive

read-lock, read-submit/share/notify, read-sign, read-reject/unlock, read-unsign, admin (assign,delete), custom
   
admin: 
admin role (cannot be deleted)
create user
create role
(un)assign user to role (*-*)
(un)assign model privilege to role. (*-*)
check privilege access to model via given role and model.
log access to model using privilege.
log write transaction.
undo transaction


*/
/*
 * the load on startup will instantiate this using config and pass in the right application
 */

//list of models and currently owned roles.

//models    role currently shown to. 

//model     state   role(s)

//requires sa

/*

public enum Privilege {
	NONE,READ,READWRITE,OWNER,ADMIN,SA   //how about the difference between a role who can see if searched but not shown in work list.
}

*/