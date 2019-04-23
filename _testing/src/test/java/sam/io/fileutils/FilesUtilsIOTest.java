package sam.io.fileutils;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;

import sam.functions.IOExceptionBiConsumer;

class FilesUtilsIOTest {
	@Test
	void writeTest() throws IOException, NoSuchAlgorithmException {
		Random r = new Random();


		int writeC[] = {0};
		ByteBuffer actual = ByteBuffer.allocate(20000);

		WritableByteChannel wbc = new WritableByteChannel() {

			@Override
			public boolean isOpen() {
				return true;
			}

			@Override
			public void close() throws IOException {
			}

			@Override
			public int write(ByteBuffer src) throws IOException {
				writeC[0]++;
				int n = src.remaining();
				actual.put(src);
				return n;
			}
		};

		ByteBuffer expected = ByteBuffer.allocate(actual.capacity()); 

		try(OutputStream fis = FilesUtilsIO.newOutputStream(wbc, ByteBuffer.allocate(200))) {
			byte[] bytes = new byte[400];
			int remain = actual.capacity();

			while(remain != 0) {
				final int len = Math.min(remain, r.nextInt(bytes.length));
				int off = r.nextInt(bytes.length);
				if(off + len >= bytes.length)
					off = 0;

				System.out.println("off: "+off+", len: "+len+", remain: "+remain);

				for (int j = off; j < bytes.length; j++) 
					bytes[j] = (byte) r.nextInt(Byte.MAX_VALUE);

				fis.write(bytes, off, len);
				expected.put(bytes, off, len);
				remain -= len;
			}
		}
		actual.flip();
		expected.flip();

		System.out.println("digest: "+ digest(actual));

		assertEquals(expected, actual);
		System.out.println("writeC: "+writeC[0]);
	}

	@Test
	void test() throws IOException, NoSuchAlgorithmException {
		test0(200, 100, 200);
		test0(2000, 100, 8124);
	}

	private void test0(final int buf_size, final int count, final int internal_buf_size) throws IOException, NoSuchAlgorithmException {
		byte[] bytes = new byte[buf_size];
		Path p = Files.createTempFile(null, null);

		ByteBuffer expected = ByteBuffer.allocate(buf_size * count);
		ByteBuffer buf = ByteBuffer.allocate(internal_buf_size);
		Random r = new Random();

		try {
			FileChannel fc = FileChannel.open(p, WRITE, TRUNCATE_EXISTING, CREATE);
			int writeC[] = {0};

			WritableByteChannel wbc = new WritableByteChannel() {

				@Override
				public boolean isOpen() {
					return fc.isOpen();
				}

				@Override
				public void close() throws IOException {
					fc.close();
				}

				@Override
				public int write(ByteBuffer src) throws IOException {
					writeC[0]++;
					return fc.write(src);
				}
			};

			try(OutputStream fis = FilesUtilsIO.newOutputStream(wbc, buf)) {
				for (int i = 0; i < count; i++) {
					for (int j = 0; j < bytes.length; j++) 
						bytes[j] = (byte) r.nextInt(Byte.MAX_VALUE);

					fis.write(bytes);
					expected.put(bytes);
				}
			}

			buf.clear();

			assertEquals(expected.limit(), expected.capacity());
			final byte[] digest = digest(expected);

			System.out.println("digest: "+Arrays.toString(digest));
			System.out.printf("size: %s, digest-len: %s, buf_size: %s, count: %s, internal_buf_size: %s, write-count: %s\n", expected.capacity(), digest.length, buf_size, count, internal_buf_size, writeC[0]);

			ByteBuffer actual = ByteBuffer.allocate(expected.capacity());

			int k = check(expected, actual, p, buf, (is, bs) -> {
				int n;
				while((n = is.read()) != -1) {
					bs.put((byte)n);
				}
			});

			System.out.println("bs.write(n): readCount: " + k);

			check(expected, actual, p, buf, 150);
			check(expected, actual, p, buf, 200);
			check(expected, actual, p, buf, 300);

			assertArrayEquals(digest, digest(expected));

			System.out.println("--------------------------------\n\n");
		} finally {
			Files.deleteIfExists(p);
		}
	}

	private byte[] digest(ByteBuffer buf) throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("md5").digest(buf.array());
	}

	private void check(ByteBuffer expected, ByteBuffer actual, Path p, ByteBuffer buf, int bytes_size) throws IOException {
		int k = check(expected, actual, p, buf, (is, bs) -> {
			assertSame(actual, bs);

			int n;
			byte[] b = new byte[bytes_size];

			while((n = is.read(b)) != -1)
				bs.put(b, 0, n);

		});
		System.out.println("bs.write(bytes("+bytes_size+")): readCount: " + k);
	}

	private int check(ByteBuffer expected, ByteBuffer actual, Path p, ByteBuffer buf, IOExceptionBiConsumer<InputStream,  ByteBuffer> consumer) throws IOException {
		FileChannel fc = FileChannel.open(p, READ);

		int[] readCount = {0};
		ReadableByteChannel rbc = new ReadableByteChannel() {

			@Override
			public boolean isOpen() {
				return fc.isOpen();
			}

			@Override
			public void close() throws IOException {
				fc.close();
			}

			@Override
			public int read(ByteBuffer dst) throws IOException {
				readCount[0]++;
				return fc.read(dst);
			}
		};

		buf.clear();
		actual.clear();
		Arrays.fill(actual.array(), (byte)0);

		try(InputStream is = FilesUtilsIO.newInputStream(rbc, buf)) {
			consumer.accept(is, actual);
		}

		assertFalse(fc.isOpen());
		expected.clear();
		actual.flip();
		assertEquals(expected, actual);

		return readCount[0];
	}

}
