package sam.tsv;

import java.util.Arrays;

public final class Row {
	/**
	 * use created Tsv instance to create Rowbuilder 
	 * @author Sameer
	 */
	public static class Rowbuilder {
		private final Row row;
		Rowbuilder(int size, Tsv parent) {
			row = new Row(new String[size], parent);
		}
		public Rowbuilder set(int index, String value) {
			row.set(index, value);
			return this;
		}
		public Rowbuilder set(String columnName, String value) {
			row.set(columnName, value);
			return this;
		}
		public Row build() {
			return row;
		}
		/**
		 * add to parent tsv
		 */
		public Row add() {
			row.parent.addFromBuilder(row);
			return row;
		}
	} 

	private static final String[] DEFAULT_ARRY = new String[0];

	private String[] values;
	private final Tsv parent;
	private String line;
	
	Row(String[] values, Tsv parent) {
		this.values = values;
		this.parent = parent;
	}

	Row(String line, Tsv parent) {
		this.line = line;
		this.parent = parent;
		init0();
	}
	private void init0() {
		if(values != null || line == null) return;
		values = line == null ? null : TsvUtils.split(line);
		if(values == null)
			values = DEFAULT_ARRY;
	}
	public String get(int index) {
		return index < values.length ? values[index] : null;
	}

	public String get(String columnName) {
		return get(indexOf(columnName));
	}
	public int getInt(int index) {
		return Integer.parseInt(get(index));
	}
	public double getDouble(int index) {
		return Double.parseDouble(get(index));
	}
	public long getLong(int index) {
		return Long.parseLong(get(index));
	}
	public int getInt(String columnName) {
		return getInt(indexOf(columnName));
	}
	public double getDouble(String columnName) {
		return getDouble(indexOf(columnName));
	}
	public long getLong(String columnName) {
		return getLong(indexOf(columnName));
	}
	public void set(int index, String value) {
		if(index >= values.length) {
			values = Arrays.copyOf(values, index + 1);
		}
		line = null;
		values[index] = value;

	}
	public void set(String columnName, String value) {
		set(indexOf(columnName), value);
	}
	private int indexOf(String columnName) {
		return parent.getColumn(columnName).index;
	}
	public int size() {
		return values == null ? 0 : values.length;
	}
	@Override
	public String toString() {
		return Arrays.toString(values);
	}
	public String join(CharSequence delimiter) {
		return String.join(delimiter, values);
	}
	public StringBuilder join(String delimiter, String nullReplacement, StringBuilder collector) {
		
		for (int i = 0; i < values.length; i++) {
			String s = values[i];
			collector.append(s == null ? nullReplacement : s).append(i == values.length - 1 ? "" : delimiter);
		}
		return collector;
	}
	public void removeFromParent() {
		parent.remove(this);
	}
	String getLine() {
		return line;
	}
	public Tsv getTsv() {
		return parent;
	}
	String[] values() {
		return values;
	}
	public String[] toArray() {
		return values == null || values == DEFAULT_ARRY ? DEFAULT_ARRY : Arrays.copyOf(values, size());  
	}
}