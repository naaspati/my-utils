package sam.io.serilizers;

import static java.nio.charset.CodingErrorAction.REPORT;
import static sam.io.BufferSupplier.DEFAULT_BUFFER_SIZE;
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
import java.util.function.Consumer;
import java.util.function.Supplier;

import sam.functions.IOExceptionConsumer;
import sam.io.BufferConsumer;
import sam.io.BufferSupplier;
import sam.io.IOConstants;
import sam.io.IOUtils;
import sam.logging.Logger;

public final class StringIOUtils {
	private static final Logger LOGGER = Logger.getLogger(StringIOUtils.class);
	private static final boolean DEBUG_ENABLED = LOGGER.isDebugEnabled();

	public static BufferConsumer writer(WritableByteChannel target) {
		return b -> IOUtils.write(b, target, false);
	}
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

		consumer.onComplete();
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

	public static abstract class ReadConfig extends BufferSupplier {
		protected CharsetDecoder decoder; 
		protected CharBuffer charsBuffer;
		protected CodingErrorAction onUnmappableCharacter;
		protected CodingErrorAction onMalformedInput;
		StringBuilder debug;
		int charsCount;

		protected void postStart() {}

		public ReadConfig decoder(CharsetDecoder decoder) {
			this.decoder = decoder;
			return this;
		}
		public CharsetDecoder decoder() {
			return this.decoder = orElse(this.decoder, () -> {
				return defaultCharset().newDecoder()
						.onMalformedInput(orElse(onMalformedInput(), REPORT))
						.onUnmappableCharacter(orElse(onUnmappableCharacter(), REPORT));

			}, debug == null ? d -> {} : d -> debug.append("decoder created: ").append(d.charset().name()).append(", "));
		}
		public ReadConfig charsBuffer(CharBuffer charsBuffer) {
			this.charsBuffer = charsBuffer;
			return this;
		}
		public CharBuffer charsBuffer() {
			return this.charsBuffer = orElse(charsBuffer, () -> CharBuffer.allocate(charsCount > 100 || charsCount < 10 ? 100 : charsCount), debug == null ? b -> {} : b -> debug.append("CharBuffer created: ").append(b.capacity()).append(", "));
		}
		public ReadConfig onUnmappableCharacter(CodingErrorAction onUnmappableCharacter) {
			this.onUnmappableCharacter = onUnmappableCharacter;
			return this;
		}
		public CodingErrorAction onUnmappableCharacter() {
			return this.onUnmappableCharacter;
		}
		public ReadConfig onMalformedInput(CodingErrorAction onMalformedInput) {
			this.onMalformedInput = onMalformedInput;
			return this;
		}
		public CodingErrorAction onMalformedInput() {
			return this.onMalformedInput;
		}
		public abstract void consume(CharBuffer chars) throws IOException;
	}

	public static abstract class DefaultReadConfig extends ReadConfig {
		int bufloop = 0, charloop = 0;
		private final BufferSupplier supplier;

		public DefaultReadConfig(BufferSupplier supplier) {
			this.supplier = supplier;
			if(DEBUG_ENABLED)
				debug = new StringBuilder();
		}

		@Override
		public ByteBuffer next() throws IOException {
			bufloop++;
			return supplier.next();
		}
		@Override
		public boolean isEndOfInput() throws IOException {
			return supplier.isEndOfInput();
		}

		@Override
		public void onComplete() {
			if(debug == null)
				return;

			debug.append("bufloops: ").append(bufloop).append(", charloops: ").append(charloop);

			LOGGER.debug(debug.toString());
			supplier.onComplete();
		}
	}

	public static void read(BufferSupplier supplier, IOExceptionConsumer<CharBuffer> resultConsumer, CharsetDecoder decoder, CharBuffer charsBuffer, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		Objects.requireNonNull(supplier);
		Objects.requireNonNull(resultConsumer);

		read(new DefaultReadConfig(supplier) {

			@Override
			public void consume(CharBuffer chars) throws IOException {
				charloop++;
				resultConsumer.accept(chars);
			}
		});
	}

	public static void read(BufferSupplier supplier, final Appendable sink, CharsetDecoder decoder, CharBuffer charsBuffer, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		Objects.requireNonNull(supplier);
		Objects.requireNonNull(sink);

		ReadConfig config = new DefaultReadConfig(supplier) {
			int initLength = -1;
			int cap = -1, dcap = -1;
			int charAppended = 0;

			@Override
			public void postStart() {
				if(charsCount < 10)
					return;
				
				if(sink instanceof StringBuilder) {
					StringBuilder sb = (StringBuilder) sink;
					cap = sb.capacity();
					sb.ensureCapacity(sb.length() + charsCount+10);
					dcap = sb.capacity();
					initLength = sb.length();
				} else if(sink instanceof StringBuffer) {
					StringBuffer sb = (StringBuffer) sink; 
					cap = sb.capacity();
					sb.ensureCapacity(sb.length() + charsCount+10);
					dcap = sb.capacity();
					initLength = sb.length();
				}
			}

			@Override
			public void consume(CharBuffer chars) throws IOException {
				charloop++;
				charAppended += chars.remaining();
				sink.append(chars);
				chars.clear();
			}
			@Override
			public void onComplete() {
				if(debug == null)
					return;

				if(initLength != -1) { 
					debug.append(sink.getClass().getSimpleName())
					.append("[ cap:").append(cap).append("->").append(dcap).append(", ")
					.append("len:").append(initLength).append("->").append(((CharSequence)sink).length()).append("], ");
				}
				debug.append("charAppended: ").append(charAppended).append(", ");
				super.onComplete();
			}
		};

		read(config);
	}

	public static void read(ReadConfig reader) throws IOException {
		Objects.requireNonNull(reader);

		if(reader.isEmpty())
			return;

		long n = reader.size(); 
		int size = n < 0 ? DEFAULT_BUFFER_SIZE : (int)n;

		CharsetDecoder decoder = Objects.requireNonNull(reader.decoder());
		decoder.reset();
		
		if(size == n) 
			reader.charsCount = (int) (size/decoder.averageCharsPerByte());	
		 else 
			reader.charsCount = -1;
		
		CharBuffer charsBuffer = Objects.requireNonNull(reader.charsBuffer());
		reader.postStart();

		while(true) {
			ByteBuffer buffer = reader.next();
			if(buffer == null)
				buffer = IOConstants.EMPTY_BUFFER;
			boolean endOfInput = reader.isEndOfInput();
			
			while(true) {
				CoderResult c = decoder.decode(buffer, charsBuffer, endOfInput);

				if(c.isUnderflow())
					break;
				else if(c.isOverflow())
					consume(charsBuffer, reader);
				else 
					c.throwException();
			}

			if(endOfInput) {
				if(buffer.hasRemaining())
					throw new IOException("not full converted");

				while(true) {
					CoderResult c = decoder.flush(charsBuffer);

					if(c.isUnderflow())
						break;
					else if(c.isOverflow())
						consume(charsBuffer, reader);
					else 
						c.throwException();					
				}
				break;
			}
		}

		consume(charsBuffer, reader);
		reader.onComplete();
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
	private static void consume(CharBuffer chars, ReadConfig config) throws IOException {
		chars.flip();
		config.consume(chars);
		if(!chars.hasRemaining())
			throw new IOException("buffer not consumed");
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
