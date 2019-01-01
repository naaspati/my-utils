package sam.io.serilizers;

import static java.lang.Long.BYTES;
import static java.nio.channels.Channels.newChannel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

public class LongSerializer extends BaseSerializer<long[]>{

	public void write(long value, Path path) throws IOException {
		Utils.write(putLong(value), path);
	}
	public ByteBuffer putLong(long value) {
		ByteBuffer buffer = ByteBuffer.allocate(BYTES);
		buffer.clear();
		buffer.putLong(value);
		buffer.flip();
		return buffer;
	}
	public void write(long value, WritableByteChannel c) throws IOException {
		Utils.write(putLong(value), c, false);
	} 
	public void write(long value, OutputStream os) throws IOException {
		write(value, newChannel(os));
	}
	public long read( Path path) throws IOException {
		return  Utils.read(BYTES, path).getLong();
	}
	public long read( InputStream is) throws IOException {
		return Utils.read(BYTES, newChannel(is)).getLong();
	}
	@Override
	int length(long[] value) {
		return value.length;
	}
	@Override
	int bytesPerEntity() {
		return BYTES;
	}
	@Override
	void set(long[] array, ByteBuffer buffer, int index) {
		array[index] = buffer.getLong();
	}
	@Override
	long[] newInstance(int size) {
		return new long[size];
	}
	@Override
	void appendToBuffer(ByteBuffer buffer, long[] value, int index) {
		buffer.putLong(value[index]);
	}
}
