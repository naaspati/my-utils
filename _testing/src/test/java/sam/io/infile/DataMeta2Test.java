package sam.io.infile;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

public class DataMeta2Test {
	private static AtomicInteger ida = new AtomicInteger(0);

	@Test
	public void singleWriteFile_generatedBuffer() throws IOException {
		singleWriteFile(null);
	}
	@Test
	public void singleWriteFile_fixedBuffer() throws IOException {
		singleWriteFile(ByteBuffer.allocate(100));
	}
	
	public void singleWriteFile(ByteBuffer buffer) throws IOException {
		System.err.println(Thread.currentThread().getStackTrace()[1]+"\n"+ Thread.currentThread().getStackTrace()[2]+"\n");
		
		Path p = Paths.get("DataMeta2Test");
		try {
			Random rad = new Random();

			assertThrows(NullPointerException.class, () -> DataMeta2.save(null, null, null));
			List<DataMeta2> expected = create(0, rad);

			DataMeta2.save(expected, p, buffer);
			assertEquals(Files.size(p), Long.BYTES + Integer.BYTES);

			List<DataMeta2> actual = DataMeta2.read(p, buffer);
			assertEquals(expected, actual);
			assertTrue(actual.isEmpty());

			for (int i = 0; i < 1000; i++) {
				Files.deleteIfExists(p); 
				expected = create(i, rad);
				
				DataMeta2.save(expected, p, buffer);
				assertEquals(Files.size(p), Long.BYTES + Integer.BYTES + expected.size() * DataMeta2.BYTES);

				actual = DataMeta2.read(p, buffer);
				assertEquals(expected.size(), actual.size());
				
				for (int n = 0; n < expected.size(); n++) {
					assertNotSame(expected.get(n), actual.get(n));
					assertEquals(expected.get(n), actual.get(n));
				} 
				
				System.out.println();
				System.err.println();
			}
		} finally {
			try {
				Files.deleteIfExists(p);
			} catch (Exception e) { }
		}

	}

	private List<DataMeta2> create(int size, Random rad) {
		System.err.println("created List<DataMeta2>.size: "+size);
		
		if(size == 0)
			return Collections.emptyList();

		DataMeta2[] list = new DataMeta2[size];

		for (int i = 0; i < list.length; i++)  
			list[i] = new DataMeta2(ida.incrementAndGet(), rad.nextLong(), rad.nextInt());
		
		return Arrays.asList(list);
	}


	@Test
	public void multiWriteFile_generatedBuffer() throws IOException {
		multiWriteFile(null);
	}
	@Test
	public void multiWriteFile_fixedBuffer() throws IOException {
		multiWriteFile(ByteBuffer.allocate(100));
	}
	@Test
	public void multiWriteFile_fixedBuffer2() throws IOException {
		multiWriteFile(ByteBuffer.allocate(8014));
	}
	
	public void multiWriteFile(ByteBuffer buffer) throws IOException {
		System.err.println(Thread.currentThread().getStackTrace()[1]+"\n"+ Thread.currentThread().getStackTrace()[2]+"\n");
		
		Path p = Paths.get("DataMeta2Test");
		try {
			Random rad = new Random();

			for (int i = 0; i < 100; i++) {
				Files.deleteIfExists(p);
				
				int size = rad.nextInt(20);
				System.err.println("size: "+size);
				ArrayList<List<DataMeta2>> expecteds = new ArrayList<>();
				
				try(FileChannel fc = FileChannel.open(p, WRITE, CREATE_NEW)) {
					for (int j = 0; j < size; j++) {
						List<DataMeta2> expected = create(j*100, rad);
						expecteds.add(expected);
						DataMeta2.write(expected, buffer, fc);
					}
				}
				
				try(FileChannel fc = FileChannel.open(p, READ)) {
					for (List<DataMeta2> expected : expecteds) {
						List<DataMeta2> actual = DataMeta2.read(fc, buffer);
				
						assertEquals(expected.size(), actual.size());
						for (int n = 0; n < expected.size(); n++) {
							assertNotSame(expected.get(n), actual.get(n));
							assertEquals(expected.get(n), actual.get(n));
						} 
							
					}
				}
			}
		} finally {
			try {
				Files.deleteIfExists(p);
			} catch (Exception e) { }
		}

	}


}
