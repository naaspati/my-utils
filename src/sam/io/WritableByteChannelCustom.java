package sam.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public interface WritableByteChannelCustom extends WritableByteChannel, HasBuffer {

	static WritableByteChannel of(WritableByteChannel target, ByteBuffer buffer) {
		if(target instanceof WritableByteChannelCustom)
			return target;
		
		if(buffer == null)
			return target;
		else {
			return new WritableByteChannelCustom() {
				
				@Override
				public ByteBuffer buffer() {
					return buffer;
				}
				
				@Override
				public boolean isOpen() {
					return target.isOpen();
				}
				
				@Override
				public void close() throws IOException {
					target.close();
				}
				
				@Override
				public int write(ByteBuffer src) throws IOException {
					return target.write(src);
				}
			};
		}
	}
}
