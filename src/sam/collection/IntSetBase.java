package sam.collection;

import java.util.Objects;
import java.util.function.IntConsumer;

/**
 * this class will be implemented by IntMap, thus hiding
 * 
 * @author Sameer
 *
 */
class IntSetBase extends IntListBase {
	IntSetBase() {}
	
	IntSetBase(int initialCapacity) {
		super(initialCapacity);
	}
	IntSetBase(int[] source, int from, int to) {
		if(from > to)
			throw new IllegalArgumentException();
		if(from != to) {
			ensureCapacity(to - from);
			for (int i = from; i < to; i++) 
				add(source[i]);
		}
	}
	IntSetBase(int[] source) {
		this(source, 0, source.length);
	}
	@Override
	int indexOf(int value) {
		return binarySearch(value);
	}
	
	@Override
	boolean add(int value) {
		if(size() == 0 || value > max())
			return super.add(value);
		else if(value == max())
			return false;
		
		int n = indexOf(value);
		if(n >= 0) return false;
		n = n*-1 - 1;
		
		if(n >= size())
			super.add(value);
		else
			super.add(n, value);
		return true;
	}
	public int max() {
		return get(size() - 1);
	}
	public int min() {
		return get(0);
	}

	@Override
	boolean remove(int value) {
		int n = indexOf(value);
		if(n < 0) return false;
		super.removeIndex(n);
		return true;
	}
	@Override
	boolean addAll(IntCollection list) {
		boolean[] b = {false};
		IntListBase list2 = (IntListBase) list.toIntListBase();
		Objects.requireNonNull(list2);
		list2.forEach(i -> b[0] = this.add(i) || b[0]);
		return b[0];
	}
	@Override
	boolean addAll(int... c) {
		boolean b = false;
		for (int i : c) 
			b = this.add(i) || b;
		return b;
	}
	@Override
	void forEach(IntConsumer action) {
		super.forEach(action);
	}
}
