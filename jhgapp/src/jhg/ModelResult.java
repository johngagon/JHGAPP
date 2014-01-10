package jhg;

import java.util.ArrayList;
import java.util.List;

import jhg.model.Model;

/**
 * DOC
 * 
 * @author John
 *
 */
public class ModelResult {

	public static enum Reason{NOT_SET,NOT_FOUND,FIELD_FAILURE,NOT_UNIQUE,STORE_FAILURE,SESSION,UNAUTHORIZED,NO_ACCESS,ERROR, NOT_RESERVED}
	
	private Model value;
	private List<Result> fieldResults;
	private boolean isSuccessful;
	private Reason reason;
	private Messages messages;
	private Exception ex;
	
	public ModelResult(){
		super();
		fieldResults = new ArrayList<Result>();
		isSuccessful = true;
		value = null;
		reason = Reason.NOT_SET;
		messages = new Messages();//Model specific results.
	}
	
	public void error(Exception e){
		this.isSuccessful=false;
		this.reason = Reason.ERROR;
		this.ex = e;
	}

	public boolean hasException(){
		return this.ex != null;
	}

	public void fail(Reason _reason, String message){
		this.isSuccessful=false;
		this.reason = _reason;
		this.messages.add(message);
	}
	
	public void addMessage(String message){
		this.messages.add(message);
	}

	public Messages getMessages(){
		return this.messages;
	}
	
	public Exception getException(){
		return this.ex;
	}
	
	public Reason getReason(){
		return this.reason;
	}
	
	public void addResult(Result r){
		fieldResults.add(r);
		isSuccessful = isSuccessful && r.isSuccessful();
		if(!isSuccessful){
			this.reason = Reason.FIELD_FAILURE;
		}
	}
	
	public boolean isSuccessful(){
		return isSuccessful;
	}
	
	public List<Result> getResults(){
		return fieldResults;
	}

	public void setModel(Model model) {
		debug(".addModel() Adding model.");
		this.value = model;
	}
	
	private void debug(String string) {
		boolean doDebug = false;
		if(doDebug){
			System.out.println("ModelResult"+string);
		}
	}

	public boolean hasModel(){
		return this.value !=null;
	}
	
	public Model getModel(){
		return this.value;//TODO make this less dangerous.
	}
}
