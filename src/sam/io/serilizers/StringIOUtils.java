package sam.io.serilizers;

import static java.nio.charset.CodingErrorAction.REPORT;
import static sam.io.BufferSupplier.DEFAULT_BUFFER_SIZE;
import static sam.io.IOConstants.defaultCharset;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import sam.functions.IOExceptionConsumer;
import sam.io.BufferConsumer;
import sam.io.BufferSupplier;
import sam.io.IOConstants;
import sam.logging.Logger;
import sam.myutils.Checker;

public final class StringIOUtils {
	private static final Logger LOGGER = Logger.getLogger(StringIOUtils.class);
	private static final boolean DEBUG_ENABLED = LOGGER.isDebugEnabled();
	
	public static void write(BufferConsumer consumer, CharSequence s) throws IOException {
		write(consumer, s, (ByteBuffer)null);
	}
	public static void write(BufferConsumer consumer, CharSequence s, ByteBuffer buffer) throws IOException {
		write(consumer, s, null, buffer);
	}
	public static void write(BufferConsumer consumer, CharSequence s, CharsetEncoder encoder) throws IOException {
		write(consumer, s, encoder, null);
	}
	public static void writeJoining(Iterator<String> itr, String separator, BufferConsumer consumer, ByteBuffer buffer, CharBuffer chars, CharsetEncoder encoder) throws IOException {
		Checker.requireNonNull("itr, separator, consumer, buffer, chars, encoder", itr, separator, consumer);
	
		chars = orElse(chars, () -> CharBuffer.allocate(100), d -> LOGGER.debug("CharBuffer created: {}", d.capacity()));  
		encoder = orElse(encoder, () -> IOConstants.newEncoder(), d -> LOGGER.debug("encoder created: {}", d.charset().name()));
		buffer = orElse(buffer, () -> ByteBuffer.allocate(DEFAULT_BUFFER_SIZE), b -> LOGGER.debug("ByteBuffer created: {}", b.capacity()));
		encoder.reset();
		
		if(!itr.hasNext())
			return;
		
		try(WriterImpl w = new WriterImpl(consumer, buffer, chars, false, encoder)) {
			char csep = ' ';
			if(separator.length() == 1) {
				csep = separator.charAt(0);
				separator = null;
			}
			
			while (itr.hasNext()) {
				String s = itr.next();
				if(s == null)
					throw new NullPointerException();
				
				w.append(s);
				
				if(separator == null)
					w.append(csep);
				else if(!separator.isEmpty())
					w.append(separator);
			}
		}
	}
	
	public static void write(BufferConsumer consumer, CharSequence s, CharsetEncoder encoder, ByteBuffer buffer) throws IOException {
		Objects.requireNonNull(s);
		
		if(s.length() == 0) return;

		CharBuffer chars = s instanceof CharBuffer ? (CharBuffer) s : CharBuffer.wrap(s);
		encoder = orElse(encoder, () -> IOConstants.newEncoder(), d -> LOGGER.debug("encoder created: {}", d.charset().name()));
		CharsetEncoder e = encoder;
		buffer = orElse(buffer, () -> ByteBuffer.allocate((int)Math.min(e.averageBytesPerChar()*chars.length() + 5, DEFAULT_BUFFER_SIZE)), b -> LOGGER.debug("ByteBuffer created: {}", b.capacity()));
		encoder.reset();
		
		while(chars.hasRemaining()) {
			CoderResult c = encoder.encode(chars, buffer, true);
			
			if(c.isUnderflow()) {
				while(true) {
					c = encoder.flush(buffer);
					consume(consumer, buffer);
					
					if(c.isUnderflow()) 
						break;
					else if(!c.isOverflow())
						c.throwException();
				}
				break;
			} else if(c.isOverflow())
				consume(consumer, buffer);
			else
				c.throwException();
		}
		
		consumer.onComplete();
	}

	private static void consume(BufferConsumer consumer, ByteBuffer buffer) throws IOException {
		buffer.flip();
		consumer.consume(buffer);
		if(!buffer.hasRemaining())
			throw new IOException("buffer not consumed");
	}
	public static StringBuilder read(BufferSupplier filler) throws IOException {
		StringBuilder sb = new StringBuilder();
		read(filler, sb);
		return sb;
	}

	public static void read(ByteBuffer buf, Appendable sink) throws IOException {
		read(buf, sink, null);
	}
	public static void read(ByteBuffer buf, Appendable sink, CharsetDecoder decoder) throws IOException {
		read(buf, sink, decoder);
	}
	public static void read(ByteBuffer buf, Appendable sink, CharsetDecoder decoder, CharBuffer chars) throws IOException {
		read(BufferSupplier.of(buf), sink, decoder, chars);
	}
	public static void read(BufferSupplier filler, Appendable sink) throws IOException {
		read(filler, sink, null);
	}
	public static void read(BufferSupplier filler, Appendable sink, CharsetDecoder decoder) throws IOException {
		read(filler, sink, decoder, null);
	}

	public static abstract class ReadConfig extends BufferSupplier {
		protected CharsetDecoder decoder; 
		protected CharBuffer charsBuffer;
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
						.onMalformedInput(REPORT)
						.onUnmappableCharacter(REPORT);

			}, debug == null ? d -> {} : d -> debug.append("decoder created: ").append(d.charset().name()).append(", "));
		}
		public ReadConfig charsBuffer(CharBuffer charsBuffer) {
			this.charsBuffer = charsBuffer;
			return this;
		}
		public CharBuffer charsBuffer() {
			return this.charsBuffer = orElse(charsBuffer, () -> CharBuffer.allocate(charsCount > 100 || charsCount < 10 ? 100 : charsCount), debug == null ? b -> {} : b -> debug.append("CharBuffer created: ").append(b.capacity()).append(", "));
		}
		public abstract void consume(CharBuffer chars) throws IOException;
	}

	private static abstract class DefaultReadConfig extends ReadConfig {
		int bufloop = 0, charloop = 0;
		private final BufferSupplier supplier;

		public DefaultReadConfig(BufferSupplier supplier, CharsetDecoder decoder, CharBuffer charBuffer) {
			this.supplier = supplier;
			this.decoder = decoder;
			this.charsBuffer = charBuffer;
			
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

	public static void read(BufferSupplier supplier, IOExceptionConsumer<CharBuffer> resultConsumer, CharsetDecoder decoder, CharBuffer charsBuffer) throws IOException {
		Objects.requireNonNull(supplier);
		Objects.requireNonNull(resultConsumer);
		
		read(new DefaultReadConfig(supplier, decoder, charsBuffer) {
			@Override
			public void consume(CharBuffer chars) throws IOException {
				charloop++;
				resultConsumer.accept(chars);
			}
		});
	}
	
	public static void collect(BufferSupplier supplier, char separater, Consumer<String> collector, CharsetDecoder decoder, CharBuffer charsBuffer, StringBuilder sbBuffer) throws IOException {
		StringBuilder sb = orElse(sbBuffer, StringBuilder::new, s -> {});
		
		IOExceptionConsumer<CharBuffer> eater = new IOExceptionConsumer<CharBuffer>() {
			@Override
			public void accept(CharBuffer e) throws IOException {
				while(e.hasRemaining()) {
					char c = e.get();
					if(c == separater) {
						if(sb.length() != 0 && c == '\n' && sb.charAt(sb.length() - 1) == '\r')
							sb.setLength(sb.length() - 1);
						
						if(sb.length() == 0) {
							collector.accept("");
						} else {
							collector.accept(sb.toString());
							sb.setLength(0);
						}
					} else {
						sb.append(c);
					}
				}
				e.clear();
			}
		};
		read(supplier, eater, decoder, charsBuffer);
		if(sb.length() != 0)
			collector.accept(sb.toString());
	}

	public static void read(BufferSupplier supplier, final Appendable sink, CharsetDecoder decoder, CharBuffer charsBuffer) throws IOException {
		Objects.requireNonNull(supplier);
		Objects.requireNonNull(sink);

		ReadConfig config = new DefaultReadConfig(supplier, decoder, charsBuffer) {
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

}
