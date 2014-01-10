package jhg.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jhg.ApplicationException;
import jhg.ModelResult;
import jhg.ModelsResult;
import jhg.ModelsResult.Reason;
import jhg.Action;
import jhg.Privilege;
import jhg.Result;
import jhg.Role;
import jhg.User;
import jhg.model.Application;
import jhg.model.Manager;
import jhg.model.Model;
import jhg.model.ModelHistory;

/**
 * A wrapper around database (hsql implementation).
 * 
 * There can be multiple databases in use that will execute queries
 * while hiding exceptions and providing more useful results that are
 * guaranteed to not return null but which can be coded if a problem occurs.
 * 
 * The intended purpose is to prevent dependencies on JDBC libraries, prevent 
 * null pointers and exceptions and comply with the result pattern.  Only the 
 * constructor can throw an exception based on programmer error of using a bad 
 * classname for the driver or not having the hsql library in place.
 *  
 * @author John
 *
 */
public class Database {
	
	public static final Boolean LOG_CONNECTION = Boolean.FALSE;
	private static final String catalog = "PUBLIC";
	private static final String defschema = "PUBLIC";

	private String connStr;
	private String schema;
	private String user;
	private String pass;
	
	
	/**
	 * Construct a database wrapper on the given connection given credentials.
	 * 
	 * @param dbConn  the database connection string
	 * @param dbName  the name of the database to connect to
	 * @param _user  the user authenticating to this database
	 * @param _pass  the password for the user authenticating to this database
	 */
	public Database(String dbConn, String dbName, String _user,	String _pass) {
		connStr = dbConn + dbName;//TODO fix problem with the DBUrl not setting.
		this.schema = defschema;
		this.user = _user;
		this.pass = _pass;
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();//the only reason could be a driver not found or the above forName call being incorrect.
		}		
	}
	
	/**
	 *  NOTUSED Was used for debugging.
	 *  
	 *  @deprecated for lack of use.
	 */
	public synchronized void dbDetails(){
		log("Database.dbDetails");
		Connection conn = getConnection();
		ResultSet rs = null;
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			rs = dbmd.getTables(catalog,schema,null,null);
			debugResultSet(rs);
		} catch (SQLException e) {
			e.printStackTrace();//TODO handle to user.
		} finally {
			closeConnection(conn,null,rs);
		}
	}
	
	/**
	 * Drop all the tables in this database.
	 * 
	 * NOTUSED May be used for resetting database in future or for testing.
	 * 
	 * @param perTableDrop The script to prepend to the table  name.
	 * @return a successful result if it worked without exception, otherwise, this result will contain the error.
	 */
	public synchronized Result dropAllTables(String perTableDropScript){
		log("Database.dropAllTables()");
		Result rv = new Result();
		Connection conn = getConnection();
		Statement s = null;
		List<String> tables = new ArrayList<String>();
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = dbmd.getTables(catalog,schema,null,null);
			while(rs.next()){
				String __tablename = rs.getString(3);
				tables.add(__tablename);
			}
			rs.close();
			s = conn.createStatement();
			for(String __tablename:tables){
				String dropSql = perTableDropScript + __tablename;
				s.execute(dropSql);
				log("Database::dropTableSQL:"+dropSql);
			}
			rv.success();
		} catch (SQLException e) {
			rv.error(e);
		} finally {
			closeConnection(conn,s,null);
		}	
		return rv;
	}
	
	/**
	 * Execute sql in the context of DDL, data definition language. 
	 * This actually is a very light wrapper around JDBC execute.
	 * 
	 * @param sql The sql to execute.
	 * @return  a result that indicates a success or error on the attempt.
	 */
	public synchronized Result executeDDL(String sql) {
		log("Database.executeDDL:"+sql);
		Result r = new Result();
		Connection conn = getConnection();
		Statement s = null;
		try {
			s = conn.createStatement();
			s.execute(sql);
			r.success();
		} catch (SQLException e) {
			r.error(e);
		} finally {
			closeConnection(conn,null,null);
		} 
		return r;
	}		
	
	
	/**
	 * Execute a delete SQL statement.
	 * This base implementation only wraps executeUpdate.
	 * 
	 * @param deleteSql  the SQL to delete.
	 * @return
	 */
	public synchronized Result executeDelete(String deleteSql) {
		log("Database.executeDelete:"+deleteSql);
		Result r = new Result();
		Connection conn = getConnection();
		Statement s = null;
		try {
			s = conn.createStatement();
			s.executeUpdate(deleteSql);
			r.success();
		} catch (SQLException e) {
			r.error(e);
		} finally {
			closeConnection(conn,s,null);
		} 
		return r;	
	}		
	
	/**
	 * DOC
	 * For use when the table does NOT have an identity.
	 * 
	 * @param sInsert
	 * @return
	 */
	public synchronized Result executeInsert(String insertSql) {
		log("Database.executeInsert:"+insertSql+",\n   ");
		Result r = new Result();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			s.executeUpdate(insertSql);
		} catch (SQLException e) {
			r.error(e);
		} finally {
			closeConnection(conn,s,rs);
		} 
		return r;			
	}
	

	/**
	 * DOC
	 * 
	 * @param conn
	 * @param insertSql
	 * @param identitySql
	 * @return
	 * @throws SQLException 
	 */
	public synchronized Result executeInsert(Connection conn, String insertSql, String identitySql){
		Result r = new Result();
		Statement s = null;
		ResultSet rs = null;
		try {
			r = insert(conn,s,rs,insertSql,identitySql);
			//r.success();    //TODO NOTE why this is!
		} catch (SQLException e) {
			r.error(e);
		}
		closeStatement(s,rs);//NOTE - DOES NOT close connection
		return r;					
	}

	public synchronized Result executeUpdate(Connection conn, String updateSql){
		Result r = new Result();
		Statement s = null;
		try {
			r = update(conn,s,updateSql);
			//r.success();
		} catch (SQLException e) {
			r.error(e);
		}
		closeStatement(s,null);//NOTE DOES NOT close connection
		return r;					
	}
	
	public synchronized Result executeInsert(String insertSql, String identitySql){
		Result r = new Result();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;		
		try {
			r = insert(conn,s,rs,insertSql,identitySql);
		} catch (SQLException e) {
			r.error(e);
		} finally {
			closeConnection(conn,s,rs);
		} 
		return r;					
	}
	

	private synchronized Result insert(Connection conn, Statement s, ResultSet rs, String insertSql, String identitySql) throws SQLException{
		log("Database.insert:"+insertSql+",\n   "+identitySql);
		Result r = new Result();
		s = conn.createStatement();
		s.executeUpdate(insertSql);

		s = conn.createStatement();
		rs = s.executeQuery(identitySql);
		while(rs.next()){
			r.success();
			Integer newId = rs.getInt(1); 
			r.objectValue(newId);
			log("  Identity assigned:"+newId);
		}
		return r;
	}	

	
	private synchronized Result update(Connection conn, Statement s, String updateSql) throws SQLException {
		log("Database.update:"+updateSql);
		Result r = new Result();
		s = conn.createStatement();
		s.executeUpdate(updateSql);
		r.success();
		return r;
	}

	
	
	/**
	 * TODO impl
	 * 
	 * @param querySql
	 * @return
	 */
	public synchronized Result executeLoadQuery(String querySql, DatabaseAdaptor rsa) {
		log("Database.executeLoadQuery:"+querySql);
		//TODO get the manager caching scheme so as to determine what kind of models to save.
		Result rv = new Result();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(querySql);
			rv.success();
			rsa.fill(rs);
			//no need to set the result set on the rv when loading, it's not a just in time result, it's the cache.
		} catch (SQLException e) {
			rv.error(e);
		} finally {
			closeConnection(conn,s,rs);
		}		
		return rv;
	}
	
	public Result executeQuerySingleInteger(String selectSql) {
		Result r = new Result();
		log("executeQuerySingleInteger:"+selectSql);
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(selectSql);
			if(rs.next()){
				Integer integer = rs.getInt(1);
				r.objectValue(integer);
				r.success();
			}else{
				r.noResult();
			}
			//no need to set the result set on the rv when loading, it's not a just in time result, it's the cache.
		} catch (SQLException e) {
			r.error(e);
		} finally {
			closeConnection(conn,s,rs);
		}	
		return r;
	}
	
	public void debugSelectAll(String selectSql) {
		log("Database.debugSelectAll:"+selectSql);
		//TODO get the manager caching scheme so as to determine what kind of models to save.
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(selectSql);
			debugResultSet(rs);
			//no need to set the result set on the rv when loading, it's not a just in time result, it's the cache.
		} catch (SQLException e) {
			e.printStackTrace();//TODO add logging of issues.
		} finally {
			closeConnection(conn,s,rs);
		}		
	}
	
	/**
	 * DOC
	 * 
	 * @param sSelect
	 * @param m
	 * @return
	 */
	public Result executeSelect(String selectSql, Manager m) {
		log("Database.executeSelect:"+selectSql);
		Result r = new Result();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(selectSql);
			if(rs.next()){
				m.setManagerId(rs.getInt("ID"));
				r.success();	
			}
			//no need to set the result set on the rv when loading, it's not a just in time result, it's the cache.
		} catch (SQLException e) {
			r.error(e);
			//rv.exception(e);			
			e.printStackTrace();//TODO add logging of issues.
		} finally {
			closeConnection(conn,s,rs);
		}		
		return r;
	}
	
	/**
	 * DOC
	 * 
	 * @param selectSql
	 * @param rsa
	 * @return
	 */
	public Result executeSelectAllIds(String selectSql, List<String> toFill) {
		log("Database.executeSelectAllIds:"+selectSql);
		//TODO get the manager caching scheme so as to determine what kind of models to save.
		Result r = new Result();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(selectSql);
			while(rs.next()){
				toFill.add(rs.getString(1));
			}
			r.success();
			//no need to set the result set on the rv when loading, it's not a just in time result, it's the cache.
		} catch (SQLException e) {
			r.error(e);
			//rv.exception(e);			
			e.printStackTrace();//TODO add logging of issues.
		} finally {
			closeConnection(conn,s,rs);
		}		
		return r;
	}	
	
	/**
	 * DOC
	 * 
	 * @param selectSql
	 * @param rsa
	 * @return
	 * @throws ApplicationException 
	 */
	public ModelsResult executeSelectAll(String selectSql, DatabaseAdaptor rsa) throws ApplicationException {
		log("Database.executeSelectAll:"+selectSql);
		//TODO get the manager caching scheme so as to determine what kind of models to save.
		ModelsResult rv = new ModelsResult();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(selectSql);
			rv.setModels(rsa.fillSelectAll(rs));
			//no need to set the result set on the rv when loading, it's not a just in time result, it's the cache.
		} catch (SQLException e) {
			rv.fail(Reason.ERROR,e);
			//rv.exception(e);			
			e.printStackTrace();//TODO add logging of issues.
		} finally {
			closeConnection(conn,s,rs);
		}		
		return rv;
	}	

	/*
	public Result executeSelectRole(String selectRoleSql, Role selectedRole) {
		log("executeSelectRole:"+selectRoleSql);
		Result result = new Result();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(selectRoleSql);
			
			if(rs.next()){
				selectedRole.init(rs.getInt("ID"),rs.getString(Role.ROLEFLD));
				result.success();
			}else{
				result.noResult();
			}
			//no need to set the result set on the rv when loading, it's not a just in time result, it's the cache.
		} catch (SQLException e) {
			result.error(e);
			//rv.exception(e);
			//e.printStackTrace();//TODO add logging of issues.
		} finally {
			closeConnection(conn,s,rs);
		}		
		return result;
	}	
	*/

	/**
	 * DOC
	 * @param selectRolesSql
	 * @param role
	 * @return
	 */
	public Result executeSelectRolePrivileges(Application app, String selectRolesSql, Role role) {
		log("executeSelectRolePrivileges:"+selectRolesSql);
		Result result = new Result();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(selectRolesSql);
			boolean haveResults = false;
			while(rs.next()){
				Integer managerId = rs.getInt(Role.MANAGER_ID);
				Integer privId = rs.getInt(Role.PRIV_ID);
				role.addPrivilege(app,managerId,privId);
				log("  adding priv:"+Privilege.values()[privId]+" for entity "+managerId);
				haveResults = true;
			}
			if(haveResults){
				result.success();
			}else{
				result.noResult();
			}
			//no need to set the result set on the rv when loading, it's not a just in time result, it's the cache.
		} catch (SQLException e) {
			result.error(e);
			
			//rv.exception(e);
			//e.printStackTrace();//TODO add logging of issues.
		} finally {
			closeConnection(conn,s,rs);
		}		
		return result;
	}	
	
	
	/**
	 * DOC, cleanup "Database." in the logging, it's redundant now.
	 * 
	 * @param selectSql
	 * @param user
	 * @return
	 */
	public synchronized Result executeSingleSelectQuery(String selectSql, User user) {
		log("Database.executeSingleSelectQuery:"+selectSql);
		
		Result result = new Result();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(selectSql);
			if(rs.next()){
				user.init(rs.getInt("ID"),rs.getString(User.USERFLD), rs.getString(User.PASSFLD));
				result.success();
			}else{
				result.noResult();
			}
			//no need to set the result set on the rv when loading, it's not a just in time result, it's the cache.
		} catch (SQLException e) {
			result.error(e);
			//rv.exception(e);
			//e.printStackTrace();//TODO add logging of issues.
		} finally {
			closeConnection(conn,s,rs);
		}		
		return result;
	}	

	/**
	 * DOC, cleanup "Database." in the logging, it's redundant now.
	 * 
	 * @param selectSql
	 * @param user
	 * @return
	 */
	public synchronized Result executeSingleSelectQuery(String selectSql, Role role) {
		log("Database.executeSingleSelectQuery:"+selectSql);
		
		Result result = new Result();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(selectSql);
			if(rs.next()){
				role.init(rs.getInt("ID"),rs.getString(Role.ROLEFLD));
				result.success();
			}else{
				result.noResult();
			}
			//no need to set the result set on the rv when loading, it's not a just in time result, it's the cache.
		} catch (SQLException e) {
			result.error(e);
			//rv.exception(e);
			//e.printStackTrace();//TODO add logging of issues.
		} finally {
			closeConnection(conn,s,rs);
		}		
		return result;
	}	
	

	
	/**
	 * DOC
	 * @param selectModelHistorySql
	 * @param historyToFill
	 * @return
	 */
	public Result executeSelectModelHistories(String selectModelHistorySql, Model model, List<ModelHistory> historyToFill) {
		log("executeSelectModelHistories:"+selectModelHistorySql);
		Result result = new Result();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(selectModelHistorySql);
			boolean haveResults = false;
			while(rs.next()){
				Integer changeId = rs.getInt("CHANGE_ID");
				Integer eventId = rs.getInt("EVENT_ID");
				Date eventStamp = rs.getDate("EVENT_STAMP");
				Integer userId = rs.getInt("USER_ID");
				Integer roleId = rs.getInt("ROLE_ID");//Shouldn't there be a privilege?
				Integer actionId = rs.getInt("EVENT_ID");
				Action.Code code =  Action.Code.values()[actionId];
				ModelHistory mh = new ModelHistory(changeId, userId, roleId, eventStamp, model, code);
				mh.setClient(rs.getString("CLIENT"));
				mh.setComments(rs.getString("COMMENTS"));
				mh.setTransferUserId(rs.getInt("TXFR_USER_ID"));//could be null?
				historyToFill.add(mh);
				haveResults = true;
			}
			if(haveResults){
				result.success();
			}else{
				result.noResult();
			}
			//no need to set the result set on the rv when loading, it's not a just in time result, it's the cache.
		} catch (SQLException e) {
			result.error(e);
			//rv.exception(e);
			//e.printStackTrace();//TODO add logging of issues.
		} finally {
			closeConnection(conn,s,rs);
		}		
		return result;
	}
	
	
	/**
	 * DOC
	 * @param selectSql
	 * @param rsa
	 * @return
	 */
	public synchronized ModelResult executeSingleSelectQuery(String selectSql, DatabaseAdaptor rsa) {
		log("Database.executeSingleSelectQuery:"+selectSql);
		
		ModelResult rv = new ModelResult();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(selectSql);
			rv = rsa.fillSingle(rs);
			//no need to set the result set on the rv when loading, it's not a just in time result, it's the cache.
		} catch (SQLException e) {
			rv.error(e);
		} finally {
			closeConnection(conn,s,rs);
		}		
		return rv;
	}
	
	/**
	 * 
	 * @param countSql
	 * @return
	 */
	public synchronized Result executeCountQuery(String countSql) {
		log("Database.executeCountQuery:"+countSql);
		Result rv = new Result();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(countSql);
			Integer count = 0;
			if(rs.next()){
				count = rs.getInt(1);
				log("  Count:"+count);
			}
			rv.success();
			rv.objectValue(count);
		} catch (SQLException e) {
			rv.error(e);
			e.printStackTrace();//TODO add logging of issues.
		} finally {
			closeConnection(conn,s,rs);
		}		
		return rv;
	}	

	/**
	 *
	 * Notes on Decimal inserts:
	 * scale is chopped and forgiven by rounding
	 * precision is not forgiven, 
	 * you are allowed precision-scale digits before decimals, 
	 * iow, scale eats precision as it cannot do exponentials
	 * e.g.: DECIMAL(10,9) only allows a single digit like 1.99989 and not 10.99989
	 * 
	 * 
	 * @param transactionSql
	 * @param messages
	 * @return
	 */
	public synchronized Result executeTransaction(String transactionSql, List<String> messages) {
		log("##Database.executeTransaction");
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param updateSql
	 * @return
	 */
	public synchronized Result executeUpdate(String updateSql) {
		log("Database.executeUpdate:"+updateSql);
		//log("  SQL:"+updateSql);
		Result r = new Result();
		Connection conn = getConnection();
		Statement s = null;
		try {
			r = update(conn,s,updateSql);//TODO prepare statement for all these statements?
		} catch (SQLException e) {
			r.error(e);
		} finally {
			closeConnection(conn,s,null);
		} 
		return r;	
	}
	
	/**
	 * 
	 * @param tablename
	 * @return
	 */
	public synchronized boolean exists(String tablename){
		//log("exists("+tablename+")");
		boolean rv = false;
		Connection conn = getConnection();
		ResultSet rs = null;
		int found = 0;
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			rs = dbmd.getTables(catalog,schema,null,null);
			
			while(rs.next()){
				found++;
				String __tablename = rs.getString(3);
				//log("  Table:"+__tablename);
				if(__tablename.equalsIgnoreCase(tablename)){
					rv = true;
				}
			}
			//log("  Found:"+found);
		} catch (SQLException e) {
			log("  Found:"+found);
			e.printStackTrace();//TODO impl logging
		} finally {
			closeConnection(conn,null,rs);
		}
		//log("");
		log("exists("+tablename+"):"+rv);
		return rv;
	}

	/**
	 * DOC
	 * 
	 * @return
	 */
	public Connection openTransactionalConnection(){
		
		Connection conn = null;
		try {
			log("  Connecting '"+connStr+"', user:'"+user+"', pass:'"+pass+"' ");
			conn = DriverManager.getConnection(connStr, user, pass);
			//log("  Connection open:"+connStr);
			if(conn==null)throw new SQLException("jhg Connection null.");
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			//TODO log this.
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * DOC
	 * @param conn
	 */
	public void closeTransactionalConnection(Connection conn){
		closeConnection(conn, null, null);
	}
	
	/**
	 * 
	 * @return
	 */
	private Connection getConnection(){
		Connection conn = null;
		try {
			if(LOG_CONNECTION){
				log("  Connecting '"+connStr+"', user:'"+user+"', pass:'"+pass+"' ");
			}
			conn = DriverManager.getConnection(connStr, user, pass);
			//log("  Connection open:"+connStr);
			if(conn==null)throw new SQLException("jhg Connection null.");
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}


	/**
	 * The database writes out to files and performs clean shut down.
	 * Otherwise, there will be an unclean shutdown when the program ends.
	 * @throws SQLException
	 */
    public void shutdown() {
    	Connection conn = getConnection();
        Statement st = null;
		try {
			st = conn.createStatement();
			st.execute("SHUTDOWN");
		} catch (SQLException e) {
			e.printStackTrace();// TODO impl stack trace error messages.
		}
        closeConnection(conn,st,null);
    }
	
	
	/**
	 * 
	 * @return
	 */
	public boolean testConnection(){
		boolean rv = false;
		Connection c = getConnection();
		Statement s = null;
		ResultSet rs = null;
		String dTable = "DROP TABLE test IF EXISTS";
		String sTable = "CREATE TABLE test ( id int )";
		String sInsert = "INSERT INTO test (id) values (1)";
		String sSelect = "SELECT * FROM test";
		try {
			s = c.createStatement();
			s.execute(dTable);
			s.execute(sTable);
			s.execute(sInsert);
			rs = s.executeQuery(sSelect);
			while(rs.next()){
				Integer testVal = rs.getInt(1);
				rv = testVal.equals(1);
			}
			//sTable = "DROP TABLE test";
			//s.execute(sTable);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(c,s,rs);
		}
		return rv;
		
	}

	

	
	
	/**
	 * 
	 * @param string
	 */
	private void log(String string) {
		System.out.println("Database: " +string);
	}


	/**
	 * DOC
	 * @param conn
	 * @return
	 */
	public Result commit(Connection conn) {
		Result r = new Result();
		try {
			conn.commit();
			r.success();
		} catch (SQLException e) {
			r.error(e);
		}
		return r;
	}

	/**
	 * DOC
	 * @param conn
	 * @return
	 */
	public Result rollback(Connection conn) {
		Result r = new Result();
		try {
			conn.rollback();
			r.success();
		} catch (SQLException e) {
			r.error(e);
		}
		return r;
	}


	public Result executeSelectUsers(String selectUsersSql, List<User> userList) {
		log("executeSelectUsers:"+selectUsersSql);
		Result result = new Result();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(selectUsersSql);
			boolean haveResults = false;
			while(rs.next()){
				User _u = new User();
				_u.init(rs.getInt("ID"),rs.getString(User.USERFLD),rs.getString(User.PASSFLD));
				userList.add(_u);
				haveResults = true;
			}
			/*
			if(haveResults){
				//result.objectValue(userList);
				result.success();
			}else{
				result.noResult();
			}
			*/
			if(!haveResults){result.addMessage("No users found.");}
			result.success();
			//no need to set the result set on the rv when loading, it's not a just in time result, it's the cache.
		} catch (SQLException e) {
			result.error(e);
			//rv.exception(e);
			//e.printStackTrace();//TODO add logging of issues.
		} finally {
			closeConnection(conn,s,rs);
		}		
		return result;
	}

	/**
	 * DOC
	 * 
	 * @param selectRolesSql
	 * @param roleList
	 * @return
	 */
	public Result executeSelectRoles(String selectRolesSql, List<Role> roleList) {
		log("executeSelect:"+selectRolesSql);
		Result result = new Result();
		Connection conn = getConnection();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			rs = s.executeQuery(selectRolesSql);
			boolean haveResults = false;
			while(rs.next()){
				Role _r = new Role();
				_r.init(rs.getInt("ID"),rs.getString(Role.ROLEFLD));
				roleList.add(_r);
				haveResults = true;
			}
			if(!haveResults){result.addMessage("No roles found.");}
			result.success();
			//no need to set the result set on the rv when loading, it's not a just in time result, it's the cache.
		} catch (SQLException e) {
			result.error(e);
			//rv.exception(e);
			//e.printStackTrace();//TODO add logging of issues.
		} finally {
			closeConnection(conn,s,rs);
		}		
		return result;
	}



	/*
	 * Close the connection but retain connection information.
	 * 
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	private void closeConnection(Connection conn,Statement stmt,ResultSet rs){
		if(conn!=null){
			if(LOG_CONNECTION){
				log("  Connection closing:"+connStr);
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		closeStatement(stmt,rs);
		//log("  Connections closed.");
		//log("");
	}
	
	private void closeStatement(Statement stmt,ResultSet rs){
		if(stmt!=null){
			try{
				stmt.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		if(rs!=null){
			try{
				rs.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}		
	}
	
	/**
	 * 
	 * @param rs
	 * @throws SQLException
	 */
	private void debugResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
		int i = 1;
		while(rs.next()){
			for(int j=1;j<=cols;j++){
				log(i+":"+rsmd.getColumnLabel(j)+"-'"+rs.getString(j)+"'");
			}
			i++;
			log("");
		}
	}		



	
    
}
