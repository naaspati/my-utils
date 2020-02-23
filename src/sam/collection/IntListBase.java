package sam.collection;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;

abstract class IntListBase implements IntCollection {
	static final int[] DEFAULT_ARRAY = new int[0];
	private static final Logger LOGGER = LoggerFactory.getLogger(IntListBase.class);
	private static final boolean DEBUG = LOGGER.isDebugEnabled();

	int modCount;

	private int[] data = DEFAULT_ARRAY;
	private int size;

	IntListBase() {
		data = DEFAULT_ARRAY;
	}
	IntListBase(int[] source) {
		init(Arrays.copyOf(source, source.length));
	}
	IntListBase(int[] source, int from, int to) {
		if(from > to)
			throw new IllegalArgumentException();
		if(from == to)
			setData(DEFAULT_ARRAY);
		else
			init(Arrays.copyOfRange(source, from, to));
	}
	private void setData(int[] data) {
		this.data = data;
		modified();
	}
	IntListBase(int initialCapacity) {
		setData(new int[initialCapacity]);
	}
	protected void init(int[] data) {
		init(data, data.length);
	}
	protected void init(int[] data, int size) {
		setData(data);
		this.size = size;
	}
	private void modified() {
		modCount++;
	}
	private void move(int srcPos, int destPos, int length) {
		System.arraycopy(data, srcPos, data, destPos, length);
		afterMove(srcPos, destPos, length);
		if(DEBUG)
			LOGGER.debug("move: srcPos: {}, destPos: {}, length: {}", srcPos, destPos, length);
	}
	private void move(int srcPos, int destPos) {
		move(srcPos, destPos, size - srcPos);
	}
	void afterMove(int srcPos, int destPos, int length) {
	}
	void trimToSize() {
		setData(size == 0 ? DEFAULT_ARRAY : Arrays.copyOf(data, size));
		afterDataResize();
	}
	void ensureCapacity(int minCapacity) {
		if(capacity() > minCapacity) return;
		grow(minCapacity + 1);
		if(capacity() <= minCapacity) throw new IllegalStateException("bad grow() implementation"); 
	}
	int capacity() {
		return data.length;
	}
	protected void grow(int minCapacity) {
		if(minCapacity < 0) throw new IllegalArgumentException(String.valueOf(minCapacity));

		modified();
		int capacity = capacity();
		if(capacity < 10)
			capacity = 10;

		while(capacity <= minCapacity) {
			capacity += capacity/2;

			if(capacity < 0)
				throw new IllegalArgumentException("int negative size");
			if(capacity > Integer.MAX_VALUE)
				throw new IllegalStateException("capacity larger than > int.MAX_VALUE");
		}
		if(DEBUG)
			LOGGER.debug("data resize {} -> {}", data.length, capacity);

		data = Arrays.copyOf(data, capacity);
		afterDataResize();
	}
	protected void afterDataResize() { }

	int size() {
		return size;
	}
	boolean isEmpty() {
		return size == 0;
	}
	boolean contains(int value) {
		return indexOf(value) >= 0;
	}
	int indexOf(int value) {
		if(size == 0) return -1;

		for (int i = 0; i < size; i++)
			if(data[i] == value) return i;

		return -1;
	}
	int lastIndexOf(int value) {
		for (int i = size - 1; i >= 0; i--) 
			if(data[i] == value) return i;

		return -1;
	}
	int[] toArray() {
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
		move(index, index+1);
		data[index] = value;
		modified();
	}
	int removeIndex(int index) {
		int value = get(index);
		modified();
		if(index != size - 1)
			move(index+1, index);
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
	@Override
	public boolean addAll(Collection<? extends Integer> list) {
		if(list.size() == 0) return false;
		ensureCapacity(size+list.size());
		modified();
		for (Integer n : list) 
			data[size++] = n;
		return true;
	}
	public boolean addAll(IntCollection list) {
		IntListBase list2 = (IntListBase) list.toIntListBase();
		Objects.requireNonNull(list2);

		if(list2.size() == 0) return false;
		ensureCapacity(size+list2.size());
		modified();
		System.arraycopy(list2.data, 0, data, size, list2.size());
		size += list2.size();

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
		move(index, index + len);
		System.arraycopy(c, 0, data, index, len);
		size = newsize;
		return true;
	}
	public boolean removeAll(int... c) {
		Objects.requireNonNull(c);
		if(c.length == 0) return false;

		if(c.length == 1) {
			int n = c[0];
			return removeIf(k -> k == n);
		}

		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;

		for (int j : c) {
			max = Math.max(max, j);
			min = Math.min(min, j);
		}

		IntPredicate filter;
		int size = max - min; 
		
		if(size >= 0 && (size < 100 || size < 4 * 8 * c.length)) { // bytes_per_int * bits_per_byte * c.length
			BitSet set = new BitSet(size);
			for (int j : c)
				set.set(j - min);

			int min2 = min;
			int max2 = max;

			filter = i -> {
				if(i == min2 || i == max2)
					return true;
				
				return i > min2 && i < max2 ? set.get(i - min2) : false;
			};
		} else {
			int[] copy = Arrays.copyOf(c, c.length);
			c = null;
			Arrays.sort(copy);
			filter = i -> Arrays.binarySearch(copy, i) >= 0;
		}

		return removeIf(filter);
	}
	private void checkModified(int m) {
		if(modCount != m)
			throw new ConcurrentModificationException();		
	}

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
	public boolean removeIf(IntPredicate filter) {
		modified();
		int m = modCount;

		int n = 0;
		for (int i = 0; i < size; i++) {
			checkModified(m);
			if(!filter.test(data[i]))
				data[n++] = data[i];
		}
		boolean b = size != n;
		size = n;

		return b;
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
	int binarySearch(int value) {
		if(size == 0) return -1;
		if(size == 1) return data[0] == value ? 0 : -1;
		return Arrays.binarySearch(data, 0, size, value);
	}
	@Override
	public Object toIntListBase() {
		return this;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		IntListBase other = (IntListBase) obj;
		if (data == other.data) return true;
		if(size != other.size ) return false;

		for (int i = 0; i < size; i++) {
			if(data[i] != other.data[i])
				return false;
		}
		return true;
	}

	@Override
	public void forEach(IntConsumer action) {
		if(size == 0) 
			return;

		int m = modCount;
		for (int i = 0; i < size; i++) {
			checkModified(m);
			action.accept(data[i]);
		}
	}

	public OfInt iterator() {
		return new OfInt() {
			int m = modCount;
			int n = 0;

			@Override
			public boolean hasNext() {
				checkModified(m);
				return n < size;
			}

			@Override
			public int nextInt() {
				checkModified(m);
				return data[n++];
			}
		};
	}
}
