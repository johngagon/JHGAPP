package jhg;

import java.util.ArrayList;
import java.util.List;

/**
 * This should usually only be instantiated from the Application. 
 * Enforce with better encapsulation.
 * 
 * DOC
 * 
 * @author John
 *
 */
public class Messages {
	
	private List<String> messages;
	
	
	public Messages(){
		messages = new ArrayList<String>();
	}
	
	public List<String> get(){
		return messages;
	}
	
	public void clear(){
		messages.clear();
	}
	
	public int size(){
		return messages.size();
	}
	
	public void add(String message){
		messages.add(message);
	}
	
	public void add(Messages _messages){
		messages.addAll(_messages.messages);
	}
	
	public boolean have(){
		return !messages.isEmpty();
	}
	
	public String htmlOut(){
		StringBuffer sb = new StringBuffer();
		for(String s:messages){
			sb.append(s+"<br/>\n");
		}
		return sb.toString();
	}
	
}
