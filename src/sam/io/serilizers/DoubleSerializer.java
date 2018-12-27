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

public interface DoubleSerializer {
	Logger LOGGER = MyLoggerFactory.logger(DoubleSerializer.class);

	public static void write(double value, Path path) throws IOException {
		try(WritableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, WRITE)) {
			write(value, c);
		}
	}
	public static void write(double value, WritableByteChannel c) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(BYTES);
		buffer.clear();
		buffer.putDouble(value);
		Utils.write(buffer, c);
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
		ByteBuffer buffer = ByteBuffer.allocate(BYTES);
		buffer.clear();
		c.read(buffer);
		buffer.flip();
		return buffer.getDouble();
	} 
	public static double read( InputStream is) throws IOException {
		return read(newChannel(is));
	}
	public static void write(double[] value, Path path) throws IOException {
		write(value, path, null);
	}
	public static void write(double[] value, Path path, ByteBuffer buffer) throws IOException {
		try(WritableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, WRITE)) {
			write(value, c, buffer);
		}
	}
	public static void write(double[] value, WritableByteChannel c) throws IOException {
		write(value, c, null);
	}
	public static void write(double[] value, WritableByteChannel c, ByteBuffer buffer) throws IOException {
		Objects.requireNonNull(value);

		if(value.length == 0) {
			writeInt(0, c);
			return;
		}

		buffer = getBuffer(buffer, value.length + 1, BYTES);

		try {
			buffer.putInt(value.length);
			int loops = 0;
			int bytes = 0;
			
			for (double v : value) {
				if(buffer.remaining() < BYTES) {
					loops++;
					bytes += Utils.write(buffer, c);
				}
				buffer.putDouble(v);	
			}

			if(buffer.position() != 0) {
				loops++;
				bytes += Utils.write(buffer, c);
			}

			int loops2 = loops;
			int cap = buffer.capacity();
			int bytes2 = bytes;
			LOGGER.fine(() -> Utils.log("WRITE", "double[]", value.length, cap, loops2, bytes2));
		} finally {
			buffer.clear();
		}
	} 
	public static void write(double[] value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public static double[] readArray( Path path) throws IOException {
		return readArray(path, null);
	}
	public static double[] readArray( Path path, ByteBuffer buffer) throws IOException {
		try(ReadableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, READ)) {
			return readArray(c, buffer);
		}
	}
	public static double[] readArray(ReadableByteChannel c, ByteBuffer buffer) throws IOException {
		final int size = readInt(c);

		if(size == 0) return new double[0];
		if(size == 1) return new double[] {read(c)};

		double[] array = new double[size];
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
				array[index] = buffer.getDouble();
			}
			
			int loops2 = loops;
			int cap = buffer.capacity();
			int bytes2 = bytes;
			LOGGER.fine(() -> Utils.log("READ", "double[]", array.length, cap, loops2, bytes2));
			return array;
		} finally {
			buffer.clear();
		}
	} 
	public static int bytes(int n) {
		return n < 0 ? 0 : n;
	}
	public static double[] readArray( InputStream is) throws IOException {
		return readArray(newChannel(is), null);
	}
}
