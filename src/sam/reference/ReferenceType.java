package sam.reference;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public enum ReferenceType {
	WEAK, SOFT;
	
	public <E> Reference<E> get(E e) {
		return this == WEAK ? new WeakReference<E>(e) : new SoftReference<E>(e); 
	}
}