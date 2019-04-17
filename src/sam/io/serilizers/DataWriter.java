package sam.io.serilizers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sam.io.IOConstants;
import sam.io.IOUtils;

public class DataWriter implements AutoCloseable {
	static final short STRING_MARKER = 8115;

	private final WritableByteChannel target;
	private final ByteBuffer buf;

	public DataWriter(WritableByteChannel sink, ByteBuffer buffer) throws IOException {
		this.target = Objects.requireNonNull(sink);
		this.buf = buffer;
	}

	private void writeIf(int requiredRemaining) throws IOException {
		if(buf.remaining() < requiredRemaining) 
			IOUtils.write(buf, target, true);
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
			if(buf.remaining() < bytes)
				IOUtils.write(buf, target, true);
			
			int pos = buf.position();
			buf.putInt(0);
			CharBuffer chars = CharBuffer.wrap(value);
			
			List<ByteBuffer> list = null;
			ByteBuffer buffer = this.buf;
			final int def_size = Math.min(100, this.buf.capacity());
			final float max = encoder.maxBytesPerChar();
			
			while(chars.hasRemaining()) {
				if(buffer.remaining() < max) {
					buffer = ByteBuffer.allocate(bytes <= 0 ? def_size : Math.min(Math.min(bytes + 10, 1024), this.buf.capacity()));
					if(list == null)
						list = new ArrayList<>();
					list.add(buffer);
				}
				
				int n = buffer.position();
				CoderResult c = encoder.encode(chars, buffer, true);
				bytes = bytes - buffer.position() - n;
				
				if(c.isUnderflow()) {
					while(true) {
						c = encoder.flush(buffer);
						
						if(c.isUnderflow())
							break;
						else if(c.isOverflow()) {
							buffer = ByteBuffer.allocate(def_size);
							if(list == null)
								list = new ArrayList<>();
							list.add(buffer);
						} else 
							c.throwException();
					}
					break;
				} else if(!c.isOverflow())
					c.throwException();
			}
			
			int size = this.buf.position() - pos - 4;
			
			if(buffer == this.buf) {
				this.buf.putInt(pos, size);
			} else {
				int size2 = 0;
				for (ByteBuffer b : list) {
					b.flip();
					size2 += b.remaining();
				}
				
				this.buf.putInt(pos, size + size2);
				IOUtils.write(this.buf, target, true);
				
				/*
				 * System.out.print("list.size(): "+list.size() + ", bytes: "+(size + size2)+", ["+this.buf.remaining()+", ");
				list.forEach(s -> System.out.print(s.remaining()+", "));
				System.out.println("]");
				 */
				
				for (ByteBuffer b : list) {
					writeIf(b.remaining());
					this.buf.put(b);
				}
			}
		}
	}

	@Override
	public void close() throws IOException {
		IOUtils.write(buf, target, true);
		target.close();
	}
}
