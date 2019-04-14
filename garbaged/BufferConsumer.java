package sam.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

@Deprecated
public interface BufferConsumer {
	void consume(ByteBuffer buffer) throws IOException;
	default void onComplete() {}
	
	public static BufferConsumer of(WritableByteChannel target, boolean flip) {
		return b -> IOUtils.write(b, target, flip);
	}
	public static BufferConsumer of(OutputStream target, boolean flip) {
		return b -> IOUtils.write(b, target, flip);
	}
}
