package sam.sql.querymaker;

import java.util.Objects;
import java.util.function.UnaryOperator;

public class Select extends QueryMakerBase {
	public Select() {
		this(QueryMaker.qm());
	}
    Select(QueryMaker maker) {
        super(maker.getBuilder(), maker, "SELECT");
    }
	Select(QueryMakerBase s) {
		super(s, "SELECT");
	}
	public Select all() {
        sb.append("* ");
        return this;
    }
	public Select distinct() {
        sb.append("DISTINCT ");
        return this;
    }
    public Select columns(String... columnNames) {
        return columns(columnNames, false);
    }
    public Select columns(String[] columnNames, boolean quoted) {
        Objects.requireNonNull(columnNames, "columnNames cannot be null");
        
        if(columnNames.length == 0)
            throw new IllegalArgumentException("no columnNames specified");
        
        append(columnNames, quoted);
        return this;
    }
    
    public Select from(String tableName) {
        sb.append(" FROM ").append(tableName);
        space();
        return this;
    }
    public Select where(UnaryOperator<Where> consumer) {
        consumer.apply(where());
        return this;
    }
    public Select orderBy(boolean inIncreasingOrder, String... columnNames) {
    	appendAndSpace(" ORDER BY");
    	append(columnNames, false).append(' ');
    	if(!inIncreasingOrder)
    		appendAndSpace("DESC");
        return this;
    }
    public Select  append(String sql) {
    	sb.append(' ').append(sql);
    	return this;
    }
}
