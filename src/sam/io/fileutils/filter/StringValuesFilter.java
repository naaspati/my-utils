package sam.io.fileutils.filter;

abstract class StringValuesFilter implements Filter {
	protected final String[] values;

	public StringValuesFilter(String[] values) {
		this.values = values;
	}
	public String[] values() {
		return values;
	}
}
