package sam.collection2;

import java.util.AbstractList;
import java.util.Arrays;

public class IntArrayWrappedList extends AbstractList<Integer> {
	public final int[] array;

	public IntArrayWrappedList(int[] array) {
		this.array = array;
	}

	@Override
	public Integer get(int index) {
		return array[index];
	}
	
	public int getInt(int index) {
		return array[index];
	}

	@Override
	public int size() {
		return array.length;
	}
	
	@Override public int indexOf(Object o) {
		if(o.getClass() != Integer.class)
			throw new IllegalArgumentException();
		return indexOf((int)o);
	}
	public int indexOf(int find) {
		for (int i = 0; i < array.length; i++) {
			if(array[i] == find)
				return i;
		}
		return -1;
	}
	
	@Override public int lastIndexOf(Object o) {
		if(o.getClass() != Integer.class)
			throw new IllegalArgumentException();
		return lastIndexOf((int)o);
	}
	public int lastIndexOf(int find) {
		for (int i = array.length - 1; i >= 0; i--) {
			if(array[i] == find)
				return i;
		}
		return -1;
	}
	@Override public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override public boolean equals(Object o) {
		if(o == null || o.getClass() != int[].class)
			return false;
		return Arrays.equals(this.array, (int[])o);
	}
	@Override public int hashCode() {
		return Arrays.hashCode(this.array);
	}
	@Override protected void removeRange(int fromIndex, int toIndex)  { throw new UnsupportedOperationException(); } 
	
}
