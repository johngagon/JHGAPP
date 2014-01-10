package jhg.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import jhg.ApplicationException;
import jhg.Messages;
import jhg.Result;
import jhg.State;
import jhg.model.field.Reference.ReferenceField;

/**
 * 
 * @author John
 * TODO setStorageFormat (if needed).
 * 
 * TODO formatter
 * 
 * if we decide to make a unique index, it can be done at the database level.
 * the cache is all or nothing except one possible "business key" that is a kind 
 * of public autogenerated string.
 * 
 * Arbitrary Business Key Concepts: 
 *   a) it has to help the user identify it without a number.
 *   b) it has to remain unique.
 *   c) it should represent all the data. city, state, country 
 *   d) it should represent data in grouped orders. CountryCode + State Code + City + number 
 *   e) the alphabetical parts should be unique so you don't need a number.
 * 
 * Static business data.
 *   geocoder. 
 *   1. Location data for the US/world. (google has a 2500 query limit)  
 *   2. timezone safe times.
 *  
 *  Find a way to configure applications to make use of other web services.
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class Model {

	/**
	 * DOC
	 * @param original
	 * @return
	 */
	public static Map<Field,Value> deepCopy(Map<Field,Value> original) {
	    Map<Field,Value> copy = new HashMap<Field,Value>(original.size());
	    for(Map.Entry<Field,Value> entry : original.entrySet()) {
	        copy.put(entry.getKey(), new Value(entry.getValue()));
	    }
	    return copy;
	}
	
	public static final long USE_TIMEOUT = 1000*60*30;//30 minutes
	
	@SuppressWarnings("unused")
	private static final String ALPHABETIC = "^[a-zA-Z]*$";
	@SuppressWarnings("unused")
	private static final String ALPHAWHITESPACE = "^[a-zA-Z0-9 ]*$";
	
	protected Map<Field,Value> fieldvalues;
	//protected Result initResult; used for creation most likely. TODO check if still needed.

	protected Map<Field,Value> updateFieldValues;
	protected Map<Field,Value> originalFieldValues;
	
	private Manager manager;
	
	
	private Integer ownerId;
	private Integer approverId;
	
	private Integer inUseById;
	private Date inUseSinceDate;
	private boolean locked;
	
	private int id;
	private int versionId;
	private State state;
	private ModelHistory modelHistory;
	public static final String AUX_MODEL = "aux_model";
	

	
	/**
	 * DOC
	 * @param _manager
	 */
	@SuppressWarnings({ "unchecked" })
	public Model(Manager _manager)
	{
		if(_manager==null){
			throw new IllegalArgumentException("Programmer error, null arguments passed.");
		}
		this.locked = false;
		this.manager = _manager;
		this.versionId = 0;
		this.state = State.BLANK;
		originalFieldValues = new HashMap<Field,Value>();//TODO consider linkedhashmap
		fieldvalues = originalFieldValues;
		List<Field> fields = manager.getFields();
		for(Field field:fields){
			if(field.usesDefault){
				Result r = new Result();
				try {
					r = this.setValue(field,field.defaultValue);
				} catch (ApplicationException e) {
					throw new IllegalStateException("Programmatic error with setting value:"+e.getMessage());
				}
				if(r.notSuccessful()){
					throw new IllegalArgumentException("Programmatic error with default setting: "+field.defaultValue+" on field:'"+field.name+"' result:"+r);
				}
			}else{
				fieldvalues.put(field,new Value(field));//TODO, should have the field make a blank value of correct type.   //new:field.factoryBlankValue() 
			}
		}
	}	
	

	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + versionId;
		return result;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Model other = (Model) obj;
		if (id != other.id)
			return false;
		if (versionId != other.versionId)
			return false;
		return true;
	}



	/**
	 * DOC
	 * 
	 * @return
	 */
	public Integer getInUseBy(){
		return this.inUseById;
	}
	
	/**
	 * DOC
	 * 
	 * @param newState
	 */
	public void setState(State newState){
		this.state = newState;
	}
	
	/**
	 * DOC
	 * @return
	 */
	public State getState(){
		return this.state;	
	}
	
	/**
	 * DOC
	 * 
	 * @return
	 */
	public Application getApplication(){
		return manager.application;
	}

	
	/**
	 * DOC
	 * @return
	 */
	public Map<Field,Value> getFieldValues(){
		return this.fieldvalues;
	}
	
	/**
	 * DOC
	 */
	public void lock(){
		this.locked = true;//TODO replace with an inuse
	}
	
	/**
	 * DOC
	 * @param next
	 */
	public void setId(Integer next) {
		this.id = next;
	}	
	
	/**
	 * DOC
	 * @param next
	 */
	public void setVersionId(Integer next){
		this.versionId = next;
	}
	
	/**
	 * DOC
	 */
	public void incrementVersion(){
		this.versionId = versionId + 1;
	}
	
	/**
	 * The implementor should execute setters that contain validation and
	 * if valid, set the field result according to the result.
	 * 
	 * @param field
	 * @param value
	 * @return
	 * @throws ApplicationException 
	 */
	public Result setValue(Field field, String _value) throws ApplicationException{
		Messages messages = new Messages();
		List<Field> notifying = new ArrayList<Field>();
		//preSetFieldValue(field,_value,messages);
		Result result = new Result();
		if(locked){
			result.noResult("This record is locked.");
		}else{
			result = field.makeValue(_value, messages,false);	
		}
		if(result.isSuccessful()){
			if(result.value()==null){
				throw new IllegalStateException("value cannot be null if result was successful.");
			}
			fieldvalues.put(field,result.value());
			notifying.add(field);
			notifySetValue(notifying);
			notifyAggregateFields(field,result.value());
		}
		return result;				
	}

	/**
	 * DOC
	 * @param _field
	 * @param _value
	 * @return
	 * @throws ApplicationException 
	 */
	public Result setValue(String _field, String _value) throws ApplicationException{
		Field field = manager.getField(_field);
		return setValue(field,_value);
	}
	
	/**
	 * DOC
	 * 
	 * Note: The caller of this won't have the ability to infer a strong type so we 
	 * have this kludge to work around with an instanceof check. However, since this 
	 * package is under more control, we won't be too concerned with calling this 
	 * incorrectly. Some tricks have already been tried to work this out better. 
	 * TODO continue to find a better solution to this implementation, even though it works for now.
	 * 
	 * @param field
	 * @param _mod
	 * @return
	 * @throws ApplicationException 
	 */
	public Result setReference(Field _field, Model _mod) throws ApplicationException{
		if(!(_field instanceof ReferenceField)){
			throw new IllegalArgumentException("field here has to be a ReferenceField");
		}
		return setValue(_field,_mod.getId().toString());
	}
	
	/**
	 * DOC
	 * @param _field
	 * @param _mod
	 * @return
	 * @throws ApplicationException 
	 */
	public Result setReference(String _field, Model _mod) throws ApplicationException{
		Field field = manager.getField(_field);
		return setReference(field,_mod);
	}
	
	/**
	 * DOC
	 * @param field
	 * @param _value
	 * @return
	 */
	public Result setCalc(List<Field> alreadyNotified, Field field, String _value){
		Messages messages = new Messages();
		//preSetFieldValue(field,_value,messages);
		Result result = field.makeValue(_value, messages,true);
		if(result.isSuccessful()){
			fieldvalues.put(field,result.value());
		}
		alreadyNotified.add(field);
		notifySetValue(alreadyNotified);//may want to ensure valid values for other calcs.
		return result;				
	}
	
	
	/**
	 * DOC
	 * @param unique
	 * @param m
	 * @return
	 */
	public boolean matches(List<Field> index, Model _model){
		boolean matches = true;
		for(Field _field:index){
			Value _modelFieldValue = _model.fieldvalues.get(_field);
			Value _thisFieldValue = fieldvalues.get(_field);
			matches = matches && _modelFieldValue.equals(_thisFieldValue);
		}
		return matches;
	}
	
	/**
	 * DOC
	 * @param index
	 * @param terms
	 * @return
	 */
	public boolean matches(List<Field> index, String[] terms){
		boolean matches = true;
		//int arrindex = 0;
		for(Field _field:index){
			Value _thisFieldValue = fieldvalues.get(_field);
			matches = matches && _thisFieldValue.toString().equals(terms[0]);
		}
		return matches;
	}	

	
	/**
	 * DOC
	 * 
	 * @param fieldname
	 * @return
	 */
	public boolean haveValue(String fieldname){
		boolean rv = false;
		for(Field f:fieldvalues.keySet()){
			if(f.name.equals(fieldname)){
				Value v = fieldvalues.get(f);
				if(v.isNotBlank()){
					rv = true;
					break;
				}
			}
		}
		return rv;
	}
	
	/**
	 * The implementor should look at the value
	 * 
	 * @param f
	 * @return
	 */
	public String getFormattedValue(Field f){
		Value v = fieldvalues.get(f);
		return v.format();
	}


	/**
	 * DOC
	 * @return
	 */
	public boolean isSaved(){
		return false;//TODO impl: should return false unless saved.
	}
	
	/**
	 * DOC
	 * @return
	 */
	public boolean isReserved(){
		checkTimeout();
		return this.inUseById!=null;
	}
	
	public Date lockExpires(){
		if(this.inUseById!=null){
			Calendar startedCal = Calendar.getInstance();
			startedCal.setTime(this.inUseSinceDate);
			long started = startedCal.getTimeInMillis();
			long expireTime = started + Model.USE_TIMEOUT;
			return new Date(expireTime);
		}else{
			return null;
		}
	}
	
	public void checkTimeout(){
		if(this.inUseById!=null){
			Calendar startedCal = Calendar.getInstance();
			startedCal.setTime(this.inUseSinceDate);
			long started = startedCal.getTimeInMillis();
			long expireTime = started + Model.USE_TIMEOUT;
			long now = Calendar.getInstance().getTimeInMillis();
			if(now>=expireTime){
				this.inUseById = null;
				this.inUseSinceDate = null;
			}
		}
	}
	
	//TODO add a check and expire reserve with a notify to the locking user if in session.
	
	/**
	 * DOC
	 * @return
	 */
	public boolean notReserved(){
		return this.inUseById==null;
	}
		
	/**
	 * DOC
	 * @param user
	 */
	public void reserve(Integer userId){
		this.inUseById = userId;
		this.setInUseSinceDate(new Date());
		if(updateFieldValues!=null){
			updateFieldValues.clear();
		}
		updateFieldValues = Model.deepCopy(originalFieldValues);
		this.fieldvalues = updateFieldValues;
		//manager.reserve(this); NOTE: no longer necessary.
	}
	
	//TODO we restore memory upon fails. do this when closing without saving.
	public void rollBack(){
		this.fieldvalues = originalFieldValues;
		updateFieldValues.clear();
	}
	
	//TODO after saving, we want to swap everything back and have the updated be the original
	public void save(){
		this.originalFieldValues.clear();
		this.originalFieldValues = Model.deepCopy(updateFieldValues);
		this.fieldvalues = originalFieldValues;
		this.updateFieldValues.clear();
	}
	
	/**
	 * DOC
	 * 
	 * @param userId
	 * @param aDate
	 
	public void reserve(Integer userId, Date aDate){
		this.inUseById = userId;
		this.setInUseSinceDate(aDate);
	}	*/
	
	/**
	 * DOC
	 */
	public void unreserve(){
		this.inUseById = null;
		this.setInUseSinceDate(null);
	}
	
	/**
	 * This set and checked when either the record is properly initialized
	 * or when the user indicates they want to keep changes. (save at any time)
	 * As soon as they "open a field" for editing, they go into unsaved mode.
	 * 
	 * @return
	 */
	public boolean isSaveReady(){
		//TODO impl
		return false;
	}

	
	/**
	 * Do calculations prior to saving.
	 */
	public void preSave(){
		;//TODO impl does nothing.
	}

	/**
	 * DOC
	 * @return
	 */
	public Manager getManager(){
		return this.manager;
	}
	
	/**
	 * Dangerous method DOC
	 * @param fieldname
	 * @return
	 */
	public Value getValue(String fieldname) {
		Value v = null;
		Field f = getField(fieldname);
		if(f!=null){
			v = getValue(f);
		}
		return v;
	}
	
	/**
	 * DOC
	 * @param f
	 * @return
	 */
	public Value getValue(Field f) {
		return fieldvalues.get(f);
	}
	

	/**
	 * DOC
	 * @return
	 */
	public Integer getId(){
		return this.id;
	}	
	
	/**
	 * DOC
	 * @return
	 */
	public String getIdString(){
		return String.valueOf(this.id);
	}
	
	
	/**
	 * DOC
	 * @return
	 */
	public abstract String getIdentifyingValue();
/* 	{
		System.out.println("Model getIdentifyingValue");
		return String.valueOf(this.id);
	}
*/

	
	/**
	 * DOC
	 * 
	 * @param foreignListener
	 * @param notifier
	 * @param _value
	 * @throws ApplicationException 
	 */
	protected void doAggregateCalculations(Field foreignListener, Field notifier, Value _value) throws ApplicationException{
		//override
	}
	
	/**
	 * DOC.
	 * 
	 * @param f
	 * @param v
	 */
	protected void doCalculation(List<Field> notifying,Field f){
		//TODO impl
	}	
	
	/**
	 * DOC
	 * 
	 * @param f
	 * @param v
	 */
	protected void doAggregate(Field f){
		//TODO impl
	}	
	
	
	/**
	 * Dangerous method.
	 * @param fieldName
	 * @return
	 */
	protected Field getField(String fieldname) {
		Field rv = null;
		for(Field f:fieldvalues.keySet()){
			if(f.name.equals(fieldname)){
				rv = f;
				break;
			}
		}
		return rv;
	}	
	
	/**
	 * DOC
	 */
	protected void unload()
	{
		fieldvalues.clear();
	}
	
	/**
	 * DOC
	 * @param fieldMap
	 */
	protected void load(Map<Field,Value> fieldMap)
	{
		fieldvalues = fieldMap;
	}	
	
	/**
	 * DOC
	 * @param notifying
	 */
	private final void notifySetValue(List<Field> notifying) {
		for(Field f:fieldvalues.keySet()){
			boolean notYetNotified = (!notifying.contains(f));
			if(f.isCalculated && notYetNotified ){
				doCalculation(notifying,f);
			}
		}
	}
	
	/**
	 * DOC
	 * @param field
	 * @param _value
	 * @throws ApplicationException 
	 */
	private final void notifyAggregateFields(Field field, Value _value) throws ApplicationException {
		for(Field aggregateListener:field.aggregateListeners){
			doAggregateCalculations(aggregateListener,field,_value);
		}
	}	


	public String toCsv(){
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(Field f: fieldvalues.keySet()){
			if(!first){
				sb.append(",");
			}else{
				first = false;
			}
			sb.append(fieldvalues.get(f).toSql());
		}
		sb.append("\n");
		return sb.toString();		
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(manager.label+"(");
		boolean first = true;
		for(Field f: fieldvalues.keySet()){
			if(!first){
				sb.append(",");
			}else{
				first = false;
			}
			sb.append(f.name);
			sb.append("='");
			sb.append(fieldvalues.get(f));
			sb.append("'");
		}
		sb.append(");");
		return sb.toString();
	}



	/**
	 * DOC
	 * @param _ownerId
	 */
	public void setOwner(Integer _ownerId) {
		this.setOwnerId(_ownerId);
	}

	/**
	 * DOC
	 * 
	 * @param _approverId
	 */
	public void setApprover(Integer _approverId) {
		this.setApproverId(_approverId);
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public Integer getApproverId() {
		return approverId;
	}

	public void setApproverId(Integer approverId) {
		this.approverId = approverId;
	}

	public Date getInUseSinceDate() {
		return inUseSinceDate;
	}

	public void setInUseSinceDate(Date inUseSinceDate) {
		this.inUseSinceDate = inUseSinceDate;
	}

	public int getVersionId() {
		return versionId;
	}

	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}

	public ModelHistory getModelHistory() {
		return modelHistory;
	}

	public void setModelHistory(ModelHistory modelHistory) {
		this.modelHistory = modelHistory;
	}

	public Map<Field, Value> getEnteredFieldValues() {
		Map<Field,Value> subMap = new HashMap<Field,Value>();//TODO consider linkedhashmap
		for(Field _f:this.fieldvalues.keySet()){
			Value _v = fieldvalues.get(_f);
			if(_v.isNotBlank()){
				subMap.put(_f,_v);
			}
		}
		return subMap;
	}

	public String getDebug() {
		return "["+this.getManager().getName()+"("+this.getId()+")"+"]";
	}

	public void decrementVersion() {
		this.versionId = versionId - 1;
	}
}

/**
 * TODO JHG note: not sure if we implement this here or in manager yet.
 *  
 * @return
 
public abstract SaveResult save();
*/

//public abstract void parse(String line);
//public abstract void parse(Map<String,String> map);
/**
 * For use by protected storage. Does not check lock value.
 * May or may not use calculations/aggregations if the calculated fields are stored as a setting.
 * 
 * @param field
 * @param _value
 * @return
 
protected Result loadValue(Field field, String _value){
	return null;//TODO impl
	//locks are set after load....anywya.
}
*/
/**
 * DOC
 * 
 * @param field
 * @param _value
 * @return
 
public Result updateValue(Field field, String _value){
	return null;//TODO impl
}*/

/*
protected void preSetFieldValue(Field field, String _value, Messages messages){
	
}
protected void postSetFieldValue(Field field, Messages messages){
	
}
*/	