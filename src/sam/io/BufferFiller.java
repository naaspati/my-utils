package sam.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;

public interface BufferFiller {
	int fillNFlip(ByteBuffer buffer) throws IOException;
	int remaining() throws IOException ;
	
	public static BufferFiller of(ReadableByteChannel channel) {
		Objects.requireNonNull(channel);

		return new BufferFiller() {
			@Override
			public int fillNFlip(ByteBuffer buffer) throws IOException {
				int n = channel.read(buffer);
				buffer.flip();
				return n;
			}

			@Override
			public int remaining() throws IOException {
				if(channel instanceof FileChannel) {
					FileChannel f = (FileChannel) channel;
					return (int) (f.size() - f.position());
				}
				return -1;
			}
		};
	}
}