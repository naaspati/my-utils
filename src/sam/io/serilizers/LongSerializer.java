package sam.io.serilizers;

import static java.lang.Long.BYTES;
import static java.nio.channels.Channels.newChannel;
import static java.nio.channels.FileChannel.open;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static sam.io.serilizers.Utils.getBuffer;
import static sam.io.serilizers.Utils.readInt;
import static sam.io.serilizers.Utils.writeInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Logger;

import sam.logging.MyLoggerFactory;

public interface LongSerializer {
	static final Logger LOGGER = MyLoggerFactory.logger(LongSerializer.class);

	public static void write(long value, Path path) throws IOException {
		try(WritableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, WRITE)) {
			write(value, c);
		}
	}
	public static void write(long value, WritableByteChannel c) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(BYTES);
		buffer.clear();
		buffer.putLong(value);
		Utils.write(buffer, c);
	} 
	public static void write(long value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public static long read( Path path) throws IOException {
		try(ReadableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, READ)) {
			return read(c);
		}
	}
	public static long read( ReadableByteChannel c) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(BYTES);
		buffer.clear();
		c.read(buffer);
		buffer.flip();
		return  buffer.getLong();
	} 
	public static long read( InputStream is) throws IOException {
		return read(newChannel(is));
	}
	public static void write(long[] value, Path path) throws IOException {
		write(value, path, null);
	}
	public static void write(long[] value, Path path, ByteBuffer buffer) throws IOException {
		try(WritableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, WRITE)) {
			write(value, c, buffer);
		}
	}
	public static void write(long[] value, WritableByteChannel c, ByteBuffer buffer) throws IOException {
		Objects.requireNonNull(value);

		if(value.length == 0) {
			writeInt(0, c);
			return;
		}

		buffer = getBuffer(buffer, value.length + 1, BYTES);
		int bytes = 0;

		try {
			buffer.putInt(value.length);

			int loops = 0;
			for (long v : value) {
				if(buffer.remaining() < BYTES) {
					loops++;
					bytes += Utils.write(buffer, c);
				}
				buffer.putLong(v);	
			}

			if(buffer.position() != 0) {
				loops++;
				bytes += Utils.write(buffer, c);
			}

			int loops2 = loops;
			int cap = buffer.capacity();
			int bytes2 = bytes;
			LOGGER.fine(() -> Utils.log("WRITE", "long[]", value.length, cap, loops2, bytes2));

		} finally {
			buffer.clear();
		}

	} 
	public static void write(long[] value, OutputStream os) throws IOException {
		write(value, newChannel(os), null);
	}
	public static long[] readArray( Path path) throws IOException {
		return readArray(path, null);
	}
	public static long[] readArray( Path path, ByteBuffer buffer) throws IOException {
		try(ReadableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, READ)) {
			return readArray(c, buffer);
		}
	}
	public static long[] readArray( ReadableByteChannel c, ByteBuffer buffer) throws IOException {
		final int size = readInt(c);

		if(size == 0) return new long[0];
		if(size == 1) return new long[] {read(c)};

		long[] array = new long[size];
		buffer = getBuffer(buffer, size, BYTES);
		int bytes = 4;

		try {
			int loops = 1;
			bytes += bytes(c.read(buffer));
			buffer.flip();
			
			for (int index = 0; index < array.length; index++) {
				if(buffer.remaining() < BYTES) {
					if(buffer.hasRemaining())
						buffer.compact();
					else
						buffer.clear();
				
					loops++;
					bytes += bytes(c.read(buffer));
					buffer.flip();
					if(buffer.remaining() < BYTES)
						throw new IOException("bad file" );
				}
				array[index] = buffer.getLong();
			}
			
			int loops2 = loops;
			int cap = buffer.capacity();
			int bytes2 = bytes;
			LOGGER.fine(() -> Utils.log("READ", "long[]", array.length, cap, loops2, bytes2));
			return array;
		} finally {
			buffer.clear();
		}
	}
	public static int bytes(int n) {
		return n < 0 ? 0 : n;
	}
	public static long[] readArray( InputStream is) throws IOException {
		return readArray(newChannel(is), null);
	}
}
