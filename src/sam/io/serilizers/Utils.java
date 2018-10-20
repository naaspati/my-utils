package sam.io.serilizers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import static sam.io.BufferSize.*;

interface Utils {
	public static int readInt(ReadableByteChannel c) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
		c.read(buffer);
		buffer.flip();
		return buffer.getInt();
	}
	public static void writeInt(int value, WritableByteChannel c) throws IOException {
		writeInt(ByteBuffer.allocate(Integer.BYTES), value, c);
	}
	public static ByteBuffer getBuffer(int length, int bytes) {
		return getBuffer(length, bytes, DEFAULT_BUFFER_SIZE);
	}
	public static ByteBuffer getBuffer(int length, int bytes, int maxsize) {
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
	public static void write(ByteBuffer buffer, WritableByteChannel channel) throws IOException {
		buffer.flip();
        channel.write(buffer);
        buffer.clear();
	}

}
