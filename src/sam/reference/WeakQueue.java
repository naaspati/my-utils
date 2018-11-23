package sam.reference;

import java.util.function.Supplier;

public class WeakQueue<E> extends ReferenceQueue<E> {
	public WeakQueue(Supplier<E> valueGenerator) {
    	super(ReferenceType.WEAK, false, valueGenerator); 
	}
    public WeakQueue(boolean threadsafe, Supplier<E> valueGenerator) {
    	super(ReferenceType.WEAK, threadsafe, valueGenerator); 
	}
	

}
