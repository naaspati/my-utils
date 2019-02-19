package sam.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

public class FilteredIterator<E> implements Iterator<E> {
	private final Iterator<E> itr;
	private final Predicate<E> filter;
	
	private E next;
	private boolean hasNext;

	public FilteredIterator(Iterator<E> itr, Predicate<E> filter) {
		this.itr = Objects.requireNonNull(itr);
		this.filter = filter;
		
		next0();
	}

	private void next0() {
		if(!itr.hasNext()) {
			hasNext = false;
			next = null;
			return;
		}
		while (itr.hasNext()) {
			E e = itr.next();
			if(filter.test(e)) {
				next = e;
				hasNext = true;
				return ;
			}
		}
		
		hasNext = false;
		next = null;
		return;
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public E next() {
		if(!hasNext)
			throw new NoSuchElementException();
		
		E e = next;
		next0();
		
		return e;
	}
}
