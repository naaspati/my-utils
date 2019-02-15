package sam.io.serilizers;

import static sam.io.IOConstants.defaultBufferSize;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public interface  StringIOUtils {
	public static final int DEFAULT_BUFFER_SIZE = defaultBufferSize();

	public static int computeBufferSize(double averageBytesPerChar, int charSequenceLength) {
		int buffersize = (int) (charSequenceLength * averageBytesPerChar);

		buffersize = buffersize > DEFAULT_BUFFER_SIZE ? DEFAULT_BUFFER_SIZE : buffersize;
		if(buffersize < 50)
			buffersize = 50;

		if(buffersize%averageBytesPerChar != 0)
			buffersize = (int) (averageBytesPerChar*(buffersize/averageBytesPerChar + 1));

		return buffersize;
	}
	public static int computeBufferSize(CharsetEncoder encoder, CharSequence chars) {
		return computeBufferSize(encoder.averageBytesPerChar(), chars.length());
	}
}
