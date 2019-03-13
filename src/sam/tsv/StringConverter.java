package sam.tsv;

@FunctionalInterface
public interface StringConverter<T> {
	T fromString(String s);
	default String toString(T t) {
		return t == null ? null : t.toString();
	}
	

}
