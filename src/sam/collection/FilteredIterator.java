package sam.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

public class FilteredIterator<E> implements Iterator<E> {
	private static final Object END = new Object();
	
	private final Iterator<E> itr;
	private final Predicate<E> filter;
	
	private Object next;

	public FilteredIterator(Iterator<E> itr, Predicate<E> filter) {
		this.itr = Objects.requireNonNull(itr);
		this.filter = filter;
		
		next0();
	}

	private void next0() {
		if(!itr.hasNext()) {
			next = END;
			return;
		}
		while (itr.hasNext()) {
			E e = itr.next();
			if(filter.test(e)) {
				next = e;
				return;
			}
		}
		
		next = END;
	}

	@Override
	public boolean hasNext() {
		return next != END;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E next() {
		if(!hasNext())
			throw new NoSuchElementException();
		
		E e = (E) next;
		next0();
		
		return e;
	}
}
