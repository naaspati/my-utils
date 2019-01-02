package sam.reference;

import java.lang.ref.Reference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LazyReference<E> {
	private volatile Reference<E> reference;
	private final ReferenceType type;
	private final Supplier<E> generator;
	private final Object lock = new Object(); 

	public LazyReference(Supplier<E> generator, ReferenceType type) {
		this.generator = generator;
		this.type = type;
		reference = type.get(null); 
	}

	/**
	 * @return if no garbage collected, return the existing value, else create a value and return.
	 */
	public E get() {
		synchronized (lock) {
			E e = ReferenceUtils.get(reference);
			if(e == null)
				reference = type.get(e = generator.get());

			return e;			
		}

	}
	public void clear() {
		synchronized (lock) {
			if(reference != null)
				reference.clear();
		}
	}
	/**
	 * @return the value if there is any (without creating), else null
	 */
	public E peek() {
		synchronized (lock) {
			return ReferenceUtils.get(reference);
		}
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
	public E pop() {
		synchronized (lock) {
			E e = ReferenceUtils.get(reference);
			reference = null;
			if(e == null) return generator.get();
			return e;
		}
	}
	public void set(E e) {
		synchronized (lock) {
			this.reference = e == null ? null : type.get(e);
		}
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
