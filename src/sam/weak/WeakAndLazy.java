package sam.weak;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

public class WeakAndLazy<E> {
    private WeakReference<E> weak = new WeakReference<E>(null);
    private final Supplier<E> generator;
    
    public WeakAndLazy(Supplier<E> generator) {
        this.generator = generator;
    }
    
    /**
     * @return if no garbage collected, return the existing value, else create a value and return.
     */
    public E get() {
        E e = weak.get();
        if(e == null)
            weak = new WeakReference<E>(e = generator.get());
        
        return e;
    }
    public void clear() {
    	weak.clear();
    }
    /**
     * @return the value if there is any (without creating).
     */
    public E getWithoutGenerating() {
    	return weak.get();
    }
}
