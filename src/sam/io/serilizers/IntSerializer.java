package sam.io.serilizers;

import static java.lang.Integer.BYTES;
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

public interface IntSerializer {
	static final Logger LOGGER = MyLoggerFactory.logger(IntSerializer.class);

	public static void write(int value, Path path) throws IOException {
		try(WritableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, WRITE)) {
			write(value, c);
		}
	}
	public static void write(int value, WritableByteChannel c) throws IOException {
		writeInt(value, c);
	} 
	public static void write(int value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public static int read(Path path) throws IOException {
		try(ReadableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, READ)) {
			return read(c);
		}
	}
	public static int read(ReadableByteChannel c) throws IOException {
		return readInt(c); 
	} 
	public static int read(InputStream is) throws IOException {
		return read(newChannel(is));
	}
	public static void write(int[] value, Path path) throws IOException {
		write(value, path, null);
	}
	public static void write(int[] value, Path path, ByteBuffer buffer) throws IOException {
		try(WritableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, WRITE)) {
			write(value, c, buffer);
		}
	}
	public static void write(int[] value, WritableByteChannel c) throws IOException {
		write(value, c, (ByteBuffer)null);
	}
	public static void write(int[] value, WritableByteChannel c, ByteBuffer buffer) throws IOException {
		Objects.requireNonNull(value);
		Objects.requireNonNull(c);
		
		if(value.length == 0) {
			writeInt(0, c);
			return;
		}
		buffer = getBuffer(buffer, value.length + 1, BYTES);
		int bytes = 0;
		
		try {
			buffer.putInt(value.length);
			
			int loops = 0;
			for (int v : value) {
				if(buffer.remaining() < BYTES) {
					loops++;
					bytes += Utils.write(buffer, c);
				}
				buffer.putInt(v);	
			}
			
			if(buffer.position() != 0) {
				loops++;
				bytes += Utils.write(buffer, c);
			}
			
			int loops2 = loops;
			int cap = buffer.capacity();
			int bytes2 = bytes;
			LOGGER.fine(() -> Utils.log("WRITE", "int[]", value.length, cap, loops2, bytes2));
		} finally {
			buffer.clear();
		}
		
	}

	public static void write(int[] value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public static int[] readArray(Path path) throws IOException {
		return readArray(path, null);
	}
	public static int[] readArray(Path path, ByteBuffer buffer) throws IOException {
		try(ReadableByteChannel c = open(path, CREATE, TRUNCATE_EXISTING, READ)) {
			return readArray(c, buffer);
		}
	}
	public static int[] readArray( ReadableByteChannel c) throws IOException {
		return readArray(c, null);
	}
	public static int[] readArray( ReadableByteChannel c, ByteBuffer buffer) throws IOException {
		final int size = readInt(c);

		if(size == 0) return new int[0];
		if(size == 1) return new int[] {read(c)};

		int[] array = new int[size];
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
				array[index] = buffer.getInt();
			}
			
			int loops2 = loops;
			int cap = buffer.capacity();
			int bytes2 = bytes;
			LOGGER.fine(() -> Utils.log("READ", "int[]", array.length, cap, loops2, bytes2));
			return array;
		} finally {
			buffer.clear();
		}
	}
	
	public static int bytes(int n) {
		return n < 0 ? 0 : n;
	}
	
	public static int[] readArray(InputStream is) throws IOException {
		return readArray(newChannel(is));
	}
}
