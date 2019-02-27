package sam.io.serilizers;

import static java.nio.charset.CodingErrorAction.REPORT;
import static sam.io.IOConstants.defaultBufferSize;
import static sam.io.IOConstants.defaultCharset;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Objects;
import java.util.function.Supplier;

import sam.io.BufferConsumer;
import sam.io.BufferFiller;
import sam.io.IOUtils;

public final class StringIOUtils {
	public static final int DEFAULT_BUFFER_SIZE = defaultBufferSize();

	public static int computeBufferSize(double averageBytesPerChar, int charSequenceLength) {
		int buffersize = (int) (charSequenceLength * averageBytesPerChar);

		buffersize = buffersize > DEFAULT_BUFFER_SIZE ? DEFAULT_BUFFER_SIZE : buffersize;
		if(buffersize < 50)
			buffersize = 50;

		if(buffersize%averageBytesPerChar != 0)
			buffersize = (int) (averageBytesPerChar*(buffersize/averageBytesPerChar + 1));

		return buffersize;
	}
	public static int computeBufferSize(CharsetEncoder encoder, CharSequence chars) {
		return computeBufferSize(encoder.averageBytesPerChar(), chars.length());
	}
	public static void write(BufferConsumer consumer, CharSequence s) throws IOException {
		write(consumer, s, defaultCharset().newEncoder(), REPORT, REPORT);
	}
	public static BufferConsumer consumer(WritableByteChannel channel) {
		return buffer -> IOUtils.write(buffer, channel, true);
	}
	public static void write(BufferConsumer consumer, CharSequence s, CharsetEncoder encoder) throws IOException {
		write(consumer, s, encoder, REPORT, REPORT);
	}
	public static void write(BufferConsumer consumer, CharSequence s, CharsetEncoder encoder, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		write(consumer, s, encoder, ByteBuffer.allocate(computeBufferSize(encoder, s)), onUnmappableCharacter, onMalformedInput);
	}
	public static void write(BufferConsumer consumer, CharSequence s, CharsetEncoder encoder, ByteBuffer buffer, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		if(s.length() == 0) return;

		CharBuffer chars = s instanceof CharBuffer ? (CharBuffer) s : CharBuffer.wrap(s);

		while(true) {
			CoderResult c = encoder.encode(chars, buffer, true);
			checkResult(c, onUnmappableCharacter, onMalformedInput);

			consumer.consume(buffer);

			if(!chars.hasRemaining()) {
				while(true) {
					c = encoder.flush(buffer);
					consumer.consume(buffer);
					if(c.isUnderflow()) break;
				}
				break;
			}
		}
	}

	public static StringBuilder read(BufferFiller filler) throws IOException {
		StringBuilder sb = new StringBuilder();
		read(filler, sb);
		return sb;
	}

	public static void read(BufferFiller filler, Appendable sink) throws IOException {
		read(filler, sink, null, null, null);
	}
	public static void read(BufferFiller filler, Appendable sink, CharsetDecoder decoder) throws IOException {
		read(filler, sink, decoder, null, null);
	}
	public static void read(BufferFiller filler, Appendable sink, CharsetDecoder decoder, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		read(filler, sink, decoder, null, null, onUnmappableCharacter, onMalformedInput);
	}
	public static void read(BufferFiller filler, Appendable sink, CharsetDecoder decoder, CharBuffer chars, ByteBuffer buf, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		Objects.requireNonNull(sink);
		int size0 = filler.remaining();

		if(size0 == 0) 
			return;

		int size = size0 < 0 ? DEFAULT_BUFFER_SIZE : size0;

		onUnmappableCharacter = orElse(onUnmappableCharacter, REPORT);
		onMalformedInput = orElse(onMalformedInput, REPORT);

		decoder = orElse(decoder, () -> defaultCharset().newDecoder());
		decoder.reset();
		double d = decoder.averageCharsPerByte();
		int bytesPer = (int) Math.round(d);

		chars = orElse(chars, () -> CharBuffer.allocate(size/bytesPer > 100 ? 100 : size/bytesPer));
		buf = orElse(buf, () -> ByteBuffer.allocate(size));

		if(size != DEFAULT_BUFFER_SIZE) {
			if(sink instanceof StringBuilder) {
				StringBuilder sb = (StringBuilder) sink; 
				sb.ensureCapacity(sb.length() + (int) (size/bytesPer)+10);
			}
			else if(sink instanceof StringBuffer) {
				StringBuffer sb = (StringBuffer) sink; 
				sb.ensureCapacity(sb.length() + (int) (size/bytesPer)+10);
			}
		}

		while(true) {
			int read = filler.fillNFlip(buf);
			boolean endOfInput = read == -1;

			while(true) {
				CoderResult c = decoder.decode(buf, chars, endOfInput);
				if(c.isUnderflow())
					break;
				else if(c.isOverflow())
					append(chars, sink);
				else 
					c.throwException();
			}

			if(endOfInput) {
				if(buf.hasRemaining())
					throw new IOException("no full converted");

				while(true) {
					CoderResult c = decoder.flush(chars);

					if(c.isUnderflow())
						break;
					else if(c.isOverflow())
						append(chars, sink);
					else 
						c.throwException();					
				}
				break;
			}

			if(buf.hasRemaining())
				buf.compact();
			else
				buf.clear();
		}
		append(chars, sink);
	}
	
	public static void decode(ByteBuffer buffer, CharBuffer charBuffer, CharsetDecoder decoder, Appendable sink, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		read(new BufferFiller() {
			@Override
			public int remaining() throws IOException {
				return buffer.remaining();
			}
			
			@Override
			public int fillNFlip(ByteBuffer buffer) throws IOException {
				return -1;
			}
		}, sink, decoder, charBuffer, buffer, onUnmappableCharacter, onMalformedInput);
	}

	private static <E> E orElse(E e, E defaultValue) {
		return e != null ? e : defaultValue;
	}
	private static <E> E orElse(E e, Supplier<E> defaultValue) {
		return e != null ? e : defaultValue.get();
	}
	private static void append(CharBuffer chars, Appendable sb) throws IOException {
		chars.flip();
		if(chars.hasRemaining())
			sb.append(chars);
		chars.clear();
	}

	private static void checkResult(CoderResult c, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws CharacterCodingException {
		if((c.isUnmappable() && onUnmappableCharacter == REPORT) || (c.isMalformed() && onMalformedInput == REPORT))
			c.throwException();
	}
	public static String concat(String result, CharBuffer cb) {
		if(result.length() == 0) return cb.toString();
		if(cb.length() == 0) return result;

		char[] chars = new char[result.length() + cb.length()];
		int n = 0;
		for (int i = 0; i < result.length(); i++) 
			chars[n++] = result.charAt(i);

		for (int i = 0; i < cb.length(); i++) 
			chars[n++] = cb.charAt(i);

		return new String(chars);
	}
}
