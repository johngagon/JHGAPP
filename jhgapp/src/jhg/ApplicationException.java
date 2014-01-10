package jhg;

/**
 * This exception is for wrapping some checked exceptions and handling them.
 * It's thrown during our own checks for programmatic exceptions / lacking guard code.
 * For example: checkNull(something), checkSuccess(..result..) checkModelNotReserved(..)
 * 
 * Any time we have an action to perform in our facade and we have to bail out 
 * and not return, and also to avoid excessive if...else... logic on preconditions, we use this
 * since the else would otherwise be small.
 * 
 * These are however always caught and handled by action wrappers (service) so as to be routed
 * to the user and exception logs appropriately.
 * 
 * @author John
 *
 */
public class ApplicationException extends Exception {

	private static final long serialVersionUID = 2460730038154073999L;

	/**
	 * RESUME documentation
	 */
	public ApplicationException() {
		super();
	}

	public ApplicationException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public ApplicationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ApplicationException(String arg0) {
		super(arg0);
	}

	public ApplicationException(Throwable arg0) {
		super(arg0);
	}

}
