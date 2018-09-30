package sam.fileutils;

import java.io.DataOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface ObjectWriter<E> {
	public void write(DataOutputStream dos, E e) throws IOException;
}
