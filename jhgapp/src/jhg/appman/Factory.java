package jhg.appman;

import jhg.appman.run.ApplicationEngine;
import jhg.appman.run.SimpleApplicationEngine;


/**
 * DOC
 * @author John
 *
 */
public class Factory {
	
	/**
	 * DOC
	 * @return
	 */
	public static ApplicationManager createApplicationManager(){
		ApplicationManager manager = new SimpleApplicationManager(new TestConfig("testing"));
		manager.init();
		return manager;
	}
	
	/**
	 * DOC
	 * 
	 * @return
	 */
	public static ApplicationEngine createApplicationEngine(){
		return new SimpleApplicationEngine();
	}
	
}
