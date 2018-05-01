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
    private boolean singleValued = true;
    private E value;
    private List<E> values;

    public void clear() {
        singleValued = true;
        value = null;
        values = null;
    }
    public boolean isEmpty() {
        return value == null && values == null;
    }
    public boolean isSingleValued() {
        return singleValued;
    }
    public E getValue() {
        if(!singleValued)
            throw new IllegalStateException("not a singleValued");
        return value;
    }
    public List<E> getValues() {
        if(singleValued)
            throw new IllegalStateException("is a singleValued");
        return values;
    }
    public void add(E e) {
        if(singleValued && value == null)
            value = e;
        else {
            singleValued = false;
            if(values == null) {
                values = new ArrayList<>(2);
                values.add(value);
                value = null;
            }
            values.add(e);
        }
    }
    @Override
    public Iterator<E> iterator() {
        if(isEmpty())
            return Iterators.empty();

        if(!singleValued)
            return values.iterator();
        
        return new Iterator<E>() {
            boolean has = true;
            @Override
            public boolean hasNext() {
                return has;
            }
            @Override
            public E next() {
                has = false;
                return value;
            }
        };

    }
}
