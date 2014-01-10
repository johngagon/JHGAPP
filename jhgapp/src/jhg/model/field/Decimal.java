package jhg.model.field;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import jhg.Messages;
import jhg.model.Field;
import jhg.model.Manager;
import jhg.model.Value;


/**
 * Tricky  note: HSQL says DECIMAL (10,2) is total digits and of those, 2 decimal places.
 * The BigDecimal meaning is a little different in that it will shorten numbers
 * if the precision is a lower number.
 * 
 * 
 * 
 * HSQL: Thus 0.2 is considered a DECIMAL value but 0.2E0 is considered a DOUBLE value
 * type definition of DECIMAL with no precision and scale is treated as DECIMAL(100,10). In normal operation, it is treated as DECIMAL(100).
 * 
 * 	see decimal format http://docs.oracle.com/javase/6/docs/api/java/text/DecimalFormat.html
 * 
 * 
The decimal precision and scale of NUMERIC and DECIMAL types can be optionally defined. 
For example, DECIMAL(10,2) means maximum total number of digits is 10 and there are always 2 digits after the decimal point, 
while DECIMAL(10) means 10 digits without a decimal point. The bit-precision of FLOAT can be defined 
but it is ignored and the default bit-precision of 64 is used. The default precision of NUMERIC and DECIMAL 
(when not defined) is 100.
 * 
The DDL is Precision THEN Scale.  The BigDecimal definition of precision is significant figures.
So we have to convert and scale is number of fraction places and cannot be incompatible with the precision.

 * 
 * @author John
 *
 */
@SuppressWarnings("rawtypes")
public class Decimal extends Value implements Serializable {
/*
 * TODO some pressure for this to split out but with interface.
 * 
 * TODO impl
 * 
 * See Field
 * 	protected BigDecimal lowLimit;  //  >
	protected BigDecimal upperLimit;//  <
	protected BigDecimal lowerBound;//  >=
	protected BigDecimal upperBound;//  <=

 * 
 * TODO impl or determine if needed . YAGNI/KISS? wait for test case need.
 * > format with leading zeros: no comma, negative sign before zeros.
 * > space pad plain format / space pad formatted (with commas) (see fixed width impl)
 * > get the print size for each type (with and without commas)
 */
	public static final BigDecimal ZERO_BD = new BigDecimal(0);
	public static final Double ZERO_DBL = 0.0;

	private static final long serialVersionUID = -8093746880932949058L;

	//quanity (additive and without regard for precision, everyday and monetary  (this is most suited however to big decimal
	//measure (can be multiplied and requires precision, scientific)              this is probably suited to double or special cases of big decimal

	/**
	 * DOC
	 * @author John
	 *
	 */
	public static class Util {

	
		/**
		 * DOC
		 * @param a
		 * @param b
		 * @return
		 */
		public static String add(Decimal a, Decimal b){
			//Note: these may have different types. if one of them is bigdecimal, add to each other.
			BigDecimal newval = a.bdValue.add(b.bdValue);
			String result = String.valueOf(newval);
			return result;
		}
		
		/**
		 * DOC
		 * @param list
		 * @return
		 */
		public static String sum(List<Decimal> list){
			BigDecimal total = new BigDecimal(0.0);
			for(Decimal n:list){
				total = total.add(n.bdValue);
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
		public static String subtract(Decimal a, Decimal b){
			//Note: these may have different types. if one of them is bigdecimal, add to each other.
			BigDecimal newval = a.bdValue.subtract(b.bdValue);
			String result = String.valueOf(newval);
			return result;
			//return newval;
		}	
		
		/**
		 * DOC
		 * @param a
		 * @param b
		 * @return
		 */
		public static String multiply(Decimal a, Decimal b){
			//Note: these may have different types. if one of them is bigdecimal, add to each other.
			BigDecimal newval = a.bdValue.multiply(b.bdValue);
			String result = String.valueOf(newval);
			return result;
			//return newval;
		}	
		
		/**
		 * DOC
		 * example wrapper.
		 * @param a
		 * @param b
		 * @return
		 * @see Math.pow(double,double)
		 */
		public static String pow(Decimal a, int exponent){
			BigDecimal newval = a.bdValue.pow(exponent);//Math.pow(a.doubleValue(), exponent);
			String result = String.valueOf(newval);
			return result;		
		}
		
		/**
		 * This method performs a calculation using the Double and Math.pow
		 * @param a
		 * @param exponent
		 * @return
		 */
		public static String pow(Decimal a, Decimal exponent, boolean useDouble){
			String result = null;
			if(useDouble){
				Double newval = Math.pow(a.doubleValue(), exponent.doubleValue);
				result = String.valueOf(newval);
			}else{
				BigDecimal newval = new BigDecimal( (Math.pow(a.bdValue.doubleValue(),exponent.bdValue.doubleValue())),((DecimalField)a.field).mc);
				result = String.valueOf(newval);
			}
			return result;				
		}
		
		/**
		 * DOC
		 * 
		 * @param a  the numerator
		 * @param b  the divisor or denominator.
		 * @return  a string representation of the double result.
		 */
		public static String divide(Decimal a, Decimal b){
			BigDecimal newval = a.bdValue.divide(b.bdValue);//Note: it may go out of bounds.
			String result = String.valueOf(newval);
			return result;
		}	
	

		/**
		 * DOC
		 * @param _value
		 * @return
		 */
		public static int decimalPlaces(String decimalStr) {
			int decimalPlaces = 0;
			if(decimalStr.indexOf(".")!=-1){
				decimalPlaces = (decimalStr.length()-1)-decimalStr.indexOf(".");
			}
			return decimalPlaces;
		}
		
		/**
		 * DOC
		 * @param decimalStr
		 * @return
		 */
		public static int totalDigits(String decimalStr){
			int count = 0;
			for (int i = 0, len = decimalStr.length(); i < len; i++) {
			    if (Character.isDigit(decimalStr.charAt(i))) {
			        count++;
			    }
			}		
			return count;
		}
	}	
	
	/**
	 * DOC
	 * @author John
	 *
	 */
	public static class DecimalField extends Field{
		
		public static final Double ZERO = 0.0;
		
		public static final int MAX_DECIMAL_SCALE = 1024;
		public static final int MAX_DECIMAL_PRECISION = 1023;	
		
		/**
		 * DOC
		 * @author John
		 */
		public static enum Type{DOUBLE,DECIMAL;}//Byte,Short,Integer,Long
		
		protected Type type;
		protected int scale;
		protected int precision;
		protected MathContext mc;
		protected NumberFormat formatter;
		private boolean preventShortening;//e.g.: E values. validate precisions.
		protected BigDecimal bdLowBound;
		protected BigDecimal bdHighBound;
		
		
		/**
		 * DOC
		 * 
		 * @param m
		 * @param _name
		 */
		public DecimalField(Manager m, String _name, int _precision, int _scale, RoundingMode _roundingMode) {
			super(m, _name);
			this.type = Type.DECIMAL;
			setScalePrecision(_scale, _precision);
			mc = new MathContext(precision,_roundingMode);
			preventShortening = true;//TODO use later if false to allow E notation
			formatter = DecimalFormat.getInstance(); 
		}

		
		/**
		 * DOC
		 * 
		 * @param m
		 * @param _name
		 */
		public DecimalField(Manager m, String _name){
			super(m,_name);
			this.type = Type.DOUBLE;
			preventShortening = true;
		}

		/**
		 * DOC
		 * @param _formatter
		 */
		public void setFormatter(NumberFormat _formatter){
			this.formatter = _formatter;
		}
		
		/**
		 * DOC
		 * @return
		 */
		public NumberFormat getFormatter(){
			return this.formatter;
		}
		
		/**
		 * DOC
		 * @return
		 */
		public Type getType(){
			return this.type;
		}
		
		/**
		 * DOC
		 * @return
		 */
		public MathContext getMathContext(){
			if(this.type.equals(Type.DECIMAL)){
				return this.mc;
			}else{
				throw new UnsupportedOperationException("This method is not supported for type:"+this.type.name());
			}
		}
		
		/**
		 * DOC
		 * @return
		 */
		public int getScale(){
			if(this.type.equals(Type.DECIMAL)){
				return this.scale;
			}else{
				throw new UnsupportedOperationException("This method is not supported for type:"+this.type.name());
			}
		}
		
		/*
		 * Sets the scale and precision based on rules.
		 */
		private void setScalePrecision(int _scale, int _precision){
			if(_scale > MAX_DECIMAL_SCALE){throw new IllegalArgumentException("The scale argument: "+_scale+" has to be less than or equal to "+MAX_DECIMAL_SCALE);}
			if(_precision > MAX_DECIMAL_PRECISION){throw new IllegalArgumentException("The precision argument: "+_precision+" must be less than or equal to "+MAX_DECIMAL_PRECISION);}
			if(_scale < 1){throw new IllegalArgumentException("The scale argument must be greater than zero.");}
			if(_precision < 1){throw new IllegalArgumentException("The precision argument must be greater than zero.");}
			//if(_precision > scale-1){throw new IllegalArgumentException("The precision arugment:"+_precision+" must be less than the scale: "+_scale+".");}
			this.scale = _scale;
			this.precision = _precision;
			//for a value, it must be < (scale-precision)*10
		}


		@Override
		public String getFormatTemplate() {
			return "";
		}
		
		/**
		 * DOC
		 * @param low
		 * @param high
		 */
		public void setBounds(BigDecimal low, BigDecimal high){
			this.bdLowBound = low;
			this.bdHighBound = high;
		}



		@Override
		protected boolean preParseValidate(String _value, Messages messages) {
			boolean valid = true;
			if(_value==null){
				valid=false;
			}else {
				if(_value.contains(",")){
					try {
						DecimalFormat.getInstance().parse(_value).toString();
					} catch (ParseException e) {
						messages.add("The value:'"+_value+"' could not be parsed using standard format. e.g.: -9,999,999,999.999 ");
						valid = false;
					}					
				}else{
					try{
						switch(this.type){
							case DOUBLE: Double.parseDouble(_value);
							break;
							case DECIMAL: 
								int places = Decimal.Util.decimalPlaces(_value);
								int digits = Decimal.Util.totalDigits(_value);
								if(places>scale){
									valid=false;
									messages.add("The number of decimal places in the value '"+_value+"' exceeds the scale:"+scale);
								}
								if(digits>precision && preventShortening){
									valid=false;
									messages.add("The number of significant digits ("+digits+") in the value '"+_value+"' exceeds the total precision:"+precision);
								}
								new BigDecimal(_value);
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
					case DOUBLE: result = new Double(_value).toString();
					break;
					case DECIMAL:
						BigDecimal _bd = new BigDecimal(_value,this.mc);
						try{
							_bd.setScale(this.scale);
						}catch(ArithmeticException ae){
							throw new IllegalArgumentException("Setting the scale this low on '"+_value+"' required rounding.  "+ae.getMessage(),ae);
						}
						result  = _bd.toString();
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
				//messages.add(translation);
			}
			//if(type.equals(Type.DECIMAL) && !validateScale(parsed,messages)){
			//	valid = false;
			//}
			return valid;			
		}
		

		private boolean validateBounds(String parsed, Messages messages) {
			boolean valid = true;
			BigDecimal bdvalue = new BigDecimal(parsed,this.mc);//Note: it's not constructed yet.
			if(bdLowBound!=null && bdvalue.compareTo(bdLowBound)<0){
				messages.add("The value "+bdvalue+" cannot be lower than "+bdLowBound);
				valid = false;
			}
			if(highBound!=null && bdvalue.compareTo(bdHighBound)>0) {
				messages.add("The value "+bdvalue+" cannot be higher than "+bdHighBound);
				valid = false;
			}
			return valid;
		}


			

		@Override
		public int getSqlType() {
			switch(this.type){
				case DECIMAL: return java.sql.Types.DECIMAL;
				case DOUBLE: return java.sql.Types.DOUBLE;
				default: return 0;
			}
		}

		@Override
		public String toDDL() {
			StringBuilder sb = new StringBuilder();
			sb.append(this.name+" ");
			sb.append(this.type.name());
			if(type.equals(Type.DECIMAL)){
				sb.append("(");
				sb.append(this.getMathContext().getPrecision());
				sb.append(",");
				sb.append(this.getScale());
				sb.append(")");
			}
			sb.append(" ");
			sb.append((isNullable)?"":"NOT ");
			sb.append("NULL ");
			return sb.toString();
		}

		@Override
		protected Value factoryValue(String v) {
			return new Decimal(this,v);
		}
		
	
		
	}

	private Double doubleValue;
	private BigDecimal bdValue;
	
	/**
	 * DOC
	 * 
	 * @param _field
	 * @param _value
	 */
	protected Decimal(DecimalField _field, String _value) {
		super(_field, _value);
		this.doubleValue = Double.parseDouble(_value);
		if(_field.getType().equals(DecimalField.Type.DECIMAL)){
			MathContext mc = _field.mc;
			bdValue = new BigDecimal(_value, mc);//MathContext contains the precision and rounding mode.
			bdValue.setScale(_field.scale);
		}else{
			bdValue = new BigDecimal(_value);//see BigDecimal(String)
		}
	}	

	@Override
	public DecimalField getField(){
		if(this.field instanceof DecimalField){
			return (DecimalField)this.field;
		}else{
			throw new IllegalStateException("The field does not match the value.");
		}
	}
	
	@Override
	public String format() {
		String result = null;
		DecimalField field = getField();
		//DecimalField.Type t = field.getType();
		NumberFormat f = field.getFormatter();
		//if(t.equals(DecimalField.Type.DECIMAL)){
		result = f.format(doubleValue);
		return result;
	}
	
	/**
	 * DOC
	 * @param msgs
	 * @return
	 */
	public Double doubleValue(){
		return this.doubleValue;
	}	
	
	/**
	 * DOC
	 * @return
	 */
	public BigDecimal bigDecimalValue(){
		return this.bdValue;
	}
	
	@Override
	public int compareTo(Value anotherValue){
		BigDecimal _bd = this.bdValue;
		BigDecimal anotherDbl = ((Decimal)anotherValue).bdValue;
		return _bd.compareTo(anotherDbl);
	}	
		
	/**
	 * DOC
	 * @return
	 */
	public String formatPercent(){
		NumberFormat formatter = DecimalFormat.getPercentInstance();
		return formatter.format(doubleValue);
	}
	
	/**
	 * DOC
	 * Note that this is superior for comparison than equals which is too exacting.
	 * 
	 * @param aThat
	 * @return
	 */
	public boolean eq(Decimal aThat) {
		return compareAmount(aThat) == 0;
	}	
	
	/**
	 * DOC
	 * @param aThat
	 * @return
	 */
	public boolean gt(Decimal aThat) { 
	    return compareAmount(aThat) > 0;  
	}
	
	/**
	 * DOC
	 * @param aThat
	 * @return
	 */
	public boolean gteq(Decimal aThat) { 
		return compareAmount(aThat) >= 0;  
	}	
	
	/**
	 * DOC
	 * @param aThat
	 * @return
	 */
	public boolean lt(Decimal aThat) { 
		return compareAmount(aThat) < 0;  
	}	
	
	/**
	 * DOC
	 * @param aThat
	 * @return
	 */
	public boolean lteq(Decimal aThat) { 
		return compareAmount(aThat) <= 0;  
	}	
	
	/*
	 * DOC
	 */
	private int compareAmount(Decimal aThat){
		return this.bdValue.compareTo(aThat.bdValue);
	}

	
}

/*   these were to validate by attempting the class parsing method "value of"
* 
	if(type==java.sql.Types.DOUBLE){
		try{
			convert = Double.valueOf(value).toString();
			rv = true;
		}catch(NumberFormatException nfe){
			error = nfe.getMessage();
			err.println(error);
			rv = false;
		}
	}
	
public BigDecimal getValidValue(String value, Integer scale, Integer precision, Messages errors) {
	//boolean rv = false;
	BigDecimal convert = null;
	String error = null;
	if(value==null)throw new IllegalArgumentException("value cannot be null.");//TODO allow nulls?
	if(scale > msupportsScale || scale < 0-msupportsScale){
		throw new IllegalArgumentException("value exceeds supported scale");
	}
	if(precision > msupportsPrec || precision < 0-msupportsPrec){
		throw new IllegalArgumentException("value exceeds supported precision");
	}		
	
	if(type==java.sql.Types.DECIMAL){
		try{
			BigDecimal bd = new BigDecimal(value,new MathContext(precision));
			bd.setScale(scale);
			convert = bd;
			//rv = true;
		}catch(ArithmeticException ae){
			error = ae.getMessage();
			err.println(error);
			//rv = false;
		}catch(NumberFormatException nfe){
			error = nfe.getMessage();
			err.println(error);
			//rv = false;
		}
	}		
	if(error!=null){
		errors.add(error);
	}
	return convert;
}			
		
		
*/
//extends Number implements Comparable<Decimal>, Serializable
//Type                     Length                     Scale                            Precision                            Use Quotes
//DOUBLE   (java.sql.Types.DOUBLE,   Type.NONE,       Type.NONE,             Type.NONE,                 false),//Double     (no precision)
//DECIMAL  (java.sql.Types.DECIMAL,  Type.NONE,       Type.MAX_DECIMAL_SCALE,Type.MAX_DECIMAL_PRECISION,false),//BigDecimal  DECIMAL(10,2)                       !!

/*
	 * Notes on Decimal inserts:
		 * scale is chopped and forgiven by rounding
		 * precision is not forgiven, 
		 * you are allowed precision-scale digits before decimals, 
		 * iow, scale eats precision as it cannot do exponentials
		 * e.g.: DECIMAL(10,9) only allows a single digit like 1.99989 and not 10.99989
	*/ 			
//for a value, it must be < (scale-precision)*10



/*
	 * Notes on Decimal inserts:
 * scale is chopped and forgiven by rounding
 * precision is not forgiven, 
 * you are allowed precision-scale digits before decimals, 
 * iow, scale eats precision as it cannot do exponentials
 * e.g.: DECIMAL(10,9) only allows a single digit like 1.99989 and not 10.99989 
* 
* 
public BigDecimal getValidValue(String value, Integer scale, Integer precision, List<String> errors) {
	//boolean rv = false;
	BigDecimal convert = null;
	String error = null;
	if(value==null)throw new IllegalArgumentException("value cannot be null.");//TODO allow nulls?
	if(scale > msupportsScale || scale < 0-msupportsScale){
		throw new IllegalArgumentException("value exceeds supported scale");
	}
	if(precision > msupportsPrec || precision < 0-msupportsPrec){
		throw new IllegalArgumentException("value exceeds supported precision");
	}		
	
	if(type==java.sql.Types.DECIMAL){
		try{
			BigDecimal bd = new BigDecimal(value,new MathContext(precision));
			bd.setScale(scale);
			convert = bd;
			//rv = true;
		}catch(ArithmeticException ae){
			error = ae.getMessage();
			err.println(error);
			//rv = false;
		}catch(NumberFormatException nfe){
			error = nfe.getMessage();
			err.println(error);
			//rv = false;
		}
	}		
	if(error!=null){
		errors.add(error);
	}
	return convert;
} */
/*

* x Finish contract and unimplemented: toDDL, 
* x format as percent     
* x format as currency    (add 5 if padding spaces)
* x calculations for adding, subtracting, multiply, divide? 
* x ensure numeric sort
* x ensure matching
* x preParseValidate - Don't use the number format exception's message, it's not informative //Exception in thread "main" java.lang.NumberFormatException: For input string: "28929233343434343434343"
* x ensure we can parse numbers having commas
* na look at the low bound and high bound on the Field class
* x parse from formats with commas, etc.
* x Checkout web4j
*/ 
