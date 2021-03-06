package sam.collection;

import java.util.NoSuchElementException;

public abstract class IndexGetterIterator<E> implements IteratorWithSize<E> {
	private int index = 0;
	private final int to;
	private final int size;
	
	public IndexGetterIterator(int from, int to) {
		this.index = from;
		this.to = to;
		this.size = to - from;
	}
	
	public abstract E at(int index);
	public IndexGetterIterator(int size) {
		this.to = size;
		this.size = size;
	}
	@Override
	public int size() {
		return size;
	}
	@Override
	public boolean hasNext() {
		return index < to;
	}
	@Override
	public E next() {
		if(!hasNext())
			throw new NoSuchElementException();
		return at(index++);
	}
	public void reset() {
		this.index = 0;
	}
}
