package sam.io.serilizers;

import java.io.DataOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface ObjectSerializer<E> {
	public void write(DataOutputStream dos, E e) throws IOException;
}
