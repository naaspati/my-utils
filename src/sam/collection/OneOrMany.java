package sam.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collector;

/**
 * store single or multiple values
 * @author Sameer
 *
 * @param <E>
 */
public class OneOrMany<E> implements Iterable<E> {
	private int size;
	private E data;
	private List<E> list;
	private int mod;

	public void clear() {
		if(list != null)
			list.clear();
		
		data = null;
		size = 0;
	}
	public boolean isEmpty() {
		return size() == 0;
	}
	public void add(E e) {
		mod++;
		
		if(list != null) {
			list.add(e);
			size = list.size();
		} else if(size == 0) {
			data = e;
			size = 1;
		} else {
			if(list == null) {
				list = new ArrayList<>();
				list.add(data);
				data = null;
			}
			
			list.add(e);
			size = list.size();
		}
	}
	public int size() {
		return size;
	}
	public E get(int index) {
		if(index >= size)
			throw new IndexOutOfBoundsException();
		
		if(list != null)
			return list.get(index);
		
		if(index == 0)
			return data;
		else 
			throw new IllegalArgumentException();
	}
	@Override
	public Iterator<E> iterator() {
		if(list != null)
			return list.iterator();
		if(size == 0)
			Collections.emptyIterator();
		
		int m = mod;
		return new Iterator<E>() {
			boolean next = true;

			@Override
			public boolean hasNext() {
				if(m != mod)
					throw new ConcurrentModificationException();
				return next;
			}

			@Override
			public E next() {
				if(m != mod)
					throw new ConcurrentModificationException();
				if(!next)
					throw new NoSuchElementException();
				
				next = false;
				return data;
			}
		}; 
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
		if(list != null)
			return list.toString();
		if(size == 0)
			return "[]";
		
		return "["+data+"]";
	}
}
