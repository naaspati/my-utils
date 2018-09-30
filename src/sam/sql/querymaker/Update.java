package sam.sql.querymaker;

import java.util.function.Consumer;

public class Update extends QueryMakerBase {
    Update(QueryMaker maker, String tablename) {
        super(maker.getBuilder(), maker, "UPDATE");
        appendAndSpace(tablename);
        appendAndSpace("SET");
    }
    
    private int count = 0;
    private final char[] separator = {',', ' '};
    
    private StringBuilder _append(String columnName) {
        if(count > 0)
            sb.append(separator);

        sb.append(columnName).append('=');
        count++;
        return sb;
    }

    public Update set(String columnName, int value) {
        _append(columnName).append(value);
        return this;
    }
    public Update set(String columnName, double value) {
        _append(columnName).append(value);
        return this;
    }
    public Update set(String columnName, float value) {
        _append(columnName).append(value);
        return this;
    }
    public Update set(String columnName, char value, boolean quoted) {
        _append(columnName);
        quote(value, quoted);
        return this;
    }
    public Update set(String columnName, long value) {
        _append(columnName).append(value);
        return this;
    }
    public <E> Update set(String columnName, E value, boolean quoted) {
        _append(columnName);
        quote(value, quoted);
        return this;
    }
    
    
    public Update where(Consumer<Where> consumer) {
        space();
        consumer.accept(where());
        return this;
    } 
    public Update placeholders(String...columnNames) {
        checkArray(columnNames);
        
        if(columnNames.length == 1)
        	sb.append(columnNames[0]).append("=?");
        else {
        	for (String s : columnNames)
                sb.append(s).append("=?,");
        	setLastCharSpace();
        }
        return this;
    }
}
