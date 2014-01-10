package jhg;

import java.util.*;

/**
 * DOC
 * 
 * @author John
 *
 */
@SuppressWarnings("rawtypes")
public abstract class Base {

	public void noNullParams(String method, Object... args){
		int i=0;
		for(Object o:args){
			if(o==null)throw new IllegalArgumentException("Argument["+i+"] on "+method+" was null.");
			i++;
		}
	}
	public void noNullReturns(String method, Object returnObj){
		if(returnObj==null){
			throw new IllegalArgumentException("Return value on "+method+" was null.");
		}
	}
	
	public void minLength(String method, int requiredSize, Collection c){
		if(c.size()<requiredSize)throw new IllegalArgumentException("Collection checked from method "+method+" requires a size of at least:"+requiredSize);		
	}
	public void minLength(String method, int requiredSize, Map m){
		if(m.size()<requiredSize)throw new IllegalArgumentException("Map checked from method "+method+" requires a size of at least:"+requiredSize);
	}
	public void minLength(String method, int requiredSize, Object[] arr){
		if(arr.length<requiredSize)throw new IllegalArgumentException("Array checked from method "+method+" requires a size of at least:"+requiredSize);
	}
	public void minLength(String method, int requiredSize, String s){
		if(s.length()<requiredSize)throw new IllegalArgumentException("String checked from method "+method+" requires a size of at least:"+requiredSize);
	}

	public void verifyInteger(String method, String parse){
		try{
			Integer.parseInt(parse);
		}catch(NumberFormatException nfe){
			throw new IllegalArgumentException("Integer string checked from method "+method+" was not a readable or parsable number.");
		}
	}
	
	public void notNull(String method, String varName, Object o) throws IllegalStateException {
		if(o==null)throw new IllegalStateException("Object "+varName+" checked from method "+method+" was null");
	}	

	
	public void checkSuccess(String method, ModelsResult r, String msg) throws ApplicationException{
		if(!r.isSuccessful()){
			throw new ApplicationException("ModelsResult checked from method "+method+" was not successful:"+msg);
		}		
	}	
	public void checkSuccess(String method, ModelResult mr, String msg) throws ApplicationException{
		if(!mr.isSuccessful()){
			log("ModelResult:"+mr.getReason().name());
			throw new ApplicationException("ModelResult checked from method "+method+" was not successful:"+msg);
			
		}		
	}
	public void checkSuccess(String method, Result r, String msg) throws ApplicationException{
		if(r.notSuccessful()){
			throw new ApplicationException("Result checked from method "+method+" was not successful:"+msg);
		}		
	}
	
	
	public void checkNull(String method, String varName, Object o) throws ApplicationException {
		if(o==null){
			throw new ApplicationException("Object "+varName+" checked from method "+method+" was null");
		}
	}	
	
	public static final String ID = "ID";
	
	public static boolean isNull(Object o){
		return o==null;
	}
	public static boolean isNotNull(Object o){
		return o!=null;
	}

	public static final String EMPTY = "";
	public static final String ALL = "*";
	public static final int    ZERO = 0;
	
	protected void log(String msg){
		System.out.println(msg);
	}
	
}
