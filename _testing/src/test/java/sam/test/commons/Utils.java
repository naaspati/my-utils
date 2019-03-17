package sam.test.commons;

import java.nio.ByteBuffer;
import java.util.Random;

public interface Utils {

	public static ByteBuffer buffer(int size, boolean fill) {
		ByteBuffer buffer = ByteBuffer.allocate(size);
		if(fill)
			fill(buffer.array());
		return buffer;
	}

	public static byte[] fill(byte[] array) {
		Random r = new Random();
		r.nextBytes(array);
		long sum = 0;
		
		for (byte b : array) 
			sum += b;
		
		System.out.println("buffer filled, sum "+sum);
		return array;
	}

	public static byte[] bytes(int size, boolean fill) {
		byte[] buffer = new byte[size];
		if(fill)
			fill(buffer);

		return buffer;
	}
}
