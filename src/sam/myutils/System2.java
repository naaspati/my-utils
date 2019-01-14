package sam.myutils;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import sam.logging.MyLoggerFactory;

public final class System2 {
	private static final Logger _LOGGER = MyLoggerFactory.logger(System2.class);
	private static final FileWriter lookupWriter;
	private static final boolean loggable;
	
	static {
		String s = getProperty("DUMP_LOOKUP");
		s = s != null ? s : getenv("DUMP_LOOKUP");
		FileWriter w = null;
		
		if(s != null) {
			if(s.trim().equalsIgnoreCase("true")) {
				File p = new File("System2.lookups.dump");
				try {
					w = new FileWriter(p);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				_LOGGER.info("dumping System2.lookups in: "+p.getAbsolutePath());
			} else
				_LOGGER.severe("bad value for DUMP_LOOKUP: \""+s+"\", possible values are[true,false]");
		}
		
		lookupWriter = w;
		loggable = lookupWriter != null || _LOGGER.isLoggable(Level.FINE);
		if(lookupWriter != null) {
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					lookupWriter.flush();
					lookupWriter.close();
				} catch (IOException e) {
					_LOGGER.log(Level.SEVERE, "failed to close ", e);
				}
			}));
		}
	}

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
			log(() -> key.concat(s2 == null ? "=" : "=".concat(s2)));
		}
	}
	private static void log(Supplier<String> msg) {
		if(!loggable)
			return;
		
		String s = msg.get();
		_LOGGER.fine(s);
		
		if(lookupWriter != null) {
			try {
				lookupWriter.write(s);
				lookupWriter.append('\n');
				lookupWriter.flush();
			} catch (IOException e) {
				_LOGGER.log(Level.SEVERE, "failed to dump lookups", e);
			}
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
				_LOGGER.warning("Unknown boolean value: "+booleanString);
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
				log(() -> "NO VALUE found for any: "+Arrays.toString(keys));
			else {
				String s2 = s, k2 = k;
				log(() -> Arrays.toString(keys)+", found: "+k2+"="+s2);
			}
		}
		return null;
	}

}
