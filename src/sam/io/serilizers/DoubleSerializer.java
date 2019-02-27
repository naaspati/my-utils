package sam.io.serilizers;

import static java.lang.Double.BYTES;
import static java.nio.channels.Channels.newChannel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

public class DoubleSerializer extends BaseSerializer<double[]> {
	public void write(double value, Path path) throws IOException {
		Utils.write(putDouble(value), path);
	}
	public ByteBuffer putDouble(double value) {
		ByteBuffer buffer = ByteBuffer.allocate(BYTES);
		buffer.clear();
		buffer.putDouble(value);
		buffer.flip();
		return buffer;
	}
	public void write(double value, WritableByteChannel c) throws IOException {
		Utils.write(putDouble(value), c, false);
	} 
	public void write(double value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public double read( Path path) throws IOException {
		return  Utils.read(BYTES, path).getDouble();
	}
	public double read( InputStream is) throws IOException {
		return Utils.read(BYTES, newChannel(is)).getDouble();
	}
	@Override
	int length(double[] value) {
		return value.length;
	}
	@Override
	int bytesPerEntity() {
		return BYTES;
	}
	@Override
	void set(double[] array, ByteBuffer buffer, int index) {
		array[index] = buffer.getDouble();
	}
	@Override
	double[] newInstance(int size) {
		return new double[size];
	}
	@Override
	void appendToBuffer(ByteBuffer buffer, double[] value, int index) {
		buffer.putDouble(value[index]);
	}
}
