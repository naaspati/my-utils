package sam.logging;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

//import sam.logging.LogFilter;

public final class MyLoggerFactory {
	private static final boolean simpleName;

	static {
		defaultLogging();

		String s2 = LogManager.getLogManager().getProperty("sam.logging.loggerName.format");
		boolean  result = false;
		if(s2 != null) {
			switch (s2.toLowerCase().trim()) {
				case "simple":
					result = true;
					break;
				case "full":
					result = false;
					break;
				default:
					Logger.getLogger(MyLoggerFactory.class.getName()).severe("unknown value for \"sam.logging.loggerName.format\": "+s2+", settting to: full");
					result = false;
					break;
			}
		}
		simpleName = result;
		logger(MyLoggerFactory.class).config(() -> "----------- DateTime: "+LocalDateTime.now()+" ----------");
	}
	private  static String lookup(String key) {
		String s = getProperty(key);
		if(s != null) return s;

		s = getenv(key);
		return s;
	}
	private  static String lookup(String key, String defaultValue) {
		String s = lookup(key);
		return s == null ? defaultValue : s;
	}

	private static void defaultLogging() {
		String s = lookup("DEFAULT_LOGGING_FILE");
		if(s == null || !s.trim().toLowerCase().equals("true"))
			return;
		
		Properties p = new Properties();
		p.put("handlers", "java.util.logging.ConsoleHandler");
		p.put(".level", lookup("logging.level", "ALL"));
		
		p.put("java.util.logging.ConsoleHandler.level", lookup("logging.level", "FINE"));
		p.put("java.util.logging.ConsoleHandler.formatter", "java.util.logging.SimpleFormatter");
		p.put("java.util.logging.ConsoleHandler.filter", "sam.logging.LogFilter");
		p.put("java.util.logging.SimpleFormatter.format", lookup("logging.format", "%4$s: %3$s -> %5$s%6$s%n"));
		p.put("sam.logging.loggerName.format", lookup("loggerName.format", "simple"));
		
		try(ByteArrayOutputStream out = new ByteArrayOutputStream();
				) {
			p.store(out, null);
			LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(out.toByteArray()));
			Logger.getLogger(MyLoggerFactory.class.getName()).config("using default_logging_file");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	MyLoggerFactory() {}

	public static Logger logger(@SuppressWarnings("rawtypes") Class cls) {
		return Logger.getLogger(simpleName ? cls.getSimpleName() : cls.getCanonicalName()); 
	}
}
