package sam.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface CollectionUtils {
    @SafeVarargs
    public static <E> E[] array(E...array) {
        return array;
    }
    @SafeVarargs
    public static <E> List<E> list(E...array) {
        List<E> list = new ArrayList<>();
        for (E e : list) list.add(e);
        return list;
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
}
