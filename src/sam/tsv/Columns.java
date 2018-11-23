package sam.tsv;

import java.util.Map;
import java.util.Objects;

import sam.myutils.Checker;

interface Columns {
    default Tsv ctsv(){
        return (Tsv)this;
    }
    
    default void addColumnIfAbsent(String columnName) {
        if(!ctsv().columns.containsKey(columnName))
            addColumn(columnName);
    } 
    default void addColumn(String columnName) {
        Objects.requireNonNull(columnName);
        Map<String, Column> map = ctsv().columns;
        
        if(map.containsKey(columnName))
            throw new TsvException("columnName already exists "+columnName+" ("+map.get(columnName)+")");
        
        Column col = new Column(columnName, map.size());
        map.put(columnName, col);
    }

    default void addColumns(String... columnNames) {
        if(Checker.isEmpty(columnNames))
            throw new TsvException("columnNames cannot be empty/null");
        
        for (String s : columnNames) 
			addColumn(s);
    }
    
    default Column[] getColumns() {
    	Map<String, Column> map = ctsv().columns;
    	Column[] columns = new Column[map.size()];
    	
    	map.forEach((s,t) -> columns[t.index] = t);
        return columns;
    }
    default String[] getColumnNames() {
    	Map<String, Column> map = ctsv().columns;
    	String[] columns = new String[map.size()];
    	
    	map.forEach((s,t) -> columns[t.index] = t.columnName);
        return columns;
    }
    default Column getColumnIfPresent(String columnName) {
    	return ctsv().columns.get(columnName);
    }
    default Column getColumn(String columnName) {
        Column c = ctsv().columns.get(columnName);
        if(c == null)
            throw new TsvException("column name not found: "+columnName);
        return c;
    }
    default boolean containsColumn(String columnName) {
        return ctsv().columns.containsKey(columnName);
    }
}
