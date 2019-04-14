package sam.io.infile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
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
import sam.io.ReadableByteChannelCustom;
import sam.io.infile.DataMeta;
import sam.myutils.MyUtilsBytes;

class InFileTestUtils {
	static int DEFAULT_SAMPLE_SIZE = Optional.ofNullable(System.getProperty("DEFAULT_SAMPLE_SIZE")).map(Integer::parseInt).orElse(100);
	static final Logger LOGGER = Logger.getLogger(InFileTestUtils.class.getName());
	
	void logMethod() {
		LOGGER.info("\n--------------------------\n"+Thread.currentThread().getStackTrace()[2]+"\n--------------------------\n");
	}
	void logMethod(String msg) {
		LOGGER.info("\n--------------------------\n"+msg+"\n"+Thread.currentThread().getStackTrace()[2]+"\n--------------------------\n");
	}

	IdentityHashMap<DataMeta, ByteBuffer> write(int maxBuffersize, int sampleSize, InFileImpl file) throws IOException {
		Random random = new Random();
		IdentityHashMap<DataMeta, ByteBuffer> map = new IdentityHashMap<>();
		long pos = 0;
		for (int i = 0; i < sampleSize; i++) {
			ByteBuffer buf = buffer(maxBuffersize, random);
			buf.flip();
			DataMeta m = random.nextBoolean() ? file.write2(buf) : file.write(ReadableByteChannelCustom.of(buf));
			buf.clear();
			map.put(new DM(i,m), buf);

			assertEquals(pos, m.position, () -> m.toString());
			pos = m.position + m.size;
		}

		return map;
	}
	
	ReadableByteChannel bufferSupplier(ByteBuffer source, int buffersize) {
		ByteBuffer buffer = ByteBuffer.allocate(buffersize);

		return new ReadableByteChannelCustom() {
			@Override
			public long size() throws IOException {
				return source.remaining();
			}
			@Override
			public ByteBuffer buffer() {
				return buffer;
			}
			
			@Override
			public boolean isOpen() {
				return true;
			}
			
			@Override
			public void close() throws IOException {
			}
			
			@Override
			public int read(ByteBuffer dst) throws IOException {
				assertSame(dst, buffer);
				
				if(!source.hasRemaining())
					return -1;
				
				int n = 0;
				while(dst.hasRemaining() && source.hasRemaining()) {
					dst.put(source.get());
					n++;
				}
				
				return n;
			}
		};
	}

	class DM extends DataMeta {
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
	ByteBuffer buffer(int maxBuffersize, Random random) {
		ByteBuffer bb = ByteBuffer.allocate(random.nextInt(maxBuffersize));
		return fill(bb, random, false);
	}
	ByteBuffer fill(ByteBuffer buffer, Random random, boolean flip) {
		while(buffer.hasRemaining())
			buffer.put((byte)random.nextInt(Byte.MAX_VALUE));
		if(flip)
			buffer.flip();
		
		
		long sum = 0;
		byte[] bytes = buffer.array();
		for (int i = buffer.position(); i < buffer.limit(); i++) 
			sum += bytes[i];
		
		LOGGER.fine(String.format("buffer filled: pos:%s, limit:%s, capacity:%s, sum:%s ", buffer.position(), buffer.limit(), buffer.capacity(), sum));
		
		return buffer;
	}

	List<DataMeta> shuffled(IdentityHashMap<DataMeta, ByteBuffer> map) {
		List<DataMeta> list = new ArrayList<>(map.keySet());
		Collections.shuffle(list);
		return list;
	}
	void common(int maxBuffersize, IOExceptionBiConsumer<IdentityHashMap<DataMeta, ByteBuffer>, InFileImpl> consumer) throws IOException {
		if(DEFAULT_SAMPLE_SIZE < 10)
			throw new JUnitException("sampleSize("+DEFAULT_SAMPLE_SIZE+") < 10");
		
		common(maxBuffersize, DEFAULT_SAMPLE_SIZE, consumer);
	}
	void common(int maxBuffersize, int sampleSize, IOExceptionBiConsumer<IdentityHashMap<DataMeta, ByteBuffer>, InFileImpl> consumer) throws IOException {
		if(sampleSize < 100)
			LOGGER.warning("very small sampleSize: "+sampleSize);
		if(sampleSize < 1)
			throw new JUnitException("sampleSize("+sampleSize+")");
		
		Path path = Files.createTempFile("TEST-INFILE", null);
		try(InFileImpl file = new InFileImpl(path, false)) {

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
