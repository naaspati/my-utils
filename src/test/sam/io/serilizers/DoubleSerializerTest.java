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

import sam.io.serilizers.DoubleSerializer;

public class DoubleSerializerTest extends BaseTest<double[], Double> {

	@Override
	protected double[] newInstance(int size) {
		return new double[size];
	}

	@Override
	protected void set(int index, double[] array, Random r) {
		array[index] = r.nextDouble();
	}

	@Override
	protected void set(int index, double[] array, DataInputStream r) throws IOException {
		array[index] = r.readDouble();
	}

	@Override
	protected Double writeSingleValue(Random r, ByteArrayOutputStream os) throws IOException {
		double n = r.nextDouble();
		DoubleSerializer.write(n, os);
		return n;
	}
	@Override
	protected Double readSingleValue(ByteArrayInputStream is) throws IOException {
		return DoubleSerializer.read(is);
	}

	@Override
	protected void assert_equals(Double expected, Double actual) {
		assertEquals(expected.doubleValue(), actual.doubleValue(), 0);
	}

	@Override
	protected double[] readArray(ByteArrayInputStream p) throws IOException {
		return DoubleSerializer.readArray(p);
	}

	@Override
	protected void write(double[] array, ByteArrayOutputStream p) throws IOException {
		DoubleSerializer.write(array, p);
	}

	@Override
	protected void write(double[] array, ByteArrayOutputStream p, ByteBuffer buffer) throws IOException {
		DoubleSerializer.write(array, Channels.newChannel(p),buffer);
	}

	@Override
	protected double[] copyOf(double[] array) {
		return Arrays.copyOf(array, array.length);
	}

	@Override
	protected void assert_ArrayEquals(double[] expecteds, double[] actuals) {
		assertArrayEquals(expecteds, actuals, 0);
	}

	@Override
	protected void writeSingleValue(int index, double[] array, DataOutputStream dos) throws IOException {
		DoubleSerializer.write(array[index], dos);
	}
	@Override
	protected double[] readArray(ByteArrayInputStream p, ByteBuffer buffer) throws IOException {
		return DoubleSerializer.readArray(Channels.newChannel(p), buffer);
	}
	
}
