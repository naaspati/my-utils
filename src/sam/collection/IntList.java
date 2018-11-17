package sam.collection;

import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
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
	public boolean isEmpty() {
		return super.isEmpty();
	}
	@Override
	public boolean contains(int value) {
		return super.contains(value);
	}
	@Override
	public int indexOf(int value) {
		return super.indexOf(value);
	}
	@Override
	public int[] toArray() {
		return super.toArray();
	}
	@Override
	public int get(int index) {
		return super.get(index);
	}
	@Override
	public boolean add(int value) {
		return super.add(value);
	}
	@Override
	public boolean remove(int value) {
		return super.remove(value);
	}
	@Override
	public void clear() {
		super.clear();
	}
	@Override
	public boolean addAll(IntCollection list) {
		return super.addAll(list);
	}
	@Override
	public boolean addAll(int... c) {
		return super.addAll(c);
	}
	@Override
	public boolean removeAll(int... c) {
		return super.removeAll(c);
	}
	@Override
	public int[] subList(int fromIndex, int toIndex) {
		return super.subList(fromIndex, toIndex);
	}
	@Override
	public void forEach(IntConsumer action) {
		super.forEach(action);
	}
	@Override
	public boolean removeIf(IntPredicate filter) {
		return super.removeIf(filter);
	}
	@Override
	public int binarySearch(int value) {
		return super.binarySearch(value);
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
