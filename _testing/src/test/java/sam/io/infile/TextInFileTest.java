package sam.io.infile;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
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

import org.junit.jupiter.api.Test;

import com.thedeanda.lorem.LoremIpsum;

public class TextInFileTest extends TextInFileTestUtils {

	@Test
	public void testGeneral() throws IOException {
		logMethod();

		Path p1 = Files.createTempFile(null, null);
		Path p2 = Files.createTempFile(null, null);

		try {
			Files.deleteIfExists(p1);
			Files.deleteIfExists(p2);

			assertTrue(Files.notExists(p1));
			assertThrows(IOException.class, () -> new Temp(p1, false));
			assertTrue(Files.notExists(p1));

			assertTrue(Files.notExists(p2));
			assertDoesNotThrow(() ->  {
				Temp file = new Temp(p2, true);
				file.close();
			});
			assertTrue(Files.exists(p2));

		} finally {
			Files.deleteIfExists(p1);
			Files.deleteIfExists(p2);
		}
	}

	@Test
	public void testReadWrite() throws IOException {
		try(Temp file = new Temp(Files.createTempFile("testReadWrite", null), false)) {
			String string = LoremIpsum.getInstance().getParagraphs(5, 15);

			DataMeta meta = file.write(string);
			String read = file.readText(meta);

			assertEquals(string,read);

			Appen sb = new Appen(read.length() + 100);
			file.readText(meta, ByteBuffer.allocate(1000), CharBuffer.allocate(100), sb);

			equalAssert(string, sb);
			System.out.println("testGeneral(), loops: "+sb.loops+", sb.len: "+sb.sb.length()+", sb.cap: "+sb.sb.capacity());
		}
	}

	@Test
	public void testLargeReadWrite() throws IOException {
		logMethod();

		common(30, 60, (map, file) -> {
			List<DataMeta> list = shuffled(map);

			for (DataMeta d : list)
				assertEquals(map.get(d), file.readText(d));
			
			Collections.shuffle(list);
			
			StringBuilder sb = new StringBuilder();
			ByteBuffer buffer = ByteBuffer.allocate(1000);
			CharBuffer chars = CharBuffer.allocate(100);
			
			for (DataMeta d : list) {
				buffer.clear();
				sb.setLength(0);
				file.readText(d, sb, buffer, chars);
				
				assertEquals(CharBuffer.wrap(map.get(d)), CharBuffer.wrap(sb));
			}
			
		});
	}

	@Test
	public void testReadWriteFixedBuffer() throws IOException {
		logMethod();

		common(100, 200, (map, file) -> {
			List<DataMeta> list = shuffled(map);

			CharBuffer chars = CharBuffer.allocate(500);
			ByteBuffer bytes = ByteBuffer.allocate(8124);
			Appen sink = new Appen();

			for (DataMeta d : list) {
				sink.sb.setLength(0);
				bytes.clear();
				chars.clear();
				file.readText(d, bytes, chars, sink);
				String expected = map.get(d);

				equalAssert(expected, sink);
				LOGGER.fine("Appen loops: "+sink.loops);
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

		common(100, 200, (map, file) -> {
			assertThrows(IOException.class, () -> file.transferTo(Collections.emptyList(), file));

			List<DataMeta> list = shuffled(map);
			Path temp = Files.createTempFile("testTransferINFILE-2", null);
			final long size = file.acutualSize();

			try(Temp fc = new Temp(temp, true)) {
				IdentityHashMap<DataMeta, DataMeta> map1 = file.transferTo(list, fc);
				Collections.shuffle(list);
				IdentityHashMap<DataMeta, DataMeta> map2 = file.transferTo(list, fc);

				assertEquals(file.size(), file.acutualSize());
				assertEquals(file.size(), size);

				assertEquals(fc.size(), fc.acutualSize());
				assertEquals(fc.size(), size * 2);

				for (Entry<DataMeta, DataMeta> m : map1.entrySet())
					assertEquals(file.readText(m.getKey()), fc.readText(m.getValue()));

				for (Entry<DataMeta, DataMeta> m : map2.entrySet())
					assertEquals(file.readText(m.getKey()), fc.readText(m.getValue()));

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

		common(100, 200, (map, file) -> {
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

	private void testTrasfer(Path temp, Map<DataMeta, String> map, List<DataMeta> list, Temp file, Type type) throws IOException {
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
				try(Temp fc = new Temp(temp, true)) {
					map2 = file.transferTo(list, fc);

					for (Entry<DataMeta, DataMeta> m : map2.entrySet()) {
						LOGGER.fine("read: "+m);
						assertEquals(file.readText(m.getKey()), fc.readText(m.getValue()));
					}
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

			ByteBuffer buffer = ByteBuffer.allocate((int) fc.size() + 5);
			while(fc.read(buffer) != -1) {}
			buffer.flip();
			assertEquals(buffer.limit(), fc.size());

			CharBuffer actual = file.decoder.decode(buffer);

			StringBuilder expected = new StringBuilder(list.stream().mapToInt(d -> map.get(d).length()).sum() + 50);
			list.forEach(d -> expected.append(map.get(d)));

			assertEquals(CharBuffer.wrap(expected), actual);
		}
	}

	private int sizeSum(Collection<DataMeta> list) {
		return list.stream().mapToInt(d -> d.size).sum();
	}


}
