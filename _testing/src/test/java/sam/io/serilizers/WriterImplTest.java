package sam.io.serilizers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.util.Random;

import org.junit.jupiter.api.Test;

import com.thedeanda.lorem.LoremIpsum;

import sam.functions.IOExceptionConsumer;
import sam.io.IOConstants;

public class WriterImplTest {

	@Test
	public void test1() throws IOException {
		try(WriterImpl w = new WriterImpl(b -> fail(), ByteBuffer.allocate(100), CharBuffer.allocate(100), false, IOConstants.newEncoder())) {

		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		LoremIpsum lorem = LoremIpsum.getInstance();
		StringBuilder sb = new StringBuilder();
		Random r = new Random();
		CharsetEncoder encoder = IOConstants.newEncoder();

		general(bos, sb, encoder,  w -> {
			char c = (char) r.nextInt(257);
			w.write(c);
			sb.append(c);
		});

		general(bos, sb, encoder,  w -> {
			String c = lorem.getWords(r.nextInt(100));
			w.write(c);
			sb.append(c);
		});
		
		general(bos, sb, encoder,  w -> {
			char[] c = lorem.getWords(r.nextInt(100)).toCharArray();
			w.write(c);
			sb.append(c);
		});
		general(bos, sb, encoder,  w -> {
			char[] c = lorem.getWords(r.nextInt(100)).toCharArray();
			int off = c.length == 0 ? 0 : r.nextInt(c.length);
			int len = c.length - off == 0 ? 0 : r.nextInt(c.length - off);
			
			w.write(c, off, len);
			sb.append(c, off, len);
		});
		
	}

	private void general(ByteArrayOutputStream bos, StringBuilder sb, CharsetEncoder encoder, IOExceptionConsumer<WriterImpl> consumer) throws IOException {
		bos.reset();
		sb.setLength(0);

		try(WriterImpl w = new WriterImpl(b -> {bos.write(b.array(), b.position(), b.remaining()); b.clear();}, ByteBuffer.allocate(100), CharBuffer.allocate(100), false, encoder)) {
			for (int i = 0; i < 1000; i++) {
				consumer.accept(w);
			}
		}

		String actual = bos.toString("utf-8");

		System.out.println("sb.length: "+sb.length()+", bytes: "+bos.size());
		assertEquals(sb.length(), actual.length());
		assertEquals(sb.toString(), actual);
	}
}
