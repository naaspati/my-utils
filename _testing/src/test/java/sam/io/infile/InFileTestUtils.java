package sam.io.infile;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

import org.junit.platform.commons.JUnitException;

import sam.functions.IOExceptionBiConsumer;
import sam.io.BufferSupplier;
import sam.myutils.MyUtilsBytes;

public class InFileTestUtils {
	protected static int DEFAULT_SAMPLE_SIZE = Optional.ofNullable(System.getProperty("DEFAULT_SAMPLE_SIZE")).map(Integer::parseInt).orElse(100);
	protected static final Logger LOGGER = Logger.getLogger(InFileTestUtils.class.getName());
	
	protected void logMethod() {
		LOGGER.info("\n--------------------------\n"+Thread.currentThread().getStackTrace()[2]+"\n--------------------------\n");
	}
	protected void logMethod(String msg) {
		LOGGER.info("\n--------------------------\n"+msg+"\n"+Thread.currentThread().getStackTrace()[2]+"\n--------------------------\n");
	}

	protected IdentityHashMap<DataMeta, ByteBuffer> write(int maxBuffersize, int sampleSize, InFile file) throws IOException {
		Random random = new Random();
		IdentityHashMap<DataMeta, ByteBuffer> map = new IdentityHashMap<>();
		long pos = 0;
		for (int i = 0; i < sampleSize; i++) {
			ByteBuffer buf = buffer(maxBuffersize, random);
			buf.flip();
			DataMeta m = random.nextBoolean() ? file.write(buf) : file.write(BufferSupplier.of(buf));
			buf.clear();
			map.put(new DM(i,m), buf);

			assertEquals(pos, m.position, () -> m.toString());
			pos = m.position + m.size;
		}

		return map;
	}

	private class DM extends DataMeta {
		public int index;

		public DM(int index, DataMeta d) {
			super(d.position, d.size);
			this.index = index;
		}
		@Override
		public String toString() {
			return "DM(i: "+index+", p:"+position+", s:"+MyUtilsBytes.bytesToHumanReadableUnits(size, false)+")";
		}
	}
	protected ByteBuffer buffer(int maxBuffersize, Random random) {
		ByteBuffer bb = ByteBuffer.allocate(random.nextInt(maxBuffersize));
		return fill(bb, random);
	}
	protected ByteBuffer fill(ByteBuffer buffer, Random random) {
		while(buffer.hasRemaining())
			buffer.put((byte)random.nextInt(Byte.MAX_VALUE));
		return buffer;
	}

	protected List<DataMeta> shuffled(IdentityHashMap<DataMeta, ByteBuffer> map) {
		List<DataMeta> list = new ArrayList<>(map.keySet());
		Collections.shuffle(list);
		return list;
	}
	protected void common(int maxBuffersize, IOExceptionBiConsumer<IdentityHashMap<DataMeta, ByteBuffer>, InFile> consumer) throws IOException {
		if(DEFAULT_SAMPLE_SIZE < 10)
			throw new JUnitException("sampleSize("+DEFAULT_SAMPLE_SIZE+") < 10");
		
		common(maxBuffersize, DEFAULT_SAMPLE_SIZE, consumer);
	}
	protected void common(int maxBuffersize, int sampleSize, IOExceptionBiConsumer<IdentityHashMap<DataMeta, ByteBuffer>, InFile> consumer) throws IOException {
		if(sampleSize < 100)
			LOGGER.warning("very small sampleSize: "+sampleSize);
		if(sampleSize < 1)
			throw new JUnitException("sampleSize("+sampleSize+")");
		
		Path path = Files.createTempFile("TEST-INFILE", null);
		try(InFile file = new InFile(path, false)) {

			IdentityHashMap<DataMeta, ByteBuffer> map = write(maxBuffersize, sampleSize, file);

			assertEquals(file.acutualSize(), file.size());

			System.out.println("maxBuffersize: "+maxBuffersize+"\nsize: "+MyUtilsBytes.bytesToHumanReadableUnits(file.size(), false));
			consumer.accept(map, file);
		} finally {
			if(path != null) {
				Files.deleteIfExists(path);
			}
		}
	}
}
