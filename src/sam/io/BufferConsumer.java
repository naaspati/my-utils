package sam.io;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface BufferConsumer {
	int consume(ByteBuffer buffer) throws IOException;
}