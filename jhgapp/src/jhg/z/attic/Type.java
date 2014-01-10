package jhg.z.attic;


/**
 * 
 * INTEGER
 * DOUBLE
 * CHAR
 * VARCHAR
 * DATE
 * TIME
 * TIMESTAMP
 * DECIMAL
 * BOOLEAN
 * TINYINT
 * SMALLINT
 * BIGINT
 * 
 * @author John
 *
 */
public enum Type {
	
}


/*  Based on hsqldb, java types and java.sql

1. INTEGER | INT	as Java type	int | java.lang.Integer

2. DOUBLE [PRECISION] | FLOAT	as Java type	double | java.lang.Double
	REAL	as Java type	double | java.lang.Double[2]

3. VARCHAR	as Integer.MAXVALUE	java.lang.String
	VARCHAR_IGNORECASE	as Integer.MAXVALUE	java.lang.String
	CHAR | CHARACTER	as Integer.MAXVALUE	java.lang.String
	LONGVARCHAR	as Integer.MAXVALUE	java.lang.String

4. DATE	as Java type	java.sql.Date
5. TIME	as Java type	java.sql.Time
6. TIMESTAMP | DATETIME	as Java type	java.sql.Timestamp

7. DECIMAL	No limit	java.math.BigDecimal
	NUMERIC	No limit	java.math.BigDecimal

8. BOOLEAN | BIT	as Java type	boolean | java.lang.Boolean

9. TINYINT	as Java type	byte | java.lang.Byte

10. SMALLINT	as Java type	short | java.lang.Short

11. BIGINT	as Java type	long | java.lang.Long				
*/

/*
Possible other types e.g.: Interval, Clob, Blob, Varying bit, Char etc.

http://hsqldb.org/doc/2.0/guide/sqlgeneral-chapt.html

*/
