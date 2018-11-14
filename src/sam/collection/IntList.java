package sam.collection;

import java.util.function.IntUnaryOperator;

public class IntList extends IntListBase {
	public IntList() {
		super();
	}
	public IntList(int initialCapacity) {
		super(initialCapacity);
	}
	public IntList(int[] source, int from, int to) {
		super(source, from, to);
	}
	public IntList(int[] source) {
		super(source);
	}
	
	@Override
	public void add(int index, int value) {
		super.add(index, value);
	}
	@Override
	public int lastIndexOf(int value) {
		return super.lastIndexOf(value);
	}
	@Override
	public int removeIndex(int index) {
		return super.removeIndex(index);
	}
	@Override
	public int set(int index, int element) {
		return super.set(index, element);
	}
	@Override
	public boolean addAllAtIndex(int index, int... c) {
		return super.addAllAtIndex(index, c);
	}
	@Override
	public void replaceAll(IntUnaryOperator operator) {
		super.replaceAll(operator);
	}
	

}
