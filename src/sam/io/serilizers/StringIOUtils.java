package sam.io.serilizers;

import static java.nio.charset.CodingErrorAction.REPORT;
import static sam.io.BufferSupplier.DEFAULT_BUFFER_SIZE;
import static sam.io.IOConstants.defaultCharset;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import sam.io.BufferConsumer;
import sam.io.BufferSupplier;
import sam.logging.Logger;

public final class StringIOUtils {
	private static final Logger LOGGER = Logger.getLogger(StringIOUtils.class);
	private static final boolean DEBUG_ENABLED = LOGGER.isDebugEnabled();

	public static void write(BufferConsumer consumer, CharSequence s) throws IOException {
		write(consumer, s, (ByteBuffer)null);
	}
	public static void write(BufferConsumer consumer, CharSequence s, ByteBuffer buffer) throws IOException {
		write(consumer, s, null, buffer, null, null);
	}
	public static void write(BufferConsumer consumer, CharSequence s, CharsetEncoder encoder) throws IOException {
		write(consumer, s, encoder, null, null);
	}
	public static void write(BufferConsumer consumer, CharSequence s, CharsetEncoder encoder, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		write(consumer, s, encoder, null, onUnmappableCharacter, onMalformedInput);
	}
	public static void write(BufferConsumer consumer, CharSequence s, CharsetEncoder encoder, ByteBuffer buffer, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		if(s.length() == 0) return;

		CharBuffer chars = s instanceof CharBuffer ? (CharBuffer) s : CharBuffer.wrap(s);
		encoder = orElse(encoder, () -> defaultCharset().newEncoder(), d -> LOGGER.debug("encoder created: {}", d.charset().name()));
		encoder.reset();
		onUnmappableCharacter = orElse(onUnmappableCharacter, REPORT);
		onMalformedInput = orElse(onMalformedInput, REPORT);

		float avg = encoder.averageBytesPerChar();
		buffer = orElse(buffer, () -> ByteBuffer.allocate((int)Math.min(avg*chars.length() + 100, DEFAULT_BUFFER_SIZE)), b -> LOGGER.debug("ByteBuffer created: {}", b.capacity()));

		while(true) {
			CoderResult c = encoder.encode(chars, buffer, true);
			checkResult(c, onUnmappableCharacter, onMalformedInput);

			buffer.flip();
			consumer.consume(buffer);

			if(!chars.hasRemaining()) {
				while(true) {
					c = encoder.flush(buffer);
					buffer.flip();
					consumer.consume(buffer);
					if(c.isUnderflow()) break;
				}
				break;
			}
		}
	}

	public static StringBuilder read(BufferSupplier filler) throws IOException {
		StringBuilder sb = new StringBuilder();
		read(filler, sb);
		return sb;
	}

	public static void read(ByteBuffer buf, Appendable sink) throws IOException {
		read(buf, sink, null, null, null);
	}
	public static void read(ByteBuffer buf, Appendable sink, CharsetDecoder decoder) throws IOException {
		read(buf, sink, decoder, null, null);
	}
	public static void read(ByteBuffer buf, Appendable sink, CharsetDecoder decoder, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		read(buf, sink, decoder, null, onUnmappableCharacter, onMalformedInput);
	}
	public static void read(ByteBuffer buf, Appendable sink, CharsetDecoder decoder, CharBuffer chars, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		read(BufferSupplier.of(buf), sink, decoder, chars, onUnmappableCharacter, onMalformedInput);
	}
	public static void read(BufferSupplier filler, Appendable sink) throws IOException {
		read(filler, sink, null, null, null);
	}
	public static void read(BufferSupplier filler, Appendable sink, CharsetDecoder decoder) throws IOException {
		read(filler, sink, decoder, null, null);
	}
	public static void read(BufferSupplier filler, Appendable sink, CharsetDecoder decoder, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		read(filler, sink, decoder, null, onUnmappableCharacter, onMalformedInput);
	}
	public static void read(BufferSupplier supplier, Appendable sink, CharsetDecoder decoder, CharBuffer charsBuffer, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		Objects.requireNonNull(supplier);
		Objects.requireNonNull(sink);

		if(supplier.isEmpty())
			return;

		StringBuilder debug = DEBUG_ENABLED ? new StringBuilder() : null; 

		long n = supplier.size(); 
		int size = n < 0 ? DEFAULT_BUFFER_SIZE : (int)n;

		onUnmappableCharacter = orElse(onUnmappableCharacter, REPORT);
		onMalformedInput = orElse(onMalformedInput, REPORT);

		decoder = orElse(decoder, () -> defaultCharset().newDecoder(), !DEBUG_ENABLED ? d -> {} : d -> debug.append("decoder created: ").append(d.charset().name()).append(", "));
		decoder.reset();
		int charsSize = (int) (size/decoder.averageCharsPerByte());

		charsBuffer = orElse(charsBuffer, () -> CharBuffer.allocate(charsSize > 100 ? 100 : charsSize), !DEBUG_ENABLED ? b -> {} : b -> debug.append("BytBuffer created: ").append(b.capacity()).append(", "));

		int initLength = -1;
		int cap = -1, dcap = -1;
		
		if(size == n) {
			if(sink instanceof StringBuilder) {
				StringBuilder sb = (StringBuilder) sink;
				cap = sb.capacity();
				sb.ensureCapacity(sb.length() + charsSize+10);
				dcap = sb.capacity();
				initLength = sb.length();
			} else if(sink instanceof StringBuffer) {
				StringBuffer sb = (StringBuffer) sink; 
				cap = sb.capacity();
				sb.ensureCapacity(sb.length() + charsSize+10);
				dcap = sb.capacity();
				initLength = sb.length();
			}
		}

		int bufloop = 0, charloop = 0;
		while(true) {
			bufloop++;
			ByteBuffer buffer = supplier.next();
			boolean endOfInput = supplier.isEndOfInput();

			while(true) {
				charloop++;
				CoderResult c = decoder.decode(buffer, charsBuffer, endOfInput);

				if(c.isUnderflow())
					break;
				else if(c.isOverflow())
					append(charsBuffer, sink);
				else 
					c.throwException();
			}

			if(endOfInput) {
				if(buffer.hasRemaining())
					throw new IOException("not full converted");

				while(true) {
					charloop++;
					CoderResult c = decoder.flush(charsBuffer);

					if(c.isUnderflow())
						break;
					else if(c.isOverflow())
						append(charsBuffer, sink);
					else 
						c.throwException();					
				}
				break;
			}
		}
		
		append(charsBuffer, sink);
		
		if(DEBUG_ENABLED) {
			if(initLength != -1) { 
				debug.append(sink.getClass().getSimpleName())
				.append("[ cap:").append(cap).append("->").append(dcap).append(", ")
				.append("len:").append(initLength).append("->").append(((CharSequence)sink).length()).append("], ");
			}
			debug.append("bufloops: ").append(bufloop).append(", charloops: ").append(charloop);
			
			LOGGER.debug(debug.toString());
		}
	}

	private static <E> E orElse(E e, E defaultValue) {
		return e != null ? e : defaultValue;
	}
	private static <E> E orElse(E e, Supplier<E> defaultValue, Consumer<E> onCreate) {
		if(e == null) {
			e = defaultValue.get();
			onCreate.accept(e);
			return e;
		}
		return e;
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
