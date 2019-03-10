package sam.io;

import static java.nio.charset.CodingErrorAction.IGNORE;
import static java.nio.charset.CodingErrorAction.REPLACE;
import static java.nio.charset.CodingErrorAction.REPORT;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import sam.logging.Logger;

import sam.myutils.System2;

public final class IOConstants {
	private static final Logger LOGGER = Logger.getLogger(IOConstants.class);

	private final static CodingErrorAction DEFAULT_ON_MALFORMED = getCodingErrorAction("onMalformedInput", "ON_MALFORMED_INPUT");
	private final static CodingErrorAction DEFAULT_ON_UNMAPPABLE_CHARACTER = getCodingErrorAction("onUnmappableCharacter", "ON_UNMAPPABLE_CHARACTER");

	public static CodingErrorAction defaultOnMalformedInput() {
		return DEFAULT_ON_MALFORMED;
	}
	public static CodingErrorAction defaultOnUnmappableCharacter() {
		return DEFAULT_ON_UNMAPPABLE_CHARACTER;
	}

	private static CodingErrorAction getCodingErrorAction(String...keys) {
		String s = System2.lookupAny(keys);

		if(s == null) return REPORT;
		
		switch (s.toUpperCase().trim()) {
			case "IGNORE":
				return IGNORE;
			case "REPLACE":
				return REPLACE;
			case "REPORT":
				return REPORT;
			default:
				LOGGER.warn("unknown value: {}", s);
				return REPORT;
		}
	}
	private static final Charset DEFAULT_CHARSET = dc();

	private static Charset dc() {
		String s = System2.lookupAny("sam.charset", "DEFAULT_CHARSET");
		Charset c = Charset.forName(s != null ? s: "utf-8");
		LOGGER.debug("DEFAULT_CHARSET: {}",DEFAULT_CHARSET);
		return c;
	}

	public static Charset defaultCharset() {
		return DEFAULT_CHARSET;
	}
	private final static int DEFAULT_BUFFER_SIZE = db();
	public static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);

	private static int db() {
		String s = System2.lookupAny("buffer_size", "BUFFER_SIZE", "buffer.size");
		int bufferSize = s != null ? Integer.parseInt(s) : 1024*8;

		if(bufferSize < 512)
			throw new RuntimeException("minimum buffer size can be: 512, but given "+bufferSize);

		LOGGER.debug("DEFAULT_BUFFER_SIZE: {}",bufferSize);		
		return bufferSize;		
	}

	public static int defaultBufferSize() {
		return DEFAULT_BUFFER_SIZE;
	}

}
