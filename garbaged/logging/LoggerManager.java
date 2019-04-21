package sam.logging;

public interface LoggerManager {
	default Logger get(@SuppressWarnings("rawtypes") Class cls) {
		return get(cls.getName());
	}
	Logger get(String loggername);
}
