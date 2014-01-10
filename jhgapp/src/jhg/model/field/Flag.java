package jhg.model.field;

import java.io.Serializable;

import jhg.Messages;
import jhg.model.Field;
import jhg.model.Manager;
import jhg.model.Value;

/**
 * DOC
 * 
 * TODO see commons for the boolean utilities/bit vector etc.
 * 
 * @author John
 *
 */
@SuppressWarnings("rawtypes")
public class Flag extends Value implements Serializable {

	private static final long serialVersionUID = -7516510125234560481L;

	/**
	 * DOC
	 * @author John
	 *
	 */
	public static class Util {
		/**
		 * DOC
		 * @param f
		 * @return
		 */
		public static String toYN(Flag f){
			if(f.isFalse()){
				return "No";
			}else{
				return "Yes";
			}
		}
	}
	
	/**
	 * DOC
	 * @author John
	 *
	 */
	public static class FlagField extends Field {

		public static final String BOOLEAN = "BOOLEAN";	
		
		public static enum Format{TF,YN}
		
		private Format format;
		
		/**
		 * Construct flag field.
		 * @param _manager
		 * @param _name
		 */
		public FlagField(Manager _manager, String _name) {
			super(_manager, _name);
			this.format = Format.TF;
		}
		
		/**
		 * DOC
		 * @param f
		 */
		public void setFormat(Format f){
			this.format = f;
		}

		@Override
		protected boolean preParseValidate(String _value, Messages messages) {
			boolean r = true;
			if(_value==null)return false;
			try{
				r = (_value.equalsIgnoreCase("true") || _value.equalsIgnoreCase("false"));
				Boolean.valueOf(_value).toString();
			}catch(Exception e){
				messages.add(e.getMessage());
				r=false;
			}
			return r;
		}

		@Override
		protected String parse(String _value) {
			return Boolean.valueOf(_value.trim().toLowerCase()).toString();
		}

		@Override
		protected boolean isValid(String parsed, Messages messages) {
			return true;//(nothing to usuall validate).
		}		
		
		@Override
		public String getFormatTemplate() {
			return "{0}";
		}

		@Override
		protected Value factoryValue(String v) {
			return new Flag(this,v);
		}		
		
		@Override
		public int getSqlType() {
			return java.sql.Types.BOOLEAN;
		}
		
		@Override
		public String toDDL(){
			StringBuilder sb = new StringBuilder();
			sb.append(this.name+" ");
			sb.append(BOOLEAN);
			sb.append(" ");
			sb.append((isNullable)?"":"NOT ");
			sb.append("NULL ");
			return sb.toString();
		}			
		
	}
	
	
	/**
	 * DOC
	 * @param _field
	 * @param _value
	 */
	public Flag(FlagField _field, String _value) {
		super(_field, _value);
	}

	/**
	 * DOC
	 * @return
	 */
	public boolean isFalse() {
		return !Boolean.valueOf(value);
	}

	/**
	 * DOC
	 * @return
	 */
	public boolean isTrue() {
		return Boolean.valueOf(value);
	}

	/**
	 * DOC
	 * @return
	 */
	public Boolean booleanValue(){
		return Boolean.parseBoolean(value);
	}

	@Override
	public FlagField getField(){
		FlagField result = (FlagField)this.field;
		return result;
	}

	@Override
	public String format(){
		FlagField.Format f = getField().format;
		switch(f){
			case TF: return (isTrue())?"T":"F";
			case YN: return (isTrue())?"Y":"N";
			default: throw new IllegalStateException("This field does not have a format.");
		}
	}

}
//Type                     Length                     Scale                            Precision                            Use Quotes
//BOOLEAN  (java.sql.Types.BOOLEAN,  Type.NONE,       Type.NONE,             Type.NONE,                 false),//Boolean

/*

	if(type==java.sql.Types.BOOLEAN){
		try{
			convert = Boolean.valueOf(value).toString();
			rv = true;
		}catch(NumberFormatException nfe){
			error = nfe.getMessage();
			err.println(error);
			rv = false;
		}
	}
*/
