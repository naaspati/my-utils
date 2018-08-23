package sam.tsv;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

interface Columns {
    default Tsv ctsv(){
        return (Tsv)this;
    }
    
    default void addColumnIfAbsent(String columnName) {
        if(hasColumnNames() && !ctsv().columnNames.containsKey(columnName))
            addColumn(columnName);
    } 
    default void addColumn(String columnName) {
        if(!hasColumnNames() && !ctsv().isEmpty())
            throw new TsvException("tsv has no column names, adding new column failed: "+columnName);
        
        Objects.requireNonNull(columnName);
        
        Map<String, Integer> map = ctsv().columnNames;
        
        if(map.containsKey(columnName))
            throw new TsvException("duplicate column "+columnName+"("+map.get(columnName)+")");
        map.put(columnName, map.size());
    }

    default void addColumns(String... columnNames) {
        if(columnNames == null || columnNames.length == 0)
            throw new TsvException("columnNames cannot be empty/null");
        
        if(!hasColumnNames() && !ctsv().isEmpty())
            throw new TsvException("tsv has no column names, adding new columns failed: "+Arrays.toString(columnNames));
        
        for (int i = 0; i < columnNames.length; i++) {
            if(columnNames[i] == null)
                throw new NullPointerException("columnNames["+i+"] == null");
        }
        
        Map<String, Integer> map = ctsv().columnNames;
        
        for (String s : columnNames) {
            if(map.containsKey(s))
                throw new TsvException("duplicate column "+s+"("+map.get(s)+")");
            map.put(s, map.size());
        }
    }
    default String[] getColumnNames() {
        if(ctsv().columnNames == null) return null;
        String[] str = new String[ctsv().columnNames.size()];
        ctsv().columnNames.forEach((name, index) -> str[index] = name);
        return str;
    }
    default boolean hasColumnNames() {
        return ctsv().columnNames != null;
    }
    default int indexOfColumn(String columnName) {
        Integer index = ctsv().columnNames.get(columnName);

        if(index == null)
            throw new TsvException("column name not found: "+columnName);

        return index;
    }
    default boolean containsColumn(String columnName) {
        if(!hasColumnNames())
            throw new TsvException("Tsv does'nt have column names");

        return ctsv().columnNames.containsKey(columnName);
    }
}
