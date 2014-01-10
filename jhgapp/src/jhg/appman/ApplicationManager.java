package jhg.appman;

import java.util.List;
import java.util.Map;

import jhg.Privilege;
import jhg.Result;
import jhg.Role;
import jhg.User;
import jhg.appman.Screen.Button;
import jhg.db.Database;
import jhg.model.Application;
import jhg.model.Manager;


/**
 * DOC
 * @author John
 */
public interface ApplicationManager {

	//public static final String USERLIST = "USERLIST";
	
	public static final String ID = "id";
	public static final String ID1 = "id1";
	public static final String ID2 = "id2";
	public static final String ID3 = "id3";
	
	
	/**
	 * DOC
	 */
	public void init();

	/**
	 * DOC
	 * 
	 * @return
	 */
	public Database getDatabase();
	
	/**
	 * DOC
	 * @return
	 */
	public Application getApplication();
	
	
	/**
	 * DOC
	 */
	public void resetDatabase();
	
	/**
	 * DOC
	 * 
	 * @param userId
	 * @return
	 */
	public Result findUser(String userId);
	
	/**
	 * DOC
	 * 
	 * @param roleId
	 * @return
	 */
	public Result findRole(String roleId);
	
	/**
	 * DOC
	 * 
	 * @return
	 */
	public Result listUsers(List<User> userList);	
	
	/**
	 * DOC
	 * 
	 * @param roleList
	 * @return
	 */
	public Result listAllRoles(List<Role> roleList);
	
	/**
	 * DOC
	 * @param user
	 * @param roleList
	 * @return
	 */
	public Result listRoles(User user, List<Role> roleList);
	
	/**
	 * DOC
	 * 
	 * @param role
	 * @param entityPrivs
	 * @return
	 */
	public Result listPrivileges(Role role);//, Map<Integer,Integer> entityPrivs);
	
	/**
	 * DOC
	 * 
	 * @param username
	 * @param pass
	 * @return
	 */
	public Result login(String username, String pass);	
	
	/**
	 * DOC
	 * 
	 * @param user
	 * @return
	 */
	public Result deleteUser(String user);
	
	
	/**
	 * DOC
	 * 
	 * @param user
	 * @return
	 */
	public boolean existsUser(String user);
	
	
	/**
	 * DOC
	 * 
	 * @param user
	 * @param pass
	 * @return
	 */
	public Result createUser(String user, String pass);
	
	
	/**
	 * DOC
	 * @param role
	 * @return
	 */
	public Result deleteRole(String role);
	
	/**
	 * DOC
	 * @param role
	 * @return
	 */
	public boolean existsRole(String role);
	
	/**
	 * DOC
	 * 
	 * @param role
	 * @return
	 */
	public Result createRole(String role);
	
	/**
	 * DOC
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public Result unassign(String userId, String roleId);
	
	/**
	 * DOC
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public boolean hasAssignment(String userId, String roleId);
	
	/**
	 * DOC
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public Result assign(String userId, String roleId);
	
	/**
	 * DOC
	 * 
	 * @param roleId
	 * @param entityId
	 * @param priv
	 * @return
	 */
	public Result grant(String roleId, String entityId, String priv);

	/**
	 * DOC
	 * 
	 * @param roleId
	 * @param entityId
	 * @param priv
	 * @return
	 */
	public Result ungrant(String roleId, String entityId, String priv);
	
	/**
	 * DOC
	 * 
	 * @param roleId
	 * @param entityId
	 * @param priv
	 * @return
	 */
	public boolean hasBeenGranted(String roleId, String entityId, String priv);
	
	
	/**
	 * DOC
	 * @return
	 */
	public Result logout();
	
	/**
	 * DOC
	 * @param role
	 * @param manager
	 * @param privilege
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean denyAccess(Role role, Manager manager, Privilege privilege);
	
	/**
	 * DOC
	 * @param command
	 */
	//public Result service(String command);

	/**
	 * DOC
	 * @return
	 */
	public String getApplicationName();

	/**
	 * DOC
	 */
	public void shutdown();

	/**
	 * DOC
	 * 
	 * @param button
	 * @param parameterMap
	 * @return
	 */
	public Result service(Button button, Map<String,String[]> parameterMap);//, Map<String,Object> valuesMap);

}
