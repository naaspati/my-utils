package sam.collection;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class IndexedMap<E> implements Iterable<E> {
	final E[] array;
	final ToIntFunction<E> indexOf;
	private final Comparator<E> comparator;
	private final int max, min;

	public IndexedMap(E[] array, ToIntFunction<E> indexOf) {
		this.array = Objects.requireNonNull(array);
		Objects.requireNonNull(indexOf);

		if(array.length == 0) {
			this.comparator = null;
			this.max = -1;
			this.min = -1;
			this.indexOf = null;
		} else {
			ToIntFunction<E> func = e -> {
				Objects.requireNonNull(e);
				int n = indexOf.applyAsInt(e);
				if(n < 0)
					throw new IllegalArgumentException("index cannot be < 0: index: "+n+", of: "+e);
				return n;
			};
			this.comparator = (s1, s2) -> {
				int n1 = func.applyAsInt(s1);
				int n2 = func.applyAsInt(s2);
				if(n1 == n2 && s1 != s2) 
					throw new IllegalArgumentException("two different element with same index("+n1+") : "+s1+"@"+System.identityHashCode(s1)+", "+s2+"@"+System.identityHashCode(s2));
				return Integer.compare(n1, n2);
			};

			Arrays.sort(array, comparator);

			this.max = func.applyAsInt(array[array.length - 1]);
			this.min = func.applyAsInt(array[0]);

			this.indexOf = func;	
		}
	}

	private static final Object MARKER = new Object();

	public E get(final int index) {
		if(isEmpty())
			return null;

		if(isInRange(index)) {
			int n = indexOf(index);
			if(n >= 0)
				return array[n];
		}

		return null;
	}

	public int size() {
		return array.length;
	}
	public boolean isEmpty() {
		return size() == 0;
	}

	int indexOf(int value) {
		if(isEmpty())
			return  -1;
		checkIndex(value);

		if(value == min) 
			return 0;
		if(value == max) 
			return array.length - 1;

		int n = value - min;

		if(n < 0 || n >= array.length) 
			return Arrays.binarySearch((Object[])array, MARKER, finder(value));

		E e = array[n];
		int actual = indexOf.applyAsInt(e);

		if(actual == value) 
			return n;

		Comparator<Object> comp = finder(value);

		if(actual > value) 
			return Arrays.binarySearch((Object[])array, 0, n, MARKER, comp);
		else 
			return Arrays.binarySearch((Object[])array, n, array.length, MARKER, comp);
	}

	private Comparator<Object> finder(int value) {
		return (s,t) -> Integer.compare(index(s, value), index(t, value));
	}

	private int checkIndex(int index) {
		if(!isInRange(index))
			throw new IllegalArgumentException("index out of bounds: ["+0+","+max+"]");
		return index;
	}

	boolean isInRange(int index) {
		return index >= min && index <= max;
	}

	@SuppressWarnings("unchecked")
	private int index(Object s, int index) {
		return s == MARKER ? index : indexOf.applyAsInt((E)s);
	}

	@Override
	public Iterator<E> iterator() {
		if(isEmpty())
			return Iterators.empty();
		else 
			return new ArrayIterator<>(array);
	}
	@Override
	public void forEach(Consumer<? super E> action) {
		if(isEmpty())
			return;

		for (E e : array) 
			action.accept(e);
	}
	@Override
	public Spliterator<E> spliterator() {
		if(isEmpty())
			return Spliterators.emptySpliterator();
		else
			return Arrays.spliterator(array);
	}
	public Stream<E> stream() {
		if(isEmpty())
			return Stream.empty();
		else
			return StreamSupport.stream(spliterator(), false);
	}
}
