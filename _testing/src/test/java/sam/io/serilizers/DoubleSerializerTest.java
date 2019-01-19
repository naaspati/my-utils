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

public class DoubleSerializerTest extends BaseTest<double[], Double> {
	
	private DoubleSerializer serializer() {
		return new DoubleSerializer();
	}

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
		serializer().write(n, os);
		return n;
	}

	@Override
	protected Double readSingleValue(ByteArrayInputStream is) throws IOException {
		return serializer().read(is);
	}

	@Override
	protected void assert_equals(Double expected, Double actual) {
		assertEquals(expected.doubleValue(), actual.doubleValue());
	}

	@Override
	protected double[] readArray(ByteArrayInputStream p) throws IOException {
		return serializer().readArray(p);
	}

	@Override
	protected void write(double[] array, ByteArrayOutputStream p) throws IOException {
		serializer().write(array, p);
	}

	@Override
	protected void write(double[] array, ByteArrayOutputStream p, ByteBuffer buffer) throws IOException {
		serializer().write(array, Channels.newChannel(p),buffer);
	}

	@Override
	protected double[] copyOf(double[] array) {
		return Arrays.copyOf(array, array.length);
	}

	@Override
	protected void assert_ArrayEquals(double[] expecteds, double[] actuals) {
		assertArrayEquals(expecteds, actuals);
	}

	@Override
	protected void writeSingleValue(int index, double[] array, DataOutputStream dos) throws IOException {
		serializer().write(array[index], dos);
	}
	@Override
	protected double[] readArray(ByteArrayInputStream p, ByteBuffer buffer) throws IOException {
		return serializer().readArray(Channels.newChannel(p), buffer);
	}
	
}
