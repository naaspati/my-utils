package sam.io.serilizers;

import static java.lang.Short.BYTES;
import static java.nio.channels.Channels.newChannel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

public class ShortSerializer extends BaseSerializer<short[]> {
	public void write(short value, Path path) throws IOException {
		Utils.write(putShort(value), path);
	}
	public ByteBuffer putShort(short value) {
		ByteBuffer buffer = ByteBuffer.allocate(BYTES);
		buffer.clear();
		buffer.putShort(value);
		buffer.flip();
		return buffer;
	}
	public void write(short value, WritableByteChannel c) throws IOException {
		Utils.write(putShort(value), c, false);
	} 
	public void write(short value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public short read( Path path) throws IOException {
		return  Utils.read(BYTES, path).getShort();
	}
	public short read( InputStream is) throws IOException {
		return Utils.read(BYTES, newChannel(is)).getShort();
	}
	@Override
	int length(short[] value) {
		return value.length;
	}
	@Override
	int bytesPerEntity() {
		return BYTES;
	}
	@Override
	void set(short[] array, ByteBuffer buffer, int index) {
		array[index] = buffer.getShort();
	}
	@Override
	short[] newInstance(int size) {
		return new short[size];
	}
	@Override
	void appendToBuffer(ByteBuffer buffer, short[] value, int index) {
		buffer.putShort(value[index]);
	}
}
