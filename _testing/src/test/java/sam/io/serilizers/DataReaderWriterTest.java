package sam.io.serilizers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
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

import com.thedeanda.lorem.LoremIpsum;

import sam.functions.IOExceptionConsumer;
import sam.functions.IOExceptionFunction;
import sam.io.IOConstants;

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
				t.write(w, r, null);
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
	}


	@Test
	void stringWriteTest() throws IOException {

		ByteBuffer data_src_sink = ByteBuffer.allocate(8124);
		ByteBuffer buffer2 = ByteBuffer.allocate(200);  

		Random r = new Random();
		LoremIpsum lorem = new LoremIpsum();
		
		CharsetEncoder encoder = IOConstants.newEncoder();
		CharsetDecoder decoder = IOConstants.newDecoder();
		
		IOExceptionConsumer<String> check = s -> {
			System.out.println("write: test for ("+s.length()+"): "+s);
			data_src_sink.clear();
			buffer2.clear();
			
			write(data_src_sink, buffer2, w -> w.writeUTF(s));
			data_src_sink.flip();
			
			ByteBuffer converted = encoder.encode(CharBuffer.wrap(s));

			assertEquals(DataWriter.STRING_MARKER, data_src_sink.getShort());
			assertEquals(s.length(), data_src_sink.getInt());
			assertEquals(converted.remaining(), data_src_sink.getInt());

			assertArrayEquals(toArray(converted), toArray(data_src_sink));
			assertEquals(decoder.decode(data_src_sink).toString(), s);
		};
		
		check.accept("sameer");
		
		for (int i = 0; i < 100; i++) 
			check.accept(lorem.getWords(10, 30));	
	}
	
	@Test
	void stringReadTest() throws IOException {
		ByteBuffer data_src_sink = ByteBuffer.allocate(8124);
		ByteBuffer buffer2 = ByteBuffer.allocate(200);  

		Random r = new Random();
		LoremIpsum lorem = new LoremIpsum();
		
		CharsetEncoder encoder = IOConstants.newEncoder();
		
		IOExceptionConsumer<String> check = expected -> {
			System.out.println("read: test for ("+expected.length()+"): "+expected);
			data_src_sink.clear();
			buffer2.clear();
			
			data_src_sink.putShort(DataWriter.STRING_MARKER);
			data_src_sink.putInt(expected.length());
			
			ByteBuffer converted = encoder.encode(CharBuffer.wrap(expected));
			
			data_src_sink.putInt(converted.remaining());
			data_src_sink.put(converted);
			
			data_src_sink.flip();
			converted.flip();
			
			assertEquals(data_src_sink.remaining(), 2 + 4 * 2 + converted.remaining());
			
			String actual = read(data_src_sink, buffer2, d -> d.readUTF());
			
			System.out.println(Arrays.toString(toArray(converted)));
			assertEquals(expected, actual);
		};
		
		check.accept(lorem.getWords(50));
		check.accept("sameer");
		
		for (int i = 0; i < 100; i++) 
			check.accept(lorem.getWords(10, 30));		
	}

	private byte[] toArray(ByteBuffer b) {
		if(b.position() == 0 && b.limit() == b.capacity())
			return b.array();
		
		return Arrays.copyOfRange(b.array(), b.position(), b.limit());
	}

	@Test
	void stringTest() throws IOException {
		ByteBuffer data_src_sink = ByteBuffer.allocate(8124);
		ByteBuffer buffer2 = ByteBuffer.allocate(200);  

		Random r = new Random();
		LoremIpsum lorem = new LoremIpsum();

		IOExceptionConsumer<String> check = expected -> {
			System.out.println("string-check: "+expected);

			data_src_sink.clear();
			buffer2.clear();

			write(data_src_sink, buffer2, w -> w.writeUTF(expected));
			data_src_sink.flip();
			System.out.println("  bytes: "+data_src_sink.remaining());
			buffer2.clear();
			String s = read(data_src_sink, buffer2, d -> d.readUTF());

			assertEquals(expected, s);	
		};

		check.accept(null);
		check.accept("");
		check.accept("sameer");

		for (int i = 0; i < 500; i++) {
			String s = lorem.getWords(0, 100);
			check.accept(s);
		} 

	}

	private <E> E read(ByteBuffer data_src_sink, ByteBuffer buffer2, IOExceptionFunction<DataReader, E> reader) throws IOException {
		try(DataReader r = new DataReader(readable(data_src_sink), buffer2)) {
			return reader.apply(r);
		}
	}

	private void check(ByteBuffer data_src_sink, ByteBuffer buffer2, Random r, Unit t) throws IOException {
		data_src_sink.clear(); 
		buffer2.clear();

		write(data_src_sink, buffer2, w -> t.write(w, r, null));

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
		buffer.clear();
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
		void write(DataWriter writer, Random r, LoremIpsum lorem) throws IOException;
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
		public void write(DataWriter writer, Random r, LoremIpsum lorem) throws IOException { 
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
		public void write(DataWriter writer, Random r, LoremIpsum lorem) throws IOException { 
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
		public void write(DataWriter writer, Random r, LoremIpsum lorem) throws IOException { 
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
		public void write(DataWriter writer, Random r, LoremIpsum lorem) throws IOException { 
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
		public void write(DataWriter writer, Random r, LoremIpsum lorem) throws IOException { 
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
		public void write(DataWriter writer, Random r, LoremIpsum lorem) throws IOException { 
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
		public void write(DataWriter writer, Random r, LoremIpsum lorem) throws IOException { 
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
		public void write(DataWriter writer, Random r, LoremIpsum lorem) throws IOException { 
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
		String read, write;

		@Override
		public void read(DataReader reader) throws IOException {
			read = reader.readUTF();
		}

		@Override
		public void read(ByteBuffer buffer) {
			throw new IllegalAccessError();
		}

		@Override
		public void write(DataWriter writer, Random r, LoremIpsum lorem) throws IOException {
			writer.writeUTF(lorem.getWords(10, 200));
		}

		@Override
		public void validate() {
			assertEquals(read, write);
		}

	}
}
