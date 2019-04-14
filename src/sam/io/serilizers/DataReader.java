package sam.io.serilizers;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.CharsetDecoder;
import java.util.Arrays;
import java.util.Objects;

import sam.io.BufferSupplier;
import sam.io.IOConstants;
import sam.io.IOUtils;
import sam.nopkg.Junk;

public class DataReader implements AutoCloseable {
	private final ReadableByteChannel src;
	private final ByteBuffer buf;

	public DataReader(ReadableByteChannel src, ByteBuffer buffer) throws IOException {
		this.src = src;

		this.buf = buffer;
		IOUtils.read(buf, false, src);

		if(!buf.hasRemaining())
			throw new EOFException();
	}

	private void readIf(int requiredRemaining) throws IOException {
		if(buf.remaining() >= requiredRemaining)
			return;

		IOUtils.compactOrClear(buf);
		src.read(buf);
		buf.flip();

		if(!buf.hasRemaining())
			throw new EOFException();

		if(buf.remaining() < requiredRemaining)
			throw new EOFException("expected to read: "+requiredRemaining+", but was: "+buf.remaining());
	}

	public final boolean readBoolean() throws IOException {
		byte b = readByte();

		switch (b) {
			case 0: return false;
			case 1: return true;
			default:
				throw new IOException("expected: 0 or 1, found: "+b);
		}
	}

	public final byte readByte() throws IOException {
		readIf(1);
		return buf.get(); 
	}

	public final short readShort() throws IOException {
		readIf(Short.BYTES);
		return buf.getShort();
	}

	public final char readChar() throws IOException {
		readIf(Character.BYTES);
		return buf.getChar();
	}
	public final int readInt() throws IOException {
		readIf(Integer.BYTES);
		return buf.getInt();
	}

	public final long readLong() throws IOException {
		readIf(Long.BYTES);
		return buf.getLong();
	}

	public final float readFloat() throws IOException {
		readIf(Float.BYTES);
		return buf.getFloat();
	}

	public final double readDouble() throws IOException {
		readIf(Double.BYTES);
		return buf.getDouble();
	}
	public final String readUTF() throws IOException {
		return readUTF(IOConstants.newDecoder());
	}

	private static final Object SINK_MARKER = new Object();
	private static final Object CHARS_MARKER = new Object();

	public final String readUTF(CharsetDecoder decoder) throws IOException {
		Object a = _readUTF(decoder, CHARS_MARKER, SINK_MARKER);
		if(a == null)
			return null;
		else if(a == SINK_MARKER)
			return "";
		else 
			return a.toString();

	} 
	public final Appendable readUTF(CharsetDecoder decoder, Appendable sink) throws IOException {
		return (Appendable) _readUTF(decoder, CHARS_MARKER, sink);
	}
	public final Appendable readUTF(CharsetDecoder decoder, CharBuffer charBuffer, Appendable sink) throws IOException {
		return (Appendable) _readUTF(decoder, charBuffer, sink);
	}

	private final Object _readUTF(CharsetDecoder decoder, Object charsBuffer, Object sink0) throws IOException {
		if(10 < System.currentTimeMillis())
			Junk.notYetImplemented();
		
		if(readShort() != DataWriter.STRING_MARKER)
			throw new IOException("data doesnt represent a String");

		int len = readInt();
		if(len == -1)
			return null;

		if(len == 0)
			return sink0;

		decoder.reset();

		Objects.requireNonNull(charsBuffer);
		Objects.requireNonNull(sink0);

		CharBuffer chars = charsBuffer == CHARS_MARKER ? CharBuffer.allocate(Math.min(len, 100)) : (CharBuffer)charsBuffer;
		Appendable sink = sink0 == SINK_MARKER ? new StringBuilder(len) : (Appendable)sink0;
		
		int bytes = readInt();
		
		BufferSupplier supplier = new BufferSupplier() {
			int remaining = bytes;
			boolean first = true;

			@Override
			public ByteBuffer next() throws IOException {
				if(remaining == 0)
					return null;
				
				if(!first) {
					IOUtils.compactOrClear(buf);
					if(IOUtils.read(buf, false, src) < 0)
						throw new EOFException();
				}
				
				first = false;
				
				ByteBuffer b = buf;
				
				if(buf.remaining() > remaining) {
					b = buf.duplicate();
					b.limit(b.position() + remaining);
					buf.position(b.limit());
				}
				
				remaining -= b.remaining();
				return b;
			}

			@Override
			public boolean isEndOfInput() throws IOException {
				return remaining == 0;
			}
		};
		
		
		ByteBuffer buf2 = ByteBuffer.allocate(bytes + 10);
		while(true) {
			buf2.put(supplier.next());
			if(supplier.isEndOfInput())
				break;
		}
		buf2.flip();
		
		System.out.println(buf2.remaining());
		System.out.println(Arrays.toString(Arrays.copyOf(buf2.array(), buf.remaining())));
		
		//FIXME StringIOUtils.read(supplier, sink, decoder, chars);
		return sink;
	}
	
	@Override
	public void close() throws IOException {
		src.close();
	}
}
