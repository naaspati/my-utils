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
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.logging.Logger;

import sam.io.IOConstants;

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
		if(flip)
			buffer.flip();
		
		int n = 0;
		while(buffer.hasRemaining())
			n += channel.write(buffer);

		buffer.clear();
		return n;
	}
	static String log(String prefix, String arrayType, int length, int bufferCapacity, int loopsCount, int bytesCount) {
		return prefix+" { "+arrayType+".length:"+length+", bytes: "+bytesCount+", capacity:"+bufferCapacity+", loopCount:"+loopsCount+"}";
	}

	@FunctionalInterface
	static interface Putter {
		void put(ByteBuffer buffer, int index);
	}

	static void write_array(Object value, int length, WritableByteChannel c, ByteBuffer buffer, int BYTES, Logger LOGGER, Putter putter) throws IOException {
		Objects.requireNonNull(value);
		Objects.requireNonNull(c);

		if(length == 0) {
			writeInt(0, c);
			return;
		}
		buffer = getBuffer(buffer, length + 1, BYTES);
		int bytes = 0;

		try {
			buffer.putInt(length);
			int loops = 0;

			for (int i = 0; i < length; i++) {
				if(buffer.remaining() < BYTES) {
					loops++;
					bytes += write(buffer, c, true);
				}
				putter.put(buffer, i);
			}

			if(buffer.position() != 0) {
				loops++;
				bytes += write(buffer, c, true);
			}

			int loops2 = loops;
			int cap = buffer.capacity();
			int bytes2 = bytes;
			LOGGER.fine(() -> log("WRITE", value.getClass().getSimpleName(), length, cap, loops2, bytes2));
		} finally {
			buffer.clear();
		}
	}

	@FunctionalInterface
	static interface Setter<E> {
		void set(E e, ByteBuffer buffer, int index);
	}

	public static <E> E read_array(ReadableByteChannel c, ByteBuffer buffer, final int bytes_per_entity, Logger LOGGER, IntFunction<E> creater, Setter<E> setter) throws IOException {
		final int size = readInt(c);
		final E array = creater.apply(size);

		if(size == 0) return array;
		if(size == 1) {
			setter.set(array, read(bytes_per_entity, c), 0);
			return array;
		}

		buffer = getBuffer(buffer, size, bytes_per_entity);
		ByteBuffer b2 = buffer;
		int bytes = 4;

		try {
			int loops = 0;
			int n = 0;
			int remaining = size * bytes_per_entity;
			final int start = buffer.capacity()%bytes_per_entity;
			
			while(n < size) {
				int pos = start;
				loops++;
				
				if(buffer.remaining() > remaining)
					pos = buffer.capacity() - remaining;
				
				buffer.position(pos);
				
				while(buffer.hasRemaining())
					c.read(buffer);
				
				buffer.flip();
				buffer.position(pos);
				
				remaining -= buffer.remaining();
				bytes += buffer.remaining();
				
				while(buffer.hasRemaining())
					setter.set(array, buffer, n++);
				
				buffer.clear();
			}

			int loops2 = loops;
			int bytes2 = bytes + 4;
			E ar2 = array;
			LOGGER.fine(() -> log("READ", ar2.getClass().getSimpleName(), size, b2.capacity(), loops2, bytes2));
			return array;
		} finally {
			buffer.clear();
		}
	}

	static int bytes(int n) {
		return n < 0 ? 0 : n;
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
