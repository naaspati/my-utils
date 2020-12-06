package sam.collection;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import sam.myutils.Checker;

public interface ArraysUtils {
	@SafeVarargs
	public static <E> E[] array(E...array) {
		return array;
	}
	
	public static <E> void forEach(E[] array, Consumer<E> action) {
		for (E e : array) 
			action.accept(e);
	}
	
	public static void forEach(int[] array, IntConsumer action) {
		for (int e : array) 
			action.accept(e);
	}
	
	public static void forEach(short[] array, IntConsumer action) {
		for (int e : array) 
			action.accept(e);
	}
	
	public static short[] mapToShort(int[] array) {
		short[] result = new short[array.length];
		int max = Short.MAX_VALUE;
		
		for (int i = 0; i < array.length; i++) {
			int n = array[i];
			if(n > max)
				throw new RuntimeException("out of bound");
			result[i] = (short)n;
		}
		return result;
	}
	
	public static int[] mapToInt(short[] array) {
		int[] result = new int[array.length];
		
		for (int i = 0; i < array.length; i++) 
			result[i] = array[i];
		
		return result;
	}
	
	public static short max(short[] array) {
		short max = array[0];
		for (short s : array) 
			max = s > max ? s : max;
		return max;
	}
	
	public static int max(int[] array) {
		int max = array[0];
		for (int s : array) 
			max = s > max ? s : max;
		return max;
	}
	
	public static short min(short[] array) {
		short min = array[0];
		for (short s : array) 
			min = s < min ? s : min;
		return min;
	}
	
	public static int min(int[] array) {
		int min = array[0];
		for (int s : array) 
			min = s < min ? s : min;
		return min;
	}
	
	public static void reverse(short[] array) {
		for (short i = 0; i < array.length/2; i++) {
			short temp = array[i]; 
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
	}
	
	public static void reverse(int[] array) {
		for (int i = 0; i < array.length/2; i++) {
			int temp = array[i]; 
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
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
	
	public static String toString(int[] array) {
		if(array.length == 0)
			return "()";
		StringBuilder sb = new StringBuilder(array.length * 2 + 2).append('(');
		for (int n : array) 
			sb.append(n).append(',');
		sb.setCharAt(sb.length() - 1, ')');
		return sb.toString();
	}
	
	public static String toString(short[] array) {
		if(array.length == 0)
			return "()";
		StringBuilder sb = new StringBuilder(array.length * 2 + 2).append('(');
		for (short n : array) 
			sb.append(n).append(',');
		sb.setCharAt(sb.length() - 1, ')');
		return sb.toString();
	}
	
	public static IntStream stream(short[] array) {
		return array.length == 0 ? IntStream.empty() : IntStream.range(0, array.length).map(i -> array[i]);
	}

}
