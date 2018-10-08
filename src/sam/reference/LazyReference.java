package sam.reference;

import java.lang.ref.Reference;
import java.util.function.Supplier;

public class LazyReference<E> {
    private Reference<E> reference;
    private final ReferenceType type;
    private final Supplier<E> generator;
    
    public LazyReference(Supplier<E> generator, ReferenceType type) {
        this.generator = generator;
        this.type = type;
        reference = type.get(null); 
    }
    
    /**
     * @return if no garbage collected, return the existing value, else create a value and return.
     */
    public E get() {
        E e = reference.get();
        if(e == null)
            reference = type.get(generator.get());
        
        return e;
    }
    public void clear() {
    	reference.clear();
    }
    /**
     * @return the value if there is any (without creating).
     */
    public E getWithoutGenerating() {
    	return reference.get();
    }
}
