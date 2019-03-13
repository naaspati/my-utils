package sam.tsv;


import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import sam.collection.Pair;

// FIXME testing remains 
public class TsvMap<K, V> implements Map<K, V> {
	private final Map<K, V> map;
	private final Pair<String, String> cols;
	private final Pair<Converter<K>, Converter<V>> converters;
	
	private TsvMap(BufferedReader source, String keyCol, String valueCol, Class<? extends Map<K, V>> mapImplemetation, Converter<K> keyConverter, Converter<V> valueConverter) throws IOException {
		if(keyCol == null && valueCol == null)
			this.cols  = new Pair<String, String>(keyCol, valueCol);
		else if(keyCol != null && valueCol != null)
			this.cols = null;
		else 
			throw new IllegalArgumentException("keyCol("+keyCol+") == null || valueCol("+valueCol+") == null");
		
		this.converters = new Pair<Converter<K>, Converter<V>>(Objects.requireNonNull(keyConverter), Objects.requireNonNull(valueConverter));
		
		try {
			this.map = mapImplemetation.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		load(source);
	}
	public void load(BufferedReader source) throws IOException {
		map.clear();
		
		TsvParser parser = new TsvParser();
		boolean first = true;
		
		while(true) {
			String line = source.readLine();
			if(first && line == null)
				return;
			
			Iterator<String> itr = line == null ? null : parser.iterator(line);
			
			if(first && cols != null) {
				if(itr == null) // empty file
					return;
				
				String k = itr.next();
				String v = itr.next();
				
				if(!k.equals(cols.key) || !v.equals(cols.value))
					throw new IOException(String.format("\"%s\" != \"%s\" || \"%s\" != \"%s\"", k, cols.key, v, cols.value));
			} else if(itr.hasNext()) {
				K k = converters.key.fromString(itr.next());
				V v = converters.value.fromString(itr.next());
				map.put(k, v);
			}
			
			if(line == null)
				break;
			
			first = false;
		}
	}

	public void save(Appendable target) throws IOException {
		TsvSaver saver = new TsvSaver();
		
		if(cols != null) 
			saver.append(cols.key, cols.value, target);
		
		for (Entry<K, V> e : map.entrySet()) 
			saver.append(converters.key.toString(e.getKey()), converters.value.toString(e.getValue()), target);
	}

	@Override
	public int size() { 
		return map.size();
	}
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}
	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}
	@Override
	public V get(Object key) {
		return map.get(key);
	}
	@Override
	public V put(K key, V value) {
		requireNonNull(key);
		requireNonNull(value);

		return map.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		requireNonNull(m);
		map.forEach((key, value) -> {
			requireNonNull(key);
			requireNonNull(value);
		});
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof TsvMap)) return false;
		return map.equals(((TsvMap)o).map);
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return map.getOrDefault(key, defaultValue);
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		map.forEach(action);
	}

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		map.replaceAll(function);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return map.putIfAbsent(key, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return map.remove(key, value);
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		requireNonNull(key);
		requireNonNull(newValue);

		return map.replace(key, oldValue, newValue);
	}

	@Override
	public V replace(K key, V value) {
		return map.replace(
				requireNonNull(key),
				requireNonNull(value)
				);
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return map.computeIfAbsent(
				requireNonNull(key),
				requireNonNull(mappingFunction)
				);
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return map.computeIfPresent(
				requireNonNull(key), 
				requireNonNull(remappingFunction)
				);
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return map.compute(
				requireNonNull(key), 
				requireNonNull(remappingFunction)
				);
	}

	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return map.merge(
				requireNonNull(key),
				value, 
				requireNonNull(remappingFunction)
				);
	} 
}
