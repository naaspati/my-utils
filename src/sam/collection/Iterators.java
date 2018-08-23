package sam.collection;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Iterators {

    public static Iterator<Double> of(double[] values) {
        Objects.requireNonNull(values);
        
        return new Iterator<Double>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < values.length;
            }
            @Override
            public Double next() {
                return values[index++];
            }
        };
    }
    public static Iterator<Integer> of(int[] values) {
        Objects.requireNonNull(values);
        
        return new Iterator<Integer>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < values.length;
            }
            @Override
            public Integer next() {
                return values[index++];
            }
        };
    }
    public static Iterator<Character> of(char[] values) {
        Objects.requireNonNull(values);
        
        return new Iterator<Character>() {
            int index = 0;
            @Override
            public boolean hasNext() {
                return index < values.length;
            }
            @Override
            public Character next() {
                return values[index++];
            }
        };
    }

    public static <E> Iterator<E> empty(){
        return new Iterator<E>() {
            @Override public boolean hasNext() { return false; }
            @Override public E next() { return null; }
        };
    }
    public static <E> Iterator<E> of(E[] values){
        Objects.requireNonNull(values);
        
        if(values.length == 0) return empty();
        
        return new Iterator<E>() {
            int n;
            @Override public boolean hasNext() { return n < values.length; }
            @Override public E next() { return values[n++]; }
        };
    }
    public static <E, F> Iterator<F> map(Iterator<E> itr, Function<E, F> mapper) {
        Objects.requireNonNull(itr);
        Objects.requireNonNull(mapper);
        
        return new Iterator<F>() {
            @Override public boolean hasNext() { return itr.hasNext(); }
            @Override public F next() { return mapper.apply(itr.next()); }
        };
    }
    
    public static <E> Iterator<E> repeat(E e, int times){
        if(times < 0)
            throw new IllegalArgumentException("times cannot be negative: "+times);
        if(times == 0)
            return Iterators.empty();
        return new Iterator<E>() {
            int n = 0;
            @Override
            public boolean hasNext() { return n < times; }
            @Override
            public E next() {
                n++;
                return e;
            }
        };
    }
	public static <E> Stream<E> stream(Iterator<E> iterator) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.IMMUTABLE), false);
	}
}
