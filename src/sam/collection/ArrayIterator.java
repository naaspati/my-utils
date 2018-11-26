package sam.collection;

public abstract class ArrayIterator<E> implements IteratorWithSize<E> {
	private int index = 0;
	private final int to;
	private final int size;
	
	public ArrayIterator(int from, int to) {
		this.index = from;
		this.to = to;
		this.size = to - from;
	}
	
	public abstract E at(int index);
	public ArrayIterator(int size) {
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
		return at(index++);
	}
}
