package sam.io;

import static sam.myutils.System2.lookup;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Optional;

import sam.logging.MyLoggerFactory;
import sam.reference.ReferenceUtils;

public final class BufferSize {
	public static final int DEFAULT_BUFFER_SIZE = get0();

	private static int get0() {
		String s = Optional.ofNullable(lookup("buffer_size")).orElseGet(() -> lookup("BUFFER_SIZE"));
		int bufferSize = s != null ? Integer.parseInt(s) : 1024*8;

		if(bufferSize < 512)
			throw new RuntimeException("minimum buffer size can be: 512, but given "+bufferSize);

		MyLoggerFactory.logger(BufferSize.class).config("DEFAULT_BUFFER_SIZE: "+bufferSize);
		return bufferSize;
	}
}
