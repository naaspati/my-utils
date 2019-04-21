package sam.logging;

public class DefaultLoggerManager implements LoggerManager {
	@Override
	public Logger get(String loggername) {
		return new DefaultLogger(java.util.logging.Logger.getLogger(loggername));
	}
}
