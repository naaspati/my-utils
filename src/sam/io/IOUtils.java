package sam.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import sam.myutils.Checker;
import sam.myutils.ThrowException;

public interface IOUtils {
	/**
	 * Reads all bytes from an input stream and writes them to an output stream.
	 * and return number of bytes read 
	 */
	public static long pipe(InputStream in, OutputStream out) throws IOException  {
		int dbs = IOConstants.defaultBufferSize();

		int buffersize = in.available() + 5;
		if(buffersize < 20)
			buffersize = 20;
		if(buffersize > dbs)
			buffersize = dbs;

		long nread = 0L;//number of bytes read
		byte[] buf = new byte[buffersize];
		int n;

		while ((n = in.read(buf)) > 0) {
			out.write(buf, 0, n);
			nread += n;
		}
		return nread;
	}

	public static long pipe(InputStream is, Path path, byte[] buffer) throws IOException {
		Checker.requireNonNull("is path buffer", is, path, buffer);
		if(buffer.length == 0)
			ThrowException.illegalArgumentException("buffer.length == 0");

		try(OutputStream out = Files.newOutputStream(path)) {
			int n = 0;
			long size = 0;
			while((n = is.read(buffer)) > 0) {
				out.write(buffer, 0, n);
				size += n;
			}
			return size;
		}
	}
	public static long pipe(Path input, OutputStream out, byte[] buffer) throws IOException {
		Checker.requireNonNull("input out buffer", input, out, buffer);
		
		if(buffer.length == 0)
			ThrowException.illegalArgumentException("buffer.length == 0");

		try(InputStream is = Files.newInputStream(input)) {
			int n = 0;
			long size = 0;
			
			while((n = is.read(buffer)) > 0) {
				out.write(buffer, 0, n);
				size += n;
			}
			return size;
		}
	}
	public static long pipe(InputStream in, WritableByteChannel out, byte[] buffer) throws IOException {
		int n = 0;
		long size = 0;
		while((n = in.read(buffer)) > 0) {
			out.write(ByteBuffer.wrap(buffer, 0, n));
			size += n;
		}
		return size;
	}

	public static int write(ByteBuffer buffer, WritableByteChannel channel, boolean flip) throws IOException {
		if(flip)
			buffer.flip();
		
		int n = 0;
		while(buffer.hasRemaining())
			n += channel.write(buffer);

		buffer.clear();
		return n;
	}

	public static void ensureCleared(Buffer buffer) throws IOException {
		if(buffer != null && (buffer.position() != 0 || buffer.limit() != buffer.capacity()))
			throw new IOException("unclear buffer: buffer.position("+buffer.position()+") != 0 || defaultBuffer.limit("+buffer.limit()+") != defaultBuffer.capacity("+buffer.capacity()+")");
	}

	
}
