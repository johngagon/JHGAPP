package jhg.appman.run.widget;

import jhg.appman.run.DisplayField;
import jhg.model.Field;
import jhg.model.Value;
import jhg.model.field.Decimal;
import jhg.model.field.DateTime.DateTimeField;
import jhg.model.field.Decimal.DecimalField;
import jhg.model.field.Flag;
import jhg.model.field.Flag.FlagField;

public class FlagDisplayField extends DisplayField {

	public FlagDisplayField(FlagField _field) {
		super(_field);
	}
	public FlagDisplayField(FlagField _field, Value _value) {
		super(_field,_value);
	}
	@Override
	public String render() {
		//Radio: one and only one of one or more   (small set)   
		//Select: one and only one of one or more  (larger set)
		
		//Multiselect: zero or more of one or more  (nothing may be selected here too)  
		//Checkbox: zero or more of a one or more
		
		StringBuffer sb = new StringBuffer();
		FlagField flagfield = (FlagField)field;
		sb.append("Y <input ");
		sb.append(attribute("type","radio"));
		sb.append(attribute("name",flagfield.getName()));
		sb.append(attribute("value","true"));
		if(readonly){
			sb.append(attribute("readonly","true"));
		}		
		if(hasValue() && (value instanceof Flag)){
			Flag flag = (Flag)value;
			if(flag.isTrue()){
				sb.append(attribute("checked","true"));
			}
		}
		sb.append("/>");
		sb.append("N <input ");
		sb.append(attribute("type","radio"));
		sb.append(attribute("name",flagfield.getName()));
		sb.append(attribute("value","false"));
		if(readonly){
			sb.append(attribute("readonly","true"));
		}		
		if(hasValue() && (value instanceof Flag)){
			Flag flag = (Flag)value;
			if(flag.isFalse()){
				sb.append(attribute("checked","true"));
			}
		}
		sb.append("/>");
		
		//autocomplete, autofocus, form, 
		//required,readonly,disabled
		return sb.toString();
	}

}
