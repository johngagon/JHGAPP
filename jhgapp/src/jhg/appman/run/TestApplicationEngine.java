package jhg.appman.run;

import java.util.*;

import jhg.*;
import jhg.account.Accounting;
import jhg.account.Balance;
import jhg.appman.ApplicationManager;
import jhg.appman.SimpleApplicationManager;
import jhg.appman.TestConfig;
import jhg.model.Manager;
import jhg.model.Model;
import jhg.model.ModelHistory;
import jhg.model.Value;

public class TestApplicationEngine {
	
	public static final Boolean DEBUG_STACK = Boolean.FALSE;
	
	public static void main(String[] args){	
		
		
		log("main");
		try{
			test();
		}catch(ApplicationException ae){
			log("!!! START TRACE APPLICATION EXCEPTION : "+ae.getMessage());
			if(DEBUG_STACK){
				ae.printStackTrace();
			}
		}catch(IllegalStateException ise){
			log("!!! START TRACE ILLEGAL STATE EXCEPTION : "+ise.getMessage());
			if(DEBUG_STACK){
				ise.printStackTrace();
			}
		}
	}	
	
	public static void test() throws ApplicationException{
		ApplicationEngine engine = new SimpleApplicationEngine();
		
		ApplicationManager manager = new SimpleApplicationManager(new TestConfig("testing"));
		//manager.resetDatabase();
		manager.init();
		engine.init(manager);
		
		UserSession session = UserSession.createInstance();
		log("\n\n\n\n------------------------------------------------");
		
		
		log("Login.");
		check("login",engine.login("clark","smallville",session));//"joe","sloppy", "frank","cheddar"

		
		log("List roles.");
		List<Role> roles = new ArrayList<Role>();
		check("listRoles",engine.listRoles(session, roles));//any user.
		Role role = null;
		for(Role r:roles){
			logIndent(2,"Role:"+r.getName());
			if(role==null){
				role = r;
			}
		}
		checkNull("Superrole is null.",role);

		
		log("Select role.");
		check("selectRole",engine.selectRole(role, session));

		log("List available actions on balance.");
		List<Action.Code> codes = new ArrayList<Action.Code>();
		check("listAvailableActions: balance ",engine.listAvailableActions(session, Balance.BALANCE, codes));
		for(Action.Code c:codes){
			log("Code:"+c.name());
		}
		doublespace();
		/*
		log("Create model.1");
		Map<String,String> fieldValues1 = new Hashtable<String,String>();
		fieldValues1.put(Balance.OWNER, "John H. Gagon");
		check("createModel",engine.createModel(userSession, Balance.BALANCE, fieldValues1));
		
		
		log("Create model.2");
		Map<String,String> fieldValues2 = new Hashtable<String,String>();
		fieldValues2.put(Balance.OWNER, "Ray L. LaValley");
		check("createModel",engine.createModel(userSession, Balance.BALANCE, fieldValues2));
		*/
		
		log("List available actions model id");
		List<Action.Code> codes2 = new ArrayList<Action.Code>();
		check("listAvailableActions",engine.listAvailableActions(session, Balance.BALANCE, "2", codes2));
		for(Action.Code c:codes2){
			log("Code for 1:"+c.name());
		}		
		
		log("Lookup Models");
		List<Model> fillList = new ArrayList<Model>();
		check("lookupModels",engine.lookupModels(session,Balance.BALANCE, fillList));
		log("Model Count Balance:"+engine.countModels(Balance.BALANCE));
		for(Model m:fillList){
			log("Model No.:"+m.getId());
			log("Model Ver No.:"+m.getVersionId());
			log("Model F:owner:"+m.getValue(Balance.OWNER));
			log("Model state:"+m.getState().name());
		}
		
		log("View Model");
		ModelResult viewModelResult = engine.viewModel(session,Balance.BALANCE,"2"); 
		check("viewModel",viewModelResult);
		Model m = viewModelResult.getModel();
		log("Model No.:"+m.getId());
		log("Model Ver No.:"+m.getVersionId());
		log("Model F:owner:"+m.getValue(Balance.OWNER));
		log("Model state:"+m.getState().name());
		log("Model ownerid:"+m.getOwnerId());
		log("Model approverid:"+m.getApproverId());
		log("Model open:"+m.isReserved());
		log("Model use by:"+m.getInUseBy());
		doublespace();
		
		/*
		log("Open model.");
		check("openModel",engine.openModel(userSession,m));
		log("Username ID/name:"+userSession.getUser().getId()+"/"+userSession.getUser().getName());
		log("Model No.:"+m.getId());
		log("Model open:"+m.isReserved());
		log("Model use by:"+m.getInUseBy());
		log("Model used since:"+m.getInUseSinceDate());
		doublespace();
		
		log("Update");
		check("update",engine.update(userSession, m, Balance.OWNER, "Mary Thomas"));
		log("Model No.:"+m.getId());
		log("Model owner:"+m.getValue(Balance.OWNER));
		doublespace();		

		log("Close model. No changes");
		check("closeModel",engine.closeModel(userSession,m));
		log("Username ID/name:"+userSession.getUser().getId()+"/"+userSession.getUser().getName());
		log("Model No.:"+m.getId());
		log("Model owner:"+m.getValue(Balance.OWNER));
		log("Model open:"+m.isReserved());
		doublespace();		
		
		log("Open model.");
		check("openModel",engine.openModel(userSession,m));
		log("Username ID/name:"+userSession.getUser().getId()+"/"+userSession.getUser().getName());
		log("Model No.:"+m.getId());
		log("Model open:"+m.isReserved());
		log("Model use by:"+m.getInUseBy());
		log("Model used since:"+m.getInUseSinceDate());
		doublespace();		
		
		log("Update");
		check("update",engine.update(userSession, m, Balance.OWNER, "Ray L. LaValley"));
		log("Model No.:"+m.getId());
		log("Model owner:"+m.getValue(Balance.OWNER));
		doublespace();		
		
		log("Save");
		check("save",engine.save(userSession, m));
		log("Model No.:"+m.getId());
		log("Model owner:"+m.getValue(Balance.OWNER));
		doublespace();			
		
		 */
		
		log("Publish.");
		check("publish",engine.publish(session,m));
		doublespace();
		
		/* 
		 * TODO client end test these
		 *  test rights, workflow testing.
		 *  (all the other resume stuff)
		 *  
		 *  public Result publish(UserSession session, Model model)
		 *  public Result unpublish(UserSession session, Model model) 
		 *  public Result share(UserSession session, Model model) 
		 *  public Result unshare(UserSession session, Model model) 
		 *  public Result submit(UserSession session, Model model,	String comment) 
		 *  public Result unsubmit(UserSession session, Model model, String comment) 
		 *  public Result approve(UserSession session, Model model, String signature, String comment)
		 *  public Result unapprove(UserSession session, Model model, String signature, String comment)
		 *  public Result reject(UserSession session, Model model, String signature, String comment)
		 *  public Result unreject(UserSession session, Model model, String signature, String comment)
		 *  public Result transferOwner(UserSession session, User newOwner, Model model)
		 *  public Result assignApprover(UserSession session, User approver, Model model) 
		 *  public Result export(UserSession session, String entity, StringBuffer buffer) 
		 *  public Result delete(UserSession session, Model model, String comment) 
		 *  public Result archive(UserSession session, Model model, String comment) 
		 *  public Result getHistory(UserSession session, Model model, List<ModelHistory> historyToFill) 
		 *  
		 *  Then start to do cleaning, then UI.
		 *  
		 */
		
		log("Importing from file.");
		String filename = "bank.txt";
		log("Reading:"+filename);
		List<String> contents = FileUtil.read(filename);//we may get this in a textarea webform instead.
		ImportResults ir = engine.importModels(session, Accounting.ENTRY, contents);
		//RESUME test client: debug import results, implement import results
		//TODO after done with imports, (the rest of this), start working on the first UI and deploying, cleaning this up.
		//TODO RIGHT after that, free to get work.
		
		//TODO after done with that first hard coded UI look at the various toolkits for UI/web ajax.
		//TODO import should also allow the same kind of "open session" that update allows or we just do create with all serverside, non-ajax validation to make it more minimal/transactional.
		
		
		doublespace();

		log("Logging out.");
		check("logout",engine.logout(session));
		doublespace();

		log("Shutting down.");
		engine.shutdown();			
		
/*
		check("cuser",manager.createUser("clark","smallville"));
		check("cuser",manager.createUser("joe","sloppy"));
		check("cuser",manager.createUser("frank","cheddar"));
		check("crole",manager.createRole("superman"));//1
		check("crole",manager.createRole("accountant"));//2
		check("crole",manager.createRole("bookkeeper"));//3
		check("assign",manager.assign("2", "2"));//joe,accountant
		check("assign",manager.assign("3", "3"));//clark,superman
		check("assign",manager.assign("1", "1"));//frank,bookkeeper
		//role,entity,priv
		check("grant",manager.grant("1","*","7"));
		check("g)rant",manager.grant("2","1","2"));
		check("grant",manager.grant("2","2","2"));
		check("grant",manager.grant("2","3","5"));
		check("grant",manager.grant("2","4","6"));
		check("grant",manager.grant("2","5","2"));
		check("grant",manager.grant("3","4","5"));		
 */
		
		
		
	
	}

	private static void doublespace() {
		System.out.println("\n\n");
	}
	
	private static void checkNull(String message, Object o) {
		if(o==null)throw new IllegalStateException(message);
	}

	private static void check(String command, ModelResult r) {
		logCommand(command);
		log("ModelResult:"+r.isSuccessful());
		if(r.isSuccessful()){                //TODO make all results follow some of the same interface and make base class.
			List<Result> results = r.getResults();
			Integer i=0;
			for(Result _r:results){
				i++;
				check("Field validation result ("+i.toString()+"):",_r);
			}
			if(r.hasModel()){
				log("Model:"+r.getModel());
			}
		}else{
			log("MR Reason:"+r.getReason().name());
			//results;
			List<Result> results = r.getResults();
			Integer i=0;
			for(Result _r:results){
				i++;
				check("Field validation result ("+i.toString()+"):",_r);
			}
			
			//messages:
			Messages m = r.getMessages();
			List<String> messages = m.get();
			logIndent(1,"MR Messages:");
			for(String s:messages){
				logIndent(2,s);
			}	
			
			//exceptions.
			if(r.hasException()){
				Exception e = r.getException();
				logIndent(1,"MR Exception("+e.getClass().getName()+"):"+e.getMessage());
			}
			
		}
		System.out.println("");
	}

	/* TODO start : "Refactor" - this with the other tester  */
	private static void check(String command,Result r) {
		logCommand(command);
		
		logResult(r.name());
		if(r.notSuccessful()){
			
			if(r.hasMessages()){
				Messages m = r.getMessages();
				List<String> messages = m.get();
				logIndent(1,"Messages:");
				for(String s:messages){
					logIndent(2,s);
				}
			}
			if(r.hasException()){
				Exception e = r.exception();
				logIndent(1,"Exception:"+e.getMessage());
				//e.printStackTrace();
			}
			if(r.hasValue()){
				Value v = r.value();
				logIndent(1,"Value:'"+v+"'");
			}
			throw new IllegalStateException(command+" not successful.");
		}
		//log("Command Successful: "+command+".");
		System.out.println("\n");
	}	

	private static void log(String s) {
		System.out.println("TestApplicationEngine: "+s);
	}	
	
	private static void logIndent(int I, String s) {
		for(int i=0;i<I;i++){
			System.out.print("  ");
		}
		System.out.println(s);
	}

	private static void logCommand(String s){
		log("Command:'"+s+"'");
	}
	
	private static void logResult(String s){
		log("Result:'"+s+"'");
	}	
	/* TODO end: "Refactor" */
	
	//cleanup happens before UI is done.
	//UI will test the application for bank statements to replace the spreadsheet.
	//it can eventually interface with email receipts or a special "receipt" email.
	
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
	 * grant(String role, String entity,String priv)                 OWN,APPROVE,SUPER,ADMIN
	 * remove(String entity, Integer id)                             OWN
	 * archive(String entity, Integer id)                            APPROVE
	 * purgeExport(String entity)                                    APPROVE
	 * shareRead(String entity, Integer id)                          OWN, (until this is done, only the owner can see/lookup,etc)
	 * shareWrite(String entity, Integer id)                         OWN  (until this is done, other owners and writers cannot write and only the owner can write.)
	 * customMethods?
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

}
/*
	x public void init();
	x public void log(String message);
	public void log(UserSession session, Model model, Event event, Map<String,String> args);
	public List<UserSession> openSessions();
	public int status();
	public Result login(String username, String passwd, UserSession blankSession);
	public Result listRoles(UserSession session, List<Role> roleList);	
	public Result selectRole(Role role, UserSession loggedInSession);
	public Result listEntities(UserSession session, List<Manager> managers);//fill list
	public Result listAvailableActions(UserSession session, String entity);
	public Result listAvailableActions(UserSession session, String entity, String id);
	public ModelResult createModel(UserSession session, String entity, Map<String,String> reqFieldValues);
	public ModelResult readModel(UserSession session, String entity, String id);
	public ModelsResult importModels(UserSession session, String csvData);
	public Result logout(UserSession userSession);
	public void clearMessages(UserSession userSession);
	public Result openModel(UserSession session, String entity, String id);
	public Result isOpen(UserSession session, String entity, String id, User openedBy, Date whenopened);
	public ModelsResult allModels(UserSession session, String entity, Sort sort);
	public ModelsResult lookup(UserSession session, String entity, String[] fieldnames);
	public ModelsResult find(UserSession session, String entity, String findMethodName, String[] params);
	public PageResult paginate(ModelsResult mr, int _pageSize);
	public ModelsResult subset(UserSession session, String entity, List<Integer> ids);
	public Result update(UserSession session, String entity, Integer id, String fieldName, String value); //does validation and updates object but not save, does not make audit entry
	public Result save(UserSession session, String entity, Integer id);//puts entry, also unopens/unreserves/closes
	public Result export(UserSession session, String entity);
	public Result publish(UserSession session, String entity, String id);
	public Result unpublish(UserSession session, String entity, String id);
	public Result share(UserSession session, String entity, String id);	
	public Result unshare(UserSession session, String entity, String id);
	public Result getHistory(UserSession session, String entity, String id, List<ModelHistory> historyToFill);
	public Result submit(UserSession session, String entity, String id, String comment);
	public Result unsubmit(UserSession session, String entity, String id, String comment);
	public Result approve(UserSession session, String entity, String id, String signature, String comment);
	public Result reject(UserSession session, String entity, String id, String signature, String comment);
	public Result unapprove(UserSession session, String entity, String id, String signature, String comment);
	public Result unreject(UserSession session, String entity, String id, String signature, String comment);
	public Result delete(UserSession session, String entity, String id, String comment);
	public Result archive(UserSession session, String entity, String id, String comment);
	public void reviewLocks();	
	public void reserve(Model m);
	public void shutdown();
	public Result listUsers(UserSession session, List<User> userList);
	
	
*/