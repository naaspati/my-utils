package sam.io;

import static sam.io.IOConstants.defaultBufferSize;

import java.io.IOException;
import java.nio.ByteBuffer;

import sam.logging.Logger;

public interface HasBuffer {
	static final Logger LOGGER = Logger.getLogger(HasBuffer.class);
	
	public static final int DEFAULT_BUFFER_SIZE = defaultBufferSize();
	
	public ByteBuffer buffer();
	
	public static ByteBuffer buffer(Object w) {
		ByteBuffer buf = null;
		if(w instanceof HasBuffer)
			buf = ((HasBuffer) w).buffer();

		if(buf == null) {
			long size;
			try {
				size = w instanceof HasSize ? ((HasSize)w).size() : -1;
			} catch (IOException e) {
				e.printStackTrace();
				size = -1;
			}
			
			buf = ByteBuffer.allocate((int)(size <= 0 ? DEFAULT_BUFFER_SIZE : Math.min(DEFAULT_BUFFER_SIZE, size)));
		}
		return buf;
	}

	public static ByteBuffer buffer(Object w, int size) {
		ByteBuffer buf = null;
		if(w instanceof HasBuffer)
			buf = ((HasBuffer) w).buffer();

		if(buf == null) 
			buf = ByteBuffer.allocate(size <= 0 ? DEFAULT_BUFFER_SIZE : Math.min(DEFAULT_BUFFER_SIZE, size));
		
		return buf;
	}

}
