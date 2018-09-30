package sam.fileutils;

import java.nio.charset.Charset;
import java.util.Optional;

import sam.myutils.MyUtilsSystem;

public class DefaultCharset {
	public static Charset get() {
		return Optional.ofNullable(MyUtilsSystem.lookup("sam.charset")).map(Charset::forName).orElse(Charset.defaultCharset());
	}
}
