package sam.config;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface Config {
	Object get(String key);
	BigDecimal getBigDecimal(String key);
	BigInteger getBigInteger(String key);
	boolean getBoolean(String key);
	double getDouble(String key);
	<E extends Enum<E>> E getEnum(Class<E> clazz, String key);
	float getFloat(String key);
	int getInt(String key);
	long getLong(String key);
	String getString(String key);
	boolean has(String key);
	double optDouble(String key, double defaultValue);
	<E extends Enum<E>> E optEnum(Class<E> clazz, String key, E defaultValue);
	float optFloat(String key, float defaultValue);
	int optInt(String key, int defaultValue);
	long optLong(String key, long defaultValue);
	String optString(String key, String defaultValue);
}