package jhg.appman;


import java.util.ResourceBundle;

import jhg.Base;
import jhg.model.Application;

/**
 * Configuration for the container. Must be public because of use on error page.
 *
 * @author jgagon
 */
public class Config extends Base {

	/*
	public boolean	CLEAR_DATA;
	public boolean  AUTHENTICATE;
	public String	
		MODEL_PROVIDER, SERVICE_PROVIDER, ADMIN_EMAIL, USERDATABASE, DATABASE_NAME, APP_NAME,
		DEFAULT_MESSAGE_RESOURCE,ADMIN_LOGIN,ADMIN_PASSWORD;
	 */
	public String APPLICATION, ADMIN_LOGIN, ADMIN_PASSWORD, DATABASE, DRIVER, DBURL;
	protected String configBundle;       
	private ResourceBundle config = null;
	
	
	/**
	 * Set the constants of the configuration.
	 */
	public Config(String bundleName){
		this.configBundle = bundleName;
	}

	/**
	 * DOC
	 */
	protected void init()
	{
		config = ResourceBundle.getBundle(configBundle);		
		APPLICATION = config.getString("Application");
		DATABASE = config.getString("Database");
		//MANDATABASE = config.getString("ManagerDatabase");
		ADMIN_LOGIN = config.getString("AdminLogin");
		ADMIN_PASSWORD = config.getString("AdminPassword");	
		DRIVER = config.getString("Driver");
		DBURL = config.getString("DBUrl");
	}

	/*
	 * Load the application.
	 */
	Application loadApplication() {
		return (Application) loadClass(APPLICATION);
	}	
	
	protected void log(String s){
		String msg = "Config: "+s;
		super.log(msg);
	}	
	
	/*
	 * Main class loading utility method.
	 */
	Object loadClass(String className){
		log("Loading "+className);
		Class<?> clas = null;
		Object rv = null;
		try{
			clas = Class.forName(className);
		}
		catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		
		try	{
			if(clas!=null){
				rv = clas.newInstance();
			}
		}
		catch (InstantiationException e){
			e.printStackTrace();
		}
		catch (IllegalAccessException e){
			e.printStackTrace();
		}
		
		return rv;
	}

}
