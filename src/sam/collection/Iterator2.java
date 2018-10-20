package sam.collection;

import java.util.Iterator;

public interface Iterator2<E> extends Iterator<E> {
	int size();
	
	@SuppressWarnings("rawtypes")
	static int size(Iterator itr) {
		return itr instanceof Iterator2 ? ((Iterator2)itr).size() : -1;
	}
}