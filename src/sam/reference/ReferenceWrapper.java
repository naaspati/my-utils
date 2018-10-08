package sam.reference;

import java.lang.ref.Reference;
import java.util.Objects;

public class ReferenceWrapper<T> {
	private Reference<T> w;
	private final ReferenceType type;
	
	/**
	 * type = ReferenceType.WEAK
	 */
	public ReferenceWrapper() {
		this(ReferenceType.WEAK);
	}
	
	public ReferenceWrapper(ReferenceType type) {
		this.type = Objects.requireNonNull(type);
	}
	public T get() {
		return ReferenceUtils.get(w); 
	}
	public void set(T t) {
		w = type.get(t);
	}

}
