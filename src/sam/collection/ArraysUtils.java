package sam.collection;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

public interface ArraysUtils {
	@SafeVarargs
    public static <E> E[] array(E...array) {
        return array;
    }
	
	public static int[] intRange(int start, int endExcluding) {
		int[] array = new int[endExcluding - start];
		for (int i = start; i < endExcluding; i++) {
			array[i] = i;
		}
		
		return array;
	}
	
    public static <E> void replace(E[] array, UnaryOperator<E> mapper) {
    	for (int i = 0; i < array.length; i++)
    		array[i] = mapper.apply(array[i]);
    }
    public static <E> E[] fill(E[] array, IntFunction<E> creater) {
    	for (int i = 0; i < array.length; i++)
    		array[i] = creater.apply(i);
    	
    	return array;
    } 
    public static <E,F> F[] map(E[] array, IntFunction<F[]> arrayMaker, Function<E, F> mapper) {
    	if(array == null)
    		return null;
    	
    	F[] fs = arrayMaker.apply(array.length);
    	
    	for (int i = 0; i < array.length; i++)
    		fs[i] = mapper.apply(array[i]);
    	
    	return fs;
    }
    public static <E,F> String toString(E[] es, Function<E, F> map){
    	if(es == null) return "null";
    	if(es.length == 0) return "[]";
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append('[');
    	
    	for (int i = 0; i < es.length - 1; i++)
    		sb.append(map.apply(es[i])).append(' ').append(',');
    	
    	sb.append(map.apply(es[es.length - 1])).append(']');
    	
    	return sb.toString();
    }
    
	
	
}
