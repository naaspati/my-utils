package sam.collection;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Iterators {

	public static Iterator<Double> of(double[] values) {
		Objects.requireNonNull(values);
		return new ArrayIterator<Double>(values.length) {
			@Override
			public Double at(int index) {
				return values[index];
			}
		};
	}
	public static Iterator<Integer> of(int[] values) {
		Objects.requireNonNull(values);

		return new ArrayIterator<Integer>(values.length) {
			@Override
			public Integer at(int index) {
				return values[index];
			}
		};
	}
	public static Iterator<Character> of(char[] values) {
		Objects.requireNonNull(values);

		return new ArrayIterator<Character>(values.length) {
			@Override
			public Character at(int index) {
				return values[index];
			}
		};
	}

	
	public static <E> Iterator<E> empty(){
		return Collections.emptyIterator();
	}
	public static <E> Iterator<E> of(E[] values){
		return of(values, 0, values.length);
	}
	public static <E> Iterator<E> of(E[] values, int from, int to){
		Objects.requireNonNull(values);

		if(values.length == 0) return empty();

		return new ArrayIterator<E>(from, to) {

			@Override
			public E at(int index) {
				return values[index];
			}
		};
	}
	public static <E, F> Iterator<F> map(Iterator<E> itr, Function<E, F> mapper) {
		Objects.requireNonNull(itr);
		Objects.requireNonNull(mapper);

		return new IteratorWithSize<F>() {
			@Override public boolean hasNext() { return itr.hasNext(); }
			@Override public F next() { return mapper.apply(itr.next()); }
			@Override
			public int size() {
				return IteratorWithSize.size(itr);
			}

		};
	}
	public static <E> Iterator<E> repeat(E e, int times){
		if(times < 0)
			throw new IllegalArgumentException("times cannot be negative: "+times);
		if(times == 0)
			return Iterators.empty();

		return new ArrayIterator<E>(times) {
			@Override
			public E at(int index) {
				return e;
			}
		};
	}

	public static <E> Stream<E> stream(Iterator<E> iterator) {
		int size = IteratorWithSize.size(iterator);
		Spliterator<E> s;

		if(size >= 0)
			s = Spliterators.spliterator(iterator, size, Spliterator.IMMUTABLE);
		else
			s = Spliterators.spliteratorUnknownSize(iterator, Spliterator.IMMUTABLE); 

		return StreamSupport.stream(s, false);
	}

	@SafeVarargs
	public static <E> Iterator<E> join(Iterator<E>...iterators) {
		Objects.requireNonNull(iterators);
		if(iterators.length == 0)
			return empty();
		if(iterators.length == 1)
			return iterators[0];

		int n = 0;

		while(!iterators[n].hasNext() && n < iterators.length) {
			n++;
		}
		if(n >= iterators.length)
			return empty();
		
		int[] n1 = {n};

		return new Iterator<E>() {
			int index = n1[0];

			private Iterator<E> iter() {
				return iterators[index];
			}
			@Override
			public boolean hasNext() {
				return index < iterators.length && iter().hasNext();
			}

			@Override
			public E next() {
				if(index >= iterators.length)
					throw new NoSuchElementException();
				
				E current = iter().next();

				if(!iter().hasNext())
					index++;
				
				return current;
			}
		};
	}
}
