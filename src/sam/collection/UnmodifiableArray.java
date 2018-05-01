package sam.collection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

public class UnmodifiableArray<E> implements Iterable<E> {
    private final E[] array;
    
    public UnmodifiableArray(E[] array) {
        this.array = Objects.requireNonNull(array);
    }
    @Override
    public Iterator<E> iterator() {
        return Iterators.of(array);
    }
    public int length() {
        return array.length;
    }
    public E get(int index) {
        return array[index];
    }
    public Stream<E> stream() {
        return Arrays.stream(array);
    }
}
