package sam.io.serilizers;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import sam.io.IOUtils;
import sam.nopkg.Junk;

public class DataReader {
	private final ReadableByteChannel supplier;
	private final ByteBuffer buf;

	public DataReader(ReadableByteChannel supplier, ByteBuffer buffer) throws IOException {
		this.supplier = supplier;

		this.buf = buffer;
		supplier.read(buffer);
		buffer.flip();
		
		if(!buf.hasRemaining())
			throw new EOFException();
	}

	private void readIf(int requiredRemaining) throws IOException {
		if(buf.remaining() >= requiredRemaining)
			return;
		
		IOUtils.compactOrClear(buf);
		supplier.read(buf);
		buf.flip();

		if(!buf.hasRemaining())
			throw new EOFException();
		
		if(buf.remaining() < requiredRemaining)
			throw new IOException("expected to read: "+requiredRemaining+", but was: "+buf.remaining());
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
		return readUTF(StandardCharsets.UTF_8.newDecoder());
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
		if(readShort() != DataWriter.STRING_MARKER)
			throw new IOException("data doesnt represent a String");
		
		int len = readInt();
		if(len == -1)
			return null;
		
		if(len == 0)
			return sink0;
		
		Objects.requireNonNull(charsBuffer);
		Objects.requireNonNull(sink0);
		
		CharBuffer chars = charsBuffer == CHARS_MARKER ? CharBuffer.allocate(Math.min(len, 100)) : (CharBuffer)charsBuffer;
		Appendable sink = sink0 == SINK_MARKER ? new StringBuilder(len) : (Appendable)sink0;
		
		int remaining = readInt();
		
		while(remaining > 0) {
			Junk.notYetImplemented();
			//FIXME
		}
		
		return sink;
	} 
}
