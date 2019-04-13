package sam.io.serilizers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Objects;

import sam.io.IOConstants;
import sam.io.IOUtils;
import sam.logging.Logger;

public class DataWriter implements AutoCloseable {
	private static final Logger LOGGER = Logger.getLogger(DataWriter.class);

	static final short STRING_MARKER = 8115;

	private final WritableByteChannel sink;
	private final ByteBuffer buf;

	public DataWriter(WritableByteChannel sink, ByteBuffer buffer) throws IOException {
		this.sink = Objects.requireNonNull(sink);
		this.buf = buffer;
	}

	private void writeIf(int requiredRemaining) throws IOException {
		if(buf.remaining() < requiredRemaining) 
			IOUtils.write(buf, sink, true);
	}

	public final void writeBoolean(boolean b) throws IOException {
		writeByte((byte) (b ? 1 : 0));
	}
	public final void writeByte(byte b) throws IOException {
		writeIf(1);
		buf.put(b);
	}

	public final void writeShort(short s) throws IOException {
		writeIf(Short.BYTES);
		buf.putShort(s);
	}

	public final void writeChar(char c) throws IOException {
		writeIf(Character.BYTES);
		buf.putChar(c);
	}
	public final void writeInt(int n) throws IOException {
		writeIf(Integer.BYTES);
		buf.putInt(n);
	}

	public final void writeLong(long value) throws IOException {
		writeIf(Long.BYTES);
		buf.putLong(value);
	}

	public final void writeFloat(float value) throws IOException {
		writeIf(Float.BYTES);
		buf.putFloat(value);
	}

	public final void writeDouble(double d) throws IOException {
		writeIf(Double.BYTES);
		buf.putDouble(d);
	}

	public final void writeUTF(CharSequence value) throws IOException {
		writeUTF(value, IOConstants.newEncoder());
	}

	/**
	 * copy pasted {@link DataOutputStream#writeUTF(String)}
	 * @param value
	 * @throws IOException
	 */
	public final void writeUTF(CharSequence value, CharsetEncoder encoder) throws IOException {
		Objects.requireNonNull(encoder);
		encoder.reset();

		writeShort(STRING_MARKER);

		if(value == null)
			writeInt(-1);
		else {
			int len = value.length(); 
			writeInt(len);

			if(len == 0)
				return;

			int bytes = (int) (encoder.averageBytesPerChar() * value.length()) + 14;
			
			if(buf.capacity() < bytes + 4)
				writeNewBuf(-1, value, encoder);
			else {
				if(buf.remaining() < bytes + 4)
					IOUtils.write(buf, sink, true);
				
				int pos = buf.position();
				buf.putInt(0);
				
				CharBuffer chars = CharBuffer.wrap(value);
				encoder.reset();
				
				while(chars.hasRemaining()) {
					CoderResult c = encoder.encode(chars, buf, true);
					
					if(c.isUnderflow()) {
						c = encoder.flush(buf);
						
						if(c.isOverflow()) {
							writeNewBuf(pos, value, encoder);
							return;
						} else if(!c.isUnderflow())
							c.throwException();
						
						break;
					} else if(c.isOverflow()) {
						writeNewBuf(pos, value, encoder);
						return;
					} else {
						c.throwException();
					} 
				}
				buf.putInt(pos, buf.position() - pos - 4);
			}
		}
	}

	private void writeNewBuf(int pos, CharSequence value, CharsetEncoder encoder) throws IOException {
		if(pos >= 0)
			buf.position(pos);
		
		encoder.reset();
		ByteBuffer buffer = encoder.encode(CharBuffer.wrap(value));
		writeInt(buffer.remaining());

		LOGGER.debug(() -> "new_bytebuffer: "+buffer.capacity());

		IOUtils.write(buf, sink, true);
		IOUtils.write(buffer, sink, false);
	}

	@Override
	public void close() throws IOException {
		IOUtils.write(buf, sink, true);
		sink.close();
	}
}
