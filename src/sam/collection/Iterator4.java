package sam.collection;

import java.util.Iterator;
import java.util.function.Consumer;

public class Iterator4<E> implements Iterator2<E> {
	private final int size;
	private final Iterator<E> itr;
	
	@Override
	public int size() {
		return size;
	}
	public Iterator4(int size, Iterator<E> itr) {
		this.size = size;
		this.itr = itr;
	}
	public boolean hasNext() {
		return itr.hasNext();
	}
	public E next() {
		return itr.next();
	}
	public void remove() {
		itr.remove();
	}
	public void forEachRemaining(Consumer<? super E> action) {
		itr.forEachRemaining(action);
	}
}
