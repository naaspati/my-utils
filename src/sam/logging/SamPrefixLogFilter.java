package sam.logging;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class SamPrefixLogFilter implements Filter {
	@Override
	public boolean isLoggable(LogRecord record) {
		return record.getLoggerName().startsWith("sam.");
	}
	
}
