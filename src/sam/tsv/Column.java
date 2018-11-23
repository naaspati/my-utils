package sam.tsv;

public class Column {
	public final int index;
	public final String columnName;
	
	public Column(String columnName, int index) {
		this.index = index;
		this.columnName = columnName; 
	}
	public int getIndex() {
		return index;
	}
	public String getColumnName() {
		return columnName;
	}

	public String get(Row row) {
		return row.get(index);
	}
	public int getInt(Row row) {
		return row.getInt(index);
	}
	public double getDouble(Row row) {
		return row.getDouble(index);
	}
	public long getLong(Row row) {
		return row.getLong(index);
	}
	public void set(Row row, String value) {
		row.set(index, value);
	}
}
