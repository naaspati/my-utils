package sam.collection;

import java.util.Objects;

public class ArrayIterator<E> extends IndexGetterIterator<E> {
	private final E[] values;
	
	public ArrayIterator(E[] values) {
		super(0, values.length);
		this.values = Objects.requireNonNull(values);
	}

	@Override
	public E at(int index) {
		return values[index];
	}
}
