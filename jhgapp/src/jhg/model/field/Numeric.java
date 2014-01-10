package jhg.model.field;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import jhg.Messages;
import jhg.model.Manager;
import jhg.model.Value;

/**
 * DOC
 * 
 * Note: 
 * -Not Implemented here: Computer hex, binary, octal formats.
 * -This will not parse the L on the end of a long
 * 
 * 
 * 
 * 
 * @author John
 *
 */
@SuppressWarnings("rawtypes")
public class Numeric extends Value implements Serializable {
/*
 * TODO see below:
 *
 * Investigate: hsql handling of long and other integer types as Id's. also, look 
 * at the possible ways of implementing this better for ID's: 
 * note: Field has isPrimary/isIdentity but this 
 * is usually done with hsql INTEGER
 */
	
	public static final long ZERO = 0L;
	
	private static final long serialVersionUID = 6676787856154470220L;

	/**
	 * DOC
	 * @author John
	 *
	 */
	public static class Util {
		
		/**
		 * DOC
		 * 
		 * @param number
		 * @param msgs
		 * @return
		 */
		public static BigInteger toBigInt(Numeric number){
			Long l = number.longValue();
			BigInteger bi = BigInteger.valueOf(l);
			return bi;
		}
	
		/**
		 * DOC
		 * @param a
		 * @param b
		 * @return
		 */
		public static String add(Numeric a, Numeric b){
			long newval = a.longValue + b.longValue;//Note: it may go out of bounds.
			String result = String.valueOf(newval);
			return result;
		}
		
		/**
		 * DOC
		 * @param list
		 * @return
		 */
		public static String sum(List<Numeric> list){
			long total = 0L;
			for(Numeric n:list){
				total = total + n.longValue;
			}
			String result = String.valueOf(total);
			return result;
		}
		
		/**
		 * DOC
		 * @param a
		 * @param b
		 * @return
		 */
		public static String subtract(Numeric a, Numeric b){
			long newval = a.longValue - b.longValue;//Note: it may go out of bounds.
			String result = String.valueOf(newval);
			return result;
		}	
		
		/**
		 * DOC
		 * @param a
		 * @param b
		 * @return
		 */
		public static String multiply(Numeric a, Numeric b){
			long newval = a.longValue * b.longValue;//Note: it may go out of bounds.
			String result = String.valueOf(newval);
			return result;
		}	
		
		/**
		 * DOC
		 * example wrapper.
		 * @param a
		 * @param b
		 * @return
		 * @see Math.pow(double,double)
		 */
		public static String pow(Numeric a, double exponent){
			double newval = Math.pow(a.doubleValue(), exponent);
			String result = String.valueOf(newval);
			return result;		
		}
		
		/**
		 * Divides two numbers using their equivalent long values.
		 * Uses the simple divisor and produces a double.
		 * This may not be suitable for financial applications.
		 * The string result may go out of bounds so it's for use in creating a new value.
		 * 
		 * @param a  the numerator
		 * @param b  the divisor or denominator.
		 * @return  a string representation of the double result.
		 */
		public static String divideDouble(Numeric a, Numeric b){
			double newval = a.longValue / b.longValue;//Note: it may go out of bounds.
			//TODO utilize bigdecimal for more fine tuned control of the division.
			String result = String.valueOf(newval);
			return result;
		}	
		
		/**
		 * Divides two numbers using their equivalent long values.
		 * Uses the simple divisor and produces a double.
		 * This may not be suitable for financial applications.
		 * The string result may go out of bounds so it's for use in creating a new value.
		 * 
		 * @param a  the numerator
		 * @param b  the divisor or denominator.
		 * @return  a big decimal object (not string) so you can choose which output is most appropriate on BigDecimal.
		 */	
		public static BigDecimal divide(Numeric a, int aScale, Numeric b, int bScale, MathContext mc){
			BigDecimal bdA = new BigDecimal(a.longValue);
			BigDecimal bdB = new BigDecimal(b.longValue);
			bdA.setScale(aScale);
			bdB.setScale(bScale);
			BigDecimal bdAnswer = bdA.divide(bdB,mc);
			return bdAnswer;
		}		
	}
	
	/**
	 * DOC
	 * @author John
	 *
	 */
	public static class NumberField extends jhg.model.Field{
		
		//public static final String parseValidationExpression = "^[+-]\\d+[L]$";
		
		/**
		 * TODO finish doc
		 * Due to SQL restrictions, we don't have a big integer except as text, we can however
		 * provide utility to parse it. 
		 * @author John
		 *
		 */
		public static enum Type{TINYINT,SMALLINT,INTEGER,BIGINT;}//Byte,Short,Integer,Long
		
		public static final long MAX_TINY = Byte.MAX_VALUE;
		public static final long MAX_SMALL = Short.MAX_VALUE;
		public static final long MAX_INT = Integer.MAX_VALUE;
		public static final long MAX_LONG = Long.MAX_VALUE;

		protected Type type;
		//protected Long lowBound;
		//protected Long highBound;
		protected int  fixedWidth;
		
		/**
		 * DOC
		 * @param m
		 * @param _name
		 */
		public NumberField(Manager m, String _name, Type _type) {
			super(m, _name);
			this.type = _type;
			switch(this.type){
				case TINYINT: length = 4;      //-128 to 127 length 4      formatted 4
				break;
				case SMALLINT:length = 6;      //-32768 to 32767           formatted 7  :-32,768 to 32,767 -32,768 to 32,767
				break;
				case INTEGER: length = 11;     //-2147483648 to 2147483647 formatted 14 :-2,147,483,648 to 2,147,483,647
				break;
				case BIGINT:  length = 20;       //-9223372036854775808 to 9223372036854775807 formatted 26 (+6 commas)  : -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807
				break;
				default:
				break;
			}
			this.fixedWidth = length;
		}

		/**
		 * DOC
		 * @return
		 */
		public int getFormattedWidth(){
			int result = length;
			switch(this.type){
				case TINYINT: result = 4;      //-128 to 127 length 4      formatted 4
				break;
				case SMALLINT:result = 7;      //-32768 to 32767           formatted 7  :-32,768 to 32,767 -32,768 to 32,767
				break;
				case INTEGER: result = 14;     //-2147483648 to 2147483647 formatted 14 :-2,147,483,648 to 2,147,483,647
				break;
				case BIGINT:  result = 26;       //-9223372036854775808 to 9223372036854775807 formatted 26 (+6 commas)  : -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807
				break;
				//default:
				//break;
			}
			return result;
		}
		
		/**
		 * DOC
		 * 
		 * @param biggerLength
		 */
		public void setFixedWidth(int biggerLength){
			if(biggerLength < length){throw new IllegalArgumentException("The fixed width must exceed the normal display length.");}
			this.fixedWidth = biggerLength;
		}
		
		/**
		 * DOC
		 * 
		 * 
		 * @param lowBound  a boundary for the type.
		 * @param highBound
		 * @throws IllegalArgumentException if the bounds are outside the valid range for the type.
		 * @throws IllegalStateException if the type is not set.
		 */
		public void setBounds(Long _lowBound, Long _highBound){
			switch(this.type){
				case TINYINT:
					if(_lowBound!=null && _lowBound<Byte.MIN_VALUE) throw new IllegalArgumentException("The low bound cannot be lower than"+Byte.MIN_VALUE);
					if(_highBound!=null && _highBound>Byte.MAX_VALUE) throw new IllegalArgumentException("The high bound cannot be high than"+Byte.MAX_VALUE);					
				break;
				case SMALLINT:
					if(_lowBound!=null && _lowBound<Short.MIN_VALUE) throw new IllegalArgumentException("The low bound cannot be lower than"+Short.MIN_VALUE);
					if(_highBound!=null && _highBound>Short.MAX_VALUE) throw new IllegalArgumentException("The high bound cannot be high than"+Short.MAX_VALUE);					
				break;						
				case INTEGER:
					if(_lowBound!=null && _lowBound<Integer.MIN_VALUE) throw new IllegalArgumentException("The low bound cannot be lower than"+Integer.MIN_VALUE);
					if(_highBound!=null && _highBound>Integer.MAX_VALUE) throw new IllegalArgumentException("The high bound cannot be high than"+Integer.MAX_VALUE);					
				break;
				case BIGINT:
					if(_lowBound!=null && _lowBound<Long.MIN_VALUE) throw new IllegalArgumentException("The low bound cannot be lower than"+Long.MIN_VALUE);
					if(_highBound!=null && _highBound>Long.MAX_VALUE) throw new IllegalArgumentException("The high bound cannot be high than"+Long.MAX_VALUE);					
				break;
				default: 
					throw new IllegalStateException("Type is not set.");
			}			
			this.lowBound = _lowBound;
			this.highBound = _highBound;
		}
		
		/**
		 * DOC
		 * @return
		 */
		public Long getMin(){
			return this.lowBound;
		}
		 
		/**
		 * DOC
		 * 
		 * @return
		 */
		public Long getMax(){
			return this.highBound;
		}
		
		
		@Override
		protected boolean preParseValidate(String _value, Messages messages) {
			boolean valid = true;
			if(_value==null){
				valid=false;
			}else {
				if(_value.contains(",")){
					try {
						NumberFormat.getInstance().parse(_value).toString();
					} catch (ParseException e) {
						messages.add("The value:'"+_value+"' could not be parsed using standard format. e.g.: -9,999,999,999 ");
						valid = false;
					}					
				}else{
					try{
						switch(this.type){
							case TINYINT: Byte.parseByte(_value);
							break;
							case SMALLINT:Short.parseShort(_value);
							break;						
							case INTEGER: Integer.parseInt(_value);
							break;
							case BIGINT:  Long.parseLong(_value);
							break;
							default: valid = false;  //should never happen... 
							break;
						}
					}catch(NumberFormatException nfe){
						setMessage(messages,_value);
						valid = false;
					}							
				}
				debug("Value prevalidation passed:"+_value);
			}
			return valid;
		}
		
		private void setMessage(Messages messages, String _value) {
			messages.add("The value: '"+_value+"' could not be parsed (number format exception) for type: "+type.name()+".");
		}

		@Override
		protected String parse(String _value) {
			String result = null;
			if(_value.contains(",")){
				_value = parseFormatted(_value);
			}//TODO we can add the percent and currency parsing if need be.
			//we can also add the leading zero parse if possible.
			try{
				switch(this.type){
					case TINYINT:
						result = new Byte(_value).toString();
					break;
					case SMALLINT:
						result = new Short(_value).toString();
					break;						
					case INTEGER:
						result = new Integer(_value).toString();
					break;
					case BIGINT:
						result = new Long(_value).toString();
					break;
					default:
						result = null;
				}
			}catch(NumberFormatException nfe){
				throw new IllegalArgumentException(nfe);
			}
			return result;
		}		
		private String parseFormatted(String _value) {
			String result = "";
			try {
				result = NumberFormat.getIntegerInstance().parse(_value).toString();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected boolean isValid(String parsed, Messages messages) {
			boolean valid = true;
			//Subtle logic changes can occur if you try && or & short cut operations instead.  Best to keep this explicit.
			if(!validateBounds(parsed,messages)){
				valid = false;
			}
			return valid;
		}		
		
		private boolean validateBounds(String parsed, Messages messages) {
			boolean valid = true;
			long value = Long.valueOf(parsed);//Note: it's not constructed yet.
			if(lowBound!=null && value < lowBound){
				messages.add("The value "+value+" cannot be lower than "+lowBound);
				valid = false;
			}
			if(highBound!=null && value > highBound) {
				messages.add("The value "+value+" cannot be higher than "+highBound);
				valid = false;
			}
			return valid;
		}

		/**
		 * DOC
		 * @return
		 */
		public Type getType(){
			return this.type;
		}


		@Override
		public String getFormatTemplate() {
			return "";
		}
		
		@Override
		protected Value factoryValue(String v) {
			return new Numeric(this,v);
		}				
		
		@Override
		public int getSqlType() {
			int result = 0;
			try{
				switch(this.type){
					case TINYINT:
						result = java.sql.Types.TINYINT;
					break;
					case SMALLINT:
						result = java.sql.Types.SMALLINT;
					break;						
					case INTEGER:
						result = java.sql.Types.INTEGER;
					break;
					case BIGINT:
						result = java.sql.Types.BIGINT;
					break;
					default:
						result = 0;
				}
			}catch(NumberFormatException nfe){
				throw new IllegalArgumentException(nfe);
			}
			return result;
		}


		@Override
		public String toDDL() {
			StringBuilder sb = new StringBuilder();
			sb.append(this.name+" ");
			sb.append(type.name());
			sb.append(" ");
			if(!isIdentity){
				sb.append((isNullable)?"":"NOT ");
				sb.append("NULL ");
			}else{
				sb.append("GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1) ");//"GENERATED ALWAYS AS IDENTITY");
			}
			if(isPrimary){
				sb.append("PRIMARY KEY");
			}
			return sb.toString();
		}
		
	}
	
	
	private Long longValue;

	/**
	 * DOC
	 * @param _field
	 * @param _value
	 */
	protected Numeric(NumberField _field, String _value) {
		super(_field, _value);
		this.longValue = Long.parseLong(this.value);
	}	

	
	@Override
	public String format() {
		NumberFormat f = NumberFormat.getIntegerInstance();
		return f.format(longValue);
		//the default will do the 3 comma separation or locale default.
		//DecimalFormat df = new DecimalFormat(field.getFormatTemplate());
		//return df.format(longValue);
	}
	
	@Override
	public String toSql(){
		return super.toSql();
	}	
	
	@Override
	public int compareTo(Value anotherValue){
		Numeric anotherNumber = (Numeric)anotherValue;
		return longValue.compareTo(anotherNumber.longValue);
	}
	@Override
	public boolean equals(Object o){
		if(o==null || !(o instanceof Numeric)){return false;}
		Numeric anotherNumber = (Numeric)o;
		return longValue.equals(anotherNumber.longValue);
	}	
	
	/**
	 * DOC
	 * @return
	 */
	public String formatCurrency(){
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		return formatter.format(longValue);
	}
	
	/**
	 * DOC
	 * @return
	 */
	public String formatPercent(){
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		return formatter.format(longValue);
	}
	
	/**
	 * DOC
	 * @return
	 */
	public String formatLeadingZeros(){
		return String.format("%0"+field.getLength()+"d", longValue);		
	}
	
	/**
	 * DOC
	 * @param msgs
	 * @return
	 */
	public Long longValue(){
		return longValue;
	}
	
	/**
	 * DOC
	 * 
	 * @return
	 */
	public Double doubleValue(){
		return longValue.doubleValue();
	}
	
	/**
	 * DOC
	 * @return
	 */
	public Integer intValue(){
		//NumberField.Type type = ((NumberField)field).getType();
		//if the field is BIGINT we narrow but if smaller, it enlarges safely.
		Integer i = Integer.parseInt(this.value);//automatically throws the runtime exception when not keeping proper track 
		return i;
	}	
	
	/**
	 * DOC
	 * @return
	 */
	public Short shortValue(){
		//NumberField.Type type = ((NumberField)field).getType();
		//if the field is BIGINT or INT we narrow dangerously but if smaller, it enlarges safely.
		Short s = Short.parseShort(this.value);
		return s;
	}
	
	/**
	 * DOC
	 * @return
	 */
	public Byte byteValue(){
		//Note: will narrow if bigint (long), int or small
		Byte b = Byte.parseByte(this.value);
		return b;
	}
	
	
	
}

/*
 * 
 * x Finish contract and unimplemented: toDDL, 
 * x space pad plain format / space pad formatted (with commas) (see fixed width impl)
 * x format as percent     
 * x format as currency    (add 5 if padding spaces)
 * x format with leading zeros: no comma, negative sign before zeros.
 * > format with special L sign for long. (too trivial)
 * x get the print size for each type (with and without commas)
 * x calculations for adding, subtracting, multiply, divide? 
 * x ensure numeric sort
 * x ensure matching
 * x preParseValidate - Don't use the number format exception's message, it's not informative //Exception in thread "main" java.lang.NumberFormatException: For input string: "28929233343434343434343"
 * x ensure we can parse numbers having commas
 * x look at the low bound and high bound on the Field class
 * x parse from formats with commas, etc.

 */
