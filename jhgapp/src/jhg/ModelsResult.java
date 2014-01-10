package jhg;

import java.util.ArrayList;
import java.util.List;

//TODO models result vs page result? combine the two.
/**
 * DOC
 * 
 * @author John
 *
 */
public class ModelsResult {
	public static enum Reason{NOT_SET,NOT_FOUND,MODEL_FAILURE,SESSION,UNAUTHORIZED,ERROR, }
	
	
	private List<ModelResult> modelResults;
	private boolean isSuccessful;
	private Reason reason;
	private Messages messages;
	private Exception ex;
	//private String entity;//TODO impl
	
	public ModelsResult(){
		super();
		modelResults = new ArrayList<ModelResult>();
		isSuccessful = true;
		reason = Reason.NOT_SET;
		messages = new Messages();
	}
	

	public void fail(Reason _reason, Exception e){
		this.isSuccessful=false;
		this.reason = _reason;
		this.ex = e;
	}	
	
	public Exception getException(){
		return this.ex;
	}

	public void fail(Reason _reason, String msg){
		this.isSuccessful=false;
		this.reason = _reason;
		this.messages.add(msg);
	}

	public Reason getReason(){
		return this.reason;
	}
	
	public void addResult(ModelResult r){
		modelResults.add(r);
		isSuccessful = isSuccessful && r.getReason().equals(Result.Reason.SUCCESS);
		if(!isSuccessful){
			this.reason = Reason.MODEL_FAILURE;
		}
	}
	
	public boolean isSuccessful(){
		return isSuccessful;
	}
	
	public List<ModelResult> getResults(){
		return modelResults;
	}

	/*
	private void debug(String string) {
		boolean doDebug = false;
		if(doDebug){
			System.out.println("ModelsResult"+string);
		}
	}
	*/
	
	public void setModels(List<ModelResult> fillSelectAll) {
		this.isSuccessful = true;
		for(ModelResult mr:fillSelectAll){
			if(!mr.isSuccessful()){
				isSuccessful = false;
				break;
			}
		}
		this.modelResults = fillSelectAll;
	}
}
