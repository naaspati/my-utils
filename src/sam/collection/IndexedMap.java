package sam.collection;

import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterator.SIZED;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class IndexedMap<E> implements Iterable<E> {
	private final E[] array0;
	private Map<Integer, E> map = Collections.emptyMap();
	private final ToIntFunction<E> indexOf;
	private final Comparator<E> comparator;
	private final int max, min;

	public IndexedMap(E[] array, ToIntFunction<E> indexOf) {
		this.array0 = Objects.requireNonNull(array);
		Objects.requireNonNull(indexOf);

		ToIntFunction<E> func = e -> {
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

		this.indexOf = indexOf;
	}

	private static final Object MARKER = new Object();

	public E get(final int index) {
		if(isInRange(index)) {
			int n = indexOf(index);
			if(n >= 0)
				return array0[n];
		}

		return map.get(index);
	}

	public int size() {
		return array0.length + map.size();
	}
	public boolean isEmpty() {
		return size() == 0;
	}

	private int indexOf(int index) {
		checkIndex(index);

		if(index == min) 
			return 0;
		if(index == max) 
			return array0.length - 1;

		if(index > min && index < max && index - min < array0.length) {
			E e = array0[index - min];
			if(indexOf.applyAsInt(e) == index) 
				return index - min;
		}

		Comparator<Object> comp = (s,t) -> Integer.compare(index(s, index), index(t, index));
		return Arrays.binarySearch((Object[])array0, MARKER, comp);
	}

	private int checkIndex(int index) {
		if(!isInRange(index))
			throw new IllegalArgumentException("index out of bounds: ["+0+","+max+"]");
		return index;
	}

	private boolean isInRange(int index) {
		return index >= min && index <= max;
	}

	@SuppressWarnings("unchecked")
	private int index(Object s, int index) {
		return s == MARKER ? index : indexOf.applyAsInt((E)s);
	}
	@Override
	public String toString() {
		return "IndexedMap [min-index=" + min + ", max-index=" + max + ", content=" + Arrays.toString(array0)+", extra: "+map+ "]";
	}

	public void put(E e) {
		Objects.requireNonNull(e);
		int index = indexOf.applyAsInt(e);

		if(isInRange(index)) {
			int n = indexOf(index);
			if(n >= 0) {
				array0[n] = e;
				return;
			}
		}
		if(map.isEmpty())
			map = new HashMap<>();

		map.put(index, e);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(!(obj instanceof IndexedMap))
			return false;
		IndexedMap map = (IndexedMap) obj;

		return Arrays.equals(map.array0, this.array0) && this.map.equals(map.map);
	}

	@Override
	public Iterator<E> iterator() {
		if(isEmpty())
			return Iterators.empty();
		else if(map.isEmpty())
			return Iterators.of(array0);
		else
			return Iterators.join(Iterators.of(array0), map.values().iterator());
	}
	@Override
	public void forEach(Consumer<? super E> action) {
		if(isEmpty())
			return;

		for (E e : array0) 
			action.accept(e);

		if(!map.isEmpty())
			map.values().forEach(action);
	}
	@Override
	public Spliterator<E> spliterator() {
		if(isEmpty())
			return Spliterators.emptySpliterator();
		else if(map.isEmpty())
			return Arrays.spliterator(array0);
		else
			return Spliterators.spliterator(Iterators.join(Iterators.of(array0), map.values().iterator()), size(), ORDERED | IMMUTABLE | SIZED | NONNULL);
	}
	public Stream<E> stream() {
		return StreamSupport.stream(spliterator(), false);
	}
}
