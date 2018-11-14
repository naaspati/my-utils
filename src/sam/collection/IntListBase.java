package sam.collection;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

/**
 * copy of ArrayList
 */
public abstract class IntListBase {
	private static final int[] DEFAULT_ARRAY = new int[0];

	int modCount;

	int[] data;
	int size;

	public IntListBase() {
		data = DEFAULT_ARRAY;
	}
	public IntListBase(int[] source) {
		init(Arrays.copyOf(source, source.length));
	}
	public IntListBase(int[] source, int from, int to) {
		init(Arrays.copyOfRange(source, from, to));
	}
	public IntListBase(int initialCapacity) {
		data = new int[initialCapacity];
		modified();
	}
	private void init(int[] data) {
		this.data = data;
		this.size = data.length;
		modified();
	}
	private void modified() {
		modCount++;
	}
	private void move(int srcPos, int destPos, int length) {
		System.arraycopy(data, srcPos, data, destPos, length);
	}
	public void trimToSize() {
		modified();
		data = Arrays.copyOf(data, size);
	}
	public void ensureCapacity(int minCapacity) {
		if(capacity() > minCapacity) return;
		modified();
		grow(minCapacity);
	}
	public int capacity() {
		return data.length;
	}
	private void grow(int minCapacity) {
		if(minCapacity < 0) throw new IllegalArgumentException(String.valueOf(minCapacity));

		modified();
		int capacity = capacity();
		if(capacity < 10)
			capacity = 10;

		while(capacity <= minCapacity) {
			capacity += capacity/2;
			if(capacity < 0)
				throw new IllegalStateException("capacity larger than > int.MAX_VALUE");
		}
		data = Arrays.copyOf(data, capacity);
	}
	public int size() {
		return size;
	}
	public boolean isEmpty() {
		return size == 0;
	}
	public boolean contains(int value) {
		return indexOf(value) >= 0;
	}
	public int indexOf(int value) {
		for (int i = 0; i < size; i++)
			if(data[i] == value) return i;
		return -1;
	}
	int lastIndexOf(int value) {
		for (int i = size - 1; i >= 0; i--) {
			if(data[i] == value) return i;
		}

		return -1;
	}
	public int[] toArray() {
		return Arrays.copyOfRange(data, 0, size);
	}
	public int get(int index) {
		checkIndex(index);
		return data[index];
	}
	private void checkIndex(int index) {
		if(index < 0 || index >= size)
			throw new IndexOutOfBoundsException(index + " out of bounds [0, "+size+")");
	}
	int set(int index, int element) {
		checkIndex(index);
		modified();
		return data[index] = element;
	}
	public boolean add(int value) {
		ensureCapacity(size+1);
		modified();
		data[size++] = value;
		return true;
	}
	void add(int index, int value) {
		checkIndex(index);
		size++;
		ensureCapacity(size);
		move(index, index+1, size - index);
		data[index] = value;
		modified();
	}
	int removeIndex(int index) {
		int value = get(index);
		modified();
		move(index+1, index, size - index);
		size--;
		return value;
	}
	public boolean remove(int value) {
		int index = indexOf(value);
		if(index < 0) return false;
		removeIndex(index);
		return true;
	}
	public void clear() {
		size = 0;
	}
	public boolean addAll(IntListBase list) {
		Objects.requireNonNull(list);
		if(list.size() == 0) return false;
		ensureCapacity(size+list.size());

		modified();
		System.arraycopy(list.data, 0, data, size, list.size());
		size += list.size();

		return true;

	}
	public boolean addAll(int... c) {
		Objects.requireNonNull(c);
		if(c.length == 0) return false;
		ensureCapacity(size+c.length);

		modified();
		System.arraycopy(c, 0, data, size, c.length);
		size += c.length;

		return true;
	}
	boolean addAllAtIndex(int index, int... c) {
		checkIndex(index);
		Objects.requireNonNull(c);
		if(c.length == 0) return false;

		modified();
		int len = c.length;
		int newsize = size + len;
		ensureCapacity(newsize);
		move(index, index + len, size - index);
		System.arraycopy(c, 0, data, index, len);
		size = newsize;
		return true;
	}

	public boolean removeAll(int... c) {
		Objects.requireNonNull(c);
		if(c.length == 0) return false;
		if(c.length == 1)
			return remove(c[0]);

		int[] copy = Arrays.copyOf(c, c.length);
		c = null;
		Arrays.sort(copy);
		return removeIf(i -> Arrays.binarySearch(copy, i) >= 0);
	}

	private void checkModified(int m) {
		if(modCount != m)
			throw new ConcurrentModificationException();		
	}

	/*
	 * TODO
	 * WORKING PERFECTLY, no intend to use
	 * 
	 * public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			int m = modCount;
			int index = 0;

			@Override
			public Integer next() {
				checkModified(m);
				return data[index++];
			}

			@Override
			public boolean hasNext() {
				checkModified(m);
				return index < size;
			}
		};
	}
	 */



	public int[] subList(int fromIndex, int toIndex) {
		checkIndex(fromIndex);
		if(fromIndex == toIndex)
			return new int[]{get(fromIndex)};

		if(fromIndex > toIndex)
			throw new IllegalArgumentException("fromIndex("+fromIndex+") > toIndex("+toIndex+")");
		if(toIndex > size)
			throw new IndexOutOfBoundsException("toIndex("+toIndex+") > size("+size+")");

		return Arrays.copyOfRange(data, fromIndex, toIndex);
	}
	public void forEach(IntConsumer action) {
		if(size == 0) return;

		int m = modCount;
		for (int i = 0; i < size; i++) {
			checkModified(m);
			action.accept(data[i]);
		}
	}
	public boolean removeIf(IntPredicate filter) {
		int oldsize = size;
		modified();
		int m = modCount;

		for (int i = 0; i < size; i++) {
			checkModified(m);
			if(filter.test(data[i])) {
				move(i+1, i, size - i);
				i--;
				size--;
			}
		}
		return size != oldsize;
	}


	void replaceAll(IntUnaryOperator operator) {
		int m = modCount;
		for (int i = 0; i < size; i++) {
			checkModified(m);
			data[i] = operator.applyAsInt(data[i]);
		}
	}
	@Override
	public String toString() {
		if(size == 0) return "[]";
		if(size == 1)  return "["+data[0]+"]";

		StringBuilder sb = new StringBuilder(size*3);
		sb.append('[');

		for (int i = 0; i < size; i++)
			sb.append(data[i]).append(',').append(' ');
		sb.setLength(sb.length() - 2);
		sb.append(']');
		return sb.toString();
	}
}
