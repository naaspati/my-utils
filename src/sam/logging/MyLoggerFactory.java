package sam.logging;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.logging.Logger;

//import sam.logging.LogFilter;

public final class MyLoggerFactory {
	static {
		String key = "java.util.logging.config.file";
		String file = System.getProperty(key);

		if(file == null) {
			Function<String, Boolean> t = k -> {
				String s = lookup(k);
				if(s != null)
					System.setProperty(key, s);
				return s != null;
			};
			boolean b = t.apply(key);
			b = b || t.apply("logging.config.file");
			b = b || t.apply("MyLoggerFactory.file");
			
			bySimpleName(MyLoggerFactory.class).fine("java.util.logging.config.file="+System.getProperty(key));
		}
		bySimpleName(MyLoggerFactory.class).config(() -> "----------- DateTime: "+LocalDateTime.now()+" ----------");
	}
	private  static String lookup(String key) {
		String s = getProperty(key);
		if(s != null) return s;

		s = getenv(key);
		return s;

	}
	
	MyLoggerFactory() {}

	public static Logger logger(String loggerName) {
		return Logger.getLogger(loggerName); 
	}
	public static Logger bySimpleName(@SuppressWarnings("rawtypes") Class cls) {
		return logger(cls.getSimpleName()); 
	}
	public static Logger byCanonicalName(@SuppressWarnings("rawtypes") Class cls) {
		return logger(cls.getCanonicalName()); 
	}
}
