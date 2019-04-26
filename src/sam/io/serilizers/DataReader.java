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
		Appendable sb = readUTF(sb(-1));
		
		if(sb == null)
			return null;
		else if(((StringBuilder)sb).length() == 0)
			return "";
		else 
			return sb.toString();
	} 

	private CharBuffer _chars;
	private StringBuilder _sb;
	private ByteBuffer temp_buf;
	private CharsetDecoder decoder;
	
	public void setDecoder(CharsetDecoder decoder) {
        this.decoder = decoder;
    }
	public void setChars(CharBuffer _chars) {
        this._chars = _chars;
    }
	public void setStringBuilder(StringBuilder _sb) {
        this._sb = _sb;
    }
	private CharsetDecoder decoder() {
	    if(decoder == null)
	        decoder = IOConstants.newDecoder();
	    return decoder;
	}

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
			_sb = new StringBuilder(len < 0 ? 100 : len);
		else if(len > 0){
			_sb.ensureCapacity(len);
			_sb.setLength(0);
		}
		return _sb;
	}

	private final Appendable readUTF(Appendable sink0) throws IOException {
		if(readShort() != DataWriter.STRING_MARKER)
			throw new IOException("data doesnt represent a String");

		int len = readInt();
		if(len == -1)
			return null;

		if(len == 0)
			return sink0;

		Objects.requireNonNull(sink0);

		CharBuffer chars = chars(len);
		Appendable sink = sb(len);
		CharsetDecoder decoder = decoder();

		int remaining = readInt();
		decoder.reset();
		
		if(buf.capacity() > remaining) {
			if(buf.remaining() < remaining) {
				IOUtils.compactOrClear(buf);
				src.read(buf);
				buf.flip();
			}
			
			ByteBuffer buf = this.buf.duplicate();
			buf.limit(buf.position() + remaining);
			
			decode(buf, chars, sink, decoder);
			this.buf.position(buf.position());
		} else {
			if(temp_buf == null || temp_buf.capacity() < remaining)
				temp_buf = ByteBuffer.allocate(remaining);
			
			temp_buf.clear();
			temp_buf.limit(remaining);
			temp_buf.put(buf);
			
			IOUtils.read(temp_buf, false, src);
			
			if(temp_buf.remaining() != remaining)
				throw new IOException("expected to read: "+remaining+", was: "+temp_buf.remaining());
			
			decode(temp_buf, chars, sink, decoder);
		}
		
		return sink;
	}

	private void decode(ByteBuffer buf, CharBuffer chars, Appendable sink, CharsetDecoder decoder) throws IOException {
		while(true) {
			if(consume(decoder.decode(buf, chars, true), chars, sink)) {
				while(!consume(decoder.flush(chars), chars, sink)) {}
				break;
			}
		}
		
		append(chars, sink);
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
