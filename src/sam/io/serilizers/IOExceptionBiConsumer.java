package sam.io.serilizers;

import java.io.IOException;

@FunctionalInterface
public interface IOExceptionBiConsumer<S,T> {
	public void accept(S s, T t) throws IOException;
}
