package sam.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
}
