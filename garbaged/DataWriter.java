package sam.io.serilizers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import sam.io.BufferConsumer;
import sam.io.IOUtils;

public class DataWriter {
	static final short STRING_MARKER = 8115;

	private final BufferConsumer consumer;
	private final ByteBuffer buf;

	public DataWriter(BufferConsumer consumer, ByteBuffer buffer) throws IOException {
		this.consumer = Objects.requireNonNull(consumer);
		this.buf = buffer;
	}

	private void writeIf(int requiredRemaining) throws IOException {
		if(buf.remaining() < requiredRemaining) {
			buf.flip();
			consumer.consume(buf);

			if(buf.remaining() < requiredRemaining) 
				throw new IOException("buffer not cosumed");
		}
	}

	public final void writeBoolean(boolean b) throws IOException {
		writeByte((byte) (b ? 1 : 0));
	}
	public final void writeByte(byte b) throws IOException {
		writeIf(1);
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
		writeUTF(value, StandardCharsets.UTF_8.newEncoder());
	}

	/**
	 * copy pasted {@link DataOutputStream#writeUTF(String)}
	 * @param value
	 * @throws IOException
	 */
	public final void writeUTF(CharSequence value, CharsetEncoder encoder) throws IOException {
		writeShort(STRING_MARKER);
		Objects.requireNonNull(encoder);

		encoder.reset();

		if(value == null)
			writeInt(-1);
		else {
			int len = value.length(); 
			writeInt(len);

			if(value.length() == 0)
				return;

			int bytes = (int) (encoder.averageBytesPerChar() * value.length()) + 14;
			if(buf.capacity() < bytes)
				writeCreatedBuf(value, encoder);
			else {
				if(buf.remaining() < bytes)
					writeFully(buf);

				writeUsingBuf(value, encoder);
			}
		}
	}

	private final void writeUsingBuf(CharSequence value, CharsetEncoder encoder) throws IOException {
		int pos = buf.position();
		buf.putInt(0);

		CoderResult c = encoder.encode(CharBuffer.wrap(value), buf, true);

		if(c.isUnderflow()) 
			c = encoder.flush(buf);

		if(c.isOverflow()) {
			buf.position(pos);
			writeCreatedBuf(value, encoder);
		} else {
			if(!c.isUnderflow())
				c.throwException();
			else
				buf.putInt(pos, buf.position() - pos);
		}
	}

	private final void writeFully(ByteBuffer buf) throws IOException {
		buf.flip();

		consumer.consume(buf);
		IOUtils.ensureCleared(buf);
	}

	private final void writeCreatedBuf(CharSequence value, CharsetEncoder encoder) throws IOException {
		ByteBuffer buffer = encoder.encode(CharBuffer.wrap(value));
		writeInt(buffer.remaining());

		consumer.consume(buffer);
		IOUtils.ensureCleared(buffer);
	}
}
