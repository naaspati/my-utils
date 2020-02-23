package sam.config;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.function.Function;

public abstract class AbstractConfig implements Config {
	protected abstract Object opt(String key, Object defaultValue);
	
	public Object get(String key) {
		Object o = opt(key, null);
		if (o == null)
			throw new NoSuchElementException("no value found for: \"".concat(key).concat("\""));
		return o;
	}
	
	@SuppressWarnings("unchecked")
	private <E> E getParsed(String key, Function<String, E> parser) {
		Object s = get(key);
		return s.getClass() != String.class ? (E)s : parser.apply((String)s);
	}
	
	@SuppressWarnings("unchecked")
	private <E> E optParsed(String key, Function<String, E> parser, E defaultValue) {
		Object s = opt(key, null);
		if(s == null)
			return defaultValue;
		
		return s.getClass() != String.class ? (E)s : parser.apply((String)s);
	} 

	public BigDecimal getBigDecimal(String key) {
		return getParsed(key, BigDecimal::new); 
	}

	public BigInteger getBigInteger(String key) {
		return getParsed(key, BigInteger::new);
	}

	public boolean getBoolean(String key) {
		return getParsed(key, Boolean::valueOf);
	}

	public double getDouble(String key) {
		return getParsed(key, Double::parseDouble);
	}

	@Override
	public <E extends Enum<E>> E getEnum(Class<E> enumType, String key) {
		return getParsed(key, s -> Enum.valueOf(enumType, s));
	}

	@Override
	public float getFloat(String key) {
		return getParsed(key, Float::parseFloat);
	}

	@Override
	public int getInt(String key) {
		return getParsed(key, Integer::parseInt);
	}

	@Override
	public long getLong(String key) {
		return getParsed(key, Long::parseLong);
	}

	@Override
	public String getString(String key) {
		return (String)get(key);
	}
	
	public BigDecimal optBigDecimal(String key, BigDecimal defaultValue) {
		return optParsed(key, BigDecimal::new, defaultValue); 
	}

	public BigInteger optBigInteger(String key, BigInteger defaultValue) {
		return optParsed(key, BigInteger::new, defaultValue);
	}

	public boolean optBoolean(String key, boolean defaultValue) {
		return optParsed(key, Boolean::valueOf, defaultValue);
	}

	public double optDouble(String key, double defaultValue) {
		return optParsed(key, Double::parseDouble, defaultValue);
	}

	@Override
	public <E extends Enum<E>> E optEnum(Class<E> enumType, String key, E defaultValue) {
		return optParsed(key, s -> Enum.valueOf(enumType, s), defaultValue);
	}

	@Override
	public float optFloat(String key, float defaultValue) {
		return optParsed(key, Float::parseFloat, defaultValue);
	}

	@Override
	public int optInt(String key, int defaultValue) {
		return optParsed(key, Integer::parseInt, defaultValue);
	}

	@Override
	public long optLong(String key, long defaultValue) {
		return optParsed(key, Long::parseLong, defaultValue);
	}

	@Override
	public String optString(String key, String defaultValue) {
		return (String)opt(key, defaultValue);
	}
}
