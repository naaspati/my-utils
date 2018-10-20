package sam.collection;

public abstract class Iterator3<E> implements Iterator2<E> {
	private int index = 0;
	private final int size;
	
	public abstract E at(int index);
	public Iterator3(int size) {
		this.size = size;
	}
	@Override
	public int size() {
		return size;
	}
	@Override
	public boolean hasNext() {
		return index < size;
	}
	@Override
	public E next() {
		return at(index++);
	}
}
