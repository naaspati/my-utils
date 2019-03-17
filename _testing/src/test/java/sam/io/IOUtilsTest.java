package sam.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import sam.nopkg.Junk;
import sam.test.commons.TempDir;
import sam.test.commons.Utils;
import static java.nio.file.StandardOpenOption.*;

class IOUtilsTest {
	private static final Logger LOGGER = Logger.getLogger(IOUtilsTest.class.getSimpleName());

	static TempDir dir;

	@BeforeAll
	static void setup() throws IOException {
		dir = new TempDir("IOUtilsTest", LOGGER);
	}
	@AfterAll
	static void cleanup() throws IOException {
		if (dir != null)
			dir.close();
	}
	
	@Test
	void testPipe() throws IOException {
		testPipe(0);
		Random r = new Random();
		
		for (int i = 0; i < 100; i++) 
			testPipe(r.nextInt(10000));
	}

	void testPipe(int size) throws IOException {
		System.out.println("testPipe(size: "+size+")");
		
		byte[] expected = Utils.bytes(size, true);
		ByteArrayInputStream bis = new ByteArrayInputStream(expected);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(expected.length);

		pipe(expected, bis, bos,  null);
		pipe(expected, bis, bos,  new byte[200]);
		
		Path p = dir.nextPath();

		pipe(expected, bis, p,  null);
		pipe(expected, bis, p,  new byte[200]);
		
		pipeWritableChannel(expected, bis, p,  null);
		pipeWritableChannel(expected, bis, p,  new byte[200]);

		pipe(expected, p, bos,  null);
		pipe(expected, p, bos,  new byte[200]);
	}
	
	private void pipeWritableChannel(byte[] expected, ByteArrayInputStream bis, Path file, byte[] buffer) throws IOException {
		bis.reset();

		long n;
		try(FileChannel fc = FileChannel.open(file, WRITE, CREATE, TRUNCATE_EXISTING )) {
			n = IOUtils.pipe(bis, fc, buffer);	
		}
		
		assertEquals(expected.length, n);
		assertEquals(expected.length, Files.size(file));
		assertArrayEquals(expected, Files.readAllBytes(file));
	}
	
	private void pipe(byte[] expected, ByteArrayInputStream bis, Path p, byte[] buffer) throws IOException {
		bis.reset();

		long n = IOUtils.pipe(bis, p, buffer);

		assertEquals(expected.length, n);
		assertEquals(expected.length, Files.size(p));
		assertArrayEquals(expected, Files.readAllBytes(p));
	}
	
	private void pipe(byte[] expected, Path p, ByteArrayOutputStream bos, byte[] buffer) throws IOException {
		try(FileChannel  fc = FileChannel.open(p, WRITE, CREATE, TRUNCATE_EXISTING )) {
			ByteBuffer buf = ByteBuffer.wrap(expected);
			while(buf.hasRemaining())
				fc.write(buf);
		}

		bos.reset();
		long n = IOUtils.pipe(p, bos, buffer);

		assertEquals(expected.length, n);
		assertEquals(expected.length, bos.size());
		assertEquals(expected.length, Files.size(p));
		assertArrayEquals(expected, bos.toByteArray());
	}

	private void pipe(byte[] expected, ByteArrayInputStream bis, ByteArrayOutputStream bos, byte[] buffer) throws IOException {
		bis.reset();
		bos.reset();

		long n = IOUtils.pipe(bis, bos, buffer);
		
		assertEquals(expected.length, n);
		assertEquals(expected.length, bos.size());
		assertArrayEquals(expected, bos.toByteArray());
	}
	
	
	
	@Test
	void readTest() throws IOException {
		Random r = new Random();
		
		readTest(r, 0);
		
		for (int i = 0; i < 100; i++) 
			readTest(r, r.nextInt(10000));
	}
	private void readTest(Random r, int size) throws IOException {
		System.out.println("readTest(size: "+size+")");
		byte[] expected = Utils.bytes(size, true);
		ByteArrayInputStream bis = new ByteArrayInputStream(expected);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(expected.length);
		
		readTest(r, expected, bis, bos, ByteBuffer.allocate(200));
		
	}
	private void readTest(Random r, byte[] expected, ByteArrayInputStream bis, ByteArrayOutputStream bos, ByteBuffer buffer) throws IOException {
		bis.reset();
		bos.reset();
		
		assertEquals(bis.available(), expected.length);
		assertEquals(bos.size(), 0);
		
		int k = 0;
		int n = 0;
		boolean flip = r.nextBoolean();
		while((k = IOUtils.read(buffer, bis, flip)) != -1) {
			int j = IOUtils.write(buffer, bos, !flip);
			assertEquals(k, j);
			n += j;
		}
		
		assertEquals(expected.length, n);
		assertEquals(expected.length, bos.size());
		assertArrayEquals(expected, bos.toByteArray());
	}
}
