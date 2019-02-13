package sam.reference;

import java.lang.ref.Reference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LazyReference<E> {
	private volatile Reference<E> reference;
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
	public synchronized E get() {
		E e = ReferenceUtils.get(reference);
		if(e == null)
			reference = type.get(e = generator.get());

		return e;			

	}
	public synchronized void clear() {
		if(reference != null)
			reference.clear();
	}
	/**
	 * @return the value if there is any (without creating), else null
	 */
	public synchronized E peek() {
		return ReferenceUtils.get(reference);
	}
	/**
	 * <pre>
	 * E e = ReferenceUtils.get(reference);
	 * reference = null;
	 * if(e == null) return generator.get();
	 * return e;
	 * </pre>
	 * @return
	 */
	public synchronized E pop() {
		E e = ReferenceUtils.get(reference);
		reference = null;
		if(e == null) return generator.get();
		return e;
	}
	public synchronized void set(E e) {
		this.reference = e == null ? null : type.get(e);
	}
	public boolean isPresent(){
		return peek() != null;
	}
	public void ifPresent(Consumer<E> action){
		E e = pop();

		if(e != null) {
			action.accept(e);
			set(e);
		}
	}
}
