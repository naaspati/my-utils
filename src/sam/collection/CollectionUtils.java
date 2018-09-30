package sam.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface CollectionUtils {
    
    @SafeVarargs
    public static <E> ArrayList<E> list(E...array) {
        return new ArrayList<>(Arrays.asList(array));
    }
    @SafeVarargs
    public static <E, C extends Collection<E>> C collection(Supplier<C> supplier, E...array) {
        return Stream.of(array).collect(Collectors.toCollection(supplier));
    }
    public static void fill(Collection<Double> sink, double[] values) {
        for (double d : values)
            sink.add(d);
    }
    public static void fill(Collection<Integer> sink, int[] values) {
        for (int d : values)
            sink.add(d);
    }
    public static <E,F> List<F> collect(Collection<E> source, Function<E, F> mapper) {
    	if(Objects.requireNonNull(source).isEmpty())
    		return new ArrayList<>();
    	
    	List<F> list = new ArrayList<>();
    	
        for (E d : source)
            list.add(mapper.apply(d));
        
        return list;
    }
    public static <E,F,G> Map<F, G> collectToMap(Collection<E> source, Function<E, F> keyMapper, Function<E, G> valueMapper) {
    	if(Objects.requireNonNull(source).isEmpty())
    		return new HashMap<>();
    	
    	Map<F, G> list = new HashMap<>();
    	
        for (E d : source)
            list.put(keyMapper.apply(d), valueMapper.apply(d));
        
        return list;
    }
}
