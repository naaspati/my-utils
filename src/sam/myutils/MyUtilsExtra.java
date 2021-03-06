package sam.myutils;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MyUtilsExtra {
	/**
	 * return ifTrue ? than : otherWise;
	 * @param ifTrue 
	 * @param than
	 * @param otherwise
	 * @return
	 */
	public static <E> E elvis(boolean ifTrue, E than, E otherwise) {
		return ifTrue ? than : otherwise;
	}
	public static <E> E elvis(boolean ifTrue, E than, Supplier<E> otherwise) {
		return ifTrue ? than : otherwise.get();
	}
	public static <E, F> F map(E value, Function<E, F> mapper) {
		return mapper.apply(value);
	}
	/**
	 * return value != null ? value : orElse
	 * 
	 * @param value
	 * @param orElse
	 * @return
	 */
	public static <E> E nullSafe(E value, E orElse) {
		return elvis(value != null, value, orElse);
	}
	public static <E> E nullSafe(E value, Supplier<E> orElse) {
		return elvis(value != null, value, orElse);
	}
	public static <E, F> F ifNotNull(E value, Function<E, F> action) {
		if(value == null)
			return null;
		return action.apply(value);
	}
	/**
	 * 
	 * @param o
	 * @param to
	 * @return if o is null or not instanceof E, then return null, else casted element
	 */
	public static <E> E cast(Object o, Class<E> to) {
		if(o == null || o.getClass() != to)
			return null;

		return to.cast(o);
	}
	
	@SafeVarargs
	public static <E> void forEach(Consumer<E> consumer, E...data ) {
		for (E e : data) 
			consumer.accept(e);
	}
	public static <E> void forEach(E[] data, Consumer<E> consumer) {
		for (E e : data) 
			consumer.accept(e);
	}
	public static <E> void forEach(Iterable<E> data, Consumer<E> consumer) {
		for (E e : data) 
			consumer.accept(e);
	}
	public static <E> void forEach(Iterator<E> data, Consumer<E> consumer) {
		forEach(new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				return data;
			}
		}, consumer);
	}
}
