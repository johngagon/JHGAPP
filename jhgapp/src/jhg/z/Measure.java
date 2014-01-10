package jhg.z;

import jhg.model.field.Decimal;

public class Measure extends Decimal {

	protected Measure(DecimalField _field, String _value) {
		super(_field, _value);
		// TODO Auto-generated constructor stub
	}

	/*
	protected Measure(Decimal _field, String _value) {
		super(_field, _value);
		// TODO Auto-generated constructor stub
	}
*/
	private static final long serialVersionUID = -5663000729735638939L;

	public Double toDouble(){
		return null;//TODO impl
	}
}
