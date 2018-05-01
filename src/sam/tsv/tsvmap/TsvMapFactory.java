package sam.tsv.tsvmap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import sam.tsv.TsvMap;


public interface TsvMapFactory {

    public static final class TsvMapBuilder<K, V> {
        private final TsvMapConfig<K, V> config;
        private final Converter<K> keyConverter;
        private final Converter<V> valueConverter;

        public TsvMapBuilder(TsvMapConfig<K, V> config, Converter<K> keyConverter, Converter<V> valueConverter) {
            this.config = config;
            this.keyConverter = keyConverter;
            this.valueConverter = valueConverter;
        }
        public TsvMapBuilder(Converter<K> keyConverter, Converter<V> valueConverter) {
            this(new TsvMapConfig<>(true), keyConverter, valueConverter);
            config.isFirstRowColumnNames = true;
            config.charset = Charset.defaultCharset();
            config.map = new HashMap<>();
        }
        public void map(Map<K, V> map) {
            config.map = map;
        }
        public void firstRowColumnNames(boolean b) {
            config.isFirstRowColumnNames  = b;
        }
        public void charset(Charset charset) {
            config.charset = charset;
        }
        public TsvMap<K, V> parse(InputStream is) throws IOException {
            return new TsvMap<>(is, config, keyConverter, valueConverter);            
        }
        public TsvMap<K, V> parse(Path path) throws IOException {
            try(InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) {
                return parse(is);   
            }            
        }
    }
    public static TsvMap<String, String> parse(InputStream is, boolean isFirstRowColumnNames) throws IOException {
        Converter<String> c = s -> s;
        return new TsvMap<>(is, new TsvMapConfig<>(isFirstRowColumnNames), c, c);
    }
    public static TsvMap<String, String> parse(Path path, boolean isFirstRowColumnNames) throws IOException {
        try(InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) {
            return parse(is, isFirstRowColumnNames);   
        }
    }
    public static <K, V> TsvMapBuilder<K, V> builder(Converter<K> keyConverter, Converter<V> valueConverter){
        return new TsvMapBuilder<>(keyConverter, valueConverter);
    }
    public static <K, V> TsvMapBuilder<K, V> builder(TsvMapConfig<K, V> config, Converter<K> keyConverter, Converter<V> valueConverter) {
        return new TsvMapBuilder<>(config, keyConverter, valueConverter);
    }
    public static <K, V> TsvMapBuilder<K, V> builder(Map<K, V> map, boolean isFirstRowColumnNames, Charset charset, Converter<K> keyConverter, Converter<V> valueConverter) {
        TsvMapConfig<K, V> c = new TsvMapConfig<>(map, isFirstRowColumnNames, charset);
        return builder(c, keyConverter, valueConverter);
    }
    public static <K, V> TsvMapBuilder<K, V> builder(boolean isFirstRowColumnNames, Charset charset, Converter<K> keyConverter, Converter<V> valueConverter) {
        return builder(new LinkedHashMap<>(), isFirstRowColumnNames, charset, keyConverter, valueConverter);
    }
    public static <K, V> TsvMapBuilder<K, V> builder(boolean isFirstRowColumnNames, Converter<K> keyConverter, Converter<V> valueConverter) {
        return builder(new LinkedHashMap<>(), isFirstRowColumnNames, Charset.defaultCharset(), keyConverter, valueConverter);
    }
}
