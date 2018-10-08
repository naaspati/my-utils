package sam.reference;

import java.lang.ref.Reference;

public class ReferenceUtils {
	public static <E> E get(Reference<E> w) {
		return w == null ? null : w.get(); 
	} 
}
