package sam.functions;

import java.io.IOException;

@FunctionalInterface
public interface IOExceptionSupplier<E> {
	public E get() throws IOException;
}
