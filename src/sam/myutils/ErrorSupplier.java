package sam.myutils;

@FunctionalInterface
public interface ErrorSupplier<T> {
	public T get() throws Exception;
}
