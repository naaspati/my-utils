package sam.collection;

import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

public interface ArraysUtils {
	@SafeVarargs
    public static <E> E[] array(E...array) {
        return array;
    }
    public static <E,F> void replace(E[] array, UnaryOperator<E> mapper) {
    	for (int i = 0; i < array.length; i++)
    		array[i] = mapper.apply(array[i]);
    }
    public static <E> E[] fill(E[] array, IntFunction<E> creater) {
    	for (int i = 0; i < array.length; i++)
    		array[i] = creater.apply(i);
    	
    	return array;
    } 
	
	
}
