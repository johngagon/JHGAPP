package jhg.model.field.special;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jhg.Messages;
import jhg.model.Manager;
import jhg.model.field.Text;

/**
 * Represents a social security number of the format 999-99-9999.
 * Can parse several ways but it must contain 9 numerals.
 * The range of these numbers can be specified.
 * The field expects to parse 999-999999 or 999999999 or 999-99-9999
 * The storage format is 999999999.
 * The display format is 999-99-9999.
 * 
 * @author John
 *
 */
public class Ssn extends Text {

	private static final long serialVersionUID = -3925585594570960109L;


	/**
	 * The field definition for all SSN and the accompanying base business rules
	 * for a social security number.
	 * 
	 * @author John
	 *
	 */
	public static final class SsnField extends Text.TextField {
        public static final String parseValidationExpression = "^\\d{3}[- ]?\\d{2}[- ]?\\d{4}$";
        public static final String parseFindExpression = "-" ;//"^(\\d{3})[- ]?(\\d{2})[- ](?\\d{4})$";
        public static final String parseReplaceExpression = "";//"$1$2$3";
        
        /**
         * Construct an SSN field with given name.
         * @param _name
         */
		@SuppressWarnings("rawtypes")
		public SsnField(Manager m, String _name) {
			super(m,_name);
			length=9;
		}
		
		
		/**
		 * The field must allow parsing as a long.
		 * It corresponds to a >=
		 * 
		 * @param lowbound
		 */
		public void setLowBound(Long aLowbound){
			this.lowBound = aLowbound;
		}
		
		/**
		 * The field must allow parsing as a long.
		 * It corresponds to a <=
		 * 
		 * @param highbound
		 */
		public void setHighBound(Long aHighbound){
			this.highBound = aHighbound;
		}	
				
		
		/*
		@Override
		protected Result makeValue(String _value, Messages messages, boolean calc) {
			debug("Begin process creating value:"+_value);
			Result result = Result.NOT_EXECUTED;
			try{
				if(!preParseValidate(_value, messages)){
					return Result.INVALID_INPUT;
				}
		        _value = parse(_value);
		        if(!isValid(_value,messages)){ //pretend we only want ssn's between 881* and 921*;
		        	return Result.INVALID_INPUT;
		        }
			}catch(Exception e){
				result = Result.ERROR;
				result.exception(e);
				return result;
			}
			result = Result.SUCCESS;
			result.value(new Ssn(this,_value));
			debug("Finished process creating value:"+_value);
			return result;
		}
		*/

		@Override
		protected boolean preParseValidate(String _value, Messages messages) {
			if(_value==null){
				messages.add("Value null:"+_value);
				return false;
			}
			CharSequence inputStr = _value;
			Pattern pattern = Pattern.compile(parseValidationExpression);
			Matcher matcher = pattern.matcher(inputStr);
			if (!matcher.matches()) {
				messages.add("Value "+_value+" did not match the expression.");
				return false;
			}
			debug("Value prevalidation passed:"+_value);
			return true;
		}

		@Override
		public String parse(String _value) {
			//String _oldValue = String.valueOf(_value);
			_value = _value.replace(parseFindExpression, parseReplaceExpression);
			debug("Parsed:"+_value);
			return _value;
		}		
		
		@Override
		public String getFormatTemplate(){
			return "{0}-{1}-{2}";
		}
		
		@Override
		public boolean isValid(String parsed, Messages messages){
			boolean valid = false;
			try{
				if(lowBound!=null || highBound!=null){
					Long longVal = Long.parseLong(parsed);
					if(lowBound!=null && longVal < lowBound){
						valid = false;
						messages.add("The value "+parsed+" was lower than the low bound "+lowBound+".  ");
					}else if(highBound!=null && longVal > highBound){
						valid = false;
						messages.add("The value "+parsed+" was higher than the high bound "+highBound+".  ");
					}else{
						valid = true;
					}
				}
			}catch(NumberFormatException nfe){
				valid = false;
				messages.add("The parsed string "+parsed+" was not formatted as a number.");
			}
			
			debug("Validated "+parsed);
			return valid;
		}
	}

	/**
	 * Make a social security number.
	 * 
	 * @param _field
	 * @param _value
	 */
	protected Ssn(SsnField _field, String _value) {
		super(_field, _value);
	}
	
	@Override
	public String format(){
		debug("Formating.");
		String formatTemplate = field.getFormatTemplate();
		String num1 = value.substring(0,3);
		String num2 = value.substring(3,5);
		String num3 = value.substring(5,9);
		return MessageFormat.format(formatTemplate,num1,num2,num3);
	}

	
	

	
	
	
	
	/*
     * Test data type.
    
    private static void test(){
    	System.out.println("Begin Test.");
    	String[] tests = {"234-56-7890","234-56-78900","234567890",
    			"234567-789","881000000","921999999",
    			"911990033","912-990033","922000000"};
    	SsnField ssnField = new SsnField(null,"SocialSecurityNumber");//TODO fixme, the manager argument is null. will need to fix later when Manager gets impl.
    	ssnField.setLowBound(881000000L);
    	ssnField.setHighBound(921999999L);
    	int i=0;
    	for(String test:tests){
    		i++;
    		Messages messages = new Messages();
    		System.out.println("\n\nTest("+i+"):"+test);
    		Result r = ssnField.makeValue(test, messages,true);//NOTE: shouldn't call it directly, use setValue(Field,Value) instead
    		if(r==Result.SUCCESS){
    			Value ssn = r.value();
    			System.out.println("Value obtained:"+ssn.format()+"   ("+ssn.toString()+")");
    		}else{
    			if(messages.have()){
    				for(String message:messages.get()){
    					System.out.println("Message:"+message);
    				}
    			}
    			if(r==Result.ERROR && r.hasException()){
    				Exception e = r.exception();
    				e.printStackTrace();
    			}
    		}
    		System.out.println("Result:"+r.name());
    	}    	
    } */
	

    
    /**
     * System hook.
     * @param args
     */
    public static void main(String[] args) {
    	//test();
    }	    
    
}


/* 

	isSSNValid: Validate Social Security number (SSN) using Java reg ex.
	This method checks if the input string is a valid SSN.
	@param ssn String. Social Security number to validate
	@return boolean: true if social security number is valid, false otherwise.

	public static boolean isSsnValid(String ssn) {
	    boolean isValid = false;
	    
	    //SSN format xxx-xx-xxxx, xxxxxxxxx, xxx-xxxxxx; xxxxx-xxxx:
	         ^\\d{3}: Starts with three numeric digits.
	        [- ]?: Followed by an optional "-"
	        \\d{2}: Two numeric digits after the optional "-"
	        [- ]?: May contain an optional second "-" character.
	        \\d{4}: ends with four numeric digits.
	        Examples: 879-89-8989; 869878789 etc.
	    
	
	
	    
	    CharSequence inputStr = ssn;
	    Pattern pattern = Pattern.compile(expression);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    
	    return isValid;
	
		//TEST
	    System.out.println("1. SSN is " + isSsnValid("234-56-7890"));//true
	    System.out.println("2. SSN is " + isSsnValid("234-56-78900"));//false too many
	    System.out.println("3. SSN is " + isSsnValid("234567890"));//true
	    System.out.println("4. SSN is " + isSsnValid("234567-789"));//false
	}
*/

/*

Expected input formats: (format).

Format.STORE 123456789 ("<regex>" example " ");
Format.COMMON 123-45-6789

isParsed(): yes if any modification occurs, no if the original value is stored as entered.          

parseValidate(String in,Messages msgs):String out   A regex/formatter.parse is used.
Case 1: 123-456789  => 123456789 (for phone numbers, pieces of the string are grabbed off)
Case 2: 123456789   => 123456789 (no parsing needed but should pass parsing algorithm), also from storage.

parseValidate(Format expected, String in, Messages msgs):String out.
Case: 	123-45-6789 => 123456789

toString()  the _storage_ value.   123456789        

format(Format.COMMON)    123456789 => 123-45-6789   formatter.format

getField(Field.subfield) 

* This should be a text field in the possible case of alpha numerics since
* the SSN is not a quantity although it may be parsed of hyphens before storage.
* We can allow that option by flag.
* 
* Inputs valid: SSN format xxx-xx-xxxx, xxxxxxxxx, xxx-xxxxxx; xxxxx-xxxx:
* Stored: as entered or as xxxxxxxxx (we lose the entry format)
* Format as entered or as xxx-xx-xxxx:

*/