package sam.io;

import java.nio.charset.Charset;
import java.util.Optional;

import sam.myutils.System2;

public interface DefaultCharset {
	static final Charset DEFAULT_CHARSET = get();
	
	public static Charset get() {
		return Optional.ofNullable(System2.lookup("sam.charset")).map(Charset::forName).orElse(Charset.forName("utf-8"));
	}
}
