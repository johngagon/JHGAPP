package test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jhg.Messages;

@SuppressWarnings("unused")
public class Tester {

	public static void main(String[] args){
		try{
			throw null;
		}catch(Throwable t){
			t.printStackTrace();
		}
		/*
		try {
			testDateParse();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		*/
	}
	
	private static void testDateParse() throws ParseException{
		final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT);
		Date d = sdf.parse("2013-07-27 05:26:33.001-0400");
		p("Date:'"+sdf.format(d)+"'");
	}
	
	
	private static void testBigDecimal2(){
		String decimalStr = "1.111";
		String decimalStrNoDecimal = "11111";
		int decimalPlaces = 0;
		if(decimalStr.indexOf(".")!=-1){
			decimalPlaces = (decimalStr.length()-1)-decimalStr.indexOf(".");
			p("dl:"+decimalStr.length());
			p("di:"+decimalStr.indexOf("."));
			p("dp:"+decimalPlaces);
		}else{
			
		}
		int count = 0;
		for (int i = 0, len = decimalStr.length(); i < len; i++) {
		    if (Character.isDigit(decimalStr.charAt(i))) {
		        count++;
		    }
		}		
		p("count:"+count);
		/*
		 * a. determine that the scale is >= portion after the decimal.
		 * b. determine that the total number digits is >= precision.
		 */
	}
	
	private static void testBigDecimal(){
		String bdStr = "1.11";//8 precision 2 over decimal
		//for a value, it must be < (scale-precision)*10
		int precision = 3;                       //sigfigs > precision - scale
		int scale = 1 ;      //increasing scale from 1 to 2 helped
		                    //decreasing precision from 4 to 3 did nothing bad
		//scale must be > the double.
		
		//scale: number of fraction places., the number cannot have more decimal places than scale
		//precision: number of sig figs reported.
		//    P S
		//1,0,1,1  1,1,1,1  1,2,1,1   1,3,1,1
		//1,0,2,1  1,1,2,1  x 
		//1,0,3,1  1,1,3,1  X                  <-is this because precision = to sf?
		//1,0,1,2  1,1,1,2  x
		//1,0,1,3  1,1,1,3  x
		//1,0,2,2  1,1,2,2  x
		//1,0,3,2  1,1,3,2  x                  <-note the scale helped.
		//1,0,2,3  1,1,2,3  x
		//1,0,3,3  1,1,3,3  x
		
		//2,1,3,1           x
		//2,2,3,1           x
		
		//when precision is too high, the number has too few figures or not enough, 
		//the scale is less than one side or the other.
		//if the digits <=precision
		
		//when the precision is the same as the digits
		//and the precision minus scale >= decimal places
		
		
		//2,1 2,2 2,3    
		//3,1 3,2 3,3  
		//0,1 0,2 0,3
		
		Double d = Double.parseDouble(bdStr);
		boolean ok = d <= Math.pow(10,precision-scale);
		MathContext mc = new MathContext(precision,RoundingMode.HALF_EVEN);
		
		//precision = sig fig
		BigDecimal bd = new BigDecimal(bdStr,mc);
		bd.setScale(scale);// if scale is smaller than precision,no problem.
		p("BD:"+bd);
		p("ok:"+ok);
		//precision 1, scale 3 no problem.  (-2)     99.999999
		//precision 8, scale 1 rounding issue. (7)
		//precision 2, scale 1 ok          (1)
		//precision 3, scale 1 no problem  (2)
		//precision 3, scale 4 no problem  (-1)
		//precision 4, scale 4 no problem  (0)
		//precision 5, scale 4 no problem   (1)
		//precision 8, scale 4, rounding issue. (4)
		//precision 7, scale 4, rounding issue. (3)
		//precision 6, scale 4, no problem  (2)
		//precision 6, scale 100, no problem    (-96)
		//precision 6, scale 1, rounding issue.      (5)
		//precision 6, scale 2, rounding issue.      (4)
		//precision 6, scale 3, rounding issue.      (3)
		//precision 7, scale 5, no problem.     (2)
		//precision 4, scale 1, rounding             (3)
		//precision 4, scale 2, no problem
		
		//precision high, scale low, number digits close to precision.
		
		//the scale should always be greater than number of decimals.
		//ideally, the precision should be greater than the length to avoid E
	}
	
	private static void testNumberParse2(){
		String aLong = "-34,343,434,343.76654";
		NumberFormat nf1 = NumberFormat.getIntegerInstance();
		String parsed1="";
		try {
			parsed1 = nf1.parse(aLong).toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p("Parsed std:"+parsed1);
	}
	
	private static void testDecimalFormat(){
		String aDouble = "-34343434.343";
		String doubleStr = new Double(aDouble).toString();
		Double doubleVal = Double.parseDouble(doubleStr);
		
		NumberFormat nf = DecimalFormat.getInstance();
		String printDbl = nf.format(doubleVal);
		
//		NumberFormat nf1 = new DecimalFormat("");
//		String printDecimalized = nf1.format(doubleVal);
		
		NumberFormat nf2 = DecimalFormat.getCurrencyInstance();
		String printCurrency = nf2.format(doubleVal);
		NumberFormat nf3 = DecimalFormat.getPercentInstance();
		String printPercent = nf3.format(doubleVal);
		NumberFormat nf4 = DecimalFormat.getIntegerInstance();
		String printInteger = nf4.format(doubleVal);
		NumberFormat nf5 = DecimalFormat.getNumberInstance();
		String printNumber = nf5.format(doubleVal);
		
		
		
		//int width = 15;
		//String leadingZeros = String.format("%0"+width+"d", doubleVal);
		
		
		p("DBL FORMATTED:"+printDbl);//default
		//p("DBL 1FORMATTED:"+printDecimalized);
		p("DBL 2FORMATTED:"+printCurrency);
		p("DBL 3FORMATTED:"+printPercent);
		p("DBL 4FORMATTED:"+printInteger);
		p("DBL 5FORMATTED:"+printNumber);
		//p("DBL 6FORMATTED:"+leadingZeros);		
	}
	
	private static void testNumberFormat(){
		String aLong = "-34343434343";
		String longStr = new Long(aLong).toString();
		Long longVal = Long.parseLong(longStr);
		NumberFormat nf = NumberFormat.getInstance();
		String printLong = nf.format(longVal);
		NumberFormat nf1 = new DecimalFormat("");
		String printDecimalized = nf1.format(longVal);
		
		NumberFormat nf2 = NumberFormat.getCurrencyInstance();
		String printCurrency = nf2.format(longVal);
		NumberFormat nf3 = NumberFormat.getPercentInstance();
		String printPercent = nf3.format(longVal);
		NumberFormat nf4 = NumberFormat.getIntegerInstance();
		String printInteger = nf4.format(longVal);
		NumberFormat nf5 = NumberFormat.getNumberInstance();
		String printNumber = nf5.format(longVal);
		
		int width = 15;
		String leadingZeros = String.format("%0"+width+"d", longVal);
		
		
		p("LONG FORMATTED:"+printLong);//default
		p("LONG 1FORMATTED:"+printDecimalized);
		p("LONG 2FORMATTED:"+printCurrency);
		p("LONG 3FORMATTED:"+printPercent);
		p("LONG 4FORMATTED:"+printInteger);
		p("LONG 5FORMATTED:"+printNumber);
		p("LONG 6FORMATTED:"+leadingZeros);
		
	}
	
	private static void testNumberParse(){
		String regex = "^[+-]\\d+$";
		/*
		 * Valid Cases: 
		 * 1. valid number without sign, assumed positive
		 * 2. valid number with negative sign assumed negative
		 * 3. valid number with positive sign assumed positive
		 * 4. decimal forgiving: decimal chopped/rounded. unforgiving, decimal not allowed
		 * 5. trailing L for long type.  TODO case for bytes.
		 * 
		 * if the type is byte, do an attempted parse etc.
		 * 
		 * Blatantly invalid:
		 * 6. blatantly invalid. includes space,
		 * 7. 
		 */
		String v1 = "a+";//valid
		String v2 = "abcd ABCDEF";//too long;
		String v3 = "aB ";//too short;
		String v4 = "a4 BC";//has number
		String v5 = "Abcd Ab Z";//9 still valid.
		Pattern pattern = Pattern.compile(regex);
		String[] values = {v1,v2,v3,v4,v5};
		
		p("Matching validity to "+regex);
		for(String v:values){
			Matcher m = pattern.matcher(v);
			p(v+":"+m.matches());
			
		}	
	}
	
	private static void testNullValueMap(){
		p("start");
		Map<String,String> map = new HashMap<String,String>();
		map.put("a",null);
		p("end");
	}
	private static void testAIOOBE1(){
		String x = "abcdefg";
		char[] y = x.toCharArray();
		Messages m = new Messages();
		for(int i=0;i<=x.length();i++){//intentional error
			char z = ' ';
			System.out.print(""+i);
			if(safeGet(y,i,m,z)){
				p(String.valueOf(y[i]));
			}
			
			//p(String.valueOf(y[i]));
		}
	}

	private static void testAIOOBE2(){
		String[] y = {"apple","birch","canary"};
		Messages m = new Messages();
		for(int i=0;i<=y.length;i++){//intentional error
			String z = "";
			System.out.print(""+i);
			if(safeGet(y,i,m,z)){        //conditional use if the index is correct.
				p(String.valueOf(y[i]));
			}
			//p(String.valueOf(y[i]));
		}
	}
	
	
	public static boolean safeGet(char[] y, int i, Messages msgs, char at) {
		if(y==null){
			msgs.add("Array error: array is null.");
			return false;
		}
		int length = y.length;
		if(i<0){
			msgs.add("Array Index Error: array-length="+length+", index="+i);
			return false;
		}
		if(i>length-1){
			msgs.add("Array Index Error: array-length="+length+", index="+i);
			return false;
		}
		at = y[i];
		return true;
	}	
	
	public static <T> boolean safeGet(T[] y, int i, Messages msgs, T at) {
		if(y==null){
			msgs.add("Array error: array is null.");
			return false;
		}
		int length = y.length;
		if(i<0){
			msgs.add("Array Index Error: array-length="+length+", index="+i);
			return false;
		}
		if(i>length-1){
			msgs.add("Array Index Error: array-length="+length+", index="+i);
			return false;
		}
		at = y[i];
		return true;
	}
	
	private static void testCompare(){
		String melon = "melon";
		String pear =  "pear";
		p(""+melon.compareTo(pear));
		p(""+melon.compareTo(melon));
		p(""+pear.compareTo(melon));
		p(""+pear.compareTo(pear));
	}
		
	
	private static void testSort(){
		List<String> strList = new ArrayList<String>();
		strList.add("orange");
		strList.add("Orange");
		strList.add("Pear");
		strList.add("Peaches");
		strList.add("peach");
		strList.add("pEach");
		strList.add("Box Car");
		strList.add("Box");
		strList.add("Box ");
		strList.add("apple");
		strList.add("Apple");
		strList.add(" ");
		Collections.sort(strList);
		for(String s:strList){
			p("'"+s+"'");
		}
	}
	
	private static void testList(){
		List<String> strList = new Vector<String>();
		for(int i=0;i<=6;i++){
			strList.add(i,null);
		}
		strList.set(5, "Hello");
		strList.set(1, "World");
		p(strList.get(5));
		p(strList.get(1));
		//for(String s:strList){
		//	p(s);
		//}
	}
	
	private static void testRegex(){
		String regex = "^[a-zA-Z ]{4,10}$";
		String v1 = "abcd ABCDE";//valid
		String v2 = "abcd ABCDEF";//too long;
		String v3 = "aB ";//too short;
		String v4 = "a4 BC";//has number
		String v5 = "Abcd Ab Z";//9 still valid.
		Pattern pattern = Pattern.compile(regex);
		String[] values = {v1,v2,v3,v4,v5};
		
		p("Matching validity to "+regex);
		for(String v:values){
			Matcher m = pattern.matcher(v);
			p(v+":"+m.matches());
			
		}		
	}
	
	private static void p(String x){
		System.out.println(x);
	}
}
