package jhg.db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class HsqlTester {

	public static void main(String[] args){
		//basicTest();
		//for(int i=1;i<Integer.MAX_VALUE;i++){
			try{
//				String x = "";
//				for(int j=1;j<=i;j++){
//					x += "a";
//				}
				ddlTest();//(i,x);
			}catch(Throwable t){
				//System.out.println(i);
				t.printStackTrace();
				//break;
			}
		//}
	}/*
	INTEGER(java.sql.Types.INTEGER,-1,-1),//Integer (aka numeric)
	DOUBLE(java.sql.Types.DOUBLE,-1,1),//Double
	VARCHAR(java.sql.Types.VARCHAR,8000,-1),
	DATE(java.sql.Types.DATE,-1,-1),
	TIME(java.sql.Types.TIME,-1,-1),
	TIMESTAMP(java.sql.Types.TIMESTAMP,-1,-1),
	DECIMAL(java.sql.Types.DECIMAL,1,1),
	BOOLEAN(java.sql.Types.BOOLEAN,-1,-1),//Boolean
	TINYINT(java.sql.Types.TINYINT,-1,-1),//Byte
	SMALLINT(java.sql.Types.SMALLINT,-1,-1),//Short
	BIGINT(java.sql.Types.BIGINT,-1,-1),//Long	
	*/
	
	private static void ddlTest() throws SQLException {//int a,String str

			Connection c = DriverManager.getConnection("jdbc:hsqldb:file:./data/testdb", "SA", "");
			String dTable = "DROP TABLE test IF EXISTS";
			String sTable = "CREATE TABLE test ( id INTEGER NOT NULL PRIMARY KEY, testdec DECIMAL(10,9)  )";//DECIMAL(1000,100) doesn't work but DECIMAL(1000,100) does
			//the scale must be smaller than the precision
			System.out.println(sTable);
			String sInsert = "INSERT INTO test (id,testdec) values (1,1.56)";//the precision is total digits, scale is after decimal
			//scale is chopped and forgiven by rounding
			//precision is not forgiven, you are allowed precision-scale digits before decimals, iow, scale eats precision as it cannot do exponentials
			System.out.println(sInsert);
			String sSelect = "SELECT * FROM test";
			Statement s = c.createStatement();
			s.execute(dTable);
			s.execute(sTable);
			s.execute(sInsert);
			ResultSet rs = s.executeQuery(sSelect);
			while(rs.next()){
				System.out.println(rs.getInt(1));
				System.out.println(rs.getString(2));
			}
			
			

	}
	@SuppressWarnings("unused")
	private static void basicTest() {
		try {
			Connection c = DriverManager.getConnection("jdbc:hsqldb:file:./data/testdb", "SA", "");
			String sTable = "CREATE TABLE test ( id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY )";
			String sInsert = "INSERT INTO test (id) values (1)";
			String sSelect = "SELECT * FROM test";
			Statement s = c.createStatement();
			s.execute(sTable);
			s.execute(sInsert);
			ResultSet rs = s.executeQuery(sSelect);
			while(rs.next()){
				System.out.println(rs.getInt(1));
			}
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
/*
 *
http://www.hsqldb.org/doc/guide/ch09.html#datatypes-section
 *
DATE '2008-08-22'
TIMESTAMP '2008-08-08 20:08:08'
TIMESTAMP '2008-08-08 20:08:08+8:00' /* Beijing 
TIME '20:08:08.034900'
TIME '20:08:08.034900-8:00' /* US Pacific 
*/
/*
GENERATED ALWAYS AS IDENTITY PRIMARY KEY
*/	
	

