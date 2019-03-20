package sam.collection;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import sam.myutils.Checker;

public interface ArraysUtils {
	@SafeVarargs
	public static <E> E[] array(E...array) {
		return array;
	}

	public static int[] intRange(int start, int endExcluding) {
		int[] array = new int[endExcluding - start];
		for (int i = 0; i < array.length; i++) 
			array[i] = start++;

		return array;
	}
	public static <E> E[] reverse(E[] array) {
		if(array == null || array.length < 2)
			return array;

		for (int i = 0; i < array.length/2; i++) {
			E temp = array[i]; 
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}

		return array;
	}

	@SuppressWarnings("unchecked")
	public static <E> E join(E...arrays) {
		Objects.requireNonNull(arrays);
		if(arrays.length == 0)
			throw new IllegalArgumentException("no arrays specified");

		if(arrays.length == 1)
			return arrays[0];

		int size = 0;
		for (Object es : arrays) 
			size += Array.getLength(es);

		if(size == 0)
			return arrays[0];

		E array =  (E) Array.newInstance(arrays[0].getClass().getComponentType(), size);

		int n = 0;
		for (Object es : arrays) {
			int len = Array.getLength(es);
			System.arraycopy(es, 0, array, n, len);
			n += len;
		}
		return array;
	}
	public static <E> void replaceAll(E[] array, UnaryOperator<E> mapper) {
		for (int i = 0; i < array.length; i++)
			array[i] = mapper.apply(array[i]);
	}
	public static <E> E[] fill(E[] array, IntFunction<E> creater) {
		for (int i = 0; i < array.length; i++)
			array[i] = creater.apply(i);

		return array;
	} 
	public static <E,F> F[] map(Iterator<E> source, F[] target, Function<E, F> mapper) {
		Objects.requireNonNull(source);
		if(Checker.isEmpty(target))
			return target;
		
		int n = 0;
		while (n < target.length && source.hasNext())
			target[n++] = mapper.apply(source.next());
		
		return target;
	}
	public static <E,F> F[] map(E[] source, F[] target, Function<E, F> mapper) {
		Objects.requireNonNull(target);
		
		if(Checker.isEmpty(source))
			return target;

		int size = Math.min(source.length, target.length);
		if(size == 0)
			return target;
					
		for (int i = 0; i < size; i++)
			target[i] = mapper.apply(source[i]);

		return target;
	}
	@SafeVarargs
	public static <E,F> String toString(Function<E, F> map, E...es){
		if(es == null) return "null";
		if(es.length == 0) return "[]";

		StringBuilder sb = new StringBuilder();
		sb.append('[');

		for (int i = 0; i < es.length - 1; i++)
			sb.append(map.apply(es[i])).append(' ').append(',');

		sb.append(map.apply(es[es.length - 1])).append(']');

		return sb.toString();
	}
	public static <E> E[] removeIf(E[] array, Predicate<E> filter) {
		int n = 0;
		for (E e : array) {
			if(!filter.test(e))
				array[n++] = e;
		}
		return n == array.length ? array : Arrays.copyOf(array, n);
	}

}
