package sam.collection2;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class LazyList<E> extends  AbstractList<E> {
    private List<E> list = Collections.emptyList();

    @Override
    public void forEach(Consumer<? super E> action) {
        list.forEach(action);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        switch (list.size()) {
            case 0: return a.length == 0 ? a : Arrays.copyOf(a, 0);
            case 1: {
                if(a.length != 1)
                    a = Arrays.copyOf(a, 1);
                a[0] = (T) list.get(0);
                return a;
            }
            default:
                return list.toArray(a);
        }
    }

    @Override
    public boolean add(E e) {
        if (!isArrayList()) {
            if (list.isEmpty()) {
                list = Collections.singletonList(e);
                return true;
            } else {
                list = new ArrayList<>(list);
            }
        }
        return list.add(e);
    }

    private boolean isArrayList() {
        return list.getClass() == ArrayList.class;
    }

    @Override
    public boolean remove(Object o) {
        if(list.isEmpty())
            return false;
        if(isArrayList())
            return list.remove(o);
        
        if(Objects.equals(list.get(0), o)) {
            list = Collections.emptyList();
            return true;
        }
        return  list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        c.forEach(this::add);
        return !c.isEmpty();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        int n = list.size();
        c.forEach(this::remove);
        return n != list.size();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        list.replaceAll(operator);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return list.removeIf(filter);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        list.sort(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean equals(Object o) {
        return list.equals(o);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public E set(int index, E element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        list.add(index, element);
    }

    @Override
    public Stream<E> stream() {
        return list.stream();
    }

    @Override
    public E remove(int index) {
        return list.remove(index);
    }

    @Override
    public Stream<E> parallelStream() {
        return list.parallelStream();
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<E> spliterator() {
        return list.spliterator();
    }

}
