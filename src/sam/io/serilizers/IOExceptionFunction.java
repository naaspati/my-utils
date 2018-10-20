package sam.io.serilizers;

import java.io.IOException;

@FunctionalInterface
public interface IOExceptionFunction<T, R> {
	public R apply(T t) throws IOException;
}
