package test.sam.io.serilizers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Arrays;
import java.util.Random;

import sam.io.serilizers.IntSerializer;

public class IntSerializerTest extends BaseTest<int[], Integer> {

	@Override
	protected int[] newInstance(int size) {
		return new int[size];
	}

	@Override
	protected void set(int index, int[] array, Random r) {
		array[index] = r.nextInt();
	}

	@Override
	protected void set(int index, int[] array, DataInputStream r) throws IOException {
		array[index] = r.readInt();
	}

	@Override
	protected Integer writeSingleValue(Random r, ByteArrayOutputStream os) throws IOException {
		int n = r.nextInt();
		IntSerializer.write(n, os);
		return n;
	}

	@Override
	protected Integer readSingleValue(ByteArrayInputStream is) throws IOException {
		return IntSerializer.read(is);
	}

	@Override
	protected void assert_equals(Integer expected, Integer actual) {
		assertEquals(expected.intValue(), actual.intValue());
	}

	@Override
	protected int[] readArray(ByteArrayInputStream p) throws IOException {
		return IntSerializer.readArray(p);
	}

	@Override
	protected void write(int[] array, ByteArrayOutputStream p) throws IOException {
		IntSerializer.write(array, p);
	}

	@Override
	protected void write(int[] array, ByteArrayOutputStream p, ByteBuffer buffer) throws IOException {
		IntSerializer.write(array, Channels.newChannel(p),buffer);
	}

	@Override
	protected int[] copyOf(int[] array) {
		return Arrays.copyOf(array, array.length);
	}

	@Override
	protected void assert_ArrayEquals(int[] expecteds, int[] actuals) {
		assertArrayEquals(expecteds, actuals);
	}

	@Override
	protected void writeSingleValue(int index, int[] array, DataOutputStream dos) throws IOException {
		IntSerializer.write(array[index], dos);
	}
	@Override
	protected int[] readArray(ByteArrayInputStream p, ByteBuffer buffer) throws IOException {
		return IntSerializer.readArray(Channels.newChannel(p), buffer);
	}
	
}
