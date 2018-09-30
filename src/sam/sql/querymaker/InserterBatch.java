package sam.sql.querymaker;

public class InserterBatch<E> extends PreparedStatementMakerBatch<E> {
	
	public InserterBatch(String tableName) {
		super(tableName);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("INSERT INTO ").append(tableName).append('(');
		for (String s : columnNames) 
			sb.append(s).append(',');
		
		if(sb.charAt(sb.length() - 1) == ',')
			sb.setLength(sb.length() - 1);
		
		sb.append(") VALUES(");
		
		for (int i = 0; i < columnNames.size(); i++)
			sb.append('?').append(',');
		
		if(sb.charAt(sb.length() - 1) == ',')
			sb.setLength(sb.length() - 1);
		
		sb.append(')');
			
		return sb.toString();
	}
}
