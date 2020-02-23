package sam.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public class ByteBufferOutputStream extends OutputStream {
	public final ByteBuffer buf;
	
	public ByteBufferOutputStream(ByteBuffer buf) {
		this.buf = Objects.requireNonNull(buf);
	}
	
	@Override
	public void write(int b) throws IOException {
		buf.put((byte)b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		buf.put(b, off, len);
	}
}
