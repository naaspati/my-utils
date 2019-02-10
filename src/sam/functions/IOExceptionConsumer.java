package sam.functions;

import java.io.IOException;

@FunctionalInterface
public interface IOExceptionConsumer<E> {
	public void accept(E e) throws IOException;
}
