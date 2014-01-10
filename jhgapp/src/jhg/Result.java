package jhg;

import java.util.*;

import jhg.model.Model;
import jhg.model.Value;
//Violates dependencies but convenient.

//TODO refactor the model result on this.
/**
 * DOC
 * 
 * @author John
 *
 */
public class Result extends Base {      //OBJ  EXC  MSG
	public static enum Reason{
	SUCCESS,			//Successfully completed, Object   Y    N    N
	INVALID_INPUT, 		//Invalid input, see messages      N    N    Y
	ERROR, 				//An exception was thrown          N    Y    N
	NO_RESULT,          //Input valid.  Codes.             N    Y?   Y                 
	NOT_EXECUTED,       //The initial state                N    N    N	
	SESSION,			//Session invalid
	NOACCESS,
	RESERVED,
	UNAUTHORIZED, 
	NOT_RESERVED, 
	NO_ACCESS;
	}
	//{NOT_SET,NOT_FOUND,FIELD_FAILURE,NOT_UNIQUE,STORE_FAILURE,ERROR}
	
	/* 
	 * If you want a "False" answer for a result, return a boolean instead of a result.
	 * 
	 * Invalid Input Messages: Use one message per reason for each input.
	 * 
	 * Good No Result Reasons:
	 * a. not found, missing, null/blank, nothing at index.
	 * b. can't insert, not unique.
	 * c. can't delete, dependent record
	 * d. can't update, locked
	 * e. incomplete processing/halted prematurely.
	 * f. not applicable
	 * g. illegal states, issues that are sometimes programmed as exceptions but manageable.
	 */
	
	private static boolean doRuntimeOnNull = false;//quietly give nulls.
	private Reason reason;
	private Object value;
	private Messages messages;
	private Exception ex;
	
	
	public Result(){
		reason = Reason.NOT_EXECUTED;
		messages = new Messages();
		
	}
	
	//TODO make a web result.(and a db result)
	
	public Result invalidInput(){
		reason = Reason.INVALID_INPUT;
		return this;
	}
	
	public Result invalidInput(String message){
		reason = Reason.INVALID_INPUT;
		messages.add(message);
		return this;
	}
	
	public Result error(Exception e){
		reason = Reason.ERROR;
		this.ex = e;
		messages.add(e.getMessage());
		return this;
	}
	
	public Result noResult(){
		reason = Reason.NO_RESULT;
		messages.add("No result.");
		return this;
	}
	
	public Result noResult(String message){
		reason = Reason.NO_RESULT;
		messages.add(message);
		return this;
	}

	public Result sessionInvalid(String message) {
		reason = Reason.SESSION;
		messages.add(message);
		return this;
	}	
	
	public Result notAuthorized(){
		reason = Reason.UNAUTHORIZED;
		messages.add("User is not logged in to perform this operation.");
		return this;
	}
	
	public Result notAuthorized(String operation){
		reason = Reason.UNAUTHORIZED;
		messages.add("Logged in user required to "+operation);
		return this;
	}
	
	public Result notAuthorized(User user, String operation){
		reason = Reason.UNAUTHORIZED;
		messages.add("User "+user+" is not authorized to "+operation);
		return this;
	}
	

	
	public Result noAccess(User user, Role role, Privilege p, Action a, Model m){
		reason = Reason.NOACCESS;
		messages.add("User "+user.getName()
				+" in role "+role.getName()
				+" with privilege "+p.name()
				+" does not have access to perform "+a.getCode().name()
				+" on "+m.getManager().getName()+"("+m.getId()+")"
				+" while in a(n) "+m.getState().name()+" state. ");
		return this;
	}	
	
	
	public void success(){
		reason = Reason.SUCCESS;
	}
	
	public boolean hasRecords(){
		boolean result = false;
		if(hasValue()){
			try{
				Integer count = (Integer)objectValue();
				if(count>0){
					result = true;
				}
			}catch(ClassCastException cce){
				result = false;
				log("Programmer error in Result{}.hasRecords(). ClassCastException:"+cce.getMessage());
				//cce.printStackTrace();
			}
		}else{
			log("Programmer error in Result{}.hasRecords(). No count object value in result.");
		}
		return result;
	}
	
	public String name(){
		return reason.name();
	}
	
	public Reason getReason(){
		return this.reason;
	}
	
	public boolean hasValue(){
		return value!=null;
	}
	
	public boolean hasException(){
		return ex!=null;
	}
	
	public boolean hasMessages(){
		return messages.have();
	}
	
	public boolean isSuccessful(){
		return Reason.SUCCESS.equals(this.reason);
	}
	
	public boolean notSuccessful(){
		return (!isSuccessful());
	}
	
	public boolean notInvalid() {
		return (!Reason.INVALID_INPUT.equals(this.reason));
	}
	
	public Messages getMessages(){
		return this.messages;
	}
	
	public void addMessage(String message){
		messages.add(message);
	}
	
	public void setMessage(String message) {
		messages.clear();
		messages.add(message);
	}	
	
	public void addMessages(Messages _messages){
		for(String s:_messages.get()){
			messages.add(s);
		}
	}
	
	public Object objectValue(Object... v){
		if(v.length>0 && v[0]!=null){    //set
			this.value = v[0];
			return null;
		}else{                           //get
			if(doRuntimeOnNull && value==null)throw new IllegalStateException();
			return value;
		}
	}
	
	public Value value(Value... v){
		//NOTE: will throw exception on subtypes.
		//if(!(value instanceof Value)){  
		//	throw new IllegalStateException("Programmatic use error: don't call this if you didn't pass in a Value");
		//}
		if(v.length>0 && v[0]!=null){    //set
			this.value = v[0];
			return null;
		}else{                           //get
			if(doRuntimeOnNull && value==null)throw new IllegalStateException();
			return (Value)value;
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
	
	public Result error(String message) {
		reason = Reason.INVALID_INPUT;
		messages.add(message);
		return this;	
	}
	
	public Result fail(Reason r, String message){
		reason = r;
		messages.add(message);
		return this;
	}

	public String allMessages() {
		StringBuffer sb = new StringBuffer();
		List<String> messageStrings = messages.get();
		for(String ss:messageStrings){
			sb.append(ss+";  ");
		}
		return sb.toString();
	}

	public Result reserved(Model model) {
		reason = Reason.RESERVED;
		messages.add("This model "+model.getDebug()+" is reserved.");//TODO spread this practice.
		return this;	
	}

	
}
/*
	private static class Test {
		 public static void main(String[] args){
			 Test a = new Test();
			 Result r = a.doSomething("a", "b");
			 System.out.println("Result:"+r);
		 }
		 
		 private Test(){		 
		 }
		 
		 
		  * Use this method "pattern" for executing a main API method or just
		  * under the main API method to avoid exposing Result so API users can
		  * use simple jdk or API only types.
		  * 
		  * @param inputA
		  * @param inputB
		  * @return
		  
		 public Result doSomething(String inputA, String inputB){
			boolean testIOException = false;
			Result result = Result.NOT_EXECUTED;
			try{
				if(inputA==null){
					result = Result.INVALID_INPUT;
					result.addMessage("inputA was null:"+inputA);
				}
				if(inputB==null){
					result = Result.INVALID_INPUT;
					result.addMessage("inputB was null:"+inputB);
				}
				if(result.notInvalid()){
					boolean badResult = false;
					//complex stuff here. if good
					if(badResult){
						result = Result.NO_RESULT;
						result.addMessage("Result bad.");
					}else{
						result = Result.SUCCESS;
						result.objectValue("Yay!");
					}
				}
				if(testIOException){
					throw new IOException("blah");
				}
			}catch(IOException ioe){
				result = Result.ERROR;
				result.exception(ioe);
			}catch(Exception e){
				result = Result.ERROR;
				result.exception(e);				
			}
			return result;
		 }
	}

*/