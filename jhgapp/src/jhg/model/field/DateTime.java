package jhg.model.field;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jhg.Messages;
import jhg.model.Manager;
import jhg.model.Value;

import org.apache.commons.lang3.time.DateUtils;


/**
 * DOC
 * @author John
 *
 */
public class DateTime extends Value implements Serializable {
/*
 *  Note the web form format: 
 *  

 * 
 *  web4j comparable
 *  may look at my proj area, old drives
 *  look at current proj area
 * 
 *  Finish contract and unimplemented: toDDL, 
 * 
 *  Format length
 *  
 *  convert Date->Stamp  Time->Stamp, Stamp->Date  Stamp->Time
 *  
 *  Date Functions:
 *  	
 *  Time Functions:     
 *  	time differences: milli-difference, convert to hour, minute, second fields.
 *  	
 *  Timestamp Functions:
 *  	carry over times, day light savings, time zone, 
 *      
 *  Calendar functions:
 *  	lazy repeat (does this date have repeat appointment)
 *  
 *  space pad plain format / space pad formatted (with commas) (see fixed width impl)
 *  format as percent     
 *  format as currency    (add 5 if padding spaces)
 *  format with leading zeros: no comma, negative sign before zeros.
 *  format with special L sign for long. (too trivial)
 *  get the print size for each type (with and without commas)
 *  calculations for adding, subtracting, multiply, divide? 
 *  ensure numeric sort
 *  ensure matching
 *  preParseValidate - Don't use the number format exception's message, it's not informative //Exception in thread "main" java.lang.NumberFormatException: For input string: "28929233343434343434343"
 *  ensure we can parse numbers having commas
 *  look at the low bound and high bound on the Field class
 *  parse from formats with commas, etc.
 *
 */

	private static final long serialVersionUID = -1764164592020914858L;

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss.SSSZ";
	public static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
	
	public static final String DEFAULT_SECONDS = ":00";
	public static final String DEFAULT_FRACTIONAL_SECONDS = ".000";
	
	public static final String DEFAULT_STANDARD_TIME = "-0500";
	public static final String DEFAULT_DAYLIGHT_TIME = "-0400";				
	
	public static final int    DEFAULT_MAX_TIMEPRECISION = 6;//Time and Timestamp use.
	public static final int    DEFAULT_MAX_TIMESTAMPPRECISION = 6;
	/**
	 * DOC
	 * @param type
	 * @return
	 */
	public static String getFormat(DateTimeField.Type type){
		switch(type){
			case TIMESTAMP:	return DEFAULT_TIMESTAMP_FORMAT;
			case DATE: return DEFAULT_DATE_FORMAT;
			case TIME: return DEFAULT_TIME_FORMAT;
			default:throw new IllegalStateException("This field needs a valid type.");
		}		
	}
	
	/**
	 * Used by constructor and field validator.
	 * @param _field
	 * @param _value
	 * @return
	 */
	public static Date parseGoodDateString(DateTimeField _field,
			String _value) {
		Date _date = null;
		SimpleDateFormat formatter = new SimpleDateFormat(_field.formatTemplate);//getFormat(_field.type));
		try {
			_date = formatter.parse(_value.toString());
		} catch (ParseException pe) {
			throw new IllegalStateException("Validation should prevent parse errors: '"+_value+"' "+pe.getMessage(),pe);
		}
		return _date;
	}	
	
	
	/**
	 * Utilities for date and time classes.
	 * 
	 * @author John
	 *
	 */
	public static class Util {
		
		/**
		 * Convert a date string from one format to another assuming valid.
		 * 
		 * @param currentFormat  The current format you have.
		 * @param desiredFormat  The format you want to convert your string date to.
		 * @param currentValue   The date string you wish to convert.
		 * @return  The date string in the new desired format.
		 */
		public static String convertDateFormat(String currentFormat,String desiredFormat, String currentValue){
			SimpleDateFormat formatterC = new SimpleDateFormat(currentFormat);
			SimpleDateFormat formatterD = new SimpleDateFormat(desiredFormat);
			String r = "";
			try {
				Date d = formatterC.parse(currentValue);
				String desired = formatterD.format(d);
				r = desired;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return r;
		}		
		
		/**
		 * Add days to given datetime.
		 * TODO more functions for other fields available.
		 *  
		 * @param datetime
		 * @param amount
		 * @return
		 */
		public static String addDays(DateTime datetime, int amount){
			//only works with Date and Timestamp
			Date _date = DateUtils.addDays(datetime.date, amount);
			String format = ((DateTimeField)datetime.field).formatTemplate;
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			String result = formatter.format(_date);
			return result;
		}
		
		/**
		 * DOC
		 * 
		 * @param value
		 * @return
		 */
		public static java.util.Date asDate(String value) {
			SimpleDateFormat formatter = new SimpleDateFormat(DateTime.DEFAULT_DATE_FORMAT);
			java.util.Date date = null;
			try {
				date = formatter.parse(value);
			} catch (ParseException e) {
				e.printStackTrace();//TODO stacktrace
			}
			return date;
		}		
		
		/**
		 * DOC
		 * @param date
		 * @return
		 */
		public static java.util.Date toDate(java.sql.Date date){
			return new java.util.Date(date.getTime());
		}
		
		/**
		 * DOC
		 * @param date
		 * @return
		 */
		public static java.util.Date toDate(java.sql.Time date){
			return new java.util.Date(date.getTime());
		}
		
		/**
		 * 
		 * @param date
		 * @return
		 */
		public static java.util.Date toDate(java.sql.Timestamp date){
			return new java.util.Date(date.getTime());
		}
		
		/**
		 * DOC
		 * @param timeString
		 * @param daylight
		 * @return
		 */
		public static String appendDefaults(String timeString, boolean daylight){
			if(daylight){
				return timeString + DEFAULT_SECONDS + DEFAULT_FRACTIONAL_SECONDS + DEFAULT_DAYLIGHT_TIME;
			}else{
				return timeString + DEFAULT_SECONDS + DEFAULT_FRACTIONAL_SECONDS + DEFAULT_STANDARD_TIME;
			}
		}		
	}
	
	/**
	 * DOC
	 * @author John
	 *
	 */
	public static class DateTimeField extends jhg.model.Field{

		
		public static enum Type {DATE,TIME,TIMESTAMP;}
		
		private Type type;
		private String formatTemplate;
		private Integer precision;
		private Date leftBound;
		private Date rightBound;
		//private String dateRangeTranslation;
		
		/**
		 * DOC
		 * @return
		 */
		public Type getType(){
			return this.type;
		}
		
		/**
		 * DOC
		 * @param m
		 * @param _name
		 * @param t
		 * @param _precision
		 */
		@SuppressWarnings("rawtypes")
		public DateTimeField(Manager m, String _name, Type t){
			super(m, _name);
			type = t;
			if(t.equals(Type.TIME) || t.equals(Type.TIMESTAMP)){
				this.precision = 0;
			}
			formatTemplate = getFormat(t);
		}
		
		/**
		 * DOC
		 * @param _precision
		 */
		public void setPrecision(int _precision){
			if(type.equals(Type.TIME) && (_precision!=0 && _precision!=DEFAULT_MAX_TIMEPRECISION)){ //&& (_precision<0 || _precision>DEFAULT_MAX_TIMEPRECISION)){
				throw new IllegalArgumentException("The api requires a time precision value to be either 0 or the max precision of "+DEFAULT_MAX_TIMEPRECISION+".");
			}else if(type.equals(Type.TIMESTAMP)&& (_precision!=0 && _precision!=DEFAULT_MAX_TIMESTAMPPRECISION)){
				throw new IllegalArgumentException("The api requires a timestamp precision value to be either 0 or "+DEFAULT_MAX_TIMESTAMPPRECISION+".");
			}
			this.precision = _precision;
		}
		
		/**
		 * Set the valid range for this date.
		 * Bounds are inclusive.
		 * 
		 * @param _leftBound
		 * @param _rightbound
		 * @param _translation
		 */
		public void setValidationRange(Date _leftBound, Date _rightBound, String _translation){
			this.leftBound = _leftBound;
			this.rightBound = _rightBound;
			//this.dateRangeTranslation = _translation;
		}
		
		/**
		 * DOC
		 * @param value
		 * @param messages
		 * @return
		 */
		public boolean validateRange(String _value, Messages messages){
			boolean valid = true;
			if(leftBound==null && rightBound==null){
				return true;
			}
			Date _date = parseGoodDateString(this, _value);
			if(leftBound!=null && _date.getTime() < leftBound.getTime()){
				valid = false;
				messages.add("The value "+_value+" ("+_date+") cannot be less than "+leftBound);
			}
			if(rightBound!=null && _date.getTime() > rightBound.getTime()){
				valid = false;
				messages.add("The value "+_value+" ("+_date+") cannot be greather than "+rightBound);
			}
			return valid;
		}			


		@Override
		protected boolean preParseValidate(String _value, Messages messages) {
			boolean valid = true;
			
			if(_value==null){
				valid=false;
			}else {
				try{
					SimpleDateFormat formatter = new SimpleDateFormat(formatTemplate);
					formatter.parse(_value);
				}catch(ParseException pe){
					messages.add("The value '"+_value+"' could not be parsed into "+type.name()+" using format:'"+formatTemplate+"'.");
					valid = false;
				}							
				debug("Value prevalidation passed:"+_value);
			}
			return valid;
		}
		
		@Override
		protected String parse(String _value) {
			String result = null;
			String defaultFormat = formatTemplate;//getFormat(this.type);
			try{
				SimpleDateFormat formatter = new SimpleDateFormat(defaultFormat);
				Date intermediateResult = formatter.parse(_value);
				result = formatter.format(intermediateResult);
				System.out.println("DateTimeField.parse("+_value+"):'"+result+"'");//FIXME System.out
			}catch(ParseException pe){
				throw new IllegalArgumentException(pe);
			}
			return result;
		}
		
		@Override
		protected boolean isValid(String parsed, Messages messages) {
			boolean valid = true;
			//Subtle logic changes can occur if you try && or & short cut operations instead.  Best to keep this explicit.
			if(!validateRange(parsed,messages)){
				valid = false;
			}
			return valid;
		}
		

		@Override
		public String toDDL() {
			StringBuilder sb = new StringBuilder();
			sb.append(this.name+" ");
			sb.append(type.name());
			if(type.equals(Type.TIME)||type.equals(Type.TIMESTAMP)){
				sb.append("(");
				sb.append(this.precision);
				sb.append(")");				
			}
			sb.append(" ");
			sb.append((isNullable)?"":"NOT ");
			sb.append("NULL ");
			
			return sb.toString();
		}


		@Override
		public int getSqlType() {
			switch(type){
				case DATE: return java.sql.Types.DATE;
				case TIME: return java.sql.Types.TIME;
				case TIMESTAMP: return java.sql.Types.TIMESTAMP;
				default: throw new IllegalStateException("No type specified. This should never happen.");
			}
		}		
		
		@Override
		protected Value factoryValue(String v) {
			return new DateTime(this,v);
		}

		@Override
		public String getFormatTemplate() {
			return this.formatTemplate;
		}

		
		public void setFormat(String _format) {
			this.formatTemplate = _format;
		}		
		
	}
	
	
	
	protected Date date;
	
	/**
	 * DOC
	 * @param _field
	 * @param _value
	 */
	public DateTime(DateTimeField _field, String _value) {
		super(_field, _value);
		date = parseGoodDateString(_field, _value);
	}

	
	
	
	/**
	 * DOC
	 * 
	 * @param value
	 * @return
	 */
	public java.util.Date asDate() {
		return date;
	}	

	@Override
	public String format() {
		return value;
	}		

	public int compareTo(DateTime value){
		return date.compareTo(value.date);
	}
	
	@Override
	public String toSql(){
		return ("'"+fixTimeZone(value)+"'");//TODO this is all hsql, need to extract this out to db library.
	}
	
	/*
	 * 
	 */
	private static String fixTimeZone(String timeIn){
		int lastIndexOfDash = timeIn.lastIndexOf("-");
		StringBuffer sb = new StringBuffer(timeIn);
		sb.insert(lastIndexOfDash+3,":");
		return sb.toString();
	}		
	
}


/*
 * DOC
 * 
 * @param value
 * @param length
 * @param scale
 * @param precision
 * @param errors
 * @return
 *
public String getValue(String value, Integer length, Integer scale, Integer precision, Messages errors){
	//System.out.println("SupportedTypes getValue:Type:"+this.type+",name:"+this.name()+",value:"+value+",length:"+length+",scale:"+scale+",precision:"+precision);
	//Result result = new Result();
	String validatedValue = "";
	/*
}else if(type==java.sql.Types.DATE){
	validatedValue = getValidValue(value, errors);
}else if(type==java.sql.Types.TIME){
	validatedValue = new SimpleDateFormat(TIME_FORMAT).format(getValidValue(value, precision, errors));
}else if(type==java.sql.Types.TIMESTAMP){
	validatedValue = new SimpleDateFormat(TIMESTAMP_FORMAT).format(getValidValue(value, precision, errors));			

	return validatedValue;
	
}*/	
/*
public String getValidValue(String value, Messages errors){
	String error = null;
	String convert = "";	
	
	if(type==java.sql.Types.DATE){
		try{
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
			java.util.Date date = formatter.parse(value); 
			convert = formatter.format(date);
			rv = true;
		}catch(ParseException pe){
			error = pe.getMessage();
			err.println(error);
			rv = false;
		}
	}
	
	if(error!=null){
		errors.add(error);
	}
	return convert;			
}*/

/*
public java.util.Date getValidValue(String value, Integer precision, Messages errors){
	String error = null;
	java.util.Date convert = new java.util.Date(0);
	/*
	if(value==null)throw new IllegalArgumentException("value cannot be null.");//TODO allow nulls?
	if(precision > msupportsPrec || precision < 0-msupportsPrec){
		throw new IllegalArgumentException("value exceeds supported precision");
	}			
	if(type==java.sql.Types.TIME){
		try{
			SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
			java.util.Date date = formatter.parse(value);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if(cal.get(Calendar.MILLISECOND)>0 && precision !=3){
				error = "You cannot store milliseconds if the precision is not 3";
			}
			convert = date;
		}catch(ParseException pe){
			error = pe.getMessage();
			err.println(error);
		}
	}			
	if(type==java.sql.Types.TIMESTAMP){
		try{
			SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
			java.util.Date date = formatter.parse(value);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if(cal.get(Calendar.MILLISECOND)>0 && precision !=3){
				error = "You cannot store milliseconds if the precision is not 3";
				
			}
			convert = date;
		}catch(ParseException pe){
			error = pe.getMessage();
			//err.println(error);
		}
	}	
	if(error!=null){
		errors.add(error);
	}
	return convert;	
}*/

/*
	if(type==java.sql.Types.DATE){/*
		try{
			
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
			java.util.Date date = formatter.parse(value); 
			convert = formatter.format(date);
			
			rv = true;
		}catch(ParseException pe){
			error = pe.getMessage();
			err.println(error);
			rv = false;
		}
	}	
*/
//  Type                     Length                     Scale                            Precision                            Use Quotes
//DATE     (java.sql.Types.DATE,     Type.NONE,       Type.NONE,             Type.NONE,                 true),//Date
//TIME     (java.sql.Types.TIME,     Type.NONE,       Type.MAX_TIMEPRECISION,Type.NONE,                 true),//Time TIME(x)       WITH TIME ZONE          !!
//TIMESTAMP(java.sql.Types.TIMESTAMP,Type.NONE,       Type.MAX_TIMEPRECISION,Type.NONE,                 true),// TIMESTAMP(x) WITH TIME ZONE
/**
 * DOC
 * @return
 
public String getValidationRangeTranslation(){
	return this.dateRangeTranslation;
}*/