package sam.io.serilizers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.function.Consumer;

import sam.functions.IOExceptionConsumer;
import sam.myutils.Checker;

public interface StringIOUtilsMinimal {

	public static void encode(CharSequence data, ByteBuffer buffer, CharsetEncoder encoder,
			IOExceptionConsumer<ByteBuffer> bufferConsumer) throws IOException {
		Checker.requireNonNull("data, buffer, encoder", data, buffer, encoder);

		if (buffer.capacity() < 50)
			throw new IllegalArgumentException("small buffer");

		encoder.reset();
		CharBuffer cb = data instanceof CharBuffer ? (CharBuffer) data : CharBuffer.wrap(data);

		while (true) {
			CoderResult c = encoder.encode(cb, buffer, true);

			if (c.isUnderflow())
				c = encoder.flush(buffer);

			if (c.isUnderflow() || c.isOverflow()) {
				buffer.flip();
				bufferConsumer.accept(buffer);
				buffer.clear();
				if (c.isUnderflow())
					return;
				else
					continue;
			}
			c.throwException();
		}
	}

	public static void decode(ByteBuffer source, CharBuffer buffer, CharsetDecoder decoder, Consumer<CharBuffer> consumer)
			throws CharacterCodingException {
		int n = (int) (source.remaining() * decoder.averageCharsPerByte());

		if (n == 0)
			return;
		
		decoder.reset();
		while(true) {
			CoderResult cr = source.hasRemaining() ? decoder.decode(source, buffer, true) : CoderResult.UNDERFLOW;
			if (cr.isUnderflow())
				cr = decoder.flush(buffer);

			if (cr.isOverflow() || cr.isUnderflow()) {
				buffer.flip();
				consumer.accept(buffer);
				buffer.clear();
				
				if (cr.isUnderflow())
					return;
				else
					continue;
			}
			cr.throwException();
		}
	}


}
