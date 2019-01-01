package sam.io.serilizers;

import static java.lang.Double.BYTES;
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

public interface DoubleSerializer {
	Logger LOGGER = MyLoggerFactory.logger(DoubleSerializer.class);

	public static void write(double value, Path path) throws IOException {
		Utils.write(putDouble(value), path);
	}
	public static ByteBuffer putDouble(double value) {
		ByteBuffer buffer = ByteBuffer.allocate(BYTES);
		buffer.clear();
		buffer.putDouble(value);
		buffer.flip();
		return buffer;
	}
	public static void write(double value, WritableByteChannel c) throws IOException {
		Utils.write(putDouble(value), c, false);
	} 
	public static void write(double value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public static double read( Path path) throws IOException {
		return  Utils.read(BYTES, path).getDouble();
	}
	public static double read( InputStream is) throws IOException {
		return Utils.read(BYTES, newChannel(is)).getDouble();
	}
	public static void write(double[] value, Path path) throws IOException {
		write(value, path, null);
	}
	public static void write(double[] value, Path path, ByteBuffer buffer) throws IOException {
		try(WritableByteChannel c = Utils.writable(path)) {
			write(value, c, buffer);
		}
	}
	public static void write(double[] value, WritableByteChannel c) throws IOException {
		write(value, c, null);
	}
	public static void write(double[] value, WritableByteChannel c, ByteBuffer buffer) throws IOException {
		Utils.write_array(value, value.length, c, buffer, BYTES, LOGGER, (b, index) -> b.putDouble(value[index]));
	} 
	public static void write(double[] value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public static double[] readArray( Path path) throws IOException {
		return readArray(path, null);
	}
	public static double[] readArray( Path path, ByteBuffer buffer) throws IOException {
		try(ReadableByteChannel c = Utils.readable(path)) {
			return readArray(c, buffer);
		}
	}
	public static double[] readArray(ReadableByteChannel c, ByteBuffer buffer) throws IOException {
		return Utils.read_array(c, buffer, BYTES, LOGGER, double[]::new, (array, buf, index) -> array[index] = buf.getDouble());
	} 
	public static double[] readArray( InputStream is) throws IOException {
		return readArray(newChannel(is), null);
	}
}
