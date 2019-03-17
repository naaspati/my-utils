package sam.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import sam.myutils.Checker;

public interface CollectionUtils {

	@SafeVarargs
	public static <E> ArrayList<E> list(E...array) {
		return new ArrayList<>(Arrays.asList(array));
	}
	public static <K,V> Map<K,V> createMap(Object...values) {
		return createMap(new HashMap<>(), values);
	}
	@SuppressWarnings("unchecked")
	public static <K,V, M extends Map<K,V>> M createMap(M sink,  Object...values) {
		if(Checker.isEmpty(values))
    		return sink;
		
		if(values.length%2 != 0)
			throw new IllegalArgumentException("size of values is not even"); 
		
		for (int i = 0; i < values.length; i+=2)
			sink.put((K)values[i], (V)values[i+1]);
		return sink;
	}
	@SafeVarargs
	public static <E, C extends Collection<E>> C collection(Supplier<C> supplier, E...array) {
		C c = supplier.get();
		for (E e : array) 
			c.add(e);
		return c;
	}
	public static void fill(Collection<Double> sink, double[] values) {
		for (double d : values)
			sink.add(d);
	}
	public static void fill(Collection<Integer> sink, int[] values) {
		for (int d : values)
			sink.add(d);
	}
	public static <E,F> ArrayList<F> map(Iterable<E> source, Function<E, F> mapper) {
		return map(source, new ArrayList<>(), mapper);
	}
	public static <E, F, G extends Collection<F>> G map(Iterable<E> source, G into, Function<E, F> mapper) {
		for (E d : source)
			into.add(mapper.apply(d));

		return into;
	}
	public static <E,F,G> HashMap<F, G> map(Iterable<E> source, Function<E, F> keyMapper, Function<E, G> valueMapper) {
		return map(source, new HashMap<>(), keyMapper, valueMapper);
	}
	public static <E, F,G, M extends Map<F, G>> M map(Iterable<E> source, M into, Function<E, F> keyMapper, Function<E, G> valueMapper) {
		for (E d : source)
			into.put(keyMapper.apply(d), valueMapper.apply(d));

		return into;
	}
	public static <E> List<E> copyOf(List<E> source) {
		return copyOf(source, ArrayList::new);
	}
	public static <E> List<E> copyOf(List<E> source, Function<List<E>, List<E>> creater) {
		if(Checker.isEmpty(source))
			return Collections.emptyList();
		if(source.size() == 1)
			return Collections.singletonList(source.get(0));
		
		return creater.apply(source);
	}
	public static <E> void repeat(Collection<E> col, E value, int times) {
		if(times < 0)
			throw new IllegalArgumentException();
		Objects.requireNonNull(col);
		
		if(times == 0)
			return;
		
		for (int i = 0; i < times; i++)
			col.add(value);
	}
}
