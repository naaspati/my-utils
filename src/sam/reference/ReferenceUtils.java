package sam.reference;

import java.lang.ref.Reference;
import java.util.function.Supplier;

public class ReferenceUtils {
	public static <E> E get(Reference<E> w) {
		return w == null ? null : w.get(); 
	}
	public static <E> E computeIfAbsent(Reference<E> w, Supplier<E> getter) {
		E e = get(w);
		return e != null ? e : getter.get();
	}
}
