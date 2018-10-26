package sam.config;

public interface SessionPutGet {
	default void sessionPut(String key, Object value) {
		Session.put(getClass(), key, value);
	}
	default Object sessionGet(String key) {
		return Session.get(getClass(), key);
	}
	default String sessionGetProperty(String key) {
		return Session.getProperty(getClass(), key);
	}
}
