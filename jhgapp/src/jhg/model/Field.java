package jhg.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jhg.Messages;
import jhg.Result;


/**
 * DOC and clean up
 * 
 * @author John
 * 
 * TODO consider using subtypes so things like ranges can be overriden
 * TODO set Date, Integer/Long, Double low and high range limits, alphabetical between
 * TODO Need:  
 *    Credit Card, Social Security, 
 *    Email, IP Address, 
 *    Time Measure
 *    Date Interval Measure, 
 *    Money
 *    
 *    Math library
 *    Validation library
 * Name and rules.
 * 
 */
public abstract class Field { //<T extends Value>
	
	/**
	 * Simple debug.
	 */
    protected static void debug(String s){
    	final boolean doDebug = false;
    	if(doDebug){
    		System.out.println(s);
    	}
    }		
	
	@SuppressWarnings("rawtypes")
	protected Manager manager;
	protected String name;
	protected String label;
	protected boolean isNullable;//not required
	protected boolean isIdentity;
	protected boolean isPrimary;
	protected boolean isParsed;
	protected boolean isCalculated;
	protected boolean usesDefault;
	protected String defaultValue;
	protected int length;           //all fields have a print length. for text this is the same as their size.
	
	protected Long lowBound;        //  >=
	protected Long highBound;       //  <=
	protected BigDecimal lowLimit;  //  >
	protected BigDecimal upperLimit;//  <
	protected BigDecimal lowerBound;//  >=
	protected BigDecimal upperBound;//  <=
	protected boolean isAggregateCalculated;
	protected Field aggregationField;
	protected List<Field> aggregateListeners;
	
	/**
	 * DOC
	 * @param _name
	 */
	@SuppressWarnings("rawtypes")
	public Field(Manager _manager, String _name) {
		this.manager = _manager;
		this.name = _name;
		this.isParsed = true;
		this.length = 0;
		this.isAggregateCalculated = false;
		this.isCalculated = false;
		this.label = "";
		this.isNullable = true;
		this.isIdentity = false;
		this.aggregationField = null;
		this.aggregateListeners = new ArrayList<Field>();
	}
	
	/**
	 * DOC
	 * @return
	 */
	public abstract String getFormatTemplate();

	/**
	 * DOC
	 * 
	 * @return
	 */
	public abstract int getSqlType();
	
	/**
	 * DOC
	 * @return
	 */
	public abstract String toDDL();		
	
	/**
	 * DOC	
	 * @param parsed
	 * @param messages
	 * @return
	 */
	protected abstract boolean isValid(String parsed, Messages messages);
	
	/**
	 * DOC
	 * @param _value
	 * @return
	 */
	protected abstract String parse(String _value);
	
	/**
	 * DOC
	 * @param _value
	 * @param messages
	 * @return
	 */
	protected abstract boolean preParseValidate(String _value, Messages messages);

	/**
	 * DOC
	 * 
	 * @param f
	 * @param v
	 */
	protected abstract Value factoryValue(String v);
	
	/**
	 * DOC
	 * Only works if the field is not nullable.
	 * 
	 * @return
	 */
	//protected abstract Value factoryBlankValue();
	
	/**
	 * Fields should ideally override this behavior.
	 * 
	 * @param pstmt
	 * @param index
	 * @param _value
	 * @param messages
	 * @return
	 */
	public boolean to(PreparedStatement pstmt, int index, Value _value, Messages messages){
		try {
			pstmt.setString(index,_value.toString());
		} catch (SQLException e) {
			messages.add(e.getMessage());//usually a closed statement or bad index.
			//if the message is not good enough, do some checking on the passed in statement.
		}
		return true;
	}
	
	/**
	 * Grab a field from the result set.
	 * While this does catch exceptions a field at a time, it's safer this way.
	 * 
	 * @param rs
	 * @param field
	 * @param messages
	 * @return
	 */
	public Result from(ResultSet rs, Field field, Messages messages){
		debug("Begin process reading db value");
		Result result = new Result();
		String _valueStr = null;
		try {
			_valueStr = rs.getString(field.name);
			if(_valueStr==null){
				//TODO empty fields should be "";  the "" should be our app equivalent of blank or null.
				//the "" (or worse, null) can mean required data is not there.
				_valueStr = "";
			}
		} catch (SQLException sqle) {
			return result.error(sqle);
		} catch (Exception e){
			return result.error(e);
		}
		result.success();
		result.value(new Value(this,_valueStr));		
		debug("Finished process creating value:"+_valueStr);
		return result;		
	}	
	
		
	/**
	 * Make a value using the standard process.
	 * 
	 * @param _value
	 * @param messages
	 * @return
	 */
	protected Result makeValue(String _value, Messages messages, boolean forCalculation) {
		debug("Field.makeValue() Begin process creating value:"+_value);
		Result result = new Result();
		result.addMessages(messages);
		if(isCalculated && !forCalculation){
			result.addMessage("Cannot make this value for an uncalculated field.");
		}else if(forCalculation && !isCalculated){
			result.addMessage("Can only  make this value for a calculated field.");
		}else{
			try{
				if(isParsed()){
					if(!preParseValidate(_value, messages)){
						result.invalidInput();
						result.addMessages(messages);
					}
					if(result.notInvalid()){
						_value = parse(_value);
						if(!isValid(_value,messages)){
							result.invalidInput();
							result.addMessages(messages);						
						}else{
							try{
								Value newVal = factoryValue(_value);//never null.
								result.success();
								result.value(newVal);							
							}catch(Exception e){
								e.printStackTrace();
								result.noResult("Could not create value.");
							}
						}
					}//if(result.notInvalid())
				}else{
					if(!isValid(_value,messages)){
						result.invalidInput();
						result.addMessages(messages);						
					}else{
						try{
							Value newVal = factoryValue(_value);//never null.
							result.success();
							result.value(newVal);							
						}catch(Exception e){
							result.noResult();
							result.addMessage("Could not create value.");
						}
					}				
				}
			}catch(Exception e){
				result.equals(e);
			}
		}
		debug("Field.makeValue() Finished process creating value: '"+_value+"' on field '"+manager.name+"."+name+"' with result:"+result.name());
		return result;
	}	
	
	/**
	 * DOC
	 * 
	 * @param str
	 * @return
	 */
	public boolean managerNameEquals(String str){
		return manager.name.equals(str);
	}	

	/**
	 * DOC
	 */
	public void setAggregateCalculated(Field _field){
		this.isAggregateCalculated = true;
		this.aggregationField = _field;
		_field.aggregateListeners.add(this);
	}	
	
	/**
	 * DOC
	 */
	public void setCalculated(){
		this.isCalculated = true;
	}
	
	/**
	 * DOC
	 * @return
	 */
	public boolean isCalculated(){
		return this.isCalculated;
	}
	
	/**
	 * DOC
	 * @return
	 */
	public boolean isAggregateCalculated(){
		return this.isAggregateCalculated;
	}
	
	/**
	 * DOC
	 * @param _label
	 */
	public void setLabel(String _label){
		this.label = _label;
	}
	
	/**
	 * DOC
	 */
	public void setParsed(){
		this.isParsed = true;
	}	
	
	/**
	 * DOC
	 */
	public void setRequired(){
		this.isNullable = false;
	}	
	
	/**
	 * DOC
	 */
	public void useDefault(){
		this.usesDefault = true;
	}	
	
	/**
	 * DOC
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public final Manager getManager(){
		return this.manager;
	}

	/**
	 * DOC
	 * @return
	 */
	public String getLabel(){
		return this.label;
	}
	
	/**
	 * DOC
	 * 
	 * @return
	 */
	public int getLength(){
		return this.length;
	}
	
	/**
	 * DOC
	 * @return
	 */
	public String getName(){
		return name;
	}

	/**
	 * DOC
	 * @return
	 */
	public boolean isIdentity(){
		return this.isIdentity;
	}
	
	/**
	 * DOC
	 * @param aName
	 * @return
	 */
	public boolean is(String aName){
		return this.name.equals(aName);
	}	
	
	/**
	 * DOC
	 * @return
	 */
	public boolean isParsed(){
		return this.isParsed;
	}	
	
	/**
	 * DOC
	 * @return
	 */
	public boolean usesDefault(){
		return this.usesDefault;
	}
    
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((manager == null) ? 0 : manager.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Field other = (Field) obj;
		if (manager == null) {
			if (other.manager != null)
				return false;
		} else if (!manager.equals(other.manager))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
    
    
	
}


/*
	/**
	 * Result type.
	 * @author John
	 *
	 *
	public static enum Result{
		IMPARSIBLE,
		VALID, 
		INVALID, 
		ERROR, 
		RESULT_ERROR,
		NOT_EXECUTED;	
		
		private static boolean doRuntimeOnNull = false;
		private Value value;
		private Exception ex;
		private Result(){}

		public boolean hasValue(){
			return value!=null;
		}
		public boolean hasException(){
			return ex!=null;
		}
		
		//getter when null but must already be set
		//setter when arg[0] supplied and will return null always
		public Value value(Value... v){
			if(v.length>0 && v[0]!=null){
				this.value = v[0];
				return null;
			}else{
				if(doRuntimeOnNull && value==null)throw new IllegalStateException();
				return value;
			}
		}
		public Exception exception(Exception... e){
			if(e.length>0 && e[0]!=null){
				this.ex = e[0];
				return null;
			}else{
				if(doRuntimeOnNull && ex==null)throw new IllegalStateException();
				return ex;
			}
		}
	}		
*/
//translate java.sql types to and from this field.


/*
private int msupportsSize;     this would be -1 if none or a number of the supported value
private int msupportsPrec;
private int msupportsScale;
private boolean isQuoted;

	 * is it used in a totaling algorithm
	 * to do that, you need to grab the right records
	 * you're going to hit the database anyway?
	 *

*/
