package sam.tsv;


import static java.util.Objects.requireNonNull;
import static sam.io.IOConstants.defaultCharset;
import static sam.io.IOConstants.defaultOnMalformedInput;
import static sam.io.IOConstants.defaultOnUnmappableCharacter;
import static sam.tsv.TsvUtils.escape;
import static sam.tsv.TsvUtils.unescape;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import sam.myutils.Checker;
import sam.tsv.tsvmap.Converter;

public class TsvMap<K, V> implements Map<K, V> {
	private final Map<K, V> map;
	private String keyColumnName, valueColumnName;
	private final Converter<K> keyConverter;
	private final Converter<V> valueConverter;
	private final boolean firstRowIsColumnNames;
	private final Charset charset;

	public static class Builder<K, V> {
		Map<K, V> map;
		String keyName, valueName;
		Converter<K> keyC;
		Converter<V> valueC;
		boolean firstRowIsColumnNames;
		Charset charset = defaultCharset();

		public Builder<K, V> map(Map<K, V> map) {this.map = map; return this;}
		public Builder<K, V> columnName(String key, String value) {
			this.keyName = Objects.requireNonNull(key);
			this.valueName = Objects.requireNonNull(value);

			if(Objects.equals(key, value))
				throw new IllegalArgumentException("keyColumnName, valueColumnName  cannot be same");
			return this;
		}
		public Builder(Converter<K> key, Converter<V> value) {
			this.keyC = Objects.requireNonNull(key);
			this.valueC = Objects.requireNonNull(value);
		}
		public Builder<K, V> charset(Charset charset) {
			this.charset = charset;
			return this;
		}
		public TsvMap<K, V> build() {
			if(keyName == null || valueName == null)
				return new TsvMap<>(map(), keyC, valueC);
			else
				return new TsvMap<>(map(), keyName, valueName, keyC, valueC, true, charset);
		}
		Map<K, V> map() {
			return map == null ? new LinkedHashMap<>() : map;
		}
		public TsvMap<K, V> load(InputStream is, boolean firstRowIsColumnNames) throws IOException {
			return new TsvMap<>(firstRowIsColumnNames, map(), is, charset, keyC, valueC);
		}
		public TsvMap<K, V> load(Path path, boolean firstRowIsColumnNames) throws IOException {
			try(InputStream is = Files.newInputStream(path)) {
				return new TsvMap<>(firstRowIsColumnNames, map(), is, charset, keyC, valueC);	
			}
		}
	}
	public static <K, V> Builder<K, V> builder(Converter<K> key, Converter<V> value) {
		return new Builder<>(key, value);
	} 
	public TsvMap(boolean firstRowIsColumnNames, Map<K, V> sink, Path path, Charset charset, Converter<K> keyConverter, Converter<V> valueConverter) throws IOException {
		this(firstRowIsColumnNames, sink, Files.newInputStream(path), charset, keyConverter, valueConverter);
	}
	public TsvMap(boolean firstRowIsColumnNames, Map<K, V> sink, InputStream is, Charset charset, Converter<K> keyConverter, Converter<V> valueConverter) throws IOException {
		this.keyConverter = requireNonNull(keyConverter);
		this.valueConverter = requireNonNull(valueConverter);

		this.map = requireNonNull(sink);
		this.firstRowIsColumnNames = firstRowIsColumnNames;
		boolean firstLine[] = {true};
		this.charset = charset;

		new LineReader() {
			@Override
			public void accept(String line) {
				int n = line.indexOf('\t');
				if(n < 0) return;
				String k = unescape(subSequence(line, 0, n)).toString();
				String v = unescape(subSequence(line, n+1, line.length())).toString();

				if(firstRowIsColumnNames && firstLine[0]) {
					firstLine[0] = false;
					keyColumnName = k;
					valueColumnName = v;
				} else {
					map.put(keyConverter.fromString(k), valueConverter.fromString(v));	
				}
			}
		}.parse(is, charset);

		if(firstRowIsColumnNames && firstLine[0]) 
			throw new TsvException("no column name found in file");
	}
	public TsvMap(Map<K, V> map, Charset charset,  String keyColumnName, String valueColumnName, Converter<K> keyConverter, Converter<V> valueConverter) {
		Checker.requireNonNull(new String[] {"keyColumnName",
				"valueColumnName",
				"keyConverter",
		"valueConverter"},

				keyColumnName,
				valueColumnName,
				keyConverter,
				valueConverter
				); 

		this.map = map;
		this.keyColumnName = keyColumnName;
		this.valueColumnName = valueColumnName;
		this.charset = charset;

		if(Objects.equals(keyColumnName, valueColumnName))
			throw new IllegalArgumentException("keyColumnName, valueColumnName  cannot be same");

		this.keyConverter = keyConverter;
		this.valueConverter = valueConverter;
		this.firstRowIsColumnNames = true;
	}

	protected CharSequence subSequence(String line, int i, int n) {
		return new CharSequence() {
			@Override
			public CharSequence subSequence(int start, int end) {
				return null;
			}
			@Override
			public int length() {
				return n;
			}

			@Override
			public char charAt(int index) {
				return line.charAt(index);
			}
			@Override
			public String toString() {
				return line.substring(i, n);
			}
		};
	}

	public TsvMap( Map<K, V> map, Converter<K> keyConverter, Converter<V> valueConverter) {
		this(map, null, null, keyConverter, valueConverter, false, defaultCharset());
	}
	private TsvMap(Map<K, V> map, String keyColumnName, String valueColumnName, Converter<K> keyConverter, Converter<V> valueConverter, boolean firstRowIsColumnNames, Charset charset) {
		super();
		this.map = map;
		if(firstRowIsColumnNames) {
			this.keyColumnName = Objects.requireNonNull(keyColumnName);
			this.valueColumnName = Objects.requireNonNull(valueColumnName);
			
			if(Objects.equals(keyColumnName, valueColumnName))
				throw new IllegalArgumentException("keyColumnName, valueColumnName  cannot be same");
		} else {
			this.keyColumnName = null;
			this.valueColumnName = null;
		}
		this.keyConverter = Objects.requireNonNull(keyConverter);
		this.valueConverter = Objects.requireNonNull(valueConverter);
		this.firstRowIsColumnNames = firstRowIsColumnNames;
		this.charset = Objects.requireNonNull(charset);
	}
	public void save(Path p) throws IOException {
		save(p, charset, defaultOnMalformedInput(), defaultOnUnmappableCharacter());
	}

	private Iterator<Entry<K, V>> itr;
	public void save(Path p, Charset charset, CodingErrorAction onMalformedInput, CodingErrorAction onUnmappableCharacter) throws IOException {
		StringBuilder sb = TsvUtils.wsb.poll();
		sb.setLength(0);

		itr = map.entrySet().iterator();

		if(firstRowIsColumnNames) 
			append(sb, keyColumnName, valueColumnName);
		while (itr.hasNext()) {
			Map.Entry<K, V> e = itr.next();
			append(sb, keyConverter.toString(e.getKey()), valueConverter.toString(e.getValue()));
		}
		TsvUtils.save(sb, p, charset, onMalformedInput, onUnmappableCharacter);
		TsvUtils.wsb.offer(sb);
	}
	private void append(StringBuilder sb, String k, String v) {
		sb.append(k == null ? "" : escape(k))
		.append('\t')
		.append(v == null ? "" : escape(v));
		if(itr.hasNext())
			sb.append('\n');
	}

	public String getKeyColumnName() {
		return keyColumnName;
	}
	public String getValueColumnName() {
		return valueColumnName;
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
