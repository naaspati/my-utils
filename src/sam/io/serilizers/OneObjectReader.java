package sam.io.serilizers;

import java.io.DataInputStream;
import java.io.IOException;

@FunctionalInterface
public interface OneObjectReader<E> {
	public E read(DataInputStream dis) throws IOException;
	
	public static final OneObjectReader< Integer> INT_READER = DataInputStream::readInt;
	public static final OneObjectReader< Long> LONG_READER = DataInputStream::readLong;
	
}
