package sam.tsv.tsvmap;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

public class TsvMapConfig<K, V> {
    Map<K, V> map;
    boolean isFirstRowColumnNames;
    Charset charset;
    
    public TsvMapConfig(Map<K, V> map, boolean isFirstRowColumnNames, Charset charset) {
        this.map = map;
        this.isFirstRowColumnNames = isFirstRowColumnNames;
        this.charset = charset;
    }
    public TsvMapConfig(boolean isFirstRowColumnNames, Charset charset) {
        this(new LinkedHashMap<>(), isFirstRowColumnNames, charset);
    }
    public TsvMapConfig(boolean isFirstRowColumnNames) {
        this(isFirstRowColumnNames, Charset.defaultCharset());
    }
    public Map<K, V> getMap() { return map; }
    public boolean isFirstRowColumnNames() { return isFirstRowColumnNames; }
    public Charset getCharset() { return charset; }
    
    /**
     *   c.firstRowColumnNames(false);
     *   c.map(new LinkedHashMap<>());
     *   c.charset(Charset.defaultCharset());
     * @return
     */
    public static <K, V> TsvMapConfig<K, V> firstRowColumnNames() {
        return new TsvMapConfig<>(true);
    }
}
