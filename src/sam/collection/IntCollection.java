package sam.collection;

import java.util.Collection;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

public interface IntCollection {
	Object toIntListBase();
	int get(int index);
	boolean add(int value);
	boolean remove(int value);
	void clear();
	boolean addAll(Collection<? extends Integer> list);
	boolean addAll(IntCollection list);
	boolean addAll(int... c);
	boolean removeAll(int... c);
	int[] subList(int fromIndex, int toIndex);
	void forEach(IntConsumer action);
	
	boolean removeIf(IntPredicate filter);
}
