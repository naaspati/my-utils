package sam.collection;

import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

public class IntSet extends IntSetBase {
	public IntSet() {}

	public IntSet(int initialCapacity) {
		super(initialCapacity);
	}
	public IntSet(int[] source, int from, int to) {
		super(source, from, to);
	}
	public IntSet(int[] source) {
		this(source, 0, source.length);
	}
	@Override
	public int indexOf(int value) {
		return super.indexOf(value);
	}
	@Override
	public 	boolean add(int value) {
		return super.add(value);
	}
	@Override
	public 	boolean remove(int value) {
		return super.remove(value);
	}
	@Override
	public 	boolean addAll(IntCollection list) {
		return super.addAll(list);
	}
	@Override
	public int get(int index) {
		return super.get(index);
	}
	@Override
	public 	boolean addAll(int... c) {
		return super.addAll(c);
	}
	@Override
	public 	void forEach(IntConsumer action) {
		super.forEach(action);
	}

	@Override
	public void trimToSize() {
		super.trimToSize();
	}

	@Override
	public void ensureCapacity(int minCapacity) {
		super.ensureCapacity(minCapacity);
	}

	@Override
	public int capacity() {
		return super.capacity();
	}

	@Override
	public int size() {
		return super.size();
	}

	@Override
	public 	boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public boolean contains(int value) {
		return super.contains(value);
	}

	@Override
	public int[] toArray() {
		return super.toArray();
	}

	@Override
	public void clear() {
		super.clear();
	}

	@Override
	public 	boolean removeAll(int... c) {
		return super.removeAll(c);
	}

	@Override
	public int[] subList(int fromIndex, int toIndex) {
		return super.subList(fromIndex, toIndex);
	}

	@Override
	public boolean removeIf(IntPredicate filter) {
		return super.removeIf(filter);
	}
	
}
