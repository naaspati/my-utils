package sam.tsv;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import sam.collection.ArrayIterator;

public abstract class Row {
	/**
	 * use created Tsv instance to create Rowbuilder 
	 * @author Sameer
	 */
	public static class Rowbuilder {
		private final Row row;
		Rowbuilder(Row row) {
			this.row = row;
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
			row.getParent().addFromBuilder(row);
			return row;
		}
	} 

	static final String[] EMPTY_ARRAY = new String[0];

	private String[] values;
	
	Row(Row source) {
		this.values = source == null || source.size() == 0 ? EMPTY_ARRAY : Arrays.copyOf(source.values, source.values.length);
	}
	
	abstract Tsv getParent();
	
	Row(String[] values) {
		this.values = values;
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
		if(index >= values.length) 
			values = Arrays.copyOf(values, index + 1);
		
		values[index] = value;
		onModified();
	}
	
	protected void onModified() { }

	public void set(String columnName, String value) {
		set(indexOf(columnName), value);
	}
	private int indexOf(String columnName) {
		return getParent().getColumn(columnName).index;
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
		getParent().remove(this);
	}
	public Tsv getTsv() {
		return getParent();
	}
	public String[] toArray() {
		return values == null || values == EMPTY_ARRAY ? EMPTY_ARRAY : Arrays.copyOf(values, size());  
	}
	
	public Iterator<String> iterator() {
		return size() == 0 ? Collections.emptyIterator() : new ArrayIterator<>(values);
	}

	String[] values() {
		return values;
	}
}