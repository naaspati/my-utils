package sam.myutils;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

import java.util.logging.Logger;

import sam.logging.MyLoggerFactory;

public final class System2 {
	private static final Logger LOGGER = MyLoggerFactory.logger(System2.class.getSimpleName());
	
	public static String lookup(Class<?> cls, String fieldName, String defaultValue) {
		return lookup(cls.getCanonicalName()+"."+fieldName, defaultValue);
	}
	public static String lookup(String key, String defaultValue) {
		String s = null;

		try {
			s = getProperty(key);
			if(s != null) return s;

			s = getenv(key);
			if(s != null) return s;

			return s = defaultValue;
		} finally {
			String s2 = s;
			LOGGER.config(() -> key+"="+s2);
		}
	}
	public static String lookup(String key) { return lookup(key, null);}
}
