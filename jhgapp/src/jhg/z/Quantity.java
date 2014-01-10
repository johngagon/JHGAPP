package jhg.z;

import java.math.BigDecimal;

import jhg.model.field.Decimal;

/**
 * A whole or fractional quantity that has additive precision and no rounding 
 * required with arithmetic functions. 
 * 
 * @author John
 *
 */
public class Quantity extends Decimal {

	/*
	protected Quantity(jhg.model.Field _field, String _value) {
		super(_field, _value);
		// TODO Auto-generated constructor stub
	}
	*/
	
	protected Quantity(DecimalField _field, String _value) {
		super(_field, _value);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8654923988892525727L;

	public BigDecimal toBigDecimal() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * number formatting
	 */
	
}
