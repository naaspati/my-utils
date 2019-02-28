package sam.io.serilizers;

import static java.nio.charset.CodingErrorAction.REPORT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.thedeanda.lorem.LoremIpsum;

import sam.io.BufferSupplier;

public class StringIOUtilsTest {

	@Test
	public void conversionTest() throws IOException {
		System.out.println("conversionTest()");

		LoremIpsum ipsum = LoremIpsum.getInstance();
		ByteBuffer[] write = {ByteBuffer.allocate(0)};
		ByteBuffer buffer = ByteBuffer.allocate(500);
		StringBuilder read = new StringBuilder();
		CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
		CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();


		for (int i = 0; i < 10; i++) {
			final String s = ipsum.getParagraphs(0, 15);
			write[0].clear();
			int loops[] = {0};

			StringIOUtils.write(buff -> {
				loops[0]++;
				ByteBuffer temp = write[0];
				if(temp.remaining() < buff.remaining()) {
					ByteBuffer bb = ByteBuffer.allocate(temp.capacity() + buff.capacity() * 2);
					temp.flip();
					bb.put(temp);
					System.out.printf("sink resize: %s -> %s\n", temp.capacity(), bb.capacity());
					write[0] = temp = bb;
				}
				temp.put(buff);
				buff.clear();
			}, s, encoder, buffer, REPORT, REPORT);

			write[0].flip();
			ByteBuffer expected = encoder.encode(CharBuffer.wrap(s));
			final int limit = expected.limit();

			System.out.printf("CONVERTED: string.length:%s, bytes:%s, loops:%s, result.limit: %s\n", s.length(), write[0].remaining(), loops[0], expected.remaining());

			assertEquals(expected, write[0]);

			read.setLength(0);
			StringIOUtils.read(expected, read);

			assertEquals(CharBuffer.wrap(s), CharBuffer.wrap(read));

			
			expected.clear();
			expected.limit(limit);

			test(s, decoder, read, expected, BufferSupplier.of(expected));
		}
	}

	private void test(String s,CharsetDecoder decoder, StringBuilder read, ByteBuffer buffer, BufferSupplier filler) throws IOException {
		int loops[] = {0};
		read.setLength(0);
		
		Appendable append = new Appendable() {
			Appendable ap = read;
			@Override
			public Appendable append(CharSequence csq, int start, int end) throws IOException {
				loops[0]++;
				return ap.append(csq, start, end);
			}
			@Override
			public Appendable append(char c) throws IOException {
				return ap.append(c);
			}
			@Override
			public Appendable append(CharSequence csq) throws IOException {
				loops[0]++;
				return ap.append(csq);
			}
		};

		StringIOUtils.read(filler, append, decoder, CharBuffer.allocate(50), REPORT, REPORT);

		assertEquals(s.length(), read.length());
		assertEquals(CharBuffer.wrap(s), CharBuffer.wrap(read));
		System.out.println("READ LOOPS: "+loops[0]);
	}
}
