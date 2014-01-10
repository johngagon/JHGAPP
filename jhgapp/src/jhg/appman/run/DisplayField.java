package jhg.appman.run;

//import hirondelle.web4j.model.Decimal;
import jhg.Base;
import jhg.appman.run.widget.*;
import jhg.model.Field;
import jhg.model.Value;
import jhg.model.field.DateTime.DateTimeField;
import jhg.model.field.Decimal.DecimalField;
import jhg.model.field.Flag.FlagField;
import jhg.model.field.Text.TextField;
import jhg.model.field.Numeric.NumberField;
import jhg.model.field.Reference.ReferenceField;
import jhg.model.field.DateTime;
import jhg.model.field.Decimal;
import jhg.model.field.Flag;
import jhg.model.field.Text;
import jhg.model.field.Numeric;
import jhg.model.field.Reference;

public abstract class DisplayField extends Base {

	public static DisplayField make(Field _field){
		//TODO is there a better solution to this?
		DisplayField df = (_field instanceof DateTimeField)? new DateTimeDisplayField((DateTimeField)_field)
			:(_field instanceof DecimalField)? new DecimalDisplayField((DecimalField)_field)
			:(_field instanceof FlagField)? new FlagDisplayField((FlagField)_field)
			:(_field instanceof ReferenceField)? new ReferenceDisplayField((ReferenceField)_field)
			:(_field instanceof NumberField)? new NumericDisplayField((NumberField)_field)
			:(_field instanceof TextField)? new TextDisplayField((TextField)_field)
			:null;
		
		return df;
	}
	public static DisplayField make(Field _field,Value value){
		//TODO is there a better solution to this?
		DisplayField df = (_field instanceof DateTimeField)? new DateTimeDisplayField((DateTimeField)_field,value)
			:(_field instanceof DecimalField)? new DecimalDisplayField((DecimalField)_field,value)
			:(_field instanceof FlagField)? new FlagDisplayField((FlagField)_field,value)
			:(_field instanceof ReferenceField)? new ReferenceDisplayField((ReferenceField)_field,value)   //subtype first
			:(_field instanceof NumberField)? new NumericDisplayField((NumberField)_field,value)
			:(_field instanceof TextField)? new TextDisplayField((TextField)_field,value)
			:null;
		
		return df;
	}	

	
	protected Field field;
	protected Value value;
	protected boolean readonly;
	
	protected DisplayField(Field _field){
		this.field = _field;
		this.readonly = false;
	}
	protected DisplayField(Field _field, Value _value){
		this(_field);
		this.value = _value;
	}

	public void setReadonly(){
		this.readonly = true;
	}
	
	protected boolean isReadonly(){
		return this.readonly;
	}
	
	protected boolean hasValue(){
		return this.value != null;
	}
	
	public abstract String render();
	
	protected String attribute(String name, String text) {
		return (name+"=\""+text+"\" ");
	}
	protected String attribute(String name, char character) {
		return (name+"=\""+character+"\" ");
	}		
	protected String attribute(String name, int number) {
		return (name+"=\""+number+"\" ");
	}
	protected String attribute(String name, boolean flag) {
		return (name+"=\""+flag+"\" ");
	}		
	protected String attribute(String name, double dbl) {
		return (name+"=\""+dbl+"\" ");
	}		
	protected String attribute(String name, long ll) {
		return (name+"=\""+ll+"\" ");
	}		
}
