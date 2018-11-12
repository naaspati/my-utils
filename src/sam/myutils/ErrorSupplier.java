package sam.myutils;

@FunctionalInterface
public interface ErrorSupplier<E> {
	public E get() throws Exception;
}
