package jhg.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import jhg.ApplicationException;
import jhg.ModelResult;
import jhg.ModelResult.Reason;
import jhg.ModelsResult;


/**
 * DOC
 * @author John
 *
 */
public abstract class Manager<T extends Model> {

	private Integer managerId;
	private Integer nextId;
	protected Application application;
	protected String name;
	protected List<Field> fields;
	protected Map<String,List<Field>> unique; 
	protected Map<Integer,T> models;
	protected boolean isVersioned;
	protected boolean isPermanentCached;
	protected String label;
	public static final String AUX_MANAGER = "aux_manager";
	
	
	/**
	 * DOC
	 * 
	 * @param _entityname
	 */
	public Manager(Application app, String _entityname){
		//TODO entity names must be validated.  alpha lc with underscore only. maxlen 50
		super();
		this.application = app;
		this.name = _entityname;
		fields = new ArrayList<Field>();//TODO consider linked hash set / Collection
		models = new HashMap<Integer,T>();
		//if the model at an index of the list is null, it is offline.
		unique = new HashMap<String,List<Field>>();
		//allModelsUncached = false;
		//NEVER call init from here.
		//if there's a persistence store, find out the next id available.
		nextId = 1;
		label = "";
		isVersioned = false;
		isPermanentCached = true;
		managerId = 0;
	}
	

	/**
	 * DOC
	 * @param rows
	 * @return
	 * @throws ApplicationException 
	 */
	public abstract ModelsResult performImport(List<String> rows) throws ApplicationException;
	
	/** 
	 * DOC
	 * 
	 * @return
	 */
	public Integer getManagerId(){
		return this.managerId;
	}
	
	/**
	 * This method handles the manager dependencies after instantiation.
	 * This method should be implemented final. It is called by the application init.
	 * DOC
	 */
	public abstract void initManager();
	
	/**
	 * DOC
	 * @return
	 */
	public abstract Model makeModel();
	
	/**
	 * DOC
	 */
	public abstract void initDependent();

	/**
	 * DOC
	 * 
	 * @param m
	 * @return
	 */
	public synchronized Integer add(T m){
		Integer next = new Integer(nextId);//newly allocated
		nextId++;
		m.setId(next);
		models.put(next,m);
		return next;
		//depends on persistence, otherwise, incremented.
	}	
	
	/**
	 * DOC
	 * @param m
	 * @return
	 */
	public synchronized void addModel(T m){
		Integer modelId = m.getId();
		models.put(modelId,m);
	}
	
	
	/**
	 * DOC
	 * 
	 * @param m
	 */
	public synchronized void load(T m){
		models.put(m.getId(),m);
	}
	
	/**
	 * DOC
	 * 
	 * @param _label
	 */
	public void setLabel(String _label){
		this.label = _label;
	}	
	
	/**
	 * DOC
	 * 
	 * @param indexName
	 * @param _list
	 */
	public void setUnique(String indexName,Field[] _list){
		unique.put(indexName,Arrays.asList(_list));
	}
	
	/**
	 * DOC
	 * Used only with create.
	 * 
	 * @param _modelToCheck
	 * @param mr
	 */
	public void verifyUnique(T _modelToCheck, ModelResult mr){
		for(String _name:unique.keySet()){
			List<Field> index = unique.get(_name);
			for(Integer id:models.keySet()){
				T _model = models.get(id);
				boolean sameModel = _modelToCheck.getId()!=null && _modelToCheck.getId().equals(_model.getId());
				if(_modelToCheck.matches(index,_model) && (!sameModel)){
					mr.fail(Reason.NOT_UNIQUE, "Match of model "+_modelToCheck+" with "+_model+" using index:"+_name+".");
					break;
				}
			}
			if(!mr.isSuccessful()){
				break;
			}
		}
	}

	/**
	 * DOC
	 * 
	 * @param indexName
	 * @param terms
	 * @return
	 */
	public ModelResult lookup(String indexName,String[] terms) {
		ModelResult mr = new ModelResult();
		List<Field> index = unique.get(indexName);
		if(index.size() != terms.length){
			throw new IllegalArgumentException("The size of the index found by "+indexName+" should match the size of the array term's length.");
		}		
		for(Integer id:models.keySet()){
			T _model = models.get(id);	
			//for(Field _field:index){
			if(_model.matches(index,terms)){
				mr.setModel(_model);
				return mr;
			}
			//}
		}
		mr.fail(Reason.NOT_FOUND, "Manager lookup failed to find results.");
		return mr;
	}	
	
	/**
	 * DOC
	 * @return
	 */
	public Map<Integer,T> getModels(){
		return this.models;
	}
	
	/**
	 * DOC
	 * UNSAFE
	 * @param _key
	 * @return
	 */
	public T getModel(Integer _key){
		//TODO determine if checking for the existence of the key is required first or if this will always be valid by nature.
		//NOTE may return null.
		return this.models.get(_key);
	}
	
	/**
	 * DOC
	 * @return
	 */
	public List<Field> getFields(){
		return fields;
	}

	/**
	 * Dangerous method (returns null) that gets the field by name.
	 * @param string
	 * @return a field if found.
	 */
	public Field getField(String _name) {
		Field result = null;
		debug("Manager.getField():Number of fields:"+fields.size());
		for(Field f:fields){
			
			if(f.getName().equals(_name)){
				result = f;
				break;
			}
		}
		return result;
	}
	
	/**
	 * DOC
	 * 
	 * @param _name
	 * @return
	 */
	public List<Field> getIndex(String _name){
		return unique.get(_name);
	}	
	
	/**
	 * DOC
	 * 
	 * @return
	 */
	public String getLabel(){
		return this.label;
	}

	/**
	 * DOC
	 * @return
	 */
	public String getName(){
		return this.name;
	}	
	
	/**
	 * DOC
	 * @return
	 */
	public boolean isVersioned(){
		return this.isVersioned;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Manager other = (Manager) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	/*
	 * 
	 */
	private static void debug(String s){
		boolean doDebug = false;
		if(doDebug){
			System.out.println(s);
		}
	}



	/**
	 * TODO doc
	 * @param managerId
	 */
	public void setManagerId(Integer _managerId) {
		this.managerId = _managerId;
	}

	public int countModels() {
		return this.models.size();
	}

	public void removeModel(Model model) {
		models.remove(model.getId());//TODO catch potential null on this? should never happen.
	}	
	
	public String toCsvHeader(){
		StringBuilder sb = new StringBuilder();
		//sb.append("John App version x, Model version y \n");
		sb.append(getName()+":"+new Date()+"\n");
		boolean first = true;
		for(Field f: fields){
			if(!first){
				sb.append(",");//TODO custom delimiters
			}else{
				first = false;
			}
			sb.append(f.name);
		}
		sb.append("\n");//TODO extract constant, make all come from Base.
		
		return sb.toString();		
	}

	/**
	 * DOC
	 * @return
	 */
	public String getManagerIdString() {
		return String.valueOf(getManagerId());
	}	

}
//TODO long support
/*
public Integer sumInteger(Field f, Messages msgs){
	//TODO validate field type compatible
	Integer sum = 0;
	for(Model m:models){
		Integer intval = m.getValue(f).toInteger(msgs);
		if(msgs.have()){
			break;
		}else{
			sum +=intval;
		}
	}
	return sum;
}

public Double sumDouble(Field f, Messages msgs){
	//TODO validate field type compatible
	Double sum = 0.0;
	for(Model m:models){
		Double dblval = m.getValue(f).toDouble();
		if(msgs.have()){
			break;
		}else{			
			sum +=dblval;
		}
	}
	return sum;
}

public Double averageInteger(Field f, Messages msgs){
	Double ave = 0.0;
	Integer sum = sumInteger(f,msgs);
	int count = models.size();
	if(!msgs.have()){
		ave = (double)(sum/count);
	}
	return ave;
}
*/




/*
Visiting database pattern. Lazy thread:
  cycles the managers for rows that need insert/update/deleting.
  confirms the save and updates the flag if saved and if not
  alerts the user. can create a bulk insert/prioritization.
  manager alone responsible for clearing the record if it 
  should remain uncached.
  Another thread is a fetching thread which takes place after
  inserts on indexed views if possible. Models that are not cached
  will get requested and this updates their status as being in 
  cache.
  
  creates log records
  
*/

/*

* Validate (regex, range), message, per field, javascript & ajax generator
* Format out 
* Format in (gen javascript)
* Autocomplete AJAX/Uniqueness check
* Validate AJAX
* Calculation formula (or just use pre and post set logic), Map entries
* navigate relationships to other fields
* Aggregate total calculation (to a parent record)
* Summary/grouping stats table
* Field groupings
* Value object can do conversions to other data types as needed with convert methods.



See Deploy Tool for examples of the robustness.
See Java Practices 
See Hsql
See rules engines
See a project which does where/order/etc

 */
/* 
 * the uniqueness validation won't happen except when an entire record is saved
 * as uniqueness may involve more than one field.  at that time, all unique 
 * constraints are checked.  At the time a field is saved onto a partial record,
 * the record is saved in memory only. This is a planned failure of the system for now.
 * TODO address the partial save failure of records.
 * 
 * when we make a model, the validation is delegated to by this base class
 * and we have to map to it to get it to be generic.  The generic setter is public
 * but the individual setters are private
 * 
 * 
 * functions:
 * 
 * Offline.
 * inject persistence.
 * online a record by id if offline.
 * "is in sync"
 * "is saved"
 * consider r/w and transactions
 * "lock" by user, session unlock.
 * 
 * define Model
 * 
 * count of models.
 * get all models with all fields.
 * get all models but only load certain fields.
 * (if matching, match in memory)filter that list by 
 *   a field matching criteria.
 * do summaries, summaries by repeated value (group)
 * sort
 * look up using id and business key, autoseek
 * view a single model, a page of models, a set
 * check out a model for edit
 * update and check in the model.
 * delete a model.
 * create a new model.
 * enter a new or update on a field in a model and validate
 * and provide any messages while updating.
 * see if a model's field is unique.
 * 
 * LATER
 * set these models to persist and unload after change or read
 * set models to only partially unload.
 * set up a search and have which fields will be seen
 * 
 * do calculated fields
 * do validation
 * 
 * last save time./read time. (make the database 
 * save async?)
 * 
 * 
 *   
 * 
 */

/* (formerly Lookup.class)
 * This holds a set of fields used for identifying a model. It can be a two part code
 * (like 1-Mr. 2-Mrs. 3-Ms. 4-Dr.) or have multiple fields (like city,state,country)
 *  
 * It can have new members or have a fixed size of members. 
 * 
 * It can be in memory or in database (if in memory, declare as part of application). 
 * i.e.:  Map<name,Map<id,List<String>>> application.getCodeTables();
 *    genders,
 *    cities,
 *    genres,
 *  
 * This models one of these.  It could also be the index of existing fields for a Model
 * for use as a foreign reference in another and for grouping.
 */


