package sam.collection;

import java.util.Iterator;

public class IterableImpl<T> implements Iterable<T> {
	private final Iterator<T> itr;

	public IterableImpl(Iterator<T> itr) {
		this.itr = itr;
	}
	
	@Override public Iterator<T> iterator() { return itr; }

}
