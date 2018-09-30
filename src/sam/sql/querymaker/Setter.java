package sam.sql.querymaker;

import java.util.function.UnaryOperator;

public class Setter extends PreparedStatementMaker {
	private StringBuilder sb;
	
	public Setter(String tableName) {
		super(tableName);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
		
		for (String s : columnNames) 
			sb.append(s).append('=').append('?').append(',');
		
		if(sb.charAt(sb.length() - 1) == ',')
			sb.setLength(sb.length() - 1);
		
		if(this.sb != null)
			sb.append(' ').append(this.sb);
		
		return sb.toString();
	}
	
	public void where(UnaryOperator<Where> consumer) {
		sb = new StringBuilder();
        consumer.apply(new Where(sb));
    }
}
