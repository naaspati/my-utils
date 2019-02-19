package sam.collection;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Iterables {
	public static Iterable<Character> of(char[] values) {
		return of(Iterators.of(values));
	}
	public static Iterable<Integer> of(int[] values) {
		return of(Iterators.of(values));
	}
	public static <E> Iterable<E> of(Iterator<E> itr) {
		return new IterableImpl<>(itr);
	}
	public static Iterable<Double> of(double[] values) {
		return of(Iterators.of(values));
	}
	public static <E> Iterable<E> of(E[] values) {
		return of(Iterators.of(values));
	}
	public static <E> Iterable<E> of(E[] values, int from, int to) {
		return of(Iterators.of(values, from, to));
	}
	public static <E, F> Iterable<F> map(Iterable<E> itr, Function<E, F> mapper) {
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
	public static <E> Iterable<E> filtered(Iterable<E> itr, Predicate<E> filter) {
		return of(Iterators.filtered(itr.iterator(), filter));
	}
}
