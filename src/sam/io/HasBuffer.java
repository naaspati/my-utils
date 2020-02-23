package sam.io;

import static sam.io.IOConstants.defaultBufferSize;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;

public interface HasBuffer {
	static final Logger LOGGER = LoggerFactory.getLogger(HasBuffer.class);
	
	public static final int DEFAULT_BUFFER_SIZE = defaultBufferSize();
	
	public ByteBuffer buffer();
	
	public static ByteBuffer buffer(Object w) {
		return buffer(w, -1);
	}

	public static ByteBuffer buffer(Object w, int size) {
		ByteBuffer buf = null;
		if(w instanceof HasBuffer)
			buf = ((HasBuffer) w).buffer();

		if(buf == null) {
			if(size <= 0) {
				if(w instanceof FileChannel) {
					try {
						size = (int) ((FileChannel) w).size();
					} catch (IOException e) { }
				}
			}
			
			return ByteBuffer.allocate(size <= 0 ? DEFAULT_BUFFER_SIZE : Math.min(DEFAULT_BUFFER_SIZE, size));
		} 
		
		return buf;
	}

}
