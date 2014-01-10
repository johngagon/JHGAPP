package jhg.appman;

public class TestConfig extends Config {

	public TestConfig(String bundleName) {
		super(bundleName);
	}

	protected void init(){
		APPLICATION = "jhg.account.Accounting";
		DATABASE = "accounting";
		//MANDATABASE = config.getString("ManagerDatabase");
		ADMIN_LOGIN = "johngagon";//config.getString("AdminLogin");
		ADMIN_PASSWORD = "thunder";//config.getString("AdminPassword");	
		DRIVER = "NONE";
		DBURL = "jdbc:hsqldb:file:./data/";		
	}
	
}
