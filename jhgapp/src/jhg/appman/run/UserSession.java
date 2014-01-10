package jhg.appman.run;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.http.HttpSession;

import jhg.Base;
import jhg.Messages;
import jhg.Privilege;
import jhg.Result;
import jhg.Role;
import jhg.User;
import jhg.model.Manager;

/**
 * DOC
 * 
 * @author John
 *
 */
@SuppressWarnings("unused")
public class UserSession extends Base {
	
	public static UserSession createInstance(){
		return new UserSession();
	}
	
	private Role role;
	private User user;
	private Calendar startTime;
	private Messages messages;
	private long sessionTime;
	
	private InetAddress ip;     //TODO impl
	private HttpSession session;//TODO impl
	
	private UserSession(){
		super();
		messages = new Messages();
	}

	
	public Messages messages(){
		return this.messages;
	}
	
	public void start(User u){
		log("Starting session for user:"+u.getName());
		this.user = u;
		startTime = Calendar.getInstance();
		sessionTime = 1000*60*30;//30 minutes
	}
	
	public void setRole(Role r){
		if(user==null)throw new IllegalStateException("User not set yet.");
		this.role = r;
	}
	
	public boolean isValid(Result r){
		boolean rv = false;
		if(user!=null ){    //don't check role yet.
			Calendar cal = Calendar.getInstance();//current time and timezone.
			long time = cal.getTimeInMillis();
			long diff = time - startTime.getTimeInMillis();
			if(diff <= sessionTime){
				rv = true;//otherwise leave the result alone.
			}else{
				r.sessionInvalid("User session invalid: Timed out.");
				//rv is still false. TODO ensure close all open locks on models.
			}
		}else{
			r.sessionInvalid("User session invalid: Not initialized.");
		}
		return rv;
	}
	
	/*
	public boolean hasRole(){
		return this.role!=null;
	}
	*/
	
	public void end(){
		log("Starting session for user:"+user.getName());
		//TODO impl
	}
	
	public Role getCurrentRole(){
		noNullReturns("getCurrentRole",this.role);
		return this.role;
	}
	
	public User getUser(){
		noNullReturns("getUser",this.user);
		return this.user;
	}

	public long timeLeft(){
		long now = Calendar.getInstance().getTimeInMillis();
		long diff = now - startTime.getTimeInMillis();		
		return sessionTime - diff;
	}
	
	public void invalidate() {
		user=null;
		role=null;
	}
	
	@Override
	public void log(String s){
		String msg = "UserSession: "+s;
		super.log(msg);
	}

	/**
	 * DOC
	 * 
	 * @param manager
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Privilege getPrivilege(Manager manager) {
		String M = "getPrivilege";
		noNullParams(M,manager);
		Map<Manager,Privilege> privs = role.getPrivileges();
		//System.out.println("UserSession: privilege count:"+privs.size());//TODO system.out
		Privilege priv = privs.get(manager);
		if(priv==null){
			priv = Privilege.NONE;
			//System.out.println("UserSession: User "+user.getName()+" privilege on role: "+role.getName()+" not set for :"+manager.getName());
		}
		//System.out.println("UserSession: User "+user.getName()+" privilege for "+manager.getName()+" is: "+priv.name());
		//noNullReturns(M,priv);
		return priv;
	}	
	
	
}
