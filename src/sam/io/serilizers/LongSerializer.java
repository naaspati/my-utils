package sam.io.serilizers;

import static java.lang.Long.BYTES;
import static java.nio.channels.Channels.newChannel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.logging.Logger;

import sam.logging.MyLoggerFactory;

public interface LongSerializer {
	Logger LOGGER = MyLoggerFactory.logger(LongSerializer.class);

	public static void write(long value, Path path) throws IOException {
		Utils.write(putLong(value), path);
	}
	public static ByteBuffer putLong(long value) {
		ByteBuffer buffer = ByteBuffer.allocate(BYTES);
		buffer.clear();
		buffer.putLong(value);
		buffer.flip();
		return buffer;
	}
	public static void write(long value, WritableByteChannel c) throws IOException {
		Utils.write(putLong(value), c, false);
	} 
	public static void write(long value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public static long read( Path path) throws IOException {
		return  Utils.read(BYTES, path).getLong();
	}
	public static long read( InputStream is) throws IOException {
		return Utils.read(BYTES, newChannel(is)).getLong();
	}
	public static void write(long[] value, Path path) throws IOException {
		write(value, path, null);
	}
	public static void write(long[] value, Path path, ByteBuffer buffer) throws IOException {
		try(WritableByteChannel c = Utils.writable(path)) {
			write(value, c, buffer);
		}
	}
	public static void write(long[] value, WritableByteChannel c) throws IOException {
		write(value, c, null);
	}
	public static void write(long[] value, WritableByteChannel c, ByteBuffer buffer) throws IOException {
		Utils.write_array(value, value.length, c, buffer, BYTES, LOGGER, (b, index) -> b.putLong(value[index]));
	} 
	public static void write(long[] value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public static long[] readArray( Path path) throws IOException {
		return readArray(path, null);
	}
	public static long[] readArray( Path path, ByteBuffer buffer) throws IOException {
		try(ReadableByteChannel c = Utils.readable(path)) {
			return readArray(c, buffer);
		}
	}
	public static long[] readArray(ReadableByteChannel c, ByteBuffer buffer) throws IOException {
		return Utils.read_array(c, buffer, BYTES, LOGGER, long[]::new, (array, buf, index) -> array[index] = buf.getLong());
	} 
	public static long[] readArray( InputStream is) throws IOException {
		return readArray(newChannel(is), null);
	}
}
