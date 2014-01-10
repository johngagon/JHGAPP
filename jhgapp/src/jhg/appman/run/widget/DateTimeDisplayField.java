package jhg.appman.run.widget;

import jhg.appman.run.DisplayField;
import jhg.model.Field;
import jhg.model.Value;
import jhg.model.field.DateTime;
import jhg.model.field.DateTime.DateTimeField;
import jhg.model.field.Text.TextField;

public class DateTimeDisplayField extends DisplayField {

	public DateTimeDisplayField(DateTimeField _field) {
		super(_field);
	}
	public DateTimeDisplayField(DateTimeField _field, Value _value) {
		super(_field,_value);
	}
	
	@Override
	public String render() {
		StringBuffer sb = new StringBuffer();
		DateTimeField datetimefield = (DateTimeField)field;

		sb.append("<input ");
		if(DateTimeField.Type.DATE.equals(datetimefield.getType())){
			sb.append(attribute("type","date"));	
		}else if(DateTimeField.Type.TIME.equals(datetimefield.getType())){
			sb.append(attribute("type","time"));
		}else if(DateTimeField.Type.TIMESTAMP.equals(datetimefield.getType())){
			sb.append(attribute("type","datetime-local"));
		}else{
			;//do nothing
		}
		sb.append(attribute("name",datetimefield.getName()));
		if(readonly){
			sb.append(attribute("readonly","true"));
		}		
		if(hasValue()){
			String _value = null;
			if(DateTimeField.Type.TIMESTAMP.equals(datetimefield.getType())){
				_value = renderDateTimeLocal(datetimefield, value.format());
			}else{
				_value = value.format();
			}
			sb.append(attribute("value",_value));
		}
		sb.append("/>");
		//autocomplete, autofocus, form, 
		//required,readonly,disabled
		return sb.toString();
	}


	
	private String renderDateTimeLocal(DateTimeField datetimefield, String _value) {
		String rendered = DateTime.Util.convertDateFormat(datetimefield.getFormatTemplate(), "yyyy-MM-dd'T'HH:mm:ss", _value);
		System.out.println("DateTimeDisplayField.renderDateTimeLocal:"+rendered);//FIXME System.out
		return rendered;
	}
	
	protected void setValue(DateTime _value){
		this.value = _value;
	}


	
	
}
