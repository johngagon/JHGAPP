package jhg.model;

import java.util.regex.Pattern;

/**
 * This class holds a value for a field object.
 * Currently Field and Value are very string centric.
 * To become less so, take care with "isBlank"
 *   
 * @author John
 *
 */
public class Value implements Comparable<Value>{
	
	protected Field field;
	protected String value;
	protected boolean isBlank;
	
	/**
	 * Create the value from a field and a string.
	 * 
	 * @param _field
	 * @param _value
	 */
	public Value(Field _field, String _value) {
		this.field = _field;
		this.value = _value;
		this.isBlank = false;
	}
	
	public Value(Field _field){
		this.field = _field;
		this.value = "";
		this.isBlank = true;
	}
	
	public Value(Value _toClone){
		this.field = _toClone.field;
		this.value = new String(_toClone.value);
		this.isBlank = _toClone.isBlank;
	}
	
	/**
	 * Override this method to get an actual value.
	 * 
	 * @return
	 */
	public String format(){
		return value;
	}

	/**
	 * Get the field this value belongs to.
	 * 
	 * @return a Field object.
	 */
	public Field getField(){
		return this.field;
	}
	
	/**
	 * Value matching.
	 * 
	 * @param regex
	 * @return
	 */
	public boolean matches(String regex){
		return Pattern.matches(regex, this.value);
	}	
	
	/**
	 * Determine if blank.
	 * 
	 * @return
	 */
	public boolean isBlank(){
		return this.isBlank;
	}
	
	
	
	/**
	 * DOC
	 * @return
	 */
	public boolean isNotBlank(){
		return !isBlank;
	}
	
	/**
	 * Compare this value to another string.
	 * 
	 * @param anotherString
	 * @return
	 */
	public int compareTo(Value anotherString){
		return value.compareTo(anotherString.toString());
	}

	
	@Override
	public String toString(){
		return value;
	}
	
	/**
	 * DOC
	 * 
	 * @return
	 */
	public String toSql(){
		return value;
	}
	
	@Override
	public int hashCode(){
		return value.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(o==null){return false;}
		return value.equals(o.toString());
	}

	
	/*
	 * Simple debug.
	 */
    protected static void debug(String s){
    	final boolean doDebug = false;
    	if(doDebug){
    		System.out.println(s);
    	}
    }	
	}



//--------------------------------------------------------------------------------

	


	
