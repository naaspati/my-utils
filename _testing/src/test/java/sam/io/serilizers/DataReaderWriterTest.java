package sam.io.serilizers;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sam.test.commons.Utils.random_string;
import static sam.test.commons.Utils.toArray;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import sam.functions.IOExceptionConsumer;
import sam.functions.IOExceptionFunction;
import sam.io.IOConstants;
import sam.io.ReadableByteChannelCustom;
@SuppressWarnings("rawtypes")
class DataReaderWriterTest {
	static final List<Supplier<Unit>> unitsGenrator = Collections.unmodifiableList(
			Arrays.asList(
					ByteUnit::new,
					ShortUnit::new,
					IntUnit::new,
					BooleanUnit::new,
					CharUnit::new,
					LongUnit::new,
					FloatUnit::new,
					DoubleUnit::new)
			);

	@Test
	void singleTest() throws IOException {
		ByteBuffer data_src_sink = ByteBuffer.allocate(200);
		ByteBuffer buffer2 = ByteBuffer.allocate(20);  

		Random r = new Random();

		for (Supplier<Unit> s : unitsGenrator) 
			check(data_src_sink, buffer2, r, s.get());

		IdentityHashMap<Class, AtomicInteger> map = new IdentityHashMap<>();
		Function<Class, AtomicInteger> computer = t -> new AtomicInteger();

		for (int i = 0; i < 10000; i++) {
			Unit t = unitsGenrator.get(r.nextInt(unitsGenrator.size())).get();
			map.computeIfAbsent(t.getClass(), computer).incrementAndGet();
			check(data_src_sink, buffer2, r, t);
		}

		map.forEach((s,t) -> System.out.println(s.getSimpleName()+"  "+t.get()));
	}

	@Test
	void multiTest() throws IOException {
		ByteBuffer data_src_sink = ByteBuffer.allocate(8124);
		ByteBuffer buffer2 = ByteBuffer.allocate(200);  

		Random r = new Random();

		IdentityHashMap<Class, AtomicInteger> map = new IdentityHashMap<>();
		Function<Class, AtomicInteger> computer = t -> new AtomicInteger();
		ArrayList<Unit> written = new ArrayList<>(1000);

		try(DataWriter w = new DataWriter(writable(data_src_sink), buffer2)) {
			while(data_src_sink.remaining() > 208) {
				Unit t = unitsGenrator.get(r.nextInt(unitsGenrator.size())).get();
				map.computeIfAbsent(t.getClass(), computer).incrementAndGet();
				t.write(w, r);
				written.add(t);
			}
		}

		data_src_sink.flip();

		System.out.println("bytes write: "+data_src_sink.remaining());
		System.out.println("items: "+written.size());
		map.forEach((s,t) -> System.out.println(s.getSimpleName()+"  "+t.get()));

		for (Unit t : written) {
			t.read(data_src_sink);
			t.validate();
		}
		
		assertEquals(0, data_src_sink.remaining());
		data_src_sink.flip();
		
		try(DataReader d = new DataReader(ReadableByteChannelCustom.of(data_src_sink), data_src_sink)) {
			for (Unit t : written) {
				t.read(d);
				t.validate();
			}	
		}

		assertEquals(0, data_src_sink.remaining());
	}


	@Test
	void stringWriteTest() throws IOException {
		ByteBuffer data_src_sink = ByteBuffer.allocate(10000);
		ByteBuffer buffer2 = ByteBuffer.allocate(200);  

		Random r = new Random();

		CharsetEncoder encoder = IOConstants.newEncoder();
		CharsetDecoder decoder = IOConstants.newDecoder();

		IOExceptionConsumer<CharSequence> check = expected -> {
			data_src_sink.clear();
			buffer2.clear();

			write(data_src_sink, buffer2, w -> w.writeUTF(expected));
			data_src_sink.flip();

			ByteBuffer converted = encoder.encode(CharBuffer.wrap(expected));

			assertEquals(DataWriter.STRING_MARKER, data_src_sink.getShort());
			assertEquals(expected.length(), data_src_sink.getInt());
			assertEquals(converted.remaining(), data_src_sink.getInt());

			assertArrayEquals(toArray(converted), toArray(data_src_sink));

			if(expected instanceof CharBuffer){
				((CharBuffer) expected).clear();
				assertEquals(decoder.decode(data_src_sink), expected);
			} else
				assertEquals(decoder.decode(data_src_sink).toString(), expected);
		};

		check.accept("sameer");
		CharBuffer chars = CharBuffer.allocate(3000);

		for (int i = 0; i < 100; i++) 
			check.accept(random_string(chars, r));	
	}
	

	@ParameterizedTest
	@ValueSource(ints={200, 8124})
	void stringReadTest(int buf_size) throws IOException {
		ByteBuffer data_src_sink = ByteBuffer.allocate(10000);
		ByteBuffer buffer2 = ByteBuffer.allocate(buf_size);  

		Random r = new Random();

		IOExceptionConsumer<CharSequence> check = expected -> {
			data_src_sink.clear();
			buffer2.clear();

			write(data_src_sink, buffer2, w -> w.writeUTF(expected));
			data_src_sink.flip();

			String actual = read(data_src_sink, buffer2, w -> w.readUTF());

			// assertEquals(expected.length(), actual.length());
			assertEquals(expected.toString(), actual);
		};

		/* TODO
		 * 		check.accept("sameer");

		for (int i = 0; i < 100; i++) 
			check.accept(lorem.getWords(10, 100));	
		 */

		
		for (int j = 0; j < 20; j++) 
			check.accept(StringUnit.build(r));
	}

	@ParameterizedTest
	@ValueSource(ints={200, 8124})
	void fullTest(int buf_size) throws IOException {
		ByteBuffer buffer2 = ByteBuffer.allocate(buf_size);
		Path temp = Files.createTempFile("fullTest", null);
		System.out.println(temp);

		ArrayList<Unit> written = new ArrayList<>(1000);
		int writeCount[] = {0, 0};

		try(FileChannel fc = FileChannel.open(temp, WRITE, CREATE, TRUNCATE_EXISTING);
				WritableByteChannel wbc = new WritableByteChannel() {

					@Override
					public boolean isOpen() {
						return fc.isOpen();
					}

					@Override
					public void close() throws IOException {
						fc.close();
					}

					@Override
					public int write(ByteBuffer src) throws IOException {
						writeCount[0] += src.remaining();
						writeCount[1]++;

						return fc.write(src);
					}
				};
				DataWriter w = new DataWriter(wbc, buffer2)) {

			Random r = new Random();

			IdentityHashMap<Class, AtomicInteger> map = new IdentityHashMap<>();
			Function<Class, AtomicInteger> computer = t -> new AtomicInteger();

			List<Supplier<Unit>> unitsGenrator = new ArrayList<>(DataReaderWriterTest.unitsGenrator);
			unitsGenrator.add(StringUnit::new);

			for (int i = 0; i < 1000; i++) {
				Unit t = unitsGenrator.get(r.nextInt(unitsGenrator.size())).get();
				map.computeIfAbsent(t.getClass(), computer).incrementAndGet();
				t.write(w, r);
				written.add(t);
			}

			System.out.println("write-trip-count: "+writeCount[1]);
			System.out.println("bytes write: "+fc.size()+", "+writeCount[0]);
			System.out.println("items: "+written.size());
			map.forEach((s,t) -> System.out.println(s.getSimpleName()+"  "+t.get()));
			System.out.println("EMPTY string: "+StringUnit.EMPTY);
			System.out.println("NULL string:  "+StringUnit.NULLS);
		}

		buffer2.clear();

		int readCount[] = {0, 0};

		try(FileChannel fc = FileChannel.open(temp, READ);
				ReadableByteChannel channel = new ReadableByteChannel() {
					@Override
					public boolean isOpen() {
						return fc.isOpen();
					}

					@Override
					public void close() throws IOException {
						fc.close();
					}

					@Override
					public int read(ByteBuffer dst) throws IOException {
						int n = fc.read(dst);
						if(n != -1) {
							readCount[0] += n;
							readCount[1]++;							
						}
						return n;
					}
				};
				DataReader w = new DataReader(channel, buffer2)) {

			int tested = 0;
			for (Unit t : written) {
				t.read(w);
				t.validate();
				tested++;
			}

			System.out.println("read-trip-count: "+readCount[1]);
			System.out.println("bytes read: "+readCount[0]);
			System.out.println("tested : "+tested);
		}

		System.out.println("------------------------------------------\n");
		System.err.println("------------------------------------------\n");
	}

	private <E> E read(ByteBuffer data_src_sink, ByteBuffer buffer2, IOExceptionFunction<DataReader, E> reader) throws IOException {
		try(DataReader r = new DataReader(readable(data_src_sink), buffer2)) {
			return reader.apply(r);
		}
	}

	private void check(ByteBuffer data_src_sink, ByteBuffer buffer2, Random r, Unit t) throws IOException {
		data_src_sink.clear(); 
		buffer2.clear();

		write(data_src_sink, buffer2, w -> t.write(w, r));

		buffer2.clear();
		data_src_sink.flip();

		try(DataReader reader = reader(data_src_sink, buffer2)) {
			t.read(reader);
			t.validate();
			assertThrows(EOFException.class, () -> reader.readByte());	
		}
	}

	private DataReader reader(ByteBuffer data_src_sink, ByteBuffer buffer) throws IOException {
		return new DataReader(readable(data_src_sink), buffer);
	}

	private ReadableByteChannel readable(ByteBuffer data_src_sink) {
		return new ReadableByteChannel() {
			boolean open = true;

			@Override
			public boolean isOpen() {
				return open;
			}

			@Override
			public void close() throws IOException {
				if(!open)
					throw new IOException();
				open = false;
			}

			@Override
			public int read(ByteBuffer dst) throws IOException {
				if(!open)
					throw new IOException();

				int n = 0;
				while(dst.hasRemaining() && data_src_sink.hasRemaining()) {
					dst.put(data_src_sink.get());
					n++;
				}
				return n;
			}
		};
	}

	private void write(ByteBuffer buffer, ByteBuffer buffer2, IOExceptionConsumer<DataWriter> consumer) throws IOException {
		assertNotSame(buffer2, buffer);
		buffer2.clear();

		try(DataWriter d = new DataWriter(writable(buffer), buffer2)) {
			consumer.accept(d);
		}
	}

	private WritableByteChannel writable(ByteBuffer buffer) {
		return new WritableByteChannel() {
			boolean open = true;

			@Override
			public boolean isOpen() {
				return open;
			}

			@Override
			public void close() throws IOException {
				if(!open)
					throw new IOException();
				open = false;
			}

			@Override
			public int write(ByteBuffer src) throws IOException {
				if(!open)
					throw new IOException();

				int n = src.remaining();
				buffer.put(src);
				return n;
			}
		};
	}



	private static interface Unit {
		void read(DataReader reader) throws IOException;
		void read(ByteBuffer buffer);
		void write(DataWriter writer, Random r) throws IOException;
		void validate();
	}

	private static class ByteUnit implements Unit {
		byte read;
		byte write;

		@Override
		public void read(DataReader reader) throws IOException {
			read = reader.readByte();
		}

		@Override
		public void write(DataWriter writer, Random r) throws IOException { 
			write = (byte) r.nextInt(Byte.MAX_VALUE);
			writer.writeByte(write);
		}

		@Override
		public void validate() {
			assertEquals(write, read);
		}

		@Override
		public void read(ByteBuffer buffer) {
			read = buffer.get();
		}

		@Override
		public String toString() {
			return getClass().getSimpleName()+" [read=" + read + ", write=" + write + "]";
		}

	}
	private static class ShortUnit implements Unit {
		short read;
		short write;

		@Override
		public void read(DataReader reader) throws IOException {
			read = reader.readShort();
		}

		@Override
		public void write(DataWriter writer, Random r) throws IOException { 
			write = (short) r.nextInt(Short.MAX_VALUE);
			writer.writeShort(write);
		}

		@Override
		public void validate() {
			assertEquals(write, read);
		}

		@Override
		public void read(ByteBuffer buffer) {
			read = buffer.getShort();
		}
		@Override
		public String toString() {
			return getClass().getSimpleName()+" [read=" + read + ", write=" + write + "]";
		}
	}
	private static class IntUnit implements Unit {
		int read;
		int write;

		@Override
		public void read(DataReader reader) throws IOException {
			read = reader.readInt();
		}

		@Override
		public void write(DataWriter writer, Random r) throws IOException { 
			write = r.nextInt();
			writer.writeInt(write);
		}

		@Override
		public void validate() {
			assertEquals(write, read);
		}

		@Override
		public void read(ByteBuffer buffer) {
			read = buffer.getInt();

		}
		@Override
		public String toString() {
			return getClass().getSimpleName()+" [read=" + read + ", write=" + write + "]";
		}
	}
	private static class BooleanUnit implements Unit {
		boolean read;
		boolean write;

		@Override
		public void read(DataReader reader) throws IOException {
			read = reader.readBoolean();
		}

		@Override
		public void write(DataWriter writer, Random r) throws IOException { 
			write = r.nextBoolean();
			writer.writeBoolean(write);
		}

		@Override
		public void validate() {
			assertEquals(write, read);
		}

		@Override
		public void read(ByteBuffer buffer) {
			read = buffer.get() == 0 ? false : true;
		}
		@Override
		public String toString() {
			return getClass().getSimpleName()+" [read=" + read + ", write=" + write + "]";
		}
	}
	private static class CharUnit implements Unit {
		char read;
		char write;

		@Override
		public void read(DataReader reader) throws IOException {
			read = reader.readChar();
		}

		@Override
		public void write(DataWriter writer, Random r) throws IOException { 
			write = (char) ('0' + r.nextInt('z'));
			writer.writeChar(write);
		}

		@Override
		public void validate() {
			assertEquals(write, read);
		}

		@Override
		public void read(ByteBuffer buffer) {
			read = buffer.getChar();
		}
		@Override
		public String toString() {
			return getClass().getSimpleName()+" [read=" + read + ", write=" + write + "]";
		}
	}
	private static class LongUnit implements Unit {
		long read;
		long write;

		@Override
		public void read(DataReader reader) throws IOException {
			read = reader.readLong();
		}

		@Override
		public void write(DataWriter writer, Random r) throws IOException { 
			write = r.nextLong();
			writer.writeLong(write);
		}

		@Override
		public void validate() {
			assertEquals(write, read);
		}

		@Override
		public void read(ByteBuffer buffer) {
			read = buffer.getLong();
		}
		@Override
		public String toString() {
			return getClass().getSimpleName()+" [read=" + read + ", write=" + write + "]";
		}
	}
	private static class FloatUnit implements Unit {
		float read;
		float write;

		@Override
		public void read(DataReader reader) throws IOException {
			read = reader.readFloat();
		}

		@Override
		public void write(DataWriter writer, Random r) throws IOException { 
			write = r.nextFloat();
			writer.writeFloat(write);
		}

		@Override
		public void validate() {
			assertEquals(write, read);
		}

		@Override
		public void read(ByteBuffer buffer) {
			read = buffer.getFloat();
		}
		@Override
		public String toString() {
			return getClass().getSimpleName()+" [read=" + read + ", write=" + write + "]";
		}
	}
	private static class DoubleUnit implements Unit {
		double read;
		double write;

		@Override
		public void read(DataReader reader) throws IOException {
			read = reader.readDouble();
		}

		@Override
		public void write(DataWriter writer, Random r) throws IOException { 
			write = r.nextDouble();
			writer.writeDouble(write);
		}

		@Override
		public void validate() {
			assertEquals(write, read);
		}

		@Override
		public void read(ByteBuffer buffer) {
			read = buffer.getDouble();
		}
		@Override
		public String toString() {
			return getClass().getSimpleName()+" [read=" + read + ", write=" + write + "]";
		}
	}


	private static class StringUnit implements Unit {
		static int EMPTY, NULLS;
		static CharBuffer chars;

		String read, write;

		@Override
		public void read(DataReader reader) throws IOException {
			read = reader.readUTF();
		}

		public synchronized static String build(Random  r) {
			if(chars == null)
				chars = CharBuffer.allocate(5000);

			chars.clear();
			chars.limit(r.nextInt(5001));

			while(chars.hasRemaining())
				chars.put((char)r.nextInt(500));

			chars.flip();
			return chars.toString();
		}

		@Override
		public void read(ByteBuffer buffer) {
			throw new IllegalAccessError();
		}

		@Override
		public void write(DataWriter writer, Random r) throws IOException {
			if(r.nextInt()%18 == 0) {
				write = null;
				NULLS++;
			} else  {
				write = build(r);
				if(write.isEmpty())
					EMPTY++;
			}
			writer.writeUTF(write);
		}

		@Override
		public void validate() {
			if(read == null)
				assertSame(read, write);
			else {
				assertEquals(read.length(), write.length());
				assertEquals(read, write);	
			}
		}
	}
}
