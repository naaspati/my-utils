package sam.collection;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import java.util.logging.Logger;
import java.util.stream.Stream;

import sam.logging.MyLoggerFactory;

public final class IndexedMap<E> implements Iterable<E> {
	private static final Logger LOGGER = MyLoggerFactory.logger(IndexedMap.class);

	private final E[] array;
	private final ToIntFunction<E> indexOf;
	private final Comparator<E> comparator;
	private final int max, min;

	public IndexedMap(E[] array, ToIntFunction<E> indexOf) {
		this.array = Objects.requireNonNull(array);

		ToIntFunction<E> func = e -> {
			int n = indexOf.applyAsInt(e);
			if(n < 0)
				throw new IllegalArgumentException("index cannot be < 0: index: "+n+", of: "+e);
			return n;
		};
		this.comparator = (s1, s2) -> {
			int n1 = func.applyAsInt(s1);
			int n2 = func.applyAsInt(s2);
			if(n1 == n2 && s1 != s2) {
				LOGGER.warning("two different element with same index("+n1+") : "+s1+"@"+System.identityHashCode(s1)+", "+s2+"@"+System.identityHashCode(s2));
				return 0;
			}
			return Integer.compare(n1, n2);
		};
		Arrays.sort(array, comparator);

		this.max = func.applyAsInt(array[array.length - 1]);
		this.min = func.applyAsInt(array[0]);

		this.indexOf = e -> checkIndex(indexOf.applyAsInt(e));
	}

	private static final Object MARKER = new Object();

	public E get(final int index) {
		int n = indexOf(index);
		return n < 0 ? null : array[n]; 
	}
	
	public int size() {
		return array.length;
	}
	
	public int indexOf(E object) {
		return indexOf(indexOf.applyAsInt(object));
	}
	private int indexOf(int index) {
		checkIndex(index);

		if(index == min) 
			return 0;
		if(index == max) 
			return array.length - 1;

		if(index > min && index < max && index - min < array.length) {
			E e = array[index - min];
			if(indexOf.applyAsInt(e) == index) 
				return index - min;
		}

		Comparator<Object> comp = (s,t) -> Integer.compare(index(s, index), index(t, index));
		return Arrays.binarySearch((Object[])array, MARKER, comp);
	}

	private int checkIndex(int index) {
		if(index < min || index > max)
			throw new IllegalArgumentException("index out of bounds: ["+0+","+max+"]");
		return index;
	}

	@SuppressWarnings("unchecked")
	private int index(Object s, int index) {
		return s == MARKER ? index : indexOf.applyAsInt((E)s);
	}
	@Override
	public String toString() {
		return "IndexedMap [min-index=" + min + ", max-index=" + max + ", content=" + Arrays.toString(array) + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(array);
		result = prime * result + max;
		result = prime * result + min;
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		IndexedMap other = (IndexedMap) obj;
		if (max != other.max)
			return false;
		if (min != other.min)
			return false;
		if (!Arrays.equals(array, other.array))
			return false;
		return true;
	}

	@Override
	public Iterator<E> iterator() {
		return Iterators.of(array);
	}
	@Override
	public void forEach(Consumer<? super E> action) {
		for (E e : array) 
			action.accept(e);
	}
	@Override
	public Spliterator<E> spliterator() {
		return Arrays.spliterator(array);
	}
	public Stream<E> stream() {
		return Arrays.stream(array);
	}

}
