package jhg.appman.run.widget;

import jhg.appman.run.DisplayField;
import jhg.model.Field;
import jhg.model.Value;
import jhg.model.field.Text;
import jhg.model.field.Text.TextField;

public class TextDisplayField extends DisplayField  {

	public TextDisplayField(TextField _field) {
		super(_field);
	}
	public TextDisplayField(TextField _field, Value _value) {
		super(_field,_value);
	}	
	@Override
	public String render() {
		StringBuffer sb = new StringBuffer();
		TextField textfield = (TextField)field;
		if(textfield.isMultiLine()){
			sb.append("<textarea");
			sb.append(attribute("name",textfield.getName()));
			sb.append(attribute("rows",textfield.rows()));
			sb.append(attribute("cols",textfield.rows()));
			if(readonly){
				sb.append(attribute("readonly","true"));
			}
			sb.append(">");
			if(hasValue()){
				sb.append(value.format());
			}
			sb.append("</textarea>");
			//readonly, required, disabled
			//form, maxlength, autofocus, placeholder (hint), wrap  (new HTML5),
		}else{
			sb.append("<input ");
			sb.append(attribute("type","text"));//conditional,   password? hidden?   
			sb.append(attribute("name",textfield.getName()));
			int textFieldLength = textfield.getLength();
			int displayLength = (textFieldLength>100)?100:textFieldLength;
			sb.append(attribute("size",displayLength));
			sb.append(attribute("maxlength",textFieldLength));
			if(readonly){
				sb.append(attribute("readonly","true"));
			}			
			if(hasValue()){
				sb.append(attribute("value",value.format()));
			}			
			sb.append("/>");
			//readonly, required, disabled
			//autocomplete, autofocus, form, pattern, placeholder, 
		}
		//INPUT (checkbox:checked,radio:checked,file(brows),etc) submit:formaction,formmethod, image    checked.
		/* type:
		 
		button
		checkbox
		* color    
		* date 
		* datetime 
		* datetime-local 
		* email 
		file
		hidden
		image
		* month 
		* number 
		password
		radio
		* range 
		reset
		* search
		submit
		* tel
		text
		* time 
		* url
		* week
		*/
		return sb.toString();
	}



	/*
<input type="text" />
<textarea> </textarea>
	 */
	 
}
