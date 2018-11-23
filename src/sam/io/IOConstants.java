package sam.io;

import static java.nio.charset.CodingErrorAction.IGNORE;
import static java.nio.charset.CodingErrorAction.REPLACE;
import static java.nio.charset.CodingErrorAction.REPORT;

import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.logging.Logger;

import sam.myutils.System2;

public final class IOConstants {
	private static final Logger LOGGER = Logger.getLogger(IOConstants.class.getName());

	private static CodingErrorAction DEFAULT_ON_MALFORMED;
	private static CodingErrorAction DEFAULT_ON_UNMAPPABLE_CHARACTER;

	public static CodingErrorAction defaultOnMalformedInput() {
		return DEFAULT_ON_MALFORMED != null ? DEFAULT_ON_MALFORMED : (DEFAULT_ON_MALFORMED = getCodingErrorAction("onMalformedInput", "ON_MALFORMED_INPUT"));
	}
	public static CodingErrorAction defaultOnUnmappableCharacter() {
		return DEFAULT_ON_UNMAPPABLE_CHARACTER != null ? DEFAULT_ON_UNMAPPABLE_CHARACTER : (DEFAULT_ON_UNMAPPABLE_CHARACTER = getCodingErrorAction("onUnmappableCharacter", "ON_UNMAPPABLE_CHARACTER"));
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
				LOGGER.warning("unknown value: "+s);
				return REPORT;
		}
	}
	private static Charset DEFAULT_CHARSET;

	public static Charset defaultCharset() {
		if(DEFAULT_CHARSET != null) return DEFAULT_CHARSET;
		
		String s = System2.lookupAny("sam.charset", "DEFAULT_CHARSET");
		DEFAULT_CHARSET = Charset.forName(s != null ? s: "utf-8");
		LOGGER.config("DEFAULT_CHARSET: "+DEFAULT_CHARSET);
		return DEFAULT_CHARSET;
	}
	private static int DEFAULT_BUFFER_SIZE = -1;

	public static int defaultBufferSize() {
		if(DEFAULT_BUFFER_SIZE != -1) return DEFAULT_BUFFER_SIZE;

		String s = System2.lookupAny("buffer_size", "BUFFER_SIZE", "buffer.size");
		int bufferSize = s != null ? Integer.parseInt(s) : 1024*8;

		if(bufferSize < 512)
			throw new RuntimeException("minimum buffer size can be: 512, but given "+bufferSize);

		DEFAULT_BUFFER_SIZE = bufferSize;
		LOGGER.config("DEFAULT_BUFFER_SIZE: "+DEFAULT_BUFFER_SIZE);		
		return DEFAULT_BUFFER_SIZE;
	}

}
