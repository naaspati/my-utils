package sam.sql.querymaker;

public class InserterBatch<E> extends PreparedStatementMakerBatch<E> {
	private final StringBuilder sb;

	public InserterBatch(String tableName) {
		sb = new StringBuilder(100).append("INSERT INTO ").append(tableName).append('(');
	}

	private int count = 0;
	@Override
	protected void addColumn(String columnName) {
		if(count != 0)
			sb.append(',');

		sb.append(columnName);
		count++;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.sb.length() + 20 + count*2);
		sb.append(this.sb);

		sb.append(") VALUES(");

		for (int i = 0; i < count; i++)
			sb.append('?').append(',');

		if(sb.charAt(sb.length() - 1) == ',')
			sb.setLength(sb.length() - 1);

		sb.append(')');

		return sb.toString();
	}
}
