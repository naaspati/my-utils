package sam.io.infile;
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
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;

import org.junit.jupiter.api.Test;

// import static org.junit.jupiter.api.Assertions.*;


//import sam.logging.SamPrefixLogFilter;

@Deprecated
public class InFileTest_old extends InFileTestUtils {

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
	public void testTransferInFile() throws IOException {
		testTransfer(Type.INFILE);

		common(50000, (map, file) -> {
			assertThrows(IOException.class, () -> file.transferTo(Collections.emptyList(), file));

			List<DataMeta> list = shuffled(map);
			Path temp = Files.createTempFile("testTransferINFILE-2", null);
			final long size = file.acutualSize();

			try(InFile fc = new InFile(temp, true)) {
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
			Path temp = Files.createTempFile("testTransfer_"+type, null);

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

	private void testTrasfer(Path temp, Map<DataMeta, ByteBuffer> map, List<DataMeta> list, InFile file, Type type) throws IOException {
		long size;

		switch (type) {
			case FILE:
				try(FileChannel fc = FileChannel.open(temp, WRITE, TRUNCATE_EXISTING)) {
					size = file.transferTo(list, fc);
				}	
				break;
			case INFILE:
				Files.deleteIfExists(temp);
				Map<DataMeta, DataMeta> map2;
				try(InFile fc = new InFile(temp, true)) {
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
		Path p = Files.createTempFile(null, null);
		try(InFile file = new InFile(p, true)) {
			assertEquals(0, file.size());
			
			
			Random r = new Random();
			ByteBuffer expected = ByteBuffer.allocate(800);
			expected.flip();
			
			fill(expected, r, true);
			DataMeta dm = file.write(expected);
			expected.clear();
			
			assertEquals(dm.position, 0);
			assertEquals(dm.size, expected.capacity());
			
			ByteBuffer expected2 = ByteBuffer.allocate(400);
			
			
			
		}
		
	}
}
