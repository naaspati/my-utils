package sam.tsv;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

interface Columns {
    default Tsv ctsv(){
        return (Tsv)this;
    }

    default void addColumns(String... columnNames) {
        if(!hasNames() && !ctsv().isEmpty())
            throw new TsvException("tsv has no column names, adding new columns failed: "+Arrays.toString(columnNames));

        if(ctsv().columnNames != null && !ctsv().columnNames.isEmpty())
            setColumns(Stream.concat(ctsv().columnNames.keySet().stream().sorted(Comparator.comparing(ctsv().columnNames::get)), Stream.of(columnNames)).toArray(String[]::new));
        else 
            setColumns(columnNames);
    }
    default String[] getColumnNames() {
        if(ctsv().columnNames == null) return null;
        String[] str = new String[ctsv().columnNames.size()];
        ctsv().columnNames.forEach((name, index) -> str[index] = name);
        return str;
    }
    default void setColumns(final String[] columnNames){
        if(columnNames == null || columnNames.length == 0)
            throw new TsvException("columnNames cannot be empty/null");

        int[] nulls = IntStream.range(0, columnNames.length)
                .filter(i -> columnNames[i] == null).toArray();

        if(nulls.length != 0) 
            throw new TsvException("columns name cannot null, found null at: "+Arrays.toString(nulls));

        ctsv().columnNames = new HashMap<>();

        Map<String, List<Integer>> duplicatesCheck = 
                IntStream.range(0, columnNames.length)
                .boxed()
                .peek(j -> ctsv().columnNames.put(columnNames[j], j))
                .collect(Collectors.groupingBy(j -> columnNames[j]));

        duplicatesCheck.values().removeIf(l -> l.size() < 2);

        if(!duplicatesCheck.isEmpty()) {
            StringBuilder b = new StringBuilder("duplicate columnNames: {");
            duplicatesCheck.forEach((s, t) -> b.append(s).append(':').append(t).append(", "));
            b.setLength(b.length() - 2);
            b.append('}');
            throw new TsvException(b.toString());
        }
    } 

    default boolean hasNames() {
        return ctsv().columnNames != null;
    }
    default int indexOf(String columnName) {
        Integer index = ctsv().columnNames.get(columnName);

        if(index == null)
            throw new TsvException("column name not found: "+columnName);

        return index;
    }
    default boolean contains(String columnName) {
        if(!hasNames())
            throw new TsvException("Tsv does'nt have column names");

        return ctsv().columnNames.containsKey(columnName);
    }
}
