package sam.io.serilizers;

import static java.nio.ByteBuffer.allocate;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

import sam.io.IOConstants;
import sam.io.IOUtils;

interface Utils {
	static final int DEFAULT_BUFFER_SIZE = IOConstants.defaultBufferSize();

	public static int readInt(ReadableByteChannel c) throws IOException {
		ByteBuffer buffer = allocate(Integer.BYTES);
		c.read(buffer);
		buffer.flip();
		int n = buffer.getInt();
		return n;
	}
	public static void writeInt(int value, WritableByteChannel c) throws IOException {
		writeInt(allocate(Integer.BYTES), value, c);
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
		return allocate(computBufferSize(length, bytes, maxsize));
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
		write(buffer, c, true);
	}
	public static int write(ByteBuffer buffer, WritableByteChannel channel, boolean flip) throws IOException {
		return IOUtils.write(buffer, channel, flip);
	}
	public static void write(ByteBuffer buffer, Path path) throws IOException {
		try(WritableByteChannel c = writable(path)) {
			write(buffer, c, false);
		}
	}
	public static ByteBuffer read(int BYTES, Path path) throws IOException {
		try(ReadableByteChannel c = readable(path)) {
			return read(BYTES, c);
		}
	}
	public static ByteBuffer read(int BYTES, ReadableByteChannel c) throws IOException {
		ByteBuffer b = allocate(BYTES);
		while(b.hasRemaining())
			c.read(b);

		b.flip();
		return b;
	}
	public static WritableByteChannel writable(Path path) throws IOException {
		return FileChannel.open(path, CREATE, TRUNCATE_EXISTING, WRITE);
	}
	public static ReadableByteChannel readable(Path path) throws IOException {
		return FileChannel.open(path, READ);
	}
}
