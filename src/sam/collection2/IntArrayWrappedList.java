package sam.collection2;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterator.OfInt;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class IntArrayWrappedList extends AbstractList<Integer> implements RandomAccess, Cloneable, java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private int[] array;

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

	@Override
	public int indexOf(Object o) {
		if (o.getClass() != Integer.class)
			throw new IllegalArgumentException();
		return indexOf((int) o);
	}

	public int indexOf(int find) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == find)
				return i;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		if (o.getClass() != Integer.class)
			throw new IllegalArgumentException();
		return lastIndexOf((int) o);
	}

	public int lastIndexOf(int find) {
		for (int i = array.length - 1; i >= 0; i--) {
			if (array[i] == find)
				return i;
		}
		return -1;
	}

	@Override
	public void replaceAll(UnaryOperator<Integer> operator) {
		replaceAll0(n -> operator.apply(n));
	}

	public void replaceAll0(IntUnaryOperator operator) {
		for (int i = 0; i < array.length; i++)
			array[i] = operator.applyAsInt(array[i]);
	}

	@Override
	public void sort(Comparator<? super Integer> c) {
		throw new UnsupportedOperationException("use sort() method");
	}

	public void sort() {
		Arrays.sort(this.array);
	}

	@Override
	public Spliterator<Integer> spliterator() {
		return spliterator0();
	}

	public OfInt spliterator0() {
		return Arrays.spliterator(array);
	}

	@Override
	public boolean removeIf(Predicate<? super Integer> filter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Stream<Integer> stream() {
		return stream0().boxed();
	}

	public IntStream stream0() {
		return Arrays.stream(array);
	}

	@Override
	public Stream<Integer> parallelStream() {
		return parallelStream0().boxed();
	}

	public IntStream parallelStream0() {
		return StreamSupport.intStream(spliterator0(), true);
	}

	@Override
	public void forEach(Consumer<? super Integer> action) {
		forEach0(action::accept);
	}

	public void forEach0(IntConsumer consumer) {
		for (int n : array)
			consumer.accept(n);
	}

	@Override
	public boolean add(Integer e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer set(int index, Integer element) {
		return set(index, element);
	}

	public int set0(int index, int element) {
		int n = array[index];
		array[index] = element;
		return n;
	}

	@Override
	public void add(int index, Integer element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends Integer> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object o) {
		return indexOf(o) >= 0;
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return Arrays.toString(array);
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(array);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof List))
			return false;
		if (o instanceof IntArrayWrappedList)
			return Arrays.equals(this.array, ((IntArrayWrappedList) o).array);
		return super.equals(o);
	}
	
	public Object clone() {
        try {
        	IntArrayWrappedList v = (IntArrayWrappedList) super.clone();
            v.array = Arrays.copyOf(array, array.length); 
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }
}
