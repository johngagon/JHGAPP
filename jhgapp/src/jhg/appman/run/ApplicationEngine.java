package jhg.appman.run;

import java.util.List;
import java.util.Map;

import jhg.Action;
import jhg.ApplicationException;
import jhg.ImportResults;
import jhg.ModelResult;
import jhg.ModelsResult;
import jhg.Result;
import jhg.Role;
import jhg.User;
import jhg.appman.ApplicationManager;
import jhg.appman.run.Screen.Button;
import jhg.model.Application;
import jhg.model.Manager;
import jhg.model.Model;
import jhg.model.ModelHistory;

@SuppressWarnings("rawtypes")
public interface ApplicationEngine {

	public static final int OFFLINE = 0;
	public static final int ONLINE = 1;
	
	public static final String ID = "id";
	public static final String ID1 = "id1";
	public static final String ID2 = "id2";
	public static final String ID3 = "id3";	
	public static final String MID = "m";
	//TODO (future) import "jobs". a "job" engine.
	
	
	
	
	/**
	 * DOC
	 * @throws ApplicationException 
	 */
	public void init(ApplicationManager _appman) throws ApplicationException;
	
	/**
	 * DOC
	 * 
	 * @return
	 */
	public int status();	

	/**
	 * DOC
	 * 
	 */
	public void shutdown();	
	
	/**
	 * DOC
	 * 
	 * @param message
	 */
	public void log(String message);
	
	/**
	 * DOC
     *
	 * @param session
	 * @param event
	 * @param args
	 */
	public void log(UserSession session, Model model, Action event, Map<String,String> args);
	
	/**
	 * DOC
	 * 
	 * @return
	 */
	public ApplicationManager getApplicationManager();	
	


	/**
	 * DOC
	 * 
	 * @param session
	 * @param roleList
	 * @return
	 */
	public Result listAllRoles(UserSession session, List<Role> roleList);	
	
	/**
	 * DOC
	 * NOTE: this is for admin
	 * 
	 * @param session
	 * @param user
	 * @param roleList
	 * @return
	 */
	public Result listRolesUser(UserSession session, String user, List<Role> roleList);
	
	/**
	 * DOC
	 * 
	 * @param session
	 * @param managers
	 * @return
	 */
	public Result listManagers(UserSession session, List<Manager> managers);
	
	/**
	 * DOC
	 * @return
	 */
	public List<UserSession> openSessions();
	
	/**
	 * DOC
	 * 
	 * @param user
	 * @param passwd
	 * @param email
	 * @return
	 */
	public Result register(String user, String passwd, String email);
	
	/**
	 * DOC
	 * 
	 * @param user
	 * @param blankSession
	 * @return
	 */
	public Result login(String username, String passwd, UserSession blankSession);
	
	/**
	 * DOC
	 * 
	 * @param userSession
	 * @return
	 */
	public Result logout(UserSession userSession);	
	
	/**
	 * DOC
	 * 
	 * @param user
	 * @return
	 */
	public Result listRoles(UserSession session, List<Role> roleList);		
	
	/**
	 * DOC
	 * @param role
	 * @param loggedInSession
	 * @return
	 */
	public Result selectRole(Role role, UserSession loggedInSession);

	/**
	 * DOC
	 * @param session
	 * @param entity
	 * @param fillList
	 * @return
	 */
	public Result listAvailableActions(UserSession session, String entity, List<Action.Code> fillList);
	
	/**
	 * DOC
	 * 
	 * @param session
	 * @param entity
	 * @param modelId
	 * @param fillList
	 * @return
	 */
	public Result listAvailableActions(UserSession session, String entity, String modelId, List<Action.Code> fillList);
	
	/**
	 * DOC
	 * 
	 * @param session
	 * @param managers
	 * @return
	 */
	public Result listEntities(UserSession session, List<Manager> managers);//fill list	

	/**
	 * DOC
	 * 
	 * @param entity
	 * @param reqFieldValues
	 * @return
	 */
	public ModelResult createModel(UserSession session, String entity, Map<String,String> reqFieldValues);
		
	/**
	 * DOC
	 * 
	 * @param session
	 * @param csvData
	 * @return
	 */
	public ImportResults importModels(UserSession session, String entity, List<String> csvDataLines);	
	
	/**
	 * DOC
	 * Requires LOOKUP.  
	 * TODO opens a paged output of models.
	 *  
	 * @param session
	 * @param entityId
	 * @param sort
	 * @return
	 */
	public Result lookupModels(UserSession session, String entityId, List<Model> models);	
	
	
	
	/**
	 * DOC
	 * Requires READ
	 * TODO (someday), make a table summary with various group counts on any field that is a repeated lookup value.
	 * TODO (someday), allow picking groups and doing precounts based on selections. (mini query form).
	 * TODO (someday), make the precount query carry over to a full QBE form.  this isn't designed for exhaustive.
	 * 
	 * We automatically have "saved searches" so that reasonable searches are allowed with good filtering.
	 * TODO use the PageResult class with another find method.
	 * 
	 * @param session
	 * @param entity
	 * @param findMethodName
	 * @param params
	 * @return
	 */
	public ModelsResult search(UserSession session, String entity, String findMethodName, String[] params);

	/**
	 * DOC
	 * Requires READ
	 * 
	 * @param session
	 * @param entity
	 * @param id
	 * @return
	 */
	public ModelResult viewModel(UserSession session, String entity, String id);
	
	/**
	 * DOC
	 * Assumed model was retrievable but not necessarily writable.
	 * @param session
	 * @param model
	 * @return
	 */
	public Result openModel(UserSession session, Model model);
	
	/**
	 * DOC
	 * Assumed model was retrievable but not necessarily writable.
	 * @param session
	 * @param entity
	 * @param id
	 * @return
	 */
	public Result closeModel(UserSession session, Model model);	
	
	/**
	 * DOC
	 * @param session
	 * @param model
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public Result update(UserSession session, Model model, String fieldName, String value); //does validation and updates object but not save, does not make audit entry

	/**
	 * DOC
	 * 
	 * @param session
	 * @param entity
	 * @param id
	 * @return
	 */
	public ModelResult save(UserSession session, Model model);//puts entry, also unopens/unreserves/closes

	/**
	 * DOC
	 * 
	 * @param session
	 * @param entity
	 * @param id
	 * @return
	 */
	public Result publish(UserSession session, Model model);	
	
	/**
	 * DOC
	 * @param session
	 * @param entity
	 * @param id
	 * @return
	 */
	public Result unpublish(UserSession session, Model model);	
	
	/**
	 * DOC
	 * 
	 * @param session
	 * @param entity
	 * @param id
	 * @return
	 */
	public Result share(UserSession session, Model model);
	
	/**
	 * DOC
	 * 
	 * @param session
	 * @param entity
	 * @param id
	 * @return
	 */
	public Result unshare(UserSession session, Model model);	
	
	/**
	 * DOC
	 * 
	 * @param session
	 * @param entity
	 * @param id
	 * @param comment
	 * @return
	 */
	public Result submit(UserSession session, Model model, String comment);	
	
	/**
	 * DOC
	 * 
	 * @param session
	 * @param entity
	 * @param id
	 * @param comment
	 * @return
	 */
	public Result unsubmit(UserSession session, Model model, String comment);	
	
	/**
	 * DOC
	 * 
	 * @param session
	 * @param entity
	 * @param id
	 * @param signature
	 * @param comment
	 * @return
	 */
	public Result approve(UserSession session, Model model, String signature, String comment);
		
	/**
	 * DOC  sends it back to submitted state
	 * 
	 * @param session
	 * @param entity
	 * @param id
	 * @param signature
	 * @param comment
	 * @return
	 */
	public Result unapprove(UserSession session, Model model, String signature, String comment);

	/**
	 * DOC
	 * 
	 * @param session
	 * @param entity
	 * @param id
	 * @param signature
	 * @param comment
	 * @return
	 */
	public Result reject(UserSession session, Model model, String signature, String comment);

	/**
	 * DOC sends it back to submitted state for re-evaluation. Handles accidental approvals/rejections.
	 * 
	 * @param session
	 * @param entity
	 * @param id
	 * @param signature
	 * @param comment
	 * @return
	 */
	public Result unreject(UserSession session, Model model, String signature, String comment);

	/**
	 * DOC
	 * @param session
	 * @param approver
	 * @param model
	 * @return
	 */
	public Result transferOwner(UserSession session, User newOwner, Model model);
	
	/**
	 * DOC
	 * aka accept
	 * @param session
	 * @param approver
	 * @param model
	 * @return
	 */
	public Result assignApprover(UserSession session, User approver, Model model);

	/**
	 * DOC
	 * Requires EXPORT
	 * 
	//use manager search methods. public Result search(UserSession session, 
	//String entity, Filter filter, Sort sort);
	//Not an SQL search: basic no join search, no grouping, no sub  select from where order
	 * 
	 * @param session
	 * @param entity
	 * @return
	 */
	public Result export(UserSession session, String entity, StringBuffer buffer);
	
	/**
	 * DOC
	 * deletes without marking it as archived. Usually you cannot delete something past submission.
	 * submitted is the only state which you cannot delete. you have to unsubmit first. the owner
	 * can do unsubmits if it was an accident usually or the owner must request the unsubmit from approver.
	 * can change these rules on the application.
	 *  
	 * @param session
	 * @param entity
	 * @param id
	 * @return
	 */
	public Result delete(UserSession session, Model model, String comment);
	
	/**
	 * DOC
	 * usually you archive to delete what is approved or rejected and old. It's up to the user to export the data.
	 * 
	 * 
	 * 
	 * @param session
	 * @param entity
	 * @param id
	 * @param comment
	 * @return
	 */
	public Result archive(UserSession session, Model model, String comment);
	
	/**
	 * DOC
	 * 
	 * @param session
	 * @param entity
	 * @param id
	 * @param historyToFill
	 * @return
	 */
	public Result getHistory(UserSession session, Model model, List<ModelHistory> historyToFill);
	
	/**
	 * DOC 
	 * TODO consider what security to require.
	 * 
	 * TODO may need to make a special table to get the privileges on this right.
	 * 
	 * @param session
	 * @param entity
	 * @param findMethodName
	 * @param params
	 * @return
	 */
	public Result customOperation(UserSession session, String entity, String id, String findMethodName, String[] params);

	/**
	 * DOC
	 * @param balance
	 * @return
	 */
	public Integer countModels(String entity);

	/**
	 * DOC
	 * @param button
	 * @param parameterMap
	 * @return
	 */
	public Result service(UserSession usersession, Screen currentScreen, Button button, Map parameterMap);

	/**
	 * DOC
	 * @param roleId
	 * @return
	 */
	public Result findRole(String roleId);

	/**
	 * DOC
	 * @return
	 */
	public Application getApplication();
	
}
/*
	//TODO public Result rollback(.... versionId);

	 * DO C
	 * no result means not opened.
	 * 
	 * @param session
	 * @param entity
	 * @param id
	 * @param openedBy
	 * @param whenopened
	 * @return

	public Result isOpen(UserSession session, String entity, String id, User openedBy, Date whenopened);

	 * DO C 
	 * Requires LOOKUP
	 * 
	 * @param session
	 * @param entity
	 * @param fieldnames
	 * @return

	public ModelsResult lookup(UserSession session, String entity, String[] fieldnames);
	
	 * DO C
	 * 
	 * @param mr
	 * @param _pageSize
	 * @return

	public PageResult paginate(ModelsResult mr, int _pageSize);

	 * DO C
	 * 
	 * @param mr
	 * @param ids
	 * @return
	public ModelsResult subset(UserSession session, String entity, List<Integer> ids);


*/






/* x login
 * x logout
 * x clear
 * 
 * public Result x(User user, Role role)
 * x lookup(String entity)                                         LOOKUP
 * x search(Map<String,String>params)                              READ    (must have access to all tables)
 * x read(String entity, Integer id)                               READ
 * x open
 * x update(String entity, Integer id, String field, String value) WRITE
 * x save(String entity, Integer id, Map<String,String> values)  WRITE
 * x create(String entity, Map<String,String> reqFieldValues);     OWN
 * x import(String entity, Map<String,String> fieldValues);        OWN
 * x export(String entity)                                         EXPORT
 * x lock(String entity, Integer id)                               OWN
 * x unlock(String entity, Integer id)                             APPROVE
 * x approve(String entity, Integer id)                            APPROVE
 * x reject(String entity, Integer id)                             APPROVE
 * 
 * grant(String role, String entity,String priv)                 OWN,APPROVE,SUPER,ADMIN
 * remove(String entity, Integer id)                             OWN
 * archive(String entity, Integer id)                            APPROVE
 * purgeExport(String entity)                                    APPROVE
 * shareRead(String entity, Integer id)                          OWN, (until this is done, only the owner can see/lookup,etc)
 * shareWrite(String entity, Integer id)                         OWN  (until this is done, other owners and writers cannot write and only the owner can write.)
 * customMethods?
 */

/**
 * D OC
 */
//public void reviewLocks();	

/* x login
 * x logout
 * x clear
 * 
 * public Result x(User user, Role role)
 * x lookup(String entity)                                         LOOKUP
 * x search(Map<String,String>params)                              READ    (must have access to all tables)
 * x read(String entity, Integer id)                               READ
 * x open
 * x update(String entity, Integer id, String field, String value) WRITE
 * x save(String entity, Integer id, Map<String,String> values)  WRITE
 * x create(String entity, Map<String,String> reqFieldValues);     OWN
 * x import(String entity, Map<String,String> fieldValues);        OWN
 * x export(String entity)                                         EXPORT
 * x lock(String entity, Integer id)                               OWN
 * x unlock(String entity, Integer id)                             APPROVE
 * x approve(String entity, Integer id)                            APPROVE
 * x reject(String entity, Integer id)                             APPROVE
 * 
 * grant(String role, String entity,String priv)                 OWN,APPROVE,SUPER,ADMIN
 * remove(String entity, Integer id)                             OWN
 * archive(String entity, Integer id)                            APPROVE
 * purgeExport(String entity)                                    APPROVE
 * shareRead(String entity, Integer id)                          OWN, (until this is done, only the owner can see/lookup,etc)
 * shareWrite(String entity, Integer id)                         OWN  (until this is done, other owners and writers cannot write and only the owner can write.)
 * customMethods?
 */


/**
 * D OC
 * 
 * @param m
 */
//public void reserve(Model m);




/*
 * reads the application manager for user access rules
 * reads the models for rules.
 * reads or synchronizes the database
 * 
 * reads anything cached.  cache can be done partially and by choice of design
 * 
 */




/*
Create the database tables themselves.
Load the records if any.
Login a user.
Logout.
Show the user the modes, commands, models, custom commands, etc.

* Process inserting accounts one at a time. (instead of how the test did it).
Have these save and check the privileges against all these.

*Make rules for these privileges.

* Process updates (lock records), deletes, locks, Standard process for notifications (in a queue for pickup) upon 
Standard process for long jobs. (notification queues).

*Save to the database after updating memory (blocking) and update flags in the appman. apprun.
Lock during edit. Other flags that govern how up to date the data is, how deleted, rules for that, etc. Versioning, undo/redo,rollback

Import jobs, validation and notification features (inboxes). (like the Result Message but asynch).  messages and such should
be stored to preserve states, provide notifications upon entering.
Provide power for importing. (maps, defaults, structural stuff) OR just have it read from a premassaged area. this can be put off
external.   (import from another database given similar structures)

Export the data to a delimited format. (or to another database given similar structures)

Import tools can prepare an import area. Export tools can prepare replication areas and cover reporting and data warehousing needs.
(read formats like xml, schema, json, etc. etc.) Use immutables.

Data protection and signing records/recording signatures.

Logging user activities to a file log.
USER MACHINE TIME ACTION processes
Application Action(s)
Database debugs 
(see my emails for good logging)


Tree structures?
Versioning.
Backups - database synchronizing. (assume an exclusive access database)
Ensure we have enough power in database areas of querying.

Test: import bank files. edit records, produce reports that I typically have in spreadsheets.  Budgets, savings, income, balance etc.

System maintenance.

Login and password, password recovery, registration etc, lockout rules, expiration rules, reset expiration rules, captchas etc.

 */