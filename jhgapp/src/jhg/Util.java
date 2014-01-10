package jhg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	
	//FIXME determine need or move to Base.
	
	/*
	 * questions before adding to this.
	 * Is it a java deficiency? (nulls/exceptions/arrays)
	 * Is it common to a particular area of modelling?
	 * Is it common to a particular type?
	 */
	
	/**
	 * DOC 
	 * 
	 * @param e
	 * @return
	 */
	public static String toString(Exception e) {  
	    StringWriter s = new StringWriter();  
	    e.printStackTrace(new PrintWriter(s));  
	    return s.toString();   
	} 	
	
	public static void main(String[] args){
		testDateTimeConvert();
	}
	private static void testDateTimeConvert(){
/*
					 *  2013-11-08T19:09
					 *  VS my format used in testing: 2013-07-27 05:26:33.001-0400			
 */
		String input = "2013-11-08T19:09";
		String currentFormat = "yyy-MM-dd'T'HH:mm";
		String desiredOutput = "2013-11-08 19:09:00.000-0500";
		String desiredFormat = "yyyy-MM-dd HH:mm:ss.SSSZ";
		String desiredValue = convert(currentFormat,desiredFormat,input);
		
		System.out.println("DesiredOutput:"+desiredOutput);
		System.out.println("DesiredValue :"+desiredValue);
		System.out.println(desiredOutput.equals(desiredValue));
	}
	
	private static String convert(String currentFormat,String desiredFormat, String currentValue){
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
	
}
