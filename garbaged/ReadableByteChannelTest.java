package sam.io;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import sam.test.commons.TempDir;
import sam.test.commons.Utils;

class ReadableByteChannelTest {
	private static final Logger logger = Logger.getLogger("ReadableByteChannelTest");
	private static TempDir dir;

	@BeforeAll
	public static void setup() throws IOException {
		dir = new TempDir("ReadableByteChannelTest", logger);
	}
	@AfterAll
	public static void cleanup() throws IOException {
		if(dir != null)
			dir.close();
	}

	@Test
	void ofBuffer() throws IOException {
		ByteBuffer buffer = Utils.buffer(1000, true);

		check1(0, 0, buffer);
		
		Random r = new Random();

		for (int i = 0; i < 1000; i++) {
			int a = r.nextInt(buffer.capacity() + 1);
			int b = r.nextInt(buffer.capacity() + 1);

			check1(Math.min(a, b), Math.max(a, b), buffer);
		}
	}
	
	private void check1(int pos, int limit, ByteBuffer buffer) throws IOException {
		buffer.clear();
		buffer.position(pos);
		buffer.limit(limit);

		String s = buffer.toString();

		int size = buffer.remaining();
		ReadableByteChannel sup = ReadableByteChannel.of(buffer);

		ByteBuffer buf2 = sup.next();

		if(size == 0)
			assertSame(IOConstants.EMPTY_BUFFER, buf2);
		else
			assertSame(buffer, buf2);
		assertTrue(sup.isEndOfInput());
		assertEquals(size, sup.size());
		assertEquals(size == 0, sup.isEmpty());
		if(size != 0)
			assertEquals(s, buf2.toString());
	}

	@Test
	void emptySupplier() throws IOException {
		ReadableByteChannel sup = ReadableByteChannel.EMPTY;

		assertSame(sup.next(), IOConstants.EMPTY_BUFFER);
		assertTrue(sup.isEndOfInput());
		assertTrue(sup.isEmpty());
		assertEquals(0, sup.size());
	}

	private int filchanel, readchanel, inputstream; 
	@Test
	void ofInputStreamTest() throws IOException {
		filchanel = 0;
		readchanel = 0;
		inputstream = 0;
		
		Path p = dir.nextPath();
		Random r = new Random();
		ofInputStreamTest(p, r, 0);
		
		for (int i = 0; i < 1000; i++) 
			ofInputStreamTest(p, r, r.nextInt(10000));
		
		System.out.printf("filchanel: %s, readchanel: %s, inputstream: %s\n", filchanel, readchanel, inputstream);
	}
	private void ofInputStreamTest(Path p, Random r, int size) throws IOException {
		byte[] bytes = Utils.bytes(size, true);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
		
		ofInputStreamTest(p, r, bytes, bis, bos, null);
		ofInputStreamTest(p, r, bytes, bis, bos, ByteBuffer.allocate(200));
	}
	private void ofInputStreamTest(Path p, Random r, byte[] expected, ByteArrayInputStream bis,ByteArrayOutputStream bos, ByteBuffer buffer) throws IOException {
		bis.reset();
		bos.reset();
		
		ReadableByteChannel b;
		FileChannel fc = null;
		
		switch (r.nextInt(3)) {
			case 0:
				b = ReadableByteChannel.of(bis, buffer);
				inputstream++;
				break;
			case 1: 
				b = ReadableByteChannel.of(Channels.newChannel(bis), buffer);
				readchanel++;
				break;
			case 2: 
				Files.write(p, expected, WRITE, CREATE, TRUNCATE_EXISTING);
				b = ReadableByteChannel.of(fc = FileChannel.open(p, READ), buffer);
				filchanel++;
				break;
			default:
				throw new IOException();
		}
		int n = 0;

		try {
			while(!b.isEndOfInput()) {
				ByteBuffer buf = b.next();
				int k = buf.remaining();
				int j = IOUtils.write(buf, bos, false); 
				n += j;
				assertEquals(k, j);
			}
			
			assertEquals(expected.length, bos.size());
			assertEquals(expected.length, n);
			assertArrayEquals(expected, bos.toByteArray());
		} finally {
			if(fc != null)
				fc.close();
		}
	}
}
