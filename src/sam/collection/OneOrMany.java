package sam.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;

/**
 * store single or multiple values
 * @author Sameer
 *
 * @param <E>
 */
public class OneOrMany<E> implements Iterable<E> {
	private List<E> list = Collections.emptyList();

	public void clear() {
		list = Collections.emptyList();
	}
	public boolean isEmpty() {
		return list.isEmpty();
	}
	public void add(E e) {
		if(list.isEmpty())
			list = Collections.singletonList(e);
		else {
			if(list.size() == 1)
				list = new ArrayList<>(list);
			list.add(e);
		}
	}
	public int size() {
		return list.size();
	}
	public E get(int index) {
		return list.get(index);
	}
	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}
	public void addAll(OneOrMany<E> n) {
		n.forEach(this::add);
	}
	public void addAll(List<E> n) {
		n.forEach(this::add);
	}
	public static <T> Collector<T, OneOrMany<T>, OneOrMany<T>> collector() {
		return Collector.of(OneOrMany::new, OneOrMany::add, (o, n) -> {o.addAll(n); return o;});
	}
	@Override
	public String toString() {
		return list.toString();
		//return list.getClass().getSimpleName()+"@"+list.toString();
	}
}
