package jhg.model.field;

import jhg.model.Manager;
import jhg.model.Model;
import jhg.model.Value;

/**
 * DOC
 * @author John
 *
 */
@SuppressWarnings("rawtypes")
public class Reference extends Numeric {
	
	/**
	 * DOC
	 * @author John
	 */
	public static class ReferenceField extends Numeric.NumberField{

		private Manager referenceManager;
		
		public ReferenceField(Manager m, String _name,Manager _manager) {
			super(m, _name, NumberField.Type.INTEGER);
			if(_manager==null)throw new IllegalArgumentException("The referenced manager is null.");
			this.referenceManager = _manager;
			super.setBounds(new Long(1),new Long(Integer.MAX_VALUE));
		}
		
		public Manager getReferencedManager(){
			return referenceManager;
		}
		
		@Override
		protected Value factoryValue(String v) {
			return new Reference(this,v);
		}		
		
		/*
		 * Note: you shouldn't call this.
		 * (non-Javadoc)
		 * @see jhg.model.field.Numeric.NumberField#setBounds(java.lang.Long, java.lang.Long)
		 */
		public final void setBounds(Long _lowBound, Long _highBound){
			throw new UnsupportedOperationException("Your constructor sets this. This is an API usage error. Do not call on this type.");
		}
		
	}
	
	
	private static final long serialVersionUID = -5895597444440629548L;

	/**
	 * DOC
	 * @param _field
	 * @param _value
	 */
	protected Reference(ReferenceField _field, String _value) {
		super(_field, _value);
	}	

	
	/**
	 * DOC
	 * @return
	 */
	public Model getReferencedModel() {
		Manager refMgr = null;
		refMgr = ((ReferenceField)field).getReferencedManager();
		Model _model = refMgr.getModel(this.intValue());
		if(_model == null){
			throw new IllegalStateException("Referential integrity is broken. The model "+refMgr.getName()+" referrred to by: "+this.value+" was not found.");
		}
		return _model;
	}

	
}
/*
 * 
		Reference Field. it stores the id of another instance of model.
		The model needs to now implement the business key so it can display when used as reference. That field must be required.
		The field itself will store the value as the numeric id.
		
		The field or manager contains the definition of what model type (by class) and what field to get the numeric id. (id always)
		
		Enforcement of referential integrity is done for any delete. (by the manager)
		the model can by default save (e.g.: the name) without having a foreign key set. but to be considered "complete" may require.
		The model referred to must be considered complete usually.
		negative integers should not be allowed since the manager uses a >0 numbering.
		Lookups that are multi-field should be fixed width separated
		
		x The number in reference has no max and min of 1 and must be integer.
*/
