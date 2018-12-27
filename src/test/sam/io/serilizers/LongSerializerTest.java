package test.sam.io.serilizers;

import static java.nio.file.StandardOpenOption.CREATE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import sam.io.serilizers.LongSerializer;

public class LongSerializerTest {

	static {
		System.setProperty("java.util.logging.config.file","test-logging.properties");
	}
	
	private long[] array(int n) {
		Random r = new Random();
		long[] array = new long[n];
		for (int i = 0; i < array.length; i++) 
			array[i] = r.nextLong();

		return array;
	}

	
	  @Test
	public void singleValueTest() throws IOException {
		Random r = new Random();

		for (long i = 0; i < 10; i++) {
			long n = r.nextLong();
			Path p = Files.createTempFile("junit", null);
			LongSerializer.write(n, p);
			assertEquals(n, LongSerializer.read(p), 0);
		}
	}

	
	 @Test
	public void writeWithDynamicBuffer() throws IOException {
		writeTest(null);
	}
	
	  @Test
	public void writeWithFixedBuffer() throws IOException {
		writeTest(ByteBuffer.allocate(1111));
	}
	private void writeTest(ByteBuffer buffer) throws IOException {
		int n = 10;
		while(n < 100000) {
			long[] array = array(n);
			long[] expected = Arrays.copyOf(array, array.length);

			Path p = Files.createTempFile("junit", null);

			if(buffer == null)
				LongSerializer.write(array, p);
			else
				LongSerializer.write(array, p, buffer);
			
			assertArrayEquals(expected, array);

			long[] loaded = null;
			try(DataInputStream dos = new DataInputStream(Files.newInputStream(p, CREATE))){
				loaded = new long[dos.readInt()];
				int x = 0;
				while(true) {
					loaded[x++] = dos.readLong();
				}
			} catch (EOFException e) {}

			assertArrayEquals(expected, loaded);
			n = n*n;
			Files.deleteIfExists(p);
		}
	}
	

	
	  @Test
	public void readWithDynamicBuffer() throws IOException {
		readTest(null);
	}
	
	
	  @Test
	public void readWithFixedBuffer() throws IOException {
		readTest(ByteBuffer.allocate(1111));
	}
	private void readTest(ByteBuffer buffer) throws IOException {
		int n = 10;
		while(n < 100000) {
			long[] array = array(n);
			long[] expected = Arrays.copyOf(array, array.length);

			Path p = Files.createTempFile("junit", null);
			
			try(DataOutputStream dos = new DataOutputStream(Files.newOutputStream(p, CREATE))){
				dos.writeInt(array.length);
				
				for (long i : array) 
					dos.writeLong(i);
			}
			
			long[] loaded;

			if(buffer == null)
				loaded = LongSerializer.readArray(p);
			else
				loaded = LongSerializer.readArray(p, buffer);

			assertArrayEquals(expected, loaded);
			n = n*n;
			Files.deleteIfExists(p);
		}
	}
	
	
	  @Test
	public void readWriteWithDynamicBuffer() throws IOException {
		readWriteTest(null);
	}
	
	 @Test
	public void readWriteWithFixedBuffer() throws IOException {
		readWriteTest(ByteBuffer.allocate(1111));
	}
	private void readWriteTest(ByteBuffer buffer) throws IOException {
		int n = 10;
		while(n < 100000) {
			long[] array = array(n);
			long[] expected = Arrays.copyOf(array, array.length);

			Path p = Files.createTempFile("junit", null);
			
			if(buffer == null)
				LongSerializer.write(array, p);
			else
				LongSerializer.write(array, p, buffer);
			
			assertArrayEquals(expected, array);
			
			long[] loaded;

			if(buffer == null)
				loaded = LongSerializer.readArray(p);
			else
				loaded = LongSerializer.readArray(p, buffer);

			assertArrayEquals(expected, loaded);
			n = n*n;
			Files.deleteIfExists(p);
		}
	}
}
