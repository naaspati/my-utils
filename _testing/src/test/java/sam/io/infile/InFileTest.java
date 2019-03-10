package sam.io.infile;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import sam.io.BufferSupplier;
import sam.io.IOConstants;
import sam.test.commons.TempDir;

public class InFileTest extends InFileTestUtils {
	protected static int DEFAULT_SAMPLE_SIZE = Optional.ofNullable(System.getProperty("DEFAULT_SAMPLE_SIZE")).map(Integer::parseInt).orElse(100);
	protected static final Logger LOGGER = Logger.getLogger(InFileTestUtils.class.getName());
	private static TempDir tempdir;
	
	@BeforeAll
	public static void setup() throws IOException {
		tempdir = new TempDir(InFileTest.class.getSimpleName()) {
			@Override
			protected Logger logger() {
				return LOGGER;
			}
		};
	}
	
	@AfterAll
	public static void cleanup() throws IOException {
		tempdir.close();
		tempdir = null;
	}
	
	private void assertEql(ByteBuffer expected, ByteBuffer actual) {
		assertEquals(expected.remaining(), expected.capacity());
		assertEquals(actual.remaining(), expected.capacity());
		assertEquals(expected, actual);
	}
	

	@Test
	public void testGeneral() throws IOException {
		logMethod();

		Path p1 = tempdir.nextPath();
		Path p2 = tempdir.nextPath();

		assertThrows(IOException.class, () -> new InFileImpl(p1, false));
		assertTrue(Files.notExists(p1));

		assertDoesNotThrow(() ->  {
			try(InFileImpl file = new InFileImpl(p2, true);) { }
		});

		Files.deleteIfExists(p2);

		assertThrows(Exception.class, () ->  {
			try(InFileImpl file = new InFileImpl(p2, true);
					InFileImpl file2 = new InFileImpl(p2, true);) {
			}
		});

		assertTrue(Files.exists(p2));
	}

	@Test
	public void testReadWrite() throws IOException {
		try(InFileImpl file = new InFileImpl(tempdir.nextPath(), true)) {
			ByteBuffer bb = file.read(new DataMeta(0, 0), null);
			assertEquals(0, bb.capacity());

			Random rand = new Random();
			ByteBuffer expected = fill(ByteBuffer.allocate(8000), rand, true);

			long pos = file.size();
			int size = file.write(expected);

			checkRead(expected, file, new DataMeta(pos, size));

			expected.clear();
			fill(expected, rand, true);

			DataMeta dm = file.write(bufferSupplier(expected, 1000));

			checkRead(expected, file, dm);
		}
	}

	private void checkRead(ByteBuffer expected, InFileImpl file, DataMeta d) throws IOException {
		assertEquals(expected.capacity(), d.size);
		ByteBuffer actual = file.read(d, null);

		expected.clear();
		assertEql(expected, actual);
	}

	@Test
	public void testLargeReadWrite() throws IOException {
		logMethod();

		common(50000, (map, file) -> {
			List<DataMeta> list = shuffled(map);

			for (DataMeta d : list)
				assertArrayEquals(map.get(d).array(), file.read(d, null).array());
		});
	}

	@Test
	public void testReadWriteFixedBuffer() throws IOException {
		logMethod();

		common(10000, (map, file) -> {
			List<DataMeta> list = shuffled(map);

			ByteBuffer buffer = ByteBuffer.allocate(5000);

			for (DataMeta d : list) {
				ByteBuffer actual = file.read(d, buffer);
				ByteBuffer expected = map.get(d);

				if(d.size < buffer.capacity()) {
					assertSame(actual, buffer);
					assertEquals(expected, actual);
				} else {
					assertArrayEquals(expected.array(), actual.array());
				}

				buffer.clear();
			}
		});
	}
	@Test
	public void testReadWriteFixedBuffer2() throws IOException {
		logMethod();

		common(50000, (map, file) -> {
			List<DataMeta> list = shuffled(map);

			ByteBuffer buffer = ByteBuffer.allocate(500);
			ByteBuffer sink = ByteBuffer.allocate(list.stream().mapToInt(d -> d.size).max().getAsInt() + 50);

			for (DataMeta d : list) {
				file.read(d, buffer, b -> {
					sink.put(b);
					b.clear();
				});

				sink.flip();
				ByteBuffer expected = map.get(d);

				assertEquals(expected, sink);

				sink.clear();
				buffer.clear();
			}
		});
	}

	@Test
	public void testTransfer() throws IOException {
		testTransfer(Type.FILE);
	}
	@Test
	public void testTransferInFileImpl() throws IOException {
		testTransfer(Type.INFILE);

		common(50000, (map, file) -> {
			assertThrows(IOException.class, () -> file.transferTo(Collections.emptyList(), file));

			List<DataMeta> list = shuffled(map);
			Path temp = tempdir.nextPath();
			final long size = file.acutualSize();

			try(InFileImpl fc = new InFileImpl(temp, true)) {
				IdentityHashMap<DataMeta, DataMeta> map1 = file.transferTo(list, fc);
				Collections.shuffle(list);
				IdentityHashMap<DataMeta, DataMeta> map2 = file.transferTo(list, fc);

				assertEquals(file.size(), file.acutualSize());
				assertEquals(file.size(), size);

				assertEquals(fc.size(), fc.acutualSize());
				assertEquals(fc.size(), size * 2);

				for (Entry<DataMeta, DataMeta> m : map1.entrySet())
					assertArrayEquals(file.read(m.getKey(), null).array(), fc.read(m.getValue(), null).array());

				for (Entry<DataMeta, DataMeta> m : map2.entrySet())
					assertArrayEquals(file.read(m.getKey(), null).array(), fc.read(m.getValue(), null).array());

			} finally {
				try {
					Files.deleteIfExists(temp);
				} catch (IOException e) { 
					System.out.println(e);
				}
			}
		});
	}

	public void testTransfer(Type type) throws IOException {
		logMethod("type: " + Objects.requireNonNull(type));

		common(10000, (map, file) -> {
			List<DataMeta> list = shuffled(map);
			Path temp = tempdir.nextPath();

			try {
				logMethod("test transfer(shuffled)");
				testTrasfer(temp, map, list, file, type);

				list.sort(Comparator.comparingLong(d -> d.position));

				logMethod("test transfer(sorted)");
				testTrasfer(temp, map, list, file, type);

				logMethod("test transfer(mixed)");
				List<DataMeta> list2 = new ArrayList<>();

				final int size = list.size();
				list2.addAll(list.subList(0, 10));
				list2.add(list.get(50));
				list2.add(list.get(30));
				list2.add(list.get(25));
				list2.add(list.get(35));
				list2.addAll(list.subList(60, 80));

				assertEquals(size, list.size());
				testTrasfer(temp, map, list2, file, type);
			} finally {
				try {
					Files.deleteIfExists(temp);
				} catch (IOException e) { 
					System.out.println(e);
				}
			}
		});
	}

	private enum Type {
		FILE, INFILE
	}

	private void testTrasfer(Path temp, Map<DataMeta, ByteBuffer> map, List<DataMeta> list, InFileImpl file, Type type) throws IOException {
		long size;

		switch (type) {
			case FILE:
				try(FileChannel fc = FileChannel.open(temp, CREATE, WRITE, TRUNCATE_EXISTING)) {
					size = file.transferTo(list, fc);
				}	
				break;
			case INFILE:
				Files.deleteIfExists(temp);
				Map<DataMeta, DataMeta> map2;
				try(InFileImpl fc = new InFileImpl(temp, true)) {
					map2 = file.transferTo(list, fc);

					for (Entry<DataMeta, DataMeta> m : map2.entrySet())
						assertArrayEquals(file.read(m.getKey(), null).array(), fc.read(m.getValue(), null).array());
				}
				for (DataMeta d : list) 
					assertEquals(d.size, map2.get(d).size);

				assertEquals(sizeSum(map2.keySet()), sizeSum(list));
				assertEquals(map2.size(), list.size());
				assertTrue(map2.keySet().containsAll(list));
				assertTrue(list.containsAll(map2.keySet()));

				size = sizeSum(map2.keySet());

				break;
			default:
				throw new NullPointerException(String.valueOf(type));
		}

		try(FileChannel fc = FileChannel.open(temp, READ)) {
			assertEquals(fc.size(), size);

			ByteBuffer actual = ByteBuffer.allocate((int) fc.size() + 5);
			while(fc.read(actual) != -1) {}
			actual.flip();
			assertEquals(actual.limit(), fc.size());

			ByteBuffer expected = ByteBuffer.allocate((int) list.stream().mapToLong(d -> d.size).sum());
			list.forEach(d -> {
				ByteBuffer b = map.get(d);
				b.clear();
				expected.put(b);
			});

			expected.flip();

			assertNotSame(expected, actual);
			assertEquals(expected.position(), actual.position(), "position");
			assertEquals(expected.limit(), actual.limit(), "limit");
			while(expected.hasRemaining())
				assertEquals(expected.get(), actual.get());

			assertFalse(expected.hasRemaining());
			assertFalse(actual.hasRemaining());
		}
	}

	private int sizeSum(Collection<DataMeta> list) {
		return list.stream().mapToInt(d -> d.size).sum();
	}

	@Test
	public void replaceTest() throws IOException {
		Path p = tempdir.nextPath();
		try(InFileImpl file = new InFileImpl(p, true)) {
			assertEquals(0, file.size());

			final int CAP = 800;

			Random r = new Random();
			ByteBuffer expected = ByteBuffer.allocate(CAP);

			long pos = file.size();
			fill(expected, r, true);
			DataMeta dm = new DataMeta(pos, file.write(expected));
			checkRead(expected, file, dm);

			ByteBuffer expected2 = ByteBuffer.allocate(CAP);
			fill(expected2, r, true);
			DataMeta d2 = file.replace(dm, expected2);
			checkRead(expected2, file, d2);

			expected2 = ByteBuffer.allocate(CAP/2);
			fill(expected2, r, true);
			d2 = file.replace(dm, expected2);
			checkRead(expected2, file, d2);

			DataMeta d3 = d2;
			assertThrows(IOException.class, () -> file.replace(d3, ByteBuffer.allocate(CAP + 1)));

			DataMeta d = new DataMeta(0, 0);
			assertSame(file.replace(d, IOConstants.EMPTY_BUFFER), d);

			d = new DataMeta(pos, 40);
			d2 = file.replace(d, IOConstants.EMPTY_BUFFER);
			assertEquals(d.position, d2.position);
			assertEquals(0, d2.size);
		}
	}
	
	@Test
	public void replaceTest2() throws IOException {
		Path p = tempdir.nextPath();
		try(InFileImpl file = new InFileImpl(p, true)) {
			assertEquals(0, file.size());

			final int CAP = 800;

			Random r = new Random();
			ByteBuffer expected = ByteBuffer.allocate(CAP);

			long pos = file.size();
			fill(expected, r, true);
			DataMeta dm = file.write(bufferSupplier(expected, CAP/10));
			assertEquals(dm, new DataMeta(pos, CAP));
			checkRead(expected, file, dm);
			
			for (int i = CAP; i >= 0; i-=100) {
				LOGGER.info("size: "+i);
				ByteBuffer expected2 = ByteBuffer.allocate(i);
				fill(expected2, r, true);
				DataMeta d2 = file.replace(dm, bufferSupplier(expected2, CAP/10));
				
				expected2.flip();
				ByteBuffer buffer = file.read(d2, null);
				
				assertEquals(expected2, buffer);
			}
		}
	}
	
	@Test
	public void checkBufferSupplier() throws IOException {
		ByteBuffer expected2 = ByteBuffer.allocate(1000);
		Random r = new Random();
		fill(expected2, r, true);
		
		BufferSupplier bs = bufferSupplier(expected2, 19);
		ByteBuffer actual = ByteBuffer.allocate(1000);
		
		int size = 0;
		int loops = 0;

		while(true) {
			loops++;
			ByteBuffer buffer = bs.next();
			if(buffer == null && bs.isEndOfInput())
				break;

			if(!buffer.hasRemaining()) { 
				buffer.clear();
			} else {
				size += buffer.remaining();
				actual.put(buffer);
				buffer.clear();
			}

			if(bs.isEndOfInput())
				break;
		}
		
		System.out.println(loops);
		
		assertEquals(size, 1000);
		actual.flip();
		expected2.clear();
		assertEql(expected2, actual);
	}
}

