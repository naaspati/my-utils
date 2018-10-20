package sam.sql.querymaker;

public class SetterBatch<E> extends PreparedStatementMakerBatch<E> {
private final StringBuilder sb;
	
	public SetterBatch(String tableName) {
		sb = new StringBuilder(100).append("UPDATE ").append(tableName).append(" SET ");
	}
	
	private int count = 0;
	@Override
	protected void addColumn(String columnName) {
		
		if(count != 0)
			sb.append(',');
		
		sb.append(columnName).append('=').append('?');
		count++;
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}
	
	public WhereColumnEqual<E> whereColumnEqual() {
		return new WhereColumnEqual<>(this);
    }
	
	public static class WhereColumnEqual<T> extends PreparedStatementMakerBatch<T> {
		private final StringBuilder sb;
		private SetterBatch<T> s;
		private WhereColumnEqual(SetterBatch<T> s) {
			super(s);
			this.s = s;
			this.sb = s.sb;
			sb.append(" WHERE ");
		}
		@Override
		protected void addColumn(String columnName) {
			sb.append(' ').append(columnName).append('=').append('?').append(' ');
		}
		public WhereColumnEqual<T> or() {
			sb.append("OR ");
			return this;
		}
		public WhereColumnEqual<T> and() {
			sb.append("AND ");
			return this;
		}
		
		@Override
		public String toString() {
			return s.toString();
		}
	}
}
