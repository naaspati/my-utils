package sam.di;

import java.util.function.Supplier;

import sam.reference.WeakAndLazy;

public abstract class WeakProvider<E> {
    protected final WeakAndLazy<E> w;
    
    public WeakProvider(Supplier<E> supp) {
        this.w = new WeakAndLazy<>(supp);
    }
    public E get() {
        return w.get();
    }
}
