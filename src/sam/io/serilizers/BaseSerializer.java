package sam.io.serilizers;

import static java.nio.channels.Channels.newChannel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.Objects;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;

public abstract class BaseSerializer<E> {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	Logger logger() {
		return LOGGER;
	}
	abstract void set(E array, ByteBuffer buffer, int index);
	abstract E newInstance(int size);
	abstract int length(E value);
	abstract int bytesPerEntity();
	abstract void appendToBuffer(ByteBuffer buffer, E value, int index);
	
	public void write(E value, Path path) throws IOException {
		write(value, path, 0, length(value));
	}
	public void write(E value, Path path, ByteBuffer buffer) throws IOException {
		write(value, path, buffer, 0, length(value));
	}
	public void write(E value, WritableByteChannel c) throws IOException {
		write(value, c, 0, length(value));
	}
	public void write(E value, WritableByteChannel c, ByteBuffer buffer) throws IOException {
		write(value, c, buffer, 0, length(value));
	}
	public void write(E value, OutputStream os) throws IOException {
		write(value, os, 0, length(value));
	}
	
	public void write(E value, Path path, int startInclusive, int endExclusive) throws IOException {
		write(value, path, null, startInclusive, endExclusive);
	}
	public void write(E value, Path path, ByteBuffer buffer, int startInclusive, int endExclusive) throws IOException {
		try(WritableByteChannel c = Utils.writable(path)) {
			write(value, c, buffer, startInclusive, endExclusive);
		}
	}
	public void write(E value, WritableByteChannel c, int startInclusive, int endExclusive) throws IOException {
		write(value, c, null, startInclusive, endExclusive);
	}
	public void write(E value, WritableByteChannel c, ByteBuffer buffer, int startInclusive, int endExclusive) throws IOException {
		write_array(value, c, buffer, startInclusive, endExclusive);
	} 
	public void write(E value, OutputStream os, int startInclusive, int endExclusive) throws IOException {
		write(value, newChannel(os), startInclusive, endExclusive);
	}
	public E readArray( Path path) throws IOException {
		return readArray(path, null);
	}
	public E readArray( Path path, ByteBuffer buffer) throws IOException {
		try(ReadableByteChannel c = Utils.readable(path)) {
			return readArray(c, buffer);
		}
	}
	public E readArray(ReadableByteChannel c) throws IOException {
		return read_array(c, null);
	}
	public E readArray(ReadableByteChannel c, ByteBuffer buffer) throws IOException {
		return read_array(c, buffer);
	} 
	public E readArray( InputStream is) throws IOException {
		return readArray(newChannel(is), null);
	}
	
	private void write_array(E value, WritableByteChannel c, ByteBuffer buffer, int startInclusive, int endExclusive) throws IOException {
		Objects.requireNonNull(value);
		Objects.requireNonNull(c);
		int length = endExclusive - startInclusive;

		if(length == 0) {
			Utils.writeInt(0, c);
			return;
		}
		
		final int BYTES = bytesPerEntity();
		
		buffer = Utils.getBuffer(buffer, length + 1, BYTES);
		int bytes = 0;

		try {
			buffer.putInt(length);
			int loops = 0;

			for (int index = startInclusive; index < endExclusive; index++) {
				if(buffer.remaining() < BYTES) {
					loops++;
					bytes += Utils.write(buffer, c, true);
				}
				appendToBuffer(buffer, value, index);
			}

			if(buffer.position() != 0) {
				loops++;
				bytes += Utils.write(buffer, c, true);
			}
			LOGGER.debug("WRITE {}.length:{}, bytes-read:{}, capacity:{}, loopCount:{}", value.getClass().getSimpleName(), length, bytes, buffer.capacity(), loops);
		} finally {
			buffer.clear();
		}
	}

	private E read_array(ReadableByteChannel c, ByteBuffer buffer) throws IOException {
		final int size = Utils.readInt(c);
		final E array = newInstance(size);

		if(size == 0) return array;
		if(size == 1) {
			set(array, Utils.read(bytesPerEntity(), c), 0);
			return array;
		}

		final int bytes_per_entity = bytesPerEntity();
		buffer = Utils.getBuffer(buffer, size, bytes_per_entity);
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
					set(array, buffer, n++);
				
				buffer.clear();
			}

			LOGGER.debug("READ {}.length:{}, bytes-read:{}, capacity:{}, loopCount:{}", array.getClass().getSimpleName(), size, bytes, buffer.capacity(), loops);
			return array;
		} finally {
			buffer.clear();
		}
	}
}
