package sam.collection;

import java.util.Arrays;

public class IntSet extends IntListBase {
	public IntSet() {
		super();
	}
	public IntSet(int initialCapacity) {
		super(initialCapacity);
	}
	public IntSet(int[] source, int from, int to) {
		super(source, from, to);
		sort();
	}
	public IntSet(int[] source) {
		super(source);
		sort();
	}
	private void sort() {
		Arrays.sort(data, 0, size);
	}
	@Override
	public int indexOf(int value) {
		return Arrays.binarySearch(data, 0, size, value);
	}
	@Override
	public boolean add(int value) {
		if(size == 0)
			return super.add(value);
		
		int n = indexOf(value);
		if(n >= 0) return false;
		n = n*-1 - 1;
		if(n >= size)
			super.add(value);
		else
			super.add(n, value);
		return true;
	}
	@Override
	public boolean remove(int value) {
		int n = indexOf(value);
		if(n < 0) return false;
		super.removeIndex(n);
		return true;
	}
	@Override
	public boolean addAll(IntListBase list) {
		boolean[] b = {false};
		list.forEach(i -> b[0] = this.add(i) || b[0]);
		return b[0];
	}
	@Override
	public boolean addAll(int... c) {
		boolean b = false;
		for (int i : c) 
			b = this.add(i) || b;
		return b;
	}
}
