package sam.collection;

import java.util.Iterator;
import java.util.function.Function;

import sam.myutils.Checker;

public class MappedIterator<E,F> implements IteratorWithSize<F> {
	private final Iterator<E> iter;
	private final Function<E, F> mapper;

	public MappedIterator(Iterator<E> iter, Function<E, F> mapper) {
		Checker.requireNonNull("iter, mapper", iter, mapper);
		this.iter = iter;
		this.mapper = mapper;
	}

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}
	@Override
	public F next() {
		return mapper.apply(iter.next());
	}

	@Override
	public int size() {
		return IteratorWithSize.size(iter);
	}

}
