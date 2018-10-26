package sam.io.serilizers;

import java.io.DataOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface OneObjectWriter<E> {
	public void write(DataOutputStream dos, E e) throws IOException;
	
	public static final OneObjectWriter<Integer> INT_WRITER = (dos, e) -> dos.writeInt(e);
	public static final OneObjectWriter<Long> LONG_WRITER = (dos, e) -> dos.writeLong(e);
	
}
