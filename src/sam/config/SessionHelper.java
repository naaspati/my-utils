package sam.config;

import java.util.function.Function;

public interface SessionHelper {
	default void sessionPut(String key, Object value) {
		Session.put(getClass(), key, value);
	}
	default <E> E sessionGet(String key) {
		return Session.get(getClass(), key);
	}
	default String sessionGetProperty(String key) {
		return Session.getProperty(getClass(), key);
	}
	default boolean sessionHas(String key) {
		return Session.has(getClass(), key);
	}
	default <E> E sessionGet(String key, E defaultValue, Function<String, E> mapper) {
		return Session.getProperty(getClass(), key, defaultValue, mapper);
	}
}
