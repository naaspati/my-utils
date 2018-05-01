package sam.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Properties2 {

    private final Properties properties = new Properties();
    private final HashMap<String, String> parsed = new HashMap<>();

    public Properties2(InputStream inStream) throws IOException {
        properties.load(inStream);
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

    public String getRaw(String key) {
        return properties.getProperty(key);
    }
    
    public boolean containsKey(String key) {
        return parsed.containsKey(key);
    }
    public String get(String key) {
        String value = parsed.get(key);
        if(value != null)
            return value;

        value = properties.getProperty(key);
        int start = 0;
        if(value == null || (start = value.indexOf('%')) < 0)
            return value;

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
                sb.append(get(value.substring(start + 1, end)));

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
    public Set<String> keys() {
        return properties.keySet().stream().map(String::valueOf).collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
    }

    public Entry getEntry(String key) {
        return new Entry(key, get(key), properties.getProperty(key));
    }
    public void forEachRaw(BiConsumer<String, String> consumer) {
        properties.forEach((s,t) -> consumer.accept((String)s, (String)t));
    }
    public void forEach(BiConsumer<String, String> consumer) {
        properties.keySet().forEach(key -> consumer.accept((String)key, get((String)key)));
    }

    public static final class Entry {
        private final String key, value, rawValue;

        public Entry(String key, String value, String rawValue) {
            this.key = key;
            this.value = value;
            this.rawValue = rawValue;
        }
        public String getKey() {
            return key;
        }
        public String getValue() {
            return value;
        }
        public String getRawValue() {
            return rawValue;
        }
        @Override
        public String toString() {
            return String.format("Entry [key=%s, value=%s, rawValue=%s]", key, value, rawValue);
        }
    }

    static class BadValueException extends RuntimeException  {
        private static final long serialVersionUID = 1L;

        public BadValueException(String message) {
            super(message);
        }

    }
}
