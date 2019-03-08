package sam.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public interface BufferConsumer {
	void consume(ByteBuffer buffer) throws IOException;
	
	public static BufferConsumer of(WritableByteChannel target, boolean flip) {
		return b -> IOUtils.write(b, target, flip);
	}
}
