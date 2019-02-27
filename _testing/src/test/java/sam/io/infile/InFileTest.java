package sam.io.infile;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import sam.functions.IOExceptionBiConsumer;
import sam.myutils.MyUtilsBytes;

// import static org.junit.jupiter.api.Assertions.*;


//import sam.logging.SamPrefixLogFilter;
public class InFileTest {

	@Test
	public void testGeneral() throws IOException {
		Path p1 = Paths.get(String.valueOf(Math.random()*100));
		Path p2 = Paths.get(String.valueOf(Math.random()*100));

		assertTrue(Files.notExists(p1));
		assertThrows(IOException.class, () -> new InFile(p1, false));
		assertTrue(Files.notExists(p1));

		assertTrue(Files.notExists(p2));
		assertDoesNotThrow(() ->  {
			InFile file = new InFile(p2, true);
			file.close();
		});
		assertTrue(Files.exists(p2));

		Files.deleteIfExists(p1);
		Files.deleteIfExists(p2);
	}

	@Test
	public void testReadWrite() throws IOException {
		common(50000, (map, file) -> {
			List<DataMeta> list = new ArrayList<>(map.keySet());
			Collections.shuffle(list);

			for (DataMeta d : list)
				assertArrayEquals(map.get(d).array(), file.read(d, null).array());
		});
	}

	private void common(int maxBuffersize, IOExceptionBiConsumer<IdentityHashMap<DataMeta, ByteBuffer>, InFile> consumer) throws IOException {
		Path path = Files.createTempFile("TEST-INFILE", null);
		try(InFile file = new InFile(path, false)) {

			IdentityHashMap<DataMeta, ByteBuffer> map = write(maxBuffersize, file);

			assertEquals(file.acutualSize(), file.size());

			System.out.println("maxBuffersize: "+maxBuffersize+"\nsize: "+MyUtilsBytes.bytesToHumanReadableUnits(file.size(), false));
			consumer.accept(map, file);
		} finally {
			if(path != null) {
				Files.deleteIfExists(path);
			}
		}
	}

	@Test
	public void testReadWriteFixedBuffer() throws IOException {
		common(10000, (map, file) -> {
			List<DataMeta> list = new ArrayList<>(map.keySet());
			Collections.shuffle(list);
			
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
		assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
			common(50000, (map, file) -> {
				List<DataMeta> list = new ArrayList<>(map.keySet());
				Collections.shuffle(list);
				
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
		});
		
	}

	private IdentityHashMap<DataMeta, ByteBuffer> write(int maxBuffersize, InFile file) throws IOException {
		Random random = new Random();
		IdentityHashMap<DataMeta, ByteBuffer> map = new IdentityHashMap<>();
		long pos = 0;
		for (int i = 0; i < 100; i++) {
			ByteBuffer buf = buffer(maxBuffersize, random);
			buf.flip();
			DataMeta m = file.write(buf);
			buf.clear();
			map.put(m, buf);

			assertEquals(pos, m.position, () -> m.toString());
			pos = m.position + m.size;
		}

		return map;
	}

	private ByteBuffer buffer(int maxBuffersize, Random random) {
		ByteBuffer bb = ByteBuffer.allocate(random.nextInt(maxBuffersize));
		return fill(bb, random);
	}
	private ByteBuffer fill(ByteBuffer buffer, Random random) {
		while(buffer.hasRemaining())
			buffer.put((byte)random.nextInt(Byte.MAX_VALUE));

		return buffer;

	}
	
}
