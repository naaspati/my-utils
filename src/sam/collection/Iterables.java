package sam.collection;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Iterables {
	public static Iterable<Character> of(char[] values) {
		Objects.requireNonNull(values);
		return of(Iterators.of(values));
	}
	public static Iterable<Integer> of(int[] values) {
		Objects.requireNonNull(values);
		return of(Iterators.of(values));
	}
	public static <E> Iterable<E> of(Iterator<E> itr) {
		return new Iterable<E>() {
			@Override public Iterator<E> iterator() { return itr; }
		};
	}
	public static Iterable<Double> of(double[] values) {
		Objects.requireNonNull(values);
		return of(Iterators.of(values));
	}

	public static <E> Iterable<E> of(E[] values) {
		Objects.requireNonNull(values);
		return of(Iterators.of(values));
	}
	public static <E> Iterable<E> of(E[] values, int from, int to) {
		Objects.requireNonNull(values);
		return of(Iterators.of(values, from, to));
	}
	public static <E, F> Iterable<F> map(Iterable<E> itr, Function<E, F> mapper) {
		Objects.requireNonNull(itr);
		Objects.requireNonNull(mapper);
		return of(Iterators.map(itr.iterator(), mapper));
	}
	public static <E> Iterable<E> empty(){
		return of(Iterators.empty());
	}
	public static <E>  Stream<E> stream(Iterable<E> itr) {
		return Iterators.stream(itr.iterator());
	}
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <E>  Iterable<E> join(Iterable<E>... itr) {
		return Iterables.of(Iterators.join(ArraysUtils.map(itr, Iterator[]::new, Iterable::iterator)));
	}
	public static <E> IterableWithSize<E> wrap(Iterable<E> iterable) {
		return iterable == null ? null : iterable instanceof IterableWithSize ? (IterableWithSize<E>) iterable : new IterableWithSize<>(iterable);
	}   
}
