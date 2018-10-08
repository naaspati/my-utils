package sam.reference;

import java.util.function.Supplier;

public class WeakAndLazy<E> extends LazyReference<E> {

	public WeakAndLazy(Supplier<E> generator) {
		super(generator, ReferenceType.WEAK);
	}

}
