package sam.io.serilizers;

import static java.lang.Double.BYTES;
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
import sam.reference.WeakAndLazy;

public interface DoubleSerializer {
	Logger LOGGER = MyLoggerFactory.logger(DoubleSerializer.class);
	WeakAndLazy<ByteBuffer> wbuffer = new WeakAndLazy<>(() -> ByteBuffer.allocate(BYTES));

	public static void write(double value, Path path) throws IOException {
		try(WritableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, WRITE)) {
			write(value, c);
		}
	}
	public static void write(double value, WritableByteChannel c) throws IOException {
		ByteBuffer buffer = wbuffer.pop();
		buffer.clear();
		buffer.putDouble(value);
		Utils.write(buffer, c);
		wbuffer.set(buffer);
	} 
	public static void write(double value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public static double read( Path path) throws IOException {
		try(ReadableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, READ)) {
			return read(c);
		}
	}
	public static double read( ReadableByteChannel c) throws IOException {
		ByteBuffer buffer = wbuffer.pop();
		buffer.clear();
		c.read(buffer);
		buffer.flip();
		double d = buffer.getDouble();
		wbuffer.set(buffer);
		return d;
	} 
	public static double read( InputStream is) throws IOException {
		return read(newChannel(is));
	}
	public static void write(double[] value, Path path) throws IOException {
		try(WritableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, WRITE)) {
			write(value, c);
		}
	}
	public static void write(double[] value, WritableByteChannel c) throws IOException {
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
				buffer.putDouble(value[n++]);
				m++;
			}
			Utils.write(buffer, c);
		}

		int loops2 = loops;
		LOGGER.finer(() -> "WRITE { double[].length:"+value.length+", ByteBuffer.capacity:"+buffer.capacity()+", loopCount:"+loops2+"}");
	} 
	public static void write(double[] value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public static double[] readArray( Path path) throws IOException {
		try(ReadableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, READ)) {
			return readArray(c);
		}
	}
	public static double[] readArray( ReadableByteChannel c) throws IOException {
		final int size = readInt(c);

		if(size == 0) return new double[0];
		if(size == 1) return new double[] {read(c)};

		double[] array = new double[size];
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
				array[n++] = buffer.getDouble();
				m++;
			}
		}

		int loops2 = loops;
		LOGGER.finer(() -> "READ { double[].length:"+array.length+", ByteBuffer.capacity:"+buffer.capacity()+", loopCount:"+loops2+"}");
		return array;
	} 
	public static double[] readArray( InputStream is) throws IOException {
		return readArray(newChannel(is));
	}
}
