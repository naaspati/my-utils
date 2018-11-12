package sam.tsv;

import static sam.io.DefaultCharset.DEFAULT_CHARSET;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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

import sam.tsv.tsvmap.Converter;
import sam.tsv.tsvmap.TsvMapConfig;

public class TsvMap<K, V> implements Map<K, V> {
    private final Map<K, V> map;
    private final Converter<K> keyConverter;
    private final Converter<V> valueConverter;
    private final TsvMapConfig<K, V> config;
    private final boolean firstRowIsColumnNames;
    private final String keyColumnName, valueColumnName;

    public TsvMap(InputStream is, TsvMapConfig<K, V> config, Converter<K> keyConverter, Converter<V> valueConverter) throws IOException {
        this.config = Objects.requireNonNull(config);
        this.keyConverter = Objects.requireNonNull(keyConverter);
        this.valueConverter = Objects.requireNonNull(valueConverter);
        
        this.map = Objects.requireNonNull(config.getMap());
        this.firstRowIsColumnNames = config.isFirstRowColumnNames();
        String[] s = new String[2];

        new Parser() {
            @Override
            void setColumnsNames(String[] values) {
                s[0] = values[0];
                s[1] = values[1];
            }
            @Override
            void addRow(String[] row) {
                map.put(keyConverter.fromString(row[0]), valueConverter.fromString(row[1]));
            }
        }.parse(is, firstRowIsColumnNames, config.getCharset(), null);

        keyColumnName = s[0];
        valueColumnName = s[1];
    }

    public TsvMap(Map<K, V> map) {
        this.keyConverter = Converter.defaultConverter();
        this.valueConverter = Converter.defaultConverter();
        
        this.config = new TsvMapConfig<>(map, false, DEFAULT_CHARSET);
        
        this.keyColumnName = null;
        this.valueColumnName = null;
        this.map = config.getMap();
        this.firstRowIsColumnNames = false;
    }
    public TsvMap() {
        this(new LinkedHashMap<>());
    }
    public TsvMap(String keyColumnName, String valueColumnName) {
        this(new TsvMapConfig<>(new LinkedHashMap<>(), true, DEFAULT_CHARSET), keyColumnName, valueColumnName, Converter.defaultConverter(), Converter.defaultConverter());
    }
    public TsvMap(TsvMapConfig<K, V> config, String keyColumnName, String valueColumnName, Converter<K> keyConverter, Converter<V> valueConverter) {
        this.config = Objects.requireNonNull(config);
        this.keyColumnName = Objects.requireNonNull(keyColumnName);
        this.valueColumnName = Objects.requireNonNull(valueColumnName);
        this.keyConverter = Objects.requireNonNull(keyConverter);
        this.valueConverter = Objects.requireNonNull(valueConverter);

        if(Objects.equals(keyColumnName, valueColumnName))
            throw new IllegalArgumentException("keyColumnName, valueColumnName  cannot be same");

        this.map = Objects.requireNonNull(config.getMap());
        this.firstRowIsColumnNames = true;
    }
    public void save(Path p) throws IOException {
        save(p, new TsvSaver());
    }
    public void save(Path p, TsvSaver tsvSaver) throws IOException {
        save(p, config.getCharset(), tsvSaver);
    }
    public void save(Path p, Charset charset, TsvSaver tsvSaver) throws IOException {
        Iterator<Entry<K, V>> itr = map.entrySet().iterator();

        tsvSaver.save(!firstRowIsColumnNames ? null : new String[] {keyColumnName, valueColumnName}, p, charset, null, new Iterator<String[]>() {
            @Override
            public String[] next() {
                Entry<K, V> e = itr.next();
                return new String[] {keyConverter.toString(e.getKey()), valueConverter.toString(e.getValue())};
            }
            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }
        });
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
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Objects.requireNonNull(m);
        map.forEach((key, value) -> {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
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

    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
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
        Objects.requireNonNull(key);
        Objects.requireNonNull(newValue);

        return map.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return map.replace(
                Objects.requireNonNull(key),
                Objects.requireNonNull(value)
                );
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return map.computeIfAbsent(
                Objects.requireNonNull(key),
                Objects.requireNonNull(mappingFunction)
                );
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.computeIfPresent(
                Objects.requireNonNull(key), 
                Objects.requireNonNull(remappingFunction)
                );
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return map.compute(
                Objects.requireNonNull(key), 
                Objects.requireNonNull(remappingFunction)
                );
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return map.merge(
                Objects.requireNonNull(key),
                value, 
                Objects.requireNonNull(remappingFunction)
                );
    } 
}
