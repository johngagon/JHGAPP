package jhg.appman;

import java.util.List;

import jhg.Messages;
import jhg.Privilege;
import jhg.Result;
import jhg.model.Value;

public class TestApplicationManager {

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
	public static void main(String[] args){
		//TODO can use the Factory.
		ApplicationManager manager = new SimpleApplicationManager(new TestConfig("testing"));
		//manager.resetDatabase();
		manager.init();
		//TODO subst services.
		check("login",manager.login("johngagon","thunder"));//use command.
		check("cuser",manager.createUser("clark","smallville"));
		check("cuser",manager.createUser("joe","sloppy"));
		check("cuser",manager.createUser("frank","cheddar"));
		final String Clark = "1";
		final String Joe = "2";
		final String Frank = "3";
		
		check("crole",manager.createRole("superman"));//1
		check("crole",manager.createRole("accountant"));//2
		check("crole",manager.createRole("bookkeeper"));//3
		final String SUPERMAN = "1";
		final String ACCOUNTANT = "2";
		final String BOOKKEEPER = "3";
		
		//user,role
		check("assign",manager.assign(Clark, SUPERMAN));
		check("assign",manager.assign(Joe,ACCOUNTANT));
		check("assign",manager.assign(Frank,BOOKKEEPER));
		
		//role,entity,priv

		final String ALL = "*";
		final String BALANCE = "1";
		final String ACCT_T = "2";
		final String ACCT = "3";
		final String ENTRY = "4";
		final String CYCLE = "5";
		check("grant",manager.grant(SUPERMAN,	ALL,		Privilege.SUPER.idString()));
		check("grant",manager.grant(ACCOUNTANT,	BALANCE,	Privilege.READ.idString()));
		check("grant",manager.grant(ACCOUNTANT,	ACCT_T,		Privilege.READ.idString()));
		check("grant",manager.grant(ACCOUNTANT,	ACCT,		Privilege.OWN.idString()));
		check("grant",manager.grant(ACCOUNTANT,	ENTRY,		Privilege.APPROVE.idString()));
		check("grant",manager.grant(ACCOUNTANT,	CYCLE,		Privilege.READ.idString()));
		check("grant",manager.grant(BOOKKEEPER,	ENTRY,		Privilege.OWN.idString()));
		//11 grants total, no duplicates.
		check("logout",manager.logout());//"johngagon"
		//TODO START HERE!
		
		//TODO right now result is an enum which means each member has combined messages.
		//Force that back into a class and use a reason enum.
		
		//TODO test that these cannot be performed without the admin logged in. right now it's just a single admin.
		//allow a backdoor that is programmatic (this is the simple manager we're testing though and other implementations or overrides can still be done.
		//validation of input on these methods we are testing.
	/*
1 balance
2 account_type
3 account
4 entry
5 cycle
0	NONE,	
1	LOOKUP, 
2	READ,	
3	WRITE,	
4	EXPORT,  
5	OWN,	
6	APPROVE, 
7	SUPER,    
x login johngagon thunder*    as admin, I can do anything by default and am the default owner.
x create user clark smallville
x create user joe sloppy     //create user <user> <pass>
x create user frank cheddar
x create role superman       
x create role accountant     
x create role bookkeeper     
x assign joe accountant
x assign frank bookkeeper
x assign clark superman
x grant superman   *       super  (all tables, all rights)
x grant accountant balance read
x grant accountant account_type read
x grant accountant cycle read
x grant accountant account owner   //the accountants who create will have private record, 
x grant accountant entry approve
//grant accountant account RW	
x grant bookkeeper entry owner

	 */
		
	}

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
			
			
		}
		System.out.println("");
	}
	
	private static void logIndent(int I, String s) {
		for(int i=0;i<I;i++){
			System.out.print("  ");
		}
		System.out.println(s);
	}

	private static void logCommand(String s){
		System.out.println("Command:'"+s+"'");
	}
	
	private static void logResult(String s){
		System.out.println("Result:'"+s+"'");
	}
	
}
