package sam.logging;

import java.util.HashMap;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogFilter implements Filter {
	public static final HashMap<String, Boolean> map = new HashMap<>();
	
	public static void put(String loggername, boolean isLoggable) {
		map.put(loggername, isLoggable);
	}

	@Override
	public boolean isLoggable(LogRecord record) {
		if(record.getLevel()  != Level.FINE)
			return true;
		Boolean b = map.get(record.getLoggerName());
		if(b == null) {
			b = record.getSourceClassName().startsWith("sam.");
			map.put(record.getLoggerName(), b);
		}
		return b;
	}
}
