package sam.collection;

import java.util.Iterator;

public interface IteratorWithSize<E> extends Iterator<E> {
	int size();
	
	@SuppressWarnings("rawtypes")
	static int size(Iterator itr) {
		return itr instanceof IteratorWithSize ? ((IteratorWithSize)itr).size() : -1;
	}
}