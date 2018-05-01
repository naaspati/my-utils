package sam.weak;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

public class WeakKeep<E> {
    private WeakReference<E> weak = new WeakReference<E>(null);
    private final Supplier<E> generator;
    
    public WeakKeep(Supplier<E> generator) {
        this.generator = generator;
    }
    
    public E get() {
        E e = weak.get();
        if(e == null)
            weak = new WeakReference<E>(e = generator.get());
        
        return e;
    }
    
}
