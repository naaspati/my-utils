package sam.io.serilizers;

import static java.lang.Integer.BYTES;
import static java.nio.channels.Channels.newChannel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

public class IntSerializer extends BaseSerializer<int[]> {
	public void write(int value, Path path) throws IOException {
		Utils.write(putInt(value), path);
	}
	public ByteBuffer putInt(int value) {
		ByteBuffer buffer = ByteBuffer.allocate(BYTES);
		buffer.clear();
		buffer.putInt(value);
		buffer.flip();
		return buffer;
	}
	public void write(int value, WritableByteChannel c) throws IOException {
		Utils.write(putInt(value), c, false);
	} 
	public void write(int value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public int read( Path path) throws IOException {
		return  Utils.read(BYTES, path).getInt();
	}
	public int read( InputStream is) throws IOException {
		return Utils.read(BYTES, newChannel(is)).getInt();
	}
	@Override
	int length(int[] value) {
		return value.length;
	}
	@Override
	int bytesPerEntity() {
		return BYTES;
	}
	@Override
	void set(int[] array, ByteBuffer buffer, int index) {
		array[index] = buffer.getInt();
	}
	@Override
	int[] newInstance(int size) {
		return new int[size];
	}
	@Override
	void appendToBuffer(ByteBuffer buffer, int[] value, int index) {
		buffer.putInt(value[index]);
	}
}
