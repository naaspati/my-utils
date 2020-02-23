package sam.myutils;

import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.event.Level;

public interface LoggerUtils {
	public static void enableSlf4jSimple(Level level) {
		String[] props = {
				"org.slf4j.simpleLogger.defaultLogLevel", level.toString(), 
				"org.slf4j.simpleLogger.showThreadName", "false", 
				"org.slf4j.simpleLogger.showShortLogName", "true" 
		};

		int n = 0;
		while(n < props.length) {
			String key = props[n++];
			String value = System2.lookup(key);

			if(value == null)
				value = props[n++];
			else
				n++;

			System.setProperty(key, value);
		}
	}

	public static void lookupEnableSlf4jSimple() {
		enableSlf4jSimple(Optional.ofNullable(System2.lookup("sam.slf4j.simple.enable")).map(s -> Level.valueOf(s.toUpperCase())).orElse(Level.INFO));
	}
	
	public static final class LazyToString {
		private final Supplier<String> src;

		public LazyToString(Supplier<String> src) {
			this.src = src;
		}
		@Override
		public String toString() {
			return src.get();
		}
	}
	
	public static LazyToString lazyMessage(Supplier<String> src) {
		return new LazyToString(src);
	} 
}
