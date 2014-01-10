package jhg;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import jhg.model.Application;
import jhg.model.Manager;

/**
 * DOC
 * 
 * @author John
 *
 */
public class Role {

	private Integer id;
	private String name;
	public static final String USER_ID = "user_id";
	public static final String ROLE_ID = "role_id";
	public static final String ROLEFLD = "role";
	public static final String AUX_ROLE = "aux_role";
	Map<Manager,Privilege> privileges;
	public static final String MANAGER_ID = "manager_id";
	public static final String PRIV_ID = "priv_id";
	public static final String AUX_ROLE_PRIV = "aux_rolepriv";
	
	/**
	 * DOC 
	 * TODO note may return null.
	 * 
	 * @param roles
	 * @param desiredRole
	 * @return
	 */
	public static Role findRole(List<Role> roles, String desiredRole){
		Role rv = null;
		for(Role role:roles){
			if(role.getName().equals(desiredRole)){
				rv = role;
			}
		}		
		return rv;
	}
	
	public Role(){
		super();
	}
	
	public Map<Manager,Privilege> getPrivileges(){
		return this.privileges;
	}
	
	public boolean isValid(){
		return (this.id!=null && this.name !=null);
	}
	
	public void init(Integer _id, String _name){
		this.id = _id;
		this.name = _name;
		privileges = new Hashtable<Manager,Privilege>();
	}	
	
	
	public void addPrivilege(Application app, Integer entityId, Integer privilegeId){
		Manager entity = app.getManagerById(entityId);
		try{
			privileges.put(entity,Privilege.values()[privilegeId]);//Check index perhaps. can go awry if removing a privilege.
		}catch(ArrayIndexOutOfBoundsException aiobe){
			throw new IllegalStateException("A stored privilege integer is no longer in Privilege enumeration or the bounds are one off.");
		}
	}
	
	/*
	public Role(String _name){
		this.id = 0;
		this.name = _name;
	}
	*/
	
	public void setId(int _id){
		this.id = _id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Integer getId(){
		return this.id;
	}
	
}
