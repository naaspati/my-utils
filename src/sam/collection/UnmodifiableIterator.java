package sam.collection;

import java.util.Iterator;

public class UnmodifiableIterator<E> implements Iterator<E> {
	private final Iterator<E> source;

	public UnmodifiableIterator(Iterator<E> source) {
		this.source = source;
	}

	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public E next() {
		return source.next();
	}

}
