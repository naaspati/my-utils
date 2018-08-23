package sam.weak;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

public class LazyAndWeak<E> {
    private WeakReference<E> weak = new WeakReference<E>(null);
    private final Supplier<E> generator;
    
    public LazyAndWeak(Supplier<E> generator) {
        this.generator = generator;
    }
    
    public E get() {
        E e = weak.get();
        if(e == null)
            weak = new WeakReference<E>(e = generator.get());
        
        return e;
    }
    
}
