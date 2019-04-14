package sam.myutils.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

import sam.functions.IOExceptionConsumer;
import sam.io.WritableByteChannelCustom;

public class Utils {
	public static byte[] toArray(ByteBuffer b) {
		if(b.position() == 0 && b.limit() == b.capacity())
			return b.array();
		
		return Arrays.copyOfRange(b.array(), b.position(), b.limit());
	}
	public static WritableByteChannel writeable(IOExceptionConsumer<ByteBuffer> consumer) {
		return new WritableByteChannel() {
			
			@Override
			public boolean isOpen() {
				return true;
			}
			
			@Override
			public void close() throws IOException {
			}
			
			@Override
			public int write(ByteBuffer src) throws IOException {
				int n = src.remaining();
				consumer.accept(src);
				return n;
			}
		};
	}
	public static WritableByteChannel writeable(IOExceptionConsumer<ByteBuffer> consumer, ByteBuffer buffer) {
		return new WritableByteChannelCustom() {
			@Override
			public ByteBuffer buffer() {
				return buffer;
			}
			@Override
			public boolean isOpen() {
				return true;
			}
			
			@Override
			public void close() throws IOException {
			}
			
			@Override
			public int write(ByteBuffer src) throws IOException {
				int n = src.remaining();
				consumer.accept(src);
				return n;
			}
		};
	}
}
