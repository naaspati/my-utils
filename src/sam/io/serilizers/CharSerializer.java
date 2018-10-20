package sam.io.serilizers;

import static java.lang.Character.BYTES;
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

public interface CharSerializer {
	static final Logger LOGGER = MyLoggerFactory.logger(CharSerializer.class.getSimpleName());

	public static void write(char value, Path path) throws IOException {
		try(WritableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, WRITE)) {
			write(value, c);
		}
	}
	public static void write(char value, WritableByteChannel c) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(BYTES);
		buffer.putChar(value);
		Utils.write(buffer, c);
	} 
	public static void write(char value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public static char read( Path path) throws IOException {
		try(ReadableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, READ)) {
			return read(c);
		}
	}
	public static char read( ReadableByteChannel c) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(BYTES);
		c.read(buffer);
		buffer.flip();
		return buffer.getChar(); 
	} 
	public static char read( InputStream is) throws IOException {
		return read(newChannel(is));
	}
	public static void write(char[] value, Path path) throws IOException {
		try(WritableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, WRITE)) {
			write(value, c);
		}
	}
	public static void write(char[] value, WritableByteChannel c) throws IOException {
		Objects.requireNonNull(value);

		if(value.length == 0) {
			writeInt(0, c);
			return;
		}

		ByteBuffer buffer = getBuffer(value.length + 1, BYTES);
		writeInt(buffer, value.length, c);

		int n = 0, m = 0;
		final int ln = value.length;
		final int max = (buffer.capacity())/BYTES;
		int loops = 0;

		while(n < ln) {
			loops++;
			m = 0;
			while(m < max && n < ln) {
				buffer.putChar(value[n++]);
				m++;
			}
			Utils.write(buffer, c);
		}
		int loops2 = loops;
		LOGGER.fine(() -> "WRITE { char[].length:"+value.length+", ByteBuffer.capacity:"+buffer.capacity()+", loopCount:"+loops2);

	} 
	public static void write(char[] value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public static char[] readArray( Path path) throws IOException {
		try(ReadableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, READ)) {
			return readArray(c);
		}
	}
	public static char[] readArray( ReadableByteChannel c) throws IOException {
		final int size = readInt(c);

		if(size == 0) return new char[0];
		if(size == 1) return new char[] {read(c)};

		char[] array = new char[size];
		ByteBuffer buffer = getBuffer(size, BYTES);

		int n = 0;
		int loops = 0;

		while(n < size) {
			loops++;

			buffer.clear();
			c.read(buffer);
			buffer.flip();

			int m = 0;
			int max = buffer.remaining()/BYTES;

			while(m < max) {
				array[n++] = buffer.getChar();
				m++;
			}
		}
		int loops2 = loops;
		LOGGER.fine(() -> "READ { char[].length:"+array.length+", ByteBuffer.capacity:"+buffer.capacity()+", loopCount:"+loops2);

		return array;
	} 
	public static char[] readArray( InputStream is) throws IOException {
		return readArray(newChannel(is));
	}
}
