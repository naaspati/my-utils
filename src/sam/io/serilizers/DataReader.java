package sam.io.serilizers;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.Objects;

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
		IOUtils.read(buf, false, src);

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

	private CharBuffer _chars;
	private StringBuilder _sb;

	private CharBuffer chars(int len) {
		int size = Math.min(_chars == null ? Integer.MAX_VALUE : _chars.capacity(), len);
		size = Math.min(size, 100);

		if(_chars == null || _chars.capacity() < size)
			_chars = CharBuffer.allocate(size);

		_chars.clear();
		return _chars;
	}

	private Appendable sb(int len) {
		if(_sb == null)
			_sb = new StringBuilder(len);
		else {
			_sb.ensureCapacity(len);
			_sb.setLength(0);
		}
		return _sb;
	}
	
	static boolean testing = false;
	
	private ByteBuffer temp_buf;

	// FIXME 
	private final Object _readUTF(CharsetDecoder decoder, Object charsBuffer, Object sink0) throws IOException {
		if(!testing)
			Junk.notYetImplemented();
		
		if(readShort() != DataWriter.STRING_MARKER)
			throw new IOException("data doesnt represent a String");

		int len = readInt();
		if(len == -1)
			return null;

		if(len == 0)
			return sink0;

		Objects.requireNonNull(charsBuffer);
		Objects.requireNonNull(sink0);

		CharBuffer chars = charsBuffer == CHARS_MARKER ? chars(len) : (CharBuffer)charsBuffer;
		Appendable sink = sink0 == SINK_MARKER ? sb(len) : (Appendable)sink0;

		int remaining = readInt();
		decoder.reset();
		
		if(buf.remaining() >  remaining) {
			ByteBuffer buf = this.buf.duplicate();
			buf.limit(buf.position() + remaining);
			
			while(true) {
				if(consume(decoder.decode(buf, chars, true), chars, sink)) {
					while(!consume(decoder.flush(chars), chars, sink)) {}
					break;
				}
			}
			this.buf.position(buf.position());
		} else {
			if(temp_buf == null)
				temp_buf = ByteBuffer.allocate(Math.min(100, buf.capacity()));
			
			temp_buf.clear();
			
			while(true) {
				readIf(4);
				
				len = Math.min(remaining, temp_buf.remaining());
				temp_buf.put(buf.array(), buf.position(), len);
				buf.position(buf.position() + len);
				remaining -= len; 
			
				while(!consume(decoder.decode(buf, chars, remaining == 0), chars, sink)) {
					System.out.println(buf.remaining());
				}
				if(remaining == 0) {
					while(!consume(decoder.flush(chars), chars, sink)) {
					}
					break;
				}
				
				IOUtils.compactOrClear(temp_buf);
			}
		}
		
		append(chars, sink);
		return sink;
	}

	private boolean consume(CoderResult c, CharBuffer chars, Appendable sink) throws IOException {
		if(c.isUnderflow()) 
			return true;
		else if (c.isOverflow()) 
			append(chars, sink);
		 else 
			c.throwException();

		return false;
	}

	private void append(CharBuffer chars, Appendable sink) throws IOException {
		chars.flip();
		sink.append(chars);
		chars.clear();
	}

	@Override
	public void close() throws IOException {
		src.close();
	}
}
