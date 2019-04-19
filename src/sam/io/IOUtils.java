package sam.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import sam.logging.Logger;
import sam.myutils.Checker;
import sam.myutils.ThrowException;

public final class IOUtils {
	private static final Logger LOGGER = Logger.getLogger(IOUtils.class);

	private IOUtils() { }

	private static final int DEAFAULT_BUFFER_SIZE = IOConstants.defaultBufferSize(); 

	private static void checkBuffer(byte[] buffer) {
		if(buffer != null && buffer.length == 0)
			ThrowException.illegalArgumentException("buffer.length == 0");
	}

	private static byte[] buffer(byte[] buffer, InputStream in) throws IOException {
		if(buffer == null) {
			int size = in.available() + 5;
			if(size < 20)
				size = 20;
			if(size > DEAFAULT_BUFFER_SIZE)
				size = DEAFAULT_BUFFER_SIZE;

			buffer = new byte[size];
			LOGGER.debug("new byte["+size+"], created");
		}
		return buffer;
	}

	public static long pipe(InputStream in, OutputStream out, byte[] buffer) throws IOException {
		Checker.requireNonNull("in, out, buffer", in, out);
		checkBuffer(buffer);
		buffer = buffer(buffer, in);

		long nread = 0L;//number of bytes read
		int n;

		while ((n = in.read(buffer)) > 0) {
			out.write(buffer, 0, n);
			nread += n;
		}
		return nread;
	}

	public static long pipe(InputStream in, Path path, byte[] buffer) throws IOException {
		Checker.requireNonNull("is path buffer", in, path);
		checkBuffer(buffer);

		try(OutputStream out = Files.newOutputStream(path)) {
			return pipe(in, out, buffer);
		}
	}
	public static long pipe(Path input, OutputStream out, byte[] buffer) throws IOException {
		Checker.requireNonNull("input out buffer", input, out);
		checkBuffer(buffer);

		try(InputStream is = Files.newInputStream(input)) {
			return pipe(is, out, buffer);
		}
	}
	public static long pipe(InputStream in, WritableByteChannel out, byte[] buffer) throws IOException {
		Checker.requireNonNull("input out buffer", in, out);
		buffer = buffer(buffer, in);

		int n = 0;
		long size = 0;

		while((n = in.read(buffer)) > 0) {
			out.write(ByteBuffer.wrap(buffer, 0, n));
			size += n;
		}
		return size;
	}

	public static int write(ByteBuffer buffer, OutputStream target, boolean flip) throws IOException {
		return write(buffer, target, flip, true);
	}
	public static int write(ByteBuffer buffer, OutputStream target, boolean flip, boolean clear) throws IOException {
		if(flip)
			buffer.flip();

		int n = buffer.remaining();
		if(n != 0) 
			target.write(buffer.array(), buffer.position(), n);

		if(clear)
			buffer.clear();
		else
			buffer.position(buffer.limit());
		
		return n;
	}

	public static int write(ByteBuffer buffer, WritableByteChannel channel, boolean flip) throws IOException {
		if(flip)
			buffer.flip();

		int n = buffer.remaining();

		while(buffer.hasRemaining())
			channel.write(buffer);

		buffer.clear();
		return n;
	}

	public static int write(ByteBuffer buffer, long pos, FileChannel channel, boolean flip) throws IOException {
		if(flip)
			buffer.flip();

		int n = 0;
		while(buffer.hasRemaining())
			n += channel.write(buffer, pos + n);

		buffer.clear();
		return n;
	}

	public static int read(ByteBuffer buffer, boolean clear, ReadableByteChannel source) throws IOException {
		return read(buffer, clear, source, true);
	}
	public static int read(ByteBuffer buffer, boolean clear, ReadableByteChannel source, boolean flip) throws IOException {
		if(clear)
			buffer.clear();

		int n = source.read(buffer);
		if(flip)
			buffer.flip();

		return n;
	}
	public static int read(ByteBuffer buffer, long pos, boolean clear, FileChannel source, boolean flip) throws IOException {
		if(clear)
			buffer.clear();

		int n = source.read(buffer, pos);
		if(flip)
			buffer.flip();

		return n;
	}
	public static int read(ByteBuffer buffer, long pos, int size, FileChannel source, boolean flip) throws IOException {
		buffer.limit(buffer.position() + Math.min(buffer.remaining(), size));
		int n = source.read(buffer, pos);
		if(flip)
			buffer.flip();

		return n;
	}
	public static int read(ByteBuffer buffer, InputStream is, boolean flip) throws IOException {
		if(!buffer.hasRemaining())
			throw new IOException("full buffer");

		int n = is.read(buffer.array(), buffer.position(), buffer.remaining());

		final int k = n;
		if(n < 0)
			n = 0;

		if(flip) {
			buffer.limit(buffer.position() + n);
			buffer.position(0);
		} else {
			buffer.position(buffer.position() + n);
		}
		return k;
	}
	public static void compactOrClear(ByteBuffer buffer) {
		if(buffer.hasRemaining())
			buffer.compact();
		else
			buffer.clear();
	}
	public static void setFilled(Buffer buffer) {
		buffer.position(buffer.capacity());
		buffer.limit(buffer.capacity());
	}

	public static void ensureCleared(Buffer buffer) throws IOException {
		if(buffer != null && (buffer.position() != 0 || buffer.limit() != buffer.capacity()))
			throw new IOException("uncleared buffer: buffer.position("+buffer.position()+") != 0 || defaultBuffer.limit("+buffer.limit()+") != defaultBuffer.capacity("+buffer.capacity()+")");
	}
}
