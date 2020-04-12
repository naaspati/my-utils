package sam.collection;

import java.util.Iterator;

public class UnmodifiableIterator<E> implements Iterator<E> {
	private final Iterator<? extends E> source;

	public <F extends E> UnmodifiableIterator(Iterator<F> source) {
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
