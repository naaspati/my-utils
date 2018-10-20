package sam.reference;

import java.util.function.Supplier;

public class WeakList<E> extends ReferenceList<E> {
	public WeakList(Supplier<E> valueGenerator) {
    	super(ReferenceType.WEAK, false, valueGenerator); 
	}
    public WeakList(boolean threadsafe, Supplier<E> valueGenerator) {
    	super(ReferenceType.WEAK, threadsafe, valueGenerator); 
	}
	

}
