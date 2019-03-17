package sam.io.serilizers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static sam.io.serilizers.StringIOUtils.write;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.thedeanda.lorem.LoremIpsum;

import sam.io.BufferSupplier;
import sam.io.IOConstants;
import sam.io.IOUtils;

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
	@Test
	void writeJoiningTest() throws IOException {
		writeJoiningTest0("\n");
		writeJoiningTest0("josen");
	}

	private void writeJoiningTest0(String separator) throws IOException {
		Random r = new Random();
		LoremIpsum lorem = LoremIpsum.getInstance();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10000);
		
		ArrayList<String> list = new ArrayList<>(100);
		
		StringIOUtils.writeJoining(new Iterator<String>() {
			int n = 0;
			@Override
			public String next() {
				if(n >= 100)
					throw new NoSuchElementException();
				
				String s = lorem.getWords(r.nextInt(10));
				list.add(s);
				n++;
				return s;
			}
			
			@Override
			public boolean hasNext() {
				return n < 100;
			}
		}, separator, b -> IOUtils.write(b, bos, false), null, null, null);
		
		StringBuilder sb = new StringBuilder();
		list.forEach(s -> sb.append(s).append(separator));
		String expected = sb.toString();
		String actual = bos.toString("utf-8");
		
		assertEquals(expected.length(), actual.length());
		assertEquals(expected, actual);
	}
	
	@Test
	public void collectTest() throws IOException {
		collectTest0('\n');
		collectTest0('\t');
	}

	private void collectTest0(char separator) throws IOException {
		Random r = new Random();
		LoremIpsum lorem = LoremIpsum.getInstance();
		final List<String> list = Stream.generate(() -> lorem.getWords(r.nextInt(10))).limit(100).collect(Collectors.toList());
		
		StringBuilder sb = new StringBuilder();
		list.forEach(s -> sb.append(s).append(separator));
		
		ByteBuffer buffer = IOConstants.newEncoder().encode(CharBuffer.wrap(sb));
		sb.setLength(0);
		
		ByteArrayInputStream bos = new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
		ArrayList<String> list2 = new ArrayList<>(100);
		
		StringBuilder sb2 = new StringBuilder();
		int cap = sb2.capacity();
		ByteBuffer buf = ByteBuffer.allocate(300);
		StringIOUtils.collect(BufferSupplier.of(bos, buf), separator, list2::add, null, null, sb2);
		
		System.out.println("max: "+list.stream().mapToInt(s -> s.length()).max().getAsInt()+", sb cap: ("+cap+" -> "+sb2.capacity()+")");
		
		assertEquals(list, list2);
	}
}
