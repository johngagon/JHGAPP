package jhg.appman.run.widget;

import jhg.appman.run.DisplayField;
import jhg.model.Field;
import jhg.model.Value;
import jhg.model.field.DateTime;
import jhg.model.field.DateTime.DateTimeField;
import jhg.model.field.Decimal;
import jhg.model.field.Decimal.DecimalField;

public class DecimalDisplayField extends DisplayField {

	public DecimalDisplayField(DecimalField _field) {
		super(_field);
	}
	public DecimalDisplayField(DecimalField _field, Value _value) {
		super(_field,_value);
	}
	@Override
	public String render() {
		StringBuffer sb = new StringBuffer();
		DecimalField decimalfield = (DecimalField)field;

		sb.append("<input ");
		sb.append(attribute("type","number"));
		sb.append(attribute("step","any"));
		sb.append(attribute("name",decimalfield.getName()));
		if(readonly){
			sb.append(attribute("readonly","true"));
		}		
		if(hasValue()){
			sb.append(attribute("value",value.format()));
		}
		sb.append("/>");
		//autocomplete, autofocus, form, 
		//required,readonly,disabled
		return sb.toString();
	}

}
