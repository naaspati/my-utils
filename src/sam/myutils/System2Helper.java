package sam.myutils;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

interface System2Helper {
	static String lookup(String key, String defaultValue) {
		String s = null;

		s = getProperty(key);
		if (s != null)
			return s;

		s = getenv(key);
		if (s != null)
			return s;
		
		return defaultValue;
	}
}
