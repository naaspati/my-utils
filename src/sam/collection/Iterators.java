package sam.collection;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator.OfDouble;
import java.util.PrimitiveIterator.OfInt;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Iterators {

	public static OfDouble of(double[] values) {
		Objects.requireNonNull(values);

		return new OfDouble() {
			int n = 0;

			@Override
			public boolean hasNext() {
				return n < values.length;
			}
			@Override
			public double nextDouble() {
				if(!hasNext())
					throw new NoSuchElementException();

				return values[n++];
			}
		};
	}
	public static OfInt of(int[] values) {
		Objects.requireNonNull(values);

		return new OfInt() {
			int n = 0;

			@Override
			public boolean hasNext() {
				return n < values.length;
			}
			@Override
			public int nextInt() {
				if(!hasNext())
					throw new NoSuchElementException();

				return values[n++];
			}
		};
	}
	public static OfInt ofInt(char[] values) {
		Objects.requireNonNull(values);

		return new OfInt() {
			int n = 0;

			@Override
			public boolean hasNext() {
				return n < values.length;
			}
			@Override
			public int nextInt() {
				if(!hasNext())
					throw new NoSuchElementException();

				return values[n++];
			}
		};
	}
	
	public static Iterator<Character> of(char[] values) {
		Objects.requireNonNull(values);

		return new Iterator<Character>() {
			int n = 0;

			@Override
			public boolean hasNext() {
				return n < values.length;
			}
			@Override
			public Character next() {
				if(!hasNext())
					throw new NoSuchElementException();

				return values[n++];
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
		if(values.length == 0) 
			return empty();
		
		return new ArrayIterator<>(values);
	}
	public static <E, F> Iterator<F> map(Iterator<E> itr, Function<E, F> mapper) {
		return new MappedIterator<>(itr, mapper);
	}
	public static <E> Iterator<E> repeat(E e, int times){
		if(times < 0)
			throw new IllegalArgumentException("times cannot be negative: "+times);
		if(times == 0)
			return Iterators.empty();

		return new IndexGetterIterator<E>(times) {
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
	public static <E> Iterator<E> filtered(Iterator<E> itr, Predicate<E> filter) {
		return new FilteredIterator<>(itr, filter);
	} 
}
