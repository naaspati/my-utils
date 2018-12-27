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

import org.junit.Ignore;
import org.junit.Test;

import sam.io.serilizers.DoubleSerializer;

public class DoubleSerializerTest {

	static {
		System.setProperty("java.util.logging.config.file","test-logging.properties");
	}
	
	private double[] array(int n) {
		Random r = new Random();
		double[] array = new double[n];
		for (int i = 0; i < array.length; i++) 
			array[i] = r.nextDouble()*r.nextInt();

		return array;
	}

	
	@Test
	public void singleValueTest() throws IOException {
		Random r = new Random();

		for (double i = 0; i < 10; i++) {
			double n = r.nextDouble();
			Path p = Files.createTempFile("junit", null);
			DoubleSerializer.write(n, p);
			assertEquals(n, DoubleSerializer.read(p), 0);
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
			double[] array = array(n);
			double[] expected = Arrays.copyOf(array, array.length);

			Path p = Files.createTempFile("junit", null);

			if(buffer == null)
				DoubleSerializer.write(array, p);
			else
				DoubleSerializer.write(array, p, buffer);
			
			assertArrayEquals(expected, array, 0);

			double[] loaded = null;
			try(DataInputStream dos = new DataInputStream(Files.newInputStream(p, StandardOpenOption.CREATE))){
				loaded = new double[dos.readInt()];
				int x = 0;
				while(true) {
					loaded[x++] = dos.readDouble();
				}
			} catch (EOFException e) {}

			assertArrayEquals(expected, loaded, 0);
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
			double[] array = array(n);
			double[] expected = Arrays.copyOf(array, array.length);

			Path p = Files.createTempFile("junit", null);
			
			try(DataOutputStream dos = new DataOutputStream(Files.newOutputStream(p, StandardOpenOption.CREATE))){
				dos.writeInt(array.length);
				
				for (double i : array) 
					dos.writeDouble(i);
			}
			
			double[] loaded;

			if(buffer == null)
				loaded = DoubleSerializer.readArray(p);
			else
				loaded = DoubleSerializer.readArray(p, buffer);

			assertArrayEquals(expected, loaded, 0);
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
			double[] array = array(n);
			double[] expected = Arrays.copyOf(array, array.length);

			Path p = Files.createTempFile("junit", null);
			
			if(buffer == null)
				DoubleSerializer.write(array, p);
			else
				DoubleSerializer.write(array, p, buffer);
			
			assertArrayEquals(expected, array, 0);
			
			double[] loaded;

			if(buffer == null)
				loaded = DoubleSerializer.readArray(p);
			else
				loaded = DoubleSerializer.readArray(p, buffer);

			assertArrayEquals(expected, loaded, 0);
			n = n*n;
			Files.deleteIfExists(p);
		}
	}
}
