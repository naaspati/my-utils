package sam.collection;

import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterator.SIZED;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public class IndexedMapModifiable<E> extends IndexedMap<E> {
	private Map<Integer, E> map = Collections.emptyMap();

	public IndexedMapModifiable(E[] array, ToIntFunction<E> indexOf) {
		super(array, indexOf);
	}
	@Override
	public E get(int index) {
		if(!isInRange(index))
			return map.get(index);
		else
			return super.get(index);
	}
	@Override
	public int size() {
		return super.size() + map.size();
	}
	public void put(E e) {
		int index = indexOf.applyAsInt(e);

		if(isInRange(index)) {
			int n = indexOf(index);
			if(n >= 0) {
				array[n] = e;
				return;
			}
		}
		if(map.getClass() != HashMap.class)
			map = new HashMap<>();

		map.put(index, e);
	}
	@Override
	public Iterator<E> iterator() {
		if(isEmpty())
			return Iterators.empty();
		else if(map.isEmpty())
			return Iterators.of(array);
		else
			return Iterators.join(Iterators.of(array), map.values().iterator());
	}
	@Override
	public void forEach(Consumer<? super E> action) {
		if(isEmpty())
			return;

		for (E e : array) 
			action.accept(e);

		if(!map.isEmpty())
			map.values().forEach(action);
	}
	@Override
	public Spliterator<E> spliterator() {
		if(isEmpty())
			return Spliterators.emptySpliterator();
		else if(map.isEmpty())
			return Arrays.spliterator(array);
		else
			return Spliterators.spliterator(iterator(), size(), ORDERED | IMMUTABLE | SIZED | NONNULL);
	}
}
