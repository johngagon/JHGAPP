package jhg.appman.run.widget;

import jhg.appman.run.DisplayField;

import jhg.model.Value;
import jhg.model.field.Numeric;
import jhg.model.field.Decimal.DecimalField;
import jhg.model.field.Numeric.NumberField;

public class NumericDisplayField extends DisplayField {

	public NumericDisplayField(NumberField _field) {
		super(_field);
	}
	public NumericDisplayField(NumberField _field, Value _value) {
		super(_field,_value);
	}
	@Override
	public String render() {
		StringBuffer sb = new StringBuffer();
		NumberField numberfield = (NumberField)field;

		sb.append("<input ");
		sb.append(attribute("type","number"));
		sb.append(attribute("step","1"));
		sb.append(attribute("min",numberfield.getMin()));
		sb.append(attribute("max",numberfield.getMax()));
		sb.append(attribute("name",numberfield.getName()));
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
	
	/*
	 Note on client side calcs:
	 

<form oninput="x.value=parseInt(a.value)+parseInt(b.value)">0
<input type="range" id="a" value="50">100
+<input type="number" id="b" value="50">
=<output name="x" for="a b"></output>
</form>	 
	 
	 */
	

}
