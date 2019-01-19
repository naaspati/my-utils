package sam.io.serilizers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.junit.jupiter.api.Test;

public abstract class BaseTest<E, F> {
	private static final int BASE_COUNT = 10 + new Random().nextInt(10); 

	static {
		System.setProperty("java.util.logging.config.file","test-logging.properties");
	}

	private ByteArrayOutputStream bos() {
		return new ByteArrayOutputStream();
	}
	private ByteArrayInputStream bis(ByteArrayOutputStream bos) {
		return new ByteArrayInputStream(bos.toByteArray());
	}
	private E array(int size) {
		if(size == 0)
			return newInstance(size);

		Random r = new Random();
		E array = newInstance(size);

		for (int i = 0; i < size; i++) 
			set(i, array, r);

		return array;
	}

	protected abstract E newInstance(int size);
	protected abstract void set(int index, E array, Random r);
	protected abstract void set(int index, E array, DataInputStream r) throws IOException;
	protected abstract F writeSingleValue(Random r, ByteArrayOutputStream os) throws IOException;
	protected abstract F readSingleValue(ByteArrayInputStream is) throws IOException;
	protected abstract void assert_equals(F expected, F actual);
	protected abstract void write(E array, ByteArrayOutputStream p) throws IOException;
	protected abstract void write(E array, ByteArrayOutputStream p, ByteBuffer buffer) throws IOException;
	protected abstract E copyOf(E array);
	protected abstract void assert_ArrayEquals(E expected, E array);
	protected abstract void writeSingleValue(int index, E array, DataOutputStream dos) throws IOException;
	protected abstract E readArray(ByteArrayInputStream p) throws IOException;
	protected abstract E readArray(ByteArrayInputStream p, ByteBuffer buffer) throws IOException;
	protected int length(E array) {
		return Array.getLength(array);
	}

	@Test
	public void singleValueTest() throws IOException {
		Random r = new Random();

		for (int i = 0; i < 10; i++) {
			ByteArrayOutputStream os = bos();
			F o = writeSingleValue(r, os);
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			F m = readSingleValue(is); 
			assert_equals(o, m);
		}
	}

	@Test
	public void writeWithDynamicBuffer() throws IOException {
		writeTest(null);
	}

	@Test
	public void writeWithFixedBuffer() throws IOException {
		writeTest(ByteBuffer.allocate(randomBufferSize()));
	}
	private void writeTest(ByteBuffer buffer) throws IOException {
		int n = BASE_COUNT;
		while(n < 100000) {
			E array = array(n);
			E expected = copyOf(array);

			ByteArrayOutputStream p = bos();

			if(buffer == null)
				write(array, p);
			else
				write(array, p, buffer);

			assert_ArrayEquals(expected, array);

			E loaded = null;
			try(InputStream is = new ByteArrayInputStream(p.toByteArray());
					DataInputStream dos = new DataInputStream(is)){
				loaded = newInstance(dos.readInt());
				int x = 0;
				while(true) 
					set(x++, loaded, dos);
			} catch (EOFException e) {}

			assert_ArrayEquals(expected, loaded);
			n = n*n;
		}
	}

	@Test
	public void readWithDynamicBuffer() throws IOException {
		readTest(null);
	}


	@Test
	public void readWithFixedBuffer() throws IOException {
		readTest(ByteBuffer.allocate(randomBufferSize()));
	}
	private void readTest(ByteBuffer buffer) throws IOException {
		int n = BASE_COUNT;
		while(n < 100000) {
			E array = array(n);
			E expected = copyOf(array);

			ByteArrayOutputStream p = bos();

			try(DataOutputStream dos = new DataOutputStream(p)){
				int len = Array.getLength(array);
				dos.writeInt(len);


				for (int i = 0; i < len; i++) 
					writeSingleValue(i, array, dos);
			}

			E loaded;

			if(buffer == null)
				loaded = readArray(bis(p));
			else
				loaded = readArray(bis(p), buffer);

			assert_ArrayEquals(expected, loaded);
			n = n*n;
		}
	}

	@Test
	public void readWriteWithDynamicBuffer() throws IOException {
		readWriteTest(null);
	}

	@Test
	public void readWriteWithFixedBuffer() throws IOException {
		readWriteTest(ByteBuffer.allocate(randomBufferSize()));
	}
	private int randomBufferSize() {
		return 512 + new Random().nextInt(1000);
	}
	private void readWriteTest(ByteBuffer buffer) throws IOException {
		int n = BASE_COUNT;
		while(n < 100000) {
			E array = array(n);
			E expected = copyOf(array);

			ByteArrayOutputStream p = bos();

			if(buffer == null)
				write(array, p);
			else
				write(array, p, buffer);

			assert_ArrayEquals(expected, array);

			E loaded;

			if(buffer == null)
				loaded = readArray(bis(p));
			else
				loaded = readArray(bis(p), buffer);

			assert_ArrayEquals(expected, loaded);
			n = n*n;
		}
	}


	@Test
	public void readWriteMultiArrayWithDynamicBuffer() throws IOException {
		readWriteMultiArrayTest(null);
	}

	@Test
	public void readWriteMultiArrayWithFixedBuffer() throws IOException {
		readWriteMultiArrayTest(ByteBuffer.allocate(randomBufferSize()));
	}

	private void readWriteMultiArrayTest(ByteBuffer buffer) throws IOException {
		ArrayList<E> expecteds = new ArrayList<>();
		Random r = new Random();
		for (int i = 0; i < 100; i++)
			expecteds.add(array(r.nextInt(500)));	

		ByteArrayOutputStream bos = bos();
		for (E e : expecteds) {
			if(buffer == null)
				write(e, bos);
			else 
				write(e, bos, buffer);
		} 

		ByteArrayInputStream bis = bis(bos);

		for (int i = 0; i < expecteds.size(); i++) {
			E actual; 
			if(buffer == null)
				actual = readArray(bis);
			else
				actual = readArray(bis, buffer);

			assert_ArrayEquals(expecteds.get(i), actual);
		}
	}
}
