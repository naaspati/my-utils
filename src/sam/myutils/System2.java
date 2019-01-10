package sam.myutils;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

import java.util.Arrays;
import java.util.logging.Logger;

import sam.logging.MyLoggerFactory;

public final class System2 {
	private static final Logger LOGGER = MyLoggerFactory.logger(System2.class);

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
			LOGGER.fine(() -> key.concat(s2 == null ? "=" : "=".concat(s2)));
		}
	}
	public static boolean lookupBoolean(String key) {
		return lookupBoolean(key, false);
	}
	public static boolean lookupBoolean(String key, boolean defaultValue) {
		String value = lookup(key, null);
		return parseBoolean(value, defaultValue);
	}
	public static boolean parseBoolean(String booleanString, boolean defaultValue) {
		if(booleanString == null)
			return defaultValue;

		String s = booleanString.trim().toLowerCase();
		if(s.isEmpty()) return defaultValue;

		switch (booleanString.trim().toLowerCase()) {
			case "true": return true;
			case "false": return false;

			case "yes": return true;
			case "no": return false;

			case "on": return true;
			case "off": return false;
			
			default:
				LOGGER.warning("Unknown boolean value: "+booleanString);
				return defaultValue;
		}
	}
	public static String lookup(String key) { return lookup(key, null);}
	public static String lookupAny(String... keys) {
		String s = null;
		String k = null; 

		try {
			for (String key : keys) {
				k = key;
				s = getProperty(key);
				if(s != null) return s;
			}

			for (String key : keys) {
				k = key;
				s = getenv(key);
				if(s != null) return s;
			}

		} finally {
			if(s == null)
				LOGGER.fine(() -> "NO VALUE found for any: "+Arrays.toString(keys));
			else {
				String s2 = s, k2 = k;
				LOGGER.fine(() -> k2+"="+s2);
			}
		}
		return null;
	}

}
