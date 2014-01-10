package jhg.model.field;

import hirondelle.web4j.util.EscapeChars;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jhg.Messages;
import jhg.model.Field;
import jhg.model.Manager;
import jhg.model.Value;

import org.apache.commons.lang3.StringUtils;

/**
 * Text is a Value holding string values.
 * 
 * TODO's Implement String methods:
 * 
 * pad a value in a fixed length string using left, right and center (do this with text)
 * 
 * unicode stuff
 * 
 * 
 * @author John
 *
 */
@SuppressWarnings("rawtypes")
public class Text extends Value implements Serializable {

	private static final long serialVersionUID = 6677256131046459137L;

	/**
	 * Wrapper adaptation of a couple string libraries.
	 * This Util may be useful in computations/string functions or calculations
	 * for use between text and other fields.
	 * 
	 * TODO: padLeft padRight object[] to string?
	 * 
	 * @author John
	 *
	 */
	public static class Util {
		
		/**
		 * DOC
		 * 
		 * @param aText
		 * @return
		 */
		public static String escapeForHtml(Text aText){
			String html = EscapeChars.forHTML(aText.toString());
			return html;
		}
		
 		
		
		/** 
		 * This String util method removes single or double quotes 
		 * from a string if its quoted. 
		 * for input string = "mystr1" output will be = mystr1 
		 * for input string = 'mystr2' output will be = mystr2 
		 * 
		 * @param String value to be unquoted. null if input is null. 
		 * 
		 */  
		public static String unquote(String s) {
			 if (s != null  
					 && ( (s.startsWith("\"") && s.endsWith("\""))  
						  || (s.startsWith("'") && s.endsWith("'")) )  
			   ) {  
			  
			  s = s.substring(1, s.length() - 1);  
			 } 
			 return s;  
		}  
		
		/**
		 * DOC
		 * 
		 * @param s
		 * @param surrounder
		 * @return
		 */
		public static String surround(String s, String surrounder){
			return surrounder+s+surrounder;
		}

		/**
		 * DOC
		 * 
		 * @param str
		 * @param delimeter
		 * @return
		 */
		public static List<String> getTokensList(String str, String delimeter) {  
			  if (str != null) {  
				  return Arrays.asList(str.split(delimeter));  
			  }  
			  return new ArrayList<String>();  
		}  		
		
		/**
		 * DOC
		 * @param str
		 * @return
		 */
		public static String lowerCase(String str){
			return StringUtils.lowerCase(str);
		}
		
		/**
		 * DOC
		 * @param str
		 * @return
		 */
		public static String upperCase(String str){
			return StringUtils.upperCase(str);
		}
		
		
		/**
		 * DOC
		 * 
		 * @param str
		 * @param len
		 * @return
		 */
		public static String padLeft(String str,int len){
			return StringUtils.left(str, len);
		}
		
	}
	
	/**
	 * The Field class for a Text value.
	 * 
	 * @author John
	 *
	 */
	public static class TextField extends Field {


		public static enum Type{CHAR,VARCHAR;}		
		
		//public static final int NONE = -1;
		public static final int MAX_VARCHAR = 8000;
		public static final int MAX_CHAR = 1000;
		
		protected Type type;
		
		protected Pattern pattern;
		protected String translation;			
		//protected boolean shouldBeSafe; TODO remove if not implementing.
		private boolean storeOriginalNoParse;
		private String strlowBound;  //alphabetical
		private String strhighBound;
		private boolean multiLine;
		private int rows;
		private int cols;
		
		/**
		 * DOC
		 * 
		 * @param _name
		 */
		public TextField(Manager m, String _name) {  
			super(m, _name);
			type = Type.VARCHAR;
			length = 50;                    //TODO avoid invariant with the set length
			storeOriginalNoParse=false;
			multiLine = false;
			//shouldBeSafe = true;  TODO remove if no impl
		}

		/**
		 * We may want to do some quick regex validation before going through 
		 * the trouble of parsing.
		 * 
		 */
		@Override
		protected boolean preParseValidate(String _value, Messages messages) {
			boolean valid = true;
			if(_value==null){
				valid=false;
			}else if(_value.length()>this.length){
				messages.add("The value of "+this.name+" requires the size to be no longer than "+this.length+" :"+_value);
				valid = false;
			}
			return valid;
		}		
		/**
		 * Parsing can be as easy as a string.replace(find,replacetxt) or
		 * use a regex or algorithm or other string function.
		 */
		@Override
		protected String parse(String _value) {
			return _value;
		}			
		/**
		 * Sometimes this is a regex, sometimes a range check, a contains, or 
		 * whatever but this is only validation as far as the value of the field itself is concerned.
		 * Length MUST be checked here.
		 * 
		 */
		@Override
		protected boolean isValid(String parsed, Messages messages) {
			boolean valid = true;
			//Subtle logic changes can occur if you try && or & short cut operations instead.  Best to keep this explicit.
			if(!validateRegex(parsed, messages)){
				valid = false;
			}
			if(!validateBounds(parsed,messages)){
				valid = false;
			}
			return valid;
		}		
		
		/**
		 * DOC
		 * 
		 * @param _type
		 */
		public void setType(Type _type){
			invariant(_type,length);
			this.type = _type;
		}
		
		
		/**
		 * @return the strlowBound
		 */
		public String getStrlowBound() {
			return strlowBound;
		}


		/**
		 * @param strlowBound the strlowBound to set
		 */
		public void setStrlowBound(String strlowBound) {
			this.strlowBound = strlowBound;
		}


		/**
		 * @return the strhighBound
		 */
		public String getStrHighBound() {
			return strhighBound;
		}


		/**
		 * @param strhighBound the strhighBound to set
		 */
		public void setStrHighBound(String strhighBound) {
			this.strhighBound = strhighBound;
		}


		/**
		 * DOC
		 * 
		 * @param _length
		 */
		public void setLength(int _length){
			invariant(type,_length);
			this.length = _length;
		}
		
		/*
		 * Make sure any mutators do not inadvertently violate the object's class invariants.
		 */
		protected void invariant(Type _type, int _length){
			if(_type.equals(Type.VARCHAR) && _length>MAX_VARCHAR){
				throw new IllegalArgumentException("The length you provide when the type is varchar must be <= "+MAX_VARCHAR);
			}
			if(_type.equals(Type.CHAR) && _length>MAX_CHAR){
				throw new IllegalArgumentException("The length you provide when the type is varchar must be <= "+MAX_VARCHAR);
			}
		}
		
		/**
		 * Determine the store as is or parse mode. 
		 * This check will see if this field should preserve the input  
		 * or if this field should be parsed and altered into a storage format losing input information.
		 * @return  true if the values for this field are saved as provided when client provides the string _value.
		 */
		public boolean isStoredNoParse(){
			return storeOriginalNoParse;
		}
		
		/**
		 * DOC
		 * 
		 */
		public void toggleStoreOriginalNoParse(){
			this.storeOriginalNoParse = !storeOriginalNoParse;
		}
		
		/**
		 * DOC
		 * @return
		 */
		public boolean isMultiLine(){
			return this.multiLine;
		}
		
		/**
		 * DOC
		 * @return
		 */
		public int rows(){
			return this.rows;
		}
		
		/**
		 * DOC
		 * @return
		 */
		public int cols(){
			return this.cols;
		}

		/**
		 * DOC
		 * @param value
		 * @return
		 */
		public void setMultiLine(int r, int c){
			this.multiLine = true;
			this.rows = r;
			this.cols = c;
		}
		
		/**
		 * DOC
		 * 
		 * @param _pattern
		 * @param _translation
		 */
		public void setValidationRegex(Pattern _pattern, String _translation) {
			this.pattern = _pattern;
			this.translation = _translation;
		}

		
		/**
		 * Validate against the regular expression.
		 * 
		 * @param value
		 * @param messages
		 * @return true if there is no regex to test against or the value is matches the regex (at least once but could be more)
		 */
		public boolean validateRegex(String value, Messages messages){
			if(pattern==null){
				return true;
			}
			Matcher m = pattern.matcher(value);
			boolean valid = m.matches();
			if(!valid){
				messages.add("Field "+name+" only allows "+translation+".");
			}
			return valid;
		}
		
		/**
		 * DOC
		 * @param value
		 * @param messages
		 * @return
		 */
		public boolean validateBounds(String value, Messages messages){
			boolean valid = true;
			if(strlowBound!=null && strlowBound.compareTo(value)>0){
				messages.add("Value must be alphabetically above '"+strlowBound+"'");
				valid = false;
			}
			if(strhighBound!=null && strhighBound.compareTo(value)<0){
				messages.add("Value must be alphabetically below '"+strhighBound+"'");
				valid = false;
			}
			return valid;
		}


		@Override
		public int getLength(){
			return this.length;
		}
		
		@Override
		public String getFormatTemplate() {
			return "{0}";
		}

		@Override
		public int getSqlType() {
			switch(type){
				case CHAR:return java.sql.Types.CHAR;
				case VARCHAR: return java.sql.Types.VARCHAR;
				default: return java.sql.Types.VARCHAR;
			}
		}
		
		@Override
		public String toDDL(){
			StringBuilder sb = new StringBuilder();
			sb.append(this.name+" ");
			sb.append(type.name());
			sb.append("(");
			sb.append(getLength());
			sb.append(") ");
			if(!isIdentity){
				sb.append((isNullable)?"":"NOT ");
				sb.append("NULL ");
			}
			//NOTE: we can't use text here with hsql default, we have to use the GUID option instead. TODO guid
			/*
			else{
				sb.append("GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1) ");//"GENERATED ALWAYS AS IDENTITY");
			}
			if(isPrimary){
				sb.append("PRIMARY KEY");
			}
			*/
			return sb.toString();
		}

		@Override
		protected Value factoryValue(String v) {
			return new Text(this,v);
		}
				
		
	}//TextField

	
	/**
	 * Construct a Text value object.
	 * 
	 * @param _field  the text field that owns this value.
	 * @param _value  the value to initialize this Text value object to.
	 */
	protected Text(TextField _field, String _value)  {
		super(_field, _value);
	}	
	
	@Override
	public String format() {
		return MessageFormat.format(field.getFormatTemplate(), value);
	}
	
	@Override
	public String toSql(){
		return "'"+value+"'";
	}
	
	
}



//      Type                     Length                     Scale                            Precision                            Use Quotes
//	CHAR     (java.sql.Types.CHAR,     Type.MAX_CHAR,   Type.NONE,             Type.NONE,                 true),//String
//	VARCHAR  (java.sql.Types.VARCHAR,  Type.MAX_VARCHAR,Type.NONE,             Type.NONE,                 true),//String  VARCHAR(8000)
