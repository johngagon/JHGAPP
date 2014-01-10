package jhg.z;

import java.util.List;

public class Criteria {

	public static enum Op{EQ,NE,LE,GE,LT,GT,IN,LK,BT,NU,NN}

	public Op op;
	public String value;
	public String value2;
	public List<String> values;
	/*
	 * eq
	 * ne
	 * le
	 * ge
	 * lt
	 * gt
	 * 
	 * in
	 * between
	 * like
	 * isnull
	 * isnotnull
	 */
}
