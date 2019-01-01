package sam.io.serilizers;

import static java.lang.Integer.BYTES;
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

public interface IntSerializer {
	Logger LOGGER = MyLoggerFactory.logger(IntSerializer.class);

	public static void write(int value, Path path) throws IOException {
		Utils.write(putInt(value), path);
	}
	public static ByteBuffer putInt(int value) {
		ByteBuffer buffer = ByteBuffer.allocate(BYTES);
		buffer.clear();
		buffer.putInt(value);
		buffer.flip();
		return buffer;
	}
	public static void write(int value, WritableByteChannel c) throws IOException {
		Utils.write(putInt(value), c, false);
	} 
	public static void write(int value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public static int read( Path path) throws IOException {
		return  Utils.read(BYTES, path).getInt();
	}
	public static int read( InputStream is) throws IOException {
		return Utils.read(BYTES, newChannel(is)).getInt();
	}
	public static void write(int[] value, Path path) throws IOException {
		write(value, path, null);
	}
	public static void write(int[] value, Path path, ByteBuffer buffer) throws IOException {
		try(WritableByteChannel c = Utils.writable(path)) {
			write(value, c, buffer);
		}
	}
	public static void write(int[] value, WritableByteChannel c) throws IOException {
		write(value, c, null);
	}
	public static void write(int[] value, WritableByteChannel c, ByteBuffer buffer) throws IOException {
		Utils.write_array(value, value.length, c, buffer, BYTES, LOGGER, (b, index) -> b.putInt(value[index]));
	} 
	public static void write(int[] value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public static int[] readArray( Path path) throws IOException {
		return readArray(path, null);
	}
	public static int[] readArray( Path path, ByteBuffer buffer) throws IOException {
		try(ReadableByteChannel c = Utils.readable(path)) {
			return readArray(c, buffer);
		}
	}
	public static int[] readArray(ReadableByteChannel c, ByteBuffer buffer) throws IOException {
		return Utils.read_array(c, buffer, BYTES, LOGGER, int[]::new, (array, buf, index) -> array[index] = buf.getInt());
	} 
	public static int[] readArray( InputStream is) throws IOException {
		return readArray(newChannel(is), null);
	}
}
