package sam.io;

import static sam.myutils.System2.lookup;

import java.util.Optional;

import sam.logging.MyLoggerFactory;

public final class BufferSize {
	public static final int DEFAULT_BUFFER_SIZE = get0();

	private static int get0() {
		String s = Optional.ofNullable(lookup("buffer_size")).orElseGet(() -> lookup("BUFFER_SIZE"));
		int bufferSize = s != null ? Integer.parseInt(s) : 1024*8;

		if(bufferSize < 512)
			throw new RuntimeException("minimum buffer size can be: 512, but given "+bufferSize);

		MyLoggerFactory.bySimpleName(BufferSize.class).config("DEFAULT_BUFFER_SIZE: "+bufferSize);
		return bufferSize;
	}

	private int bufferSize = -1;

	public BufferSize() {}
	public BufferSize(int buffersize) {
		set(buffersize);
	}

	public void set(int bufferSize) {
		if(bufferSize < 512)
			throw new IllegalArgumentException("minimum buffer size can be: 512, but given "+bufferSize);
		this.bufferSize = bufferSize;
	}

	public int get() {
		if(bufferSize > 0 )
			return bufferSize;

		bufferSize = get0();
		return bufferSize; 
	}

}
