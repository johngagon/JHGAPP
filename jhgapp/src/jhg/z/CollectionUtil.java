package jhg.z;

import jhg.Messages;

/**
 * DOC
 * @author John
 *
 */
public class CollectionUtil {

	public static boolean safeGet(char[] y, int i, Messages msgs, char at) {
		if(y==null){
			msgs.add("Array error: array is null.");
			return false;
		}
		int length = y.length;
		if(i<0){
			msgs.add("Array Index Error: array-length="+length+", index="+i);
			return false;
		}
		if(i>length-1){
			msgs.add("Array Index Error: array-length="+length+", index="+i);
			return false;
		}
		at = y[i];
		return true;
	}	
	
	public static <T> boolean safeGet(T[] y, int i, Messages msgs, T at) {
		if(y==null){
			msgs.add("Array error: array is null.");
			return false;
		}
		int length = y.length;
		if(i<0){
			msgs.add("Array Index Error: array-length="+length+", index="+i);
			return false;
		}
		if(i>length-1){
			msgs.add("Array Index Error: array-length="+length+", index="+i);
			return false;
		}
		at = y[i];
		return true;
	}	
	
}
