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
	public static <E> Iterable2<E> wrap(Iterable<E> iterable) {
		return iterable == null ? null : iterable instanceof Iterable2 ? (Iterable2<E>) iterable : new Iterable2<>(iterable);
	}   
}
