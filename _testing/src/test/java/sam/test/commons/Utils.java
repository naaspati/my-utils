package sam.test.commons;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.Random;

import sam.functions.IOExceptionConsumer;
import sam.io.WritableByteChannelCustom;

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
	
	public static String random_string(CharBuffer chars, Random r) {
		chars.clear();
		int limit = r.nextInt(chars.capacity() + 1);
		chars.limit(limit);
		
		for (int i = 0; i < limit; i++) 
			chars.put((char)r.nextInt(10000));
		
		chars.flip();
		return chars.toString();
	}
	
	public static byte[] toArray(ByteBuffer b) {
		if(b.position() == 0 && b.limit() == b.capacity())
			return b.array();
		
		return Arrays.copyOfRange(b.array(), b.position(), b.limit());
	}
	public static WritableByteChannel writeable(IOExceptionConsumer<ByteBuffer> consumer) {
		return new WritableByteChannel() {
			
			@Override
			public boolean isOpen() {
				return true;
			}
			
			@Override
			public void close() throws IOException {
			}
			
			@Override
			public int write(ByteBuffer src) throws IOException {
				int n = src.remaining();
				consumer.accept(src);
				return n;
			}
		};
	}
	public static WritableByteChannel writeable(IOExceptionConsumer<ByteBuffer> consumer, ByteBuffer buffer) {
		return new WritableByteChannelCustom() {
			@Override
			public ByteBuffer buffer() {
				return buffer;
			}
			@Override
			public boolean isOpen() {
				return true;
			}
			
			@Override
			public void close() throws IOException {
			}
			
			@Override
			public int write(ByteBuffer src) throws IOException {
				int n = src.remaining();
				consumer.accept(src);
				return n;
			}
		};
	}
}
