package sam.tsv.tsvmap;

@FunctionalInterface
public interface Converter<V> {
    /**
     * return a converter which always return fromString(...) -> null
     * @return
     */
    public static <V> Converter<V> defaultConverter() {
        return v -> null;
    }
    
    V fromString(String value);
    default String toString(V value) {
        return value == null ? null : value.toString();
    }
}
