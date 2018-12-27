package sam.io.serilizers;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import sam.io.IOConstants;


interface Utils {
	static final int DEFAULT_BUFFER_SIZE = IOConstants.defaultBufferSize();

	public static int readInt(ReadableByteChannel c) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
		c.read(buffer);
		buffer.flip();
		int n = buffer.getInt();
		return n;
	}
	public static void writeInt(int value, WritableByteChannel c) throws IOException {
		writeInt(ByteBuffer.allocate(Integer.BYTES), value, c);
	}
	public static ByteBuffer getBuffer(ByteBuffer buffer, int length, int bytes) {
		return getBuffer(buffer, length, bytes, DEFAULT_BUFFER_SIZE);
	}
	public static ByteBuffer getBuffer(ByteBuffer buffer, int length, int bytes, int maxsize) {
		if(buffer != null) {
			if(buffer.position() != 0 || buffer.limit() != buffer.capacity())
				throw new IllegalStateException(String.format("buffer.position(%s) != 0 || buffer.limit(%s) != buffer.capacity(%s)", buffer.position(),buffer.limit(),buffer.capacity()));
			if(buffer.capacity() < 512)
				throw new IllegalArgumentException("minimum buffer size is 512, but found: "+buffer.capacity());
			
			return buffer;
		}
		return ByteBuffer.allocate(computBufferSize(length, bytes, maxsize));
	}
	public static int computBufferSize(int length, int bytes, int maxsize) {
		int buffersize = length * bytes;
		buffersize = buffersize > maxsize ? maxsize : buffersize;
		if(buffersize < 50)
			buffersize = 50;

		if(buffersize%bytes != 0)
			buffersize = bytes*(buffersize/bytes + 1);
		return buffersize;
	}
	public static void writeInt(ByteBuffer buffer, int value, WritableByteChannel c) throws IOException {
		buffer.clear();
		buffer.putInt(value);
		write(buffer, c);
	}
	public static int write(ByteBuffer buffer, WritableByteChannel channel) throws IOException {
		buffer.flip();
		int n = 0;
		while(buffer.hasRemaining())
			n += channel.write(buffer);

		buffer.clear();
		return n;
	}
	static String log(String prefix, String arrayType, int length, int bufferCapacity, int loopsCount, int bytesCount) {
		return prefix+" { "+arrayType+".length:"+length+", bytes: "+bytesCount+", ByteBuffer.capacity:"+bufferCapacity+", loopCount:"+loopsCount+"}";
	}
}
