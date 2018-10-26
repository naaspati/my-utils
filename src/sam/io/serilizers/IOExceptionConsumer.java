package sam.io.serilizers;

import java.io.IOException;

@FunctionalInterface
public interface IOExceptionConsumer<E> {
	public void accept(E e) throws IOException;
}
