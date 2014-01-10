package jhg.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents your primary Application abstraction. 
 * It's a base class that contains a collection of managers which in turn 
 * contains a collections of models. You extend this base class to perform
 * any application level business logic, logic that is not manager specific and
 * so on.  It's purpose is to contain logic common to most applications like 
 * knowing what kinds of tables or collections of models you need.  To use this, 
 * you will subclass it in your own application package.  This is the primary 
 * point of extension for your application.  
 * 
 * smart custom application.
 * 
 * @author John
 *
 */
@SuppressWarnings("rawtypes")
public abstract class Application {

	/**
	 * Get the name of the application.
	 * 
	 * @return String object containing name.
	 */
	public abstract String getName();	
	
	/**
	 * This is where custom managers can be loaded and other startup tasks performed.
	 * 	 
	 */
	public abstract void initialize();	
	
	
	/**
	 * Construct an application with the given name.
	 * 
	 * @param _appname  the name to call this application.
	 */
	public Application(String _appname){
		super();
		managers = new ArrayList<Manager>();
		this.name = _appname;
	}
	
	/**
	 * Obtain a list of the model managers or model collection objects.
	 * 
	 * @return a List of Manager objects. 
	 */
	public List<Manager> getManagers(){
		return managers;
	}
	
	/**
	 * Get a manager by the name of the manager.
	 * 
	 * @param name  the name of the manager you want.
	 * @return  the Manager object with the given name.
	 */
	public Manager getManager(String name){
		Manager rv = null;
		for(Manager m:managers){
			if(m.getName().equals(name)){
				rv = m;
				break;
			}
		}
		return rv;
	}

	/**
	 * Get a manager by it's integer identifier.
	 * 
	 * @param entityId  The identifier commonly used as a protected storage id reference.
	 * @return  the Manager object wanted.
	 */
	public Manager getManagerById(Integer entityId) {
		Manager rv = null;
		for(Manager m:managers){
			if(m.getManagerId().equals(entityId)){
				rv = m;
				break;
			}
		}
		return rv;
	}
	
	@Override
	public String toString(){
		return this.name;
	}	
	
	protected List<Manager> managers;
	private String name;
	
}


	