package sam.reference;

import java.util.function.Supplier;

public class WeakPool<E> extends ReferencePool<E> {
	public WeakPool(Supplier<E> valueGenerator) {
    	super(ReferenceType.WEAK, false, valueGenerator); 
	}
    public WeakPool(boolean threadsafe, Supplier<E> valueGenerator) {
    	super(ReferenceType.WEAK, threadsafe, valueGenerator); 
	}
	

}
