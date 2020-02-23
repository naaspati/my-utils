package sam.functions;

import java.io.IOException;
import java.util.concurrent.Callable;

@FunctionalInterface
public interface CallableWithIOException<E> extends Callable<E>  {
	E call() throws IOException;
}
