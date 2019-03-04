package sam.io.serilizers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static sam.io.serilizers.StringIOUtils.write;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.thedeanda.lorem.LoremIpsum;

public class StringIOUtilsTest {

	@Test
	public void writeTest() throws IOException {
		assertThrows(NullPointerException.class, () -> write(b -> fail(), null));

		//check empty write
		write(b -> fail(), "");

		LoremIpsum ipsum = LoremIpsum.getInstance();

		for (int i = 0; i < 100; i++) {
			writeTest(ipsum.getWords(i));
			writeTest(ipsum.getParagraphs(i, i));
		}
	}

	private void writeTest(String s) throws IOException {
		CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
		ByteBuffer expected = encoder.encode(CharBuffer.wrap(s)).asReadOnlyBuffer();

		ByteBuffer actual = ByteBuffer.allocate(expected.remaining() + 10);

		write0(expected, actual, null, s);
		write0(expected, actual, null, CharBuffer.wrap(s));

		ByteBuffer buffer = ByteBuffer.allocate(100);
		write0(expected, actual, buffer, s);
		write0(expected, actual, buffer, CharBuffer.wrap(s));
	}

	private void write0(ByteBuffer expected, ByteBuffer actual, ByteBuffer buffer, CharSequence s) throws IOException {
		actual.clear();

		StringIOUtils.write(b -> {
			if(actual.position() > expected.remaining())
				fail(String.format("actual.position(%s) > expected.remaining(%s)", actual.position(), expected.remaining()));
			actual.put(b);
			b.clear();
		}, s, buffer);

		actual.flip();

		assertEquals(expected, actual);
	}
}
