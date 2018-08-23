package sam.myutils;

import java.util.logging.Logger;
import static java.lang.System.*;

public interface MyUtilsSystem {
	static final boolean print = Boolean.valueOf(lookup("sam.myutils.MyUtilsSystem.print", "false", false));

	public static String lookup(String key, String defaultValue) {
		return lookup(key, defaultValue, print);
	}
	public static String lookup(Class<?> cls, String fieldName, String defaultValue) {
		return lookup(cls.getCanonicalName()+"."+fieldName, defaultValue);
	}
	public static String lookup(String key, String defaultValue, boolean print) {
		String s = null;

		try {
			s = getProperty(key);
			if(s != null) return s;

			s = getenv(key);
			if(s != null) return s;

			return s = defaultValue;
		} finally {
			if(print)
				Logger.getLogger("MyUtilsSystem").info(key+"="+s);
		}
	}
	public static String lookup(String key) { return lookup(key, null);}
}
