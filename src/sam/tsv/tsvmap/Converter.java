package sam.tsv.tsvmap;

@FunctionalInterface
public interface Converter<T> {
	Converter<Integer> INTEGER = new Converter<Integer>() {
		@Override
		public Integer fromString(String value) {
			return Integer.valueOf(value);
		}
	};
	Converter<Long> LONG = new Converter<Long>() {
		@Override
		public Long fromString(String value) {
			return Long.valueOf(value);
		}
	};
	Converter<Double> DOUBLE = new Converter<Double>() {
		@Override
		public Double fromString(String value) {
			return Double.valueOf(value);
		}
	};
	Converter<String> STRING = new Converter<String>() {
		@Override
		public String fromString(String value) {
			return value;
		}
	};
	
    
    T fromString(String value);
    default String toString(T value) {
        return value == null ? null : value.toString();
    }
}
