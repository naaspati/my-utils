package sam.nopkg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TsvMapTemp implements Map<String, String> {
	private final HashMap<String, String> map;
	
	public TsvMapTemp() {
		map = new HashMap<>();
	}
	public TsvMapTemp(Path path) throws IOException {
		map = new HashMap<>();
		
		try(BufferedReader reader = Files.newBufferedReader(path)) {
			String line = null;
			while((line = reader.readLine()) != null) {
				int n = line.indexOf('\t');
				if(n > 0) {
					map.put(line.substring(0, n), line.substring(n+1));
				}
			}
		}
	}
	public void save(Path path) throws IOException {
		try(BufferedWriter w = Files.newBufferedWriter(path)) {
			if(!map.isEmpty()) {
				for (Entry<String, String> s  : map.entrySet())
					w.append(s.getKey()).append('\t').append(s.getValue()).append('\n');
			}
				
		}
	}
	
	@Override public boolean equals(Object o) {
		return o instanceof TsvMapTemp && map.equals(o);
	}
	@Override public int hashCode() {
		return map.hashCode();
	}
	@Override public String toString() {
		return map.toString();
	}
	@Override public int size() {
		return map.size();
	}
	@Override public boolean isEmpty() {
		return map.isEmpty();
	}
	@Override public String get(Object key) {
		return map.get(key);
	}
	@Override public boolean containsKey(Object key) {
		return map.containsKey(key);
	}
	@Override public String put(String key, String value) {
		return map.put(key, value);
	}
	@Override public void putAll(Map<? extends String, ? extends String> m) {
		map.putAll(m);
	}
	@Override public String remove(Object key) {
		return map.remove(key);
	}
	@Override public void clear() {
		map.clear();
	}
	@Override public boolean containsValue(Object value) {
		return map.containsValue(value);
	}
	@Override public Set<String> keySet() {
		return map.keySet();
	}
	@Override public Collection<String> values() {
		return map.values();
	}
	@Override public Set<Entry<String, String>> entrySet() {
		return map.entrySet();
	}
	@Override public String getOrDefault(Object key, String defaultValue) {
		return map.getOrDefault(key, defaultValue);
	}
	@Override public String putIfAbsent(String key, String value) {
		return map.putIfAbsent(key, value);
	}
	@Override public boolean remove(Object key, Object value) {
		return map.remove(key, value);
	}
	@Override public boolean replace(String key, String oldValue, String newValue) {
		return map.replace(key, oldValue, newValue);
	}
	@Override public String replace(String key, String value) {
		return map.replace(key, value);
	}
	@Override public String computeIfAbsent(String key, Function<? super String, ? extends String> mappingFunction) {
		return map.computeIfAbsent(key, mappingFunction);
	}
	@Override public String computeIfPresent(String key,
			BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
		return map.computeIfPresent(key, remappingFunction);
	}
	@Override public String compute(String key, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
		return map.compute(key, remappingFunction);
	}
	@Override public String merge(String key, String value,
			BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
		return map.merge(key, value, remappingFunction);
	}
	@Override public void forEach(BiConsumer<? super String, ? super String> action) {
		map.forEach(action);
	}
	@Override public void replaceAll(BiFunction<? super String, ? super String, ? extends String> function) {
		map.replaceAll(function);
	}
}
