package sam.io.serilizers;

import static java.nio.charset.CodingErrorAction.REPORT;
import static sam.io.IOConstants.defaultBufferSize;
import static sam.io.IOConstants.defaultCharset;

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
import java.nio.charset.CodingErrorAction;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

import sam.io.IOUtils;

public final class StringIOUtils {
	static final Logger LOGGER = Logger.getLogger(StringIOUtils.class.getSimpleName());
	static final int SIZE = 0, BUFFER_SIZE = 1; 

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
	public static void write(WritableByteChannel c, CharSequence s) throws IOException {
		write(c, s, defaultCharset().newEncoder(), REPORT, REPORT);
	}
	public static void write(WritableByteChannel c, CharSequence s, CharsetEncoder encoder) throws IOException {
		write(c, s, encoder, REPORT, REPORT);
	}
	public static void write(WritableByteChannel c, CharSequence s, CharsetEncoder encoder, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		write(c, s, encoder, ByteBuffer.allocate(computeBufferSize(encoder, s)), onUnmappableCharacter, onMalformedInput);
	}
	public static void write(WritableByteChannel channel, CharSequence s, CharsetEncoder encoder, ByteBuffer buffer, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		if(s.length() == 0) return;

		CharBuffer chars = s instanceof CharBuffer ? (CharBuffer) s : CharBuffer.wrap(s);
		int loops = 0;

		while(true) {
			loops++;
			CoderResult c = encoder.encode(chars, buffer, true);
			checkResult(c, onUnmappableCharacter, onMalformedInput);

			IOUtils.write(buffer, channel, true);

			if(!chars.hasRemaining()) {
				while(true) {
					c = encoder.flush(buffer);
					IOUtils.write(buffer, channel, true);
					if(c.isUnderflow()) break;
				}
				break;
			}
		}

		int t2 = loops;
		LOGGER.fine(() -> "WRITE { charset:"+encoder.charset()+", CharSequence.length:"+s.length()+", ByteBuffer.capacity:"+buffer.capacity()+", loopCount:"+t2+"}"); 
	}

	public static StringBuilder read(ReadableByteChannel c) throws IOException {
		StringBuilder sb = new StringBuilder();
		read(c, sb);
		return sb;
	}
	public static void read(ReadableByteChannel c, Appendable sink) throws IOException {
		read(c, sink, null, null, null);
	}
	public static void read(ReadableByteChannel c, Appendable sink, CharsetDecoder decoder) throws IOException {
		read(c, sink, decoder, null, null);
	}
	public static void read(ReadableByteChannel c, Appendable sink, CharsetDecoder decoder, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		read(c, sink, decoder, null, null, onUnmappableCharacter, onMalformedInput);
	}
	public static void read(ReadableByteChannel channel, Appendable sink, CharsetDecoder decoder, CharBuffer chars, ByteBuffer buf, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		Objects.requireNonNull(sink);

		long[] bs = compute(channel);

		if(bs[SIZE] == 0) 
			return;
		
		onUnmappableCharacter = orElse(onUnmappableCharacter, REPORT);
		onMalformedInput = orElse(onMalformedInput, REPORT);
		
		decoder = orElse(decoder, () -> defaultCharset().newDecoder());
		decoder.reset();
		double d = decoder.averageCharsPerByte();
		int bytesPer = (int) Math.round(d);

		int buffersize = (int) bs[BUFFER_SIZE];

		chars = orElse(chars, () -> CharBuffer.allocate(buffersize/bytesPer > 100 ? 100 : buffersize/bytesPer));
		buf = orElse(buf, () -> ByteBuffer.allocate(buffersize));
		
		if(sink instanceof StringBuilder) {
			StringBuilder sb = (StringBuilder) sink; 
			sb.ensureCapacity(sb.length() + (int) (bs[SIZE]/bytesPer)+10);
		}
		else if(sink instanceof StringBuffer) {
			StringBuffer sb = (StringBuffer) sink; 
			sb.ensureCapacity(sb.length() + (int) (bs[SIZE]/bytesPer)+10);
		}

		while(true) {
			int read = channel.read(buf);
			boolean endOfInput = read == -1;
			buf.flip();

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
	static long[] compute(ReadableByteChannel c) throws IOException {
		long[] sizes = {-1, -1}; 

		if(c instanceof FileChannel) {
			long size = sizes[SIZE] = ((FileChannel)c).size();
			if(size == 0)
				return sizes;

			sizes[BUFFER_SIZE] = (int)(size < DEFAULT_BUFFER_SIZE ? size : DEFAULT_BUFFER_SIZE);
		} else {
			sizes[SIZE] = -1;
			sizes[BUFFER_SIZE] = DEFAULT_BUFFER_SIZE;
		}
		if(sizes[BUFFER_SIZE] < 20)
			sizes[BUFFER_SIZE] = 20;

		return sizes;
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
