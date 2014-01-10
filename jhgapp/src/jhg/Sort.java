package jhg;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DOC
 * 
 * @author John
 *
 */
public class Sort {

	public enum Direction{ASC,DESC}
	
	private Map<String,Direction> sortBy;
	private boolean validated;
	
	public Sort(){
		super();
		sortBy = new LinkedHashMap<String,Direction>();
		validated = false;
	}
	
	public void add(String fieldName, Direction d){
		sortBy.put(fieldName, d);
	}
	
	public Map<String,Direction> getSortParamters(){
		return sortBy;
	}
	
	public void setValidationResult(boolean flag){//used for the validation of fieldnames
		this.validated = flag;
	}
	
	public boolean isValid(){
		return this.validated;
	}
	
	public String toSql(){//a default.
		if(!isValid())throw new IllegalStateException("PROGRAMMER ERROR: Not valid, do not get sql without checking first.");
		StringBuffer sb = new StringBuffer();
		sb.append("ORDER BY ");
		int size = sortBy.size();
		int idx = 0;
		for(String _fieldKey:sortBy.keySet()){
			idx++;
			Direction _dir = sortBy.get(_fieldKey);
			sb.append(_fieldKey+" "+_dir.name());
			if(idx<size){
				sb.append(", ");
			}
		}
		return sb.toString();
	}
	
}
