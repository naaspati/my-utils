package test.sam.io.serilizers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import sam.io.serilizers.IntSerializer;

public class IntSerializerTest {

	static {
		System.setProperty("java.util.logging.config.file","test-logging.properties");
	}

	private int[] array(int n) {
		Random r = new Random();
		int[] array = new int[n];
		for (int i = 0; i < array.length; i++) 
			array[i] = r.nextInt();

		return array;
	}
	
	@Test
	public void singleValueTest() throws IOException {
		Random r = new Random();

		for (int i = 0; i < 10; i++) {
			int n = r.nextInt();
			Path p = Files.createTempFile("junit", null);
			IntSerializer.write(n, p);
			assertEquals(n, IntSerializer.read(p));
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
			int[] array = array(n);
			int[] expected = Arrays.copyOf(array, array.length);

			Path p = Files.createTempFile("junit", null);

			if(buffer == null)
				IntSerializer.write(array, p);
			else
				IntSerializer.write(array, p, buffer);
			
			assertArrayEquals(expected, array);

			int[] loaded = null;
			try(DataInputStream dos = new DataInputStream(Files.newInputStream(p, StandardOpenOption.CREATE))){
				loaded = new int[dos.readInt()];
				int x = 0;
				while(true) {
					loaded[x++] = dos.readInt();
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
			int[] array = array(n);
			int[] expected = Arrays.copyOf(array, array.length);

			Path p = Files.createTempFile("junit", null);
			
			try(DataOutputStream dos = new DataOutputStream(Files.newOutputStream(p, StandardOpenOption.CREATE))){
				dos.writeInt(array.length);
				
				for (int i : array) 
					dos.writeInt(i);
			}
			
			int[] loaded;

			if(buffer == null)
				loaded = IntSerializer.readArray(p);
			else
				loaded = IntSerializer.readArray(p, buffer);

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
			int[] array = array(n);
			int[] expected = Arrays.copyOf(array, array.length);

			Path p = Files.createTempFile("junit", null);
			
			if(buffer == null)
				IntSerializer.write(array, p);
			else
				IntSerializer.write(array, p, buffer);
			
			assertArrayEquals(expected, array);
			
			int[] loaded;

			if(buffer == null)
				loaded = IntSerializer.readArray(p);
			else
				loaded = IntSerializer.readArray(p, buffer);

			assertArrayEquals(expected, loaded);
			n = n*n;
			Files.deleteIfExists(p);
		}
	}


	
}
