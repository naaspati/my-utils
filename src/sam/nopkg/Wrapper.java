package sam.nopkg;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Wrapper {
	public static <E> Supplier<E> supplier(Supplier<E> e) {
		return e;
	}
	public static <E> Consumer<E> consumer(Consumer<E> e) {
		return e;
	}
}
