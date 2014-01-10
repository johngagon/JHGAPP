package jhg.appman.run.widget;

import java.util.*;
import jhg.appman.run.DisplayField;
import jhg.model.Model;
import jhg.model.Field;
import jhg.model.Manager;
import jhg.model.Value;
import jhg.model.field.Numeric;
import jhg.model.field.Decimal.DecimalField;
import jhg.model.field.Numeric.NumberField;
import jhg.model.field.Reference;
import jhg.model.field.Reference.ReferenceField;

public class ReferenceDisplayField  extends DisplayField {

	public ReferenceDisplayField(ReferenceField _field) {
		super(_field);
	}
	public ReferenceDisplayField(ReferenceField _field, Value _value) {
		super(_field,_value);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String render() {
		StringBuffer sb = new StringBuffer();
		ReferenceField referencefield = (ReferenceField)field;
		Reference ref = (Reference)value;
		//String valueStr = value.toString();
		sb.append("<select ");
		sb.append(attribute("name",referencefield.getName()));
		if(readonly){
			sb.append(attribute("readonly","true"));
		}		
		sb.append(">");
		log("Reference Field:"+referencefield.getLabel());
		Manager refManager = referencefield.getReferencedManager();
		log("Reference Manager :"+refManager.getLabel());
		Map<Integer,Model> refModels = refManager.getModels();
		log("Reference Manager getModels() size:"+refModels.size());
		for(Integer idx:refModels.keySet()){
			sb.append("<option ");
			Model model = refModels.get(idx);
			String identifier = model.getIdentifyingValue();
			sb.append(attribute("value",model.getIdString()));
			if(hasValue()){
				if(idx.equals(ref.intValue())){
					sb.append(attribute("selected","true"));
				}
			}
			sb.append(">");
			sb.append(identifier);
			sb.append("</option>");
		}
		sb.append("<select>");
		//autocomplete, autofocus, form, 
		//required,readonly,disabled
		return sb.toString();
	}

}
