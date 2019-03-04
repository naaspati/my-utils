package sam.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Properties2 {
	private final Logger logger;

	private final Properties properties = new Properties();
	private final HashMap<String, String> parsed = new HashMap<>();
	private boolean envLooup;
	private boolean propertyLooup;

	public void setSystemLookup(boolean envLooup, boolean propertyLooup) {
		this.envLooup = envLooup;
		this.propertyLooup = propertyLooup;
	}
	public Properties2(InputStream inStream) throws IOException {
		this(inStream, true);
	}
	Properties2(InputStream inStream, boolean createLogger) throws IOException {
		properties.load(inStream);
		logger = createLogger ? Logger.getLogger(Properties2.class.getName())  : null;
	}
	public void load(Reader reader) throws IOException {
		properties.load(reader);
	}
	public void load(InputStream inStream) throws IOException {
		properties.load(inStream);
	}
	public void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
		properties.loadFromXML(in);
	}
	public void put(String key, String value) {
		properties.put(key, value);
	}
	public void putAll(Map<? extends Object, ? extends Object> t) {
		properties.putAll(t);
	}
	public Object putIfAbsent(Object key, Object value) {
		return properties.putIfAbsent(key, value);
	}
	public Object computeIfAbsent(Object key, Function<? super Object, ? extends Object> mappingFunction) {
		return properties.computeIfAbsent(key, mappingFunction);
	}
	public Object computeIfPresent(Object key,
			BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
		return properties.computeIfPresent(key, remappingFunction);
	}
	public Object compute(Object key, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
		return properties.compute(key, remappingFunction);
	}
	public String getRaw(String key) {
		return properties.getProperty(key);
	}

	public boolean containsKey(String key) {
		return parsed.containsKey(key);
	}
	public String get(String key) {
		String s = find(key);
		
		if(logger != null) {
			if(s == null)
				logger.config("value not found for key: "+key);
			else 
				logger.fine(() -> key.concat(s == null ? "=" : "=".concat(s)));	
		}
		return s;
	}
	private  String find(String key) {
		String value = parsed.get(key);

		if(value != null)
			return value;

		if(propertyLooup)
			value = System.getProperty(key);
		if(value == null && envLooup)
			value = System.getenv(key);

		if(value == null) 
			value = properties.getProperty(key);

		int start = 0;
		if(value == null || (start = value.indexOf('%')) < 0) {
			parsed.put(key, value);
			return value;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(value, 0, start);

		int end = 0;
		while(start >= 0) {
			end = value.indexOf('%', start + 1);
			if(end < 0)
				throw new BadValueException(String.format("key: %s, value: %s, error: closing %% not found for %% at %d ", key, value, start));

			if(end == start + 1)
				sb.append('%');
			else
				sb.append(find(value.substring(start + 1, end)));

			start = value.indexOf('%', end + 1);
			if(start > 0)
				sb.append(value, end+1, start);
		}
		if(value.charAt(value.length() - 1) != '%')
			sb.append(value, end + 1, value.length());


		value = sb.toString();
		parsed.put(key, value);
		return value;
	}
	public int getInt(String key) {
		return get(key, Integer::parseInt);
	}
	public double getDouble(String key) {
		return get(key, Double::parseDouble);
	}
	public <E> E get(String key, Function<String, E> mapper) {
		return mapper.apply(get(key));
	}

	public Set<String> keys() {
		return properties.keySet().stream().map(String::valueOf).collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
	}
	public void forEach(BiConsumer<String, String> consumer) {
		properties.keySet().forEach(key -> consumer.accept((String)key, get((String)key)));
	}
	static class BadValueException extends RuntimeException  {
		private static final long serialVersionUID = 1L;

		public BadValueException(String message) {
			super(message);
		}
	}
}
