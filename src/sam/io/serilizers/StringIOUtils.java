package sam.io.serilizers;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static sam.io.HasBuffer.DEFAULT_BUFFER_SIZE;
import static sam.io.HasBuffer.buffer;
import static sam.io.IOConstants.newEncoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;

import sam.functions.IOExceptionConsumer;
import sam.io.HasBuffer;
import sam.io.HasSize;
import sam.io.IOConstants;
import sam.io.IOUtils;
import sam.io.ReadableByteChannelCustom;
import sam.myutils.Checker;

public final class StringIOUtils implements StringIOUtilsMinimal {
	private StringIOUtils() { }
	
	public static <E extends CharSequence> void writeJoining(Iterator<E> itr, CharSequence separator,
			WritableByteChannel target, CharBuffer chars, CharsetEncoder encoder) throws IOException {
		Checker.requireNonNull("itr, separator, target", itr, separator, target);

		chars = orElse(chars, () -> CharBuffer.allocate(100));
		encoder = orElse(encoder, () -> IOConstants.newEncoder());
		encoder.reset();

		if (!itr.hasNext())
			return;

		try (WriterImpl w = new WriterImpl(target, chars, false, encoder)) {
			char csep = ' ';

			if (separator.length() == 1) {
				csep = separator.charAt(0);
				separator = null;
			}

			boolean notEmpty = Checker.isNotEmpty(separator);

			while (itr.hasNext()) {
				CharSequence s = itr.next();
				if (s == null)
					throw new NullPointerException();

				w.append(s);

				if (separator == null)
					w.append(csep);
				else if (notEmpty)
					w.append(separator);
			}
		}
	}

	public static void write(WritableByteChannel target, CharSequence s) throws IOException {
		write(target, s, newEncoder());
	}

	public static void write(WritableByteChannel target, CharSequence s, CharsetEncoder encoder) throws IOException {
		Objects.requireNonNull(s);

		if (s.length() == 0)
			return;

		CharBuffer chars = s instanceof CharBuffer ? (CharBuffer) s : CharBuffer.wrap(s);
		encoder = orElse(encoder, () -> IOConstants.newEncoder());
		CharsetEncoder e = encoder;
		ByteBuffer buffer = buffer(target, (int) (e.averageBytesPerChar() * chars.length() + 5));
		encoder.reset();

		while (chars.hasRemaining()) {
			if (consume(encoder.encode(chars, buffer, true), target, buffer)) {
				while (!consume(encoder.flush(buffer), target, buffer)) {
				}
				break;
			}
		}

		IOUtils.write(buffer, target, true);
		target.close();
	}

	private static boolean consume(CoderResult c, WritableByteChannel target, ByteBuffer buffer) throws IOException {
		if (c.isUnderflow())
			return true;
		else if (c.isOverflow())
			IOUtils.write(buffer, target, true);
		else
			c.throwException();

		return false;
	}

	public static StringBuilder read(ReadableByteChannel filler) throws IOException {
		StringBuilder sb = new StringBuilder();
		read(filler, sb);
		return sb;
	}

	public static void read(ReadableByteChannel src, Appendable sink) throws IOException {
		read(src, sink, null);
	}

	public static void read(ReadableByteChannel src, Appendable sink, CharsetDecoder decoder) throws IOException {
		read(src, sink, decoder, null);
	}

	public static abstract class ReadConfig implements ReadableByteChannelCustom {
		protected CharsetDecoder decoder;
		protected CharBuffer charsBuffer;
		int charsCount;

		protected void postStart() {
		}

		public ReadConfig decoder(CharsetDecoder decoder) {
			this.decoder = decoder;
			return this;
		}

		public CharsetDecoder decoder() {
			if (this.decoder == null)
				this.decoder = IOConstants.newDecoder();
			return this.decoder;
		}

		public ReadConfig charsBuffer(CharBuffer charsBuffer) {
			this.charsBuffer = charsBuffer;
			return this;
		}

		public CharBuffer charsBuffer() {
			if (this.charsBuffer == null)
				this.charsBuffer = CharBuffer.allocate(charsCount > 100 || charsCount < 10 ? 100 : charsCount);

			return this.charsBuffer;
		}

		public abstract void consume(CharBuffer chars) throws IOException;
	}

	private static abstract class DefaultReadConfig extends ReadConfig {
		private final ReadableByteChannel src;

		public DefaultReadConfig(ReadableByteChannel src, CharsetDecoder decoder, CharBuffer charBuffer) {
			this.src = src;
			this.decoder = decoder;
			this.charsBuffer = charBuffer;
		}

		@Override
		public boolean isOpen() {
			return src.isOpen();
		}

		@Override
		public int read(ByteBuffer dst) throws IOException {
			return src.read(dst);
		}

		@Override
		public void close() throws IOException {
			src.close();
		}

		@Override
		public ByteBuffer buffer() {
			if (src instanceof HasBuffer)
				return ((HasBuffer) src).buffer();
			else
				return null;
		}

		@Override
		public long size() throws IOException {
			if (src instanceof HasSize)
				return ((HasSize) src).size();
			else
				return -1;
		}

	}

	public static void read(ReadableByteChannel src, IOExceptionConsumer<CharBuffer> resultConsumer,
			CharsetDecoder decoder, CharBuffer charsBuffer) throws IOException {
		Objects.requireNonNull(src);
		Objects.requireNonNull(resultConsumer);

		read(new DefaultReadConfig(src, decoder, charsBuffer) {
			@Override
			public void consume(CharBuffer chars) throws IOException {
				resultConsumer.accept(chars);
			}
		});
	}

	public static void collect(ReadableByteChannel supplier, char separater, IOExceptionConsumer<String> collector,
			CharsetDecoder decoder, CharBuffer charsBuffer, StringBuilder sbBuffer) throws IOException {
		IOExceptionConsumer<StringBuilder> col = sb -> collector.accept(sb.length() == 0 ? "" : sb.toString());
		collect0(supplier, separater, col, decoder, charsBuffer, sbBuffer);
	}

	public static void collect0(ReadableByteChannel supplier, char separater,
			IOExceptionConsumer<StringBuilder> collector, CharsetDecoder decoder, CharBuffer charsBuffer,
			StringBuilder sbBuffer) throws IOException {
		StringBuilder sb = orElse(sbBuffer, StringBuilder::new);

		IOExceptionConsumer<CharBuffer> eater = new IOExceptionConsumer<CharBuffer>() {
			@Override
			public void accept(CharBuffer e) throws IOException {
				while (e.hasRemaining()) {
					char c = e.get();
					if (c == separater) {
						if (sb.length() != 0 && c == '\n' && sb.charAt(sb.length() - 1) == '\r')
							sb.setLength(sb.length() - 1);

						collector.accept(sb);
						sb.setLength(0);
					} else {
						sb.append(c);
					}
				}
				e.clear();
			}
		};

		read(supplier, eater, decoder, charsBuffer);
		if (sb.length() != 0)
			collector.accept(sb);
	}

	public static void read(ReadableByteChannel supplier, final Appendable sink, CharsetDecoder decoder,
			CharBuffer charsBuffer) throws IOException {
		Objects.requireNonNull(supplier);
		Objects.requireNonNull(sink);

		ReadConfig config = new DefaultReadConfig(supplier, decoder, charsBuffer) {
			@Override
			public void postStart() {
				if (charsCount > 10)
					ensureCapacity(sink, charsCount);
			}

			@Override
			public void consume(CharBuffer chars) throws IOException {
				sink.append(chars);
				chars.clear();
			}
		};

		read(config);
	}

	public static void read(ReadConfig reader) throws IOException {
		Objects.requireNonNull(reader);

		final long n = reader.size();
		int size = n < 0 ? DEFAULT_BUFFER_SIZE : (int) n;

		ByteBuffer buf = buffer(reader, size);

		int read = reader.read(buf);
		buf.flip();

		if (!buf.hasRemaining())
			return;

		CharsetDecoder decoder = Objects.requireNonNull(reader.decoder());
		decoder.reset();

		if (size == n)
			reader.charsCount = (int) (size / decoder.averageCharsPerByte());
		else
			reader.charsCount = -1;

		CharBuffer charsBuffer = Objects.requireNonNull(reader.charsBuffer());
		reader.postStart();

		while (true) {
			boolean endOfInput = read == -1;

			while (true) {
				CoderResult c = decoder.decode(buf, charsBuffer, endOfInput);

				if (c.isUnderflow())
					break;
				else if (c.isOverflow())
					consume(charsBuffer, reader);
				else
					c.throwException();
			}

			if (endOfInput) {
				if (buf.hasRemaining())
					throw new IOException("not full converted");

				while (true) {
					CoderResult c = decoder.flush(charsBuffer);

					if (c.isUnderflow())
						break;
					else if (c.isOverflow())
						consume(charsBuffer, reader);
					else
						c.throwException();
				}
				break;
			}

			IOUtils.compactOrClear(buf);
			read = IOUtils.read(buf, false, reader);
		}

		consume(charsBuffer, reader);
		reader.close();
	}

	private static <E> E orElse(E e, Supplier<E> defaultValue) {
		if (e == null) {
			e = defaultValue.get();
			return e;
		}
		return e;
	}

	private static void consume(CharBuffer chars, ReadConfig config) throws IOException {
		chars.flip();
		config.consume(chars);
		if (!chars.hasRemaining())
			throw new IOException("buffer not consumed");
	}

	static void ensureCapacity(Appendable sink, int len) {
		if (sink instanceof StringBuilder) {
			StringBuilder sb = (StringBuilder) sink;
			sb.ensureCapacity(sb.length() + len + 10);
		} else if (sink instanceof StringBuffer) {
			StringBuffer sb = (StringBuffer) sink;
			sb.ensureCapacity(sb.length() + len + 10);
		}
	}

	public static void write(CharSequence sb, Path path) throws IOException {
		try (FileChannel fc = FileChannel.open(path, WRITE, CREATE, TRUNCATE_EXISTING)) {
			write(fc, sb);
		}
	}

	public static void appendText(CharSequence sb, Path path) throws IOException {
		try (FileChannel fc = FileChannel.open(path, WRITE, CREATE, APPEND)) {
			write(fc, sb);
		}
	}

	public static ByteBuffer encode(CharSequence data, ByteBuffer buffer, CharsetEncoder encoder)
			throws CharacterCodingException {
		Checker.requireNonNull("data, buffer, encoder", data, buffer, encoder);

		encoder.reset();
		CharBuffer cb = data instanceof CharBuffer ? (CharBuffer) data : CharBuffer.wrap(data);
		CoderResult c = encoder.encode(cb, buffer, true);

		if (c.isUnderflow())
			c = encoder.flush(buffer);

		if (c.isUnderflow()) {
			buffer.flip();
			return buffer;
		}
		StandardCharsets.UTF_8.encode(cb);

		if (c.isOverflow())
			return encoder.encode(cb);
		else {
			c.throwException();
			return null;
		}
	}
}
