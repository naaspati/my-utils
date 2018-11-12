package sam.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class Iterable2<E> implements Iterable<E> {
	public static <T> Iterable2<T> wrap(Iterable<T> iter) {
		return new Iterable2<>(iter);
	}
	
	// private final Iterable<E> iterable;
	private final Iterator<E> iterator;
	private final int size;

	@SuppressWarnings("rawtypes")
	public Iterable2(Iterable<E> iterable) {
		Objects.requireNonNull(iterable);
		
		this.iterator = iterable.iterator();
		int n = Iterator2.size(iterator);
		
		if(n < 0 && iterable instanceof Collection)
			n = ((Collection)iterable).size();
		
		this.size = n;
	}
	public boolean hasNext() {
		return iterator.hasNext();
	}
	public E next() {
		return iterator.next();
	}
	public void remove() {
		iterator.remove();
	}
	@Override
	public Iterator<E> iterator() {
		return iterator;
	}
	/**
	 * if size is not determined, then -1 is returned otherwise the size
	 * @return
	 */
	public int size() {
		return size;
	}
}