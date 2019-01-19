package sam.io.serilizers;


import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Arrays;
import java.util.Random;

public class LongSerializerTest extends BaseTest<long[], Long> {
	
	private LongSerializer serializer() {
		return new LongSerializer();
	}

	@Override
	protected long[] newInstance(int size) {
		return new long[size];
	}

	@Override
	protected void set(int index, long[] array, Random r) {
		array[index] = r.nextLong();
	}

	@Override
	protected void set(int index, long[] array, DataInputStream r) throws IOException {
		array[index] = r.readLong();
	}

	@Override
	protected Long writeSingleValue(Random r, ByteArrayOutputStream os) throws IOException {
		long n = r.nextLong();
		serializer().write(n, os);
		return n;
	}
	@Override
	protected Long readSingleValue(ByteArrayInputStream is) throws IOException {
		return serializer().read(is);
	}

	@Override
	protected void assert_equals(Long expected, Long actual) {
		assertEquals(expected.longValue(), actual.longValue());
	}

	@Override
	protected long[] readArray(ByteArrayInputStream p) throws IOException {
		return serializer().readArray(p);
	}

	@Override
	protected void write(long[] array, ByteArrayOutputStream p) throws IOException {
		serializer().write(array, p);
	}

	@Override
	protected void write(long[] array, ByteArrayOutputStream p, ByteBuffer buffer) throws IOException {
		serializer().write(array, Channels.newChannel(p),buffer);
	}

	@Override
	protected long[] copyOf(long[] array) {
		return Arrays.copyOf(array, array.length);
	}

	@Override
	protected void assert_ArrayEquals(long[] expecteds, long[] actuals) {
		assertArrayEquals(expecteds, actuals);
	}

	@Override
	protected void writeSingleValue(int index, long[] array, DataOutputStream dos) throws IOException {
		serializer().write(array[index], dos);
	}
	@Override
	protected long[] readArray(ByteArrayInputStream p, ByteBuffer buffer) throws IOException {
		return serializer().readArray(Channels.newChannel(p), buffer);
	}
	
}
