package sam.tsv;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

interface Rows {
    default Tsv tsv() {
        return (Tsv)this;
    }
    default Row addRow(String...values){
        Row row = new Row(Arrays.copyOf(values, tsv().columnNames.size()), tsv());
        tsv().rows.add(row);
        return row;
    }
    default Row addRow(Row row){
        if(tsv() != row.parent)
            return addRow(row.values);

        tsv().rows.add(row);
        return row;
    }
    /**
     * for any missing columnName null is set to the cell
     * @param columnNameValueMap map containing columnName -> value mapping
     * @return add Row
     * @throws IllegalStateException if firstRowIsHeader = false
     */
    default Row addRow(Map<String, String> columnNameValueMap){
        String[] str = new String[ tsv().columnNames.size()];

        columnNameValueMap.forEach((key, value) -> str[tsv().indexOfColumn(key)] = value);

        Row row = new Row(str, tsv());
        tsv().rows.add(row);
        return row;
    }
    default Row addRow(RowCell...rowCells){
        String[] str = new String[tsv().columnNames.size()];

        for (RowCell cv : rowCells)
            str[tsv().indexOfColumn(cv.columnName)] = cv.value;
        
        Row row = new Row(str, tsv());
        tsv().rows.add(row);
        return row;
    }
    default boolean remove(Row row) {
        return tsv().rows.remove(row);
    }
    default void removeAll(Collection<Row> rows){
        tsv().rows.removeAll(rows);
    }
    /**
     * removes rows that matches the condition 
     * @param columnNumber 1 based columnNumber 
     * @param value to compare with
     * @return removed row (if removed, otherwise null)
     */
    default void removesIf(Predicate<Row> condition){
        tsv().rows.removeIf(condition);
    }
    default int size() {
        return tsv().rows.size();
    }
    default boolean isEmpty() {
        return tsv().rows.isEmpty();
    }
    
    default void sort(Comparator<Row> comparator) {
        if (tsv().rows instanceof List<?>)
            Collections.sort((List<Row>) tsv().rows, comparator);
        else {
            List<Row> sorted = stream().sorted(comparator).collect(Collectors.toList());
            tsv().rows.clear();
            tsv().rows.addAll(sorted);
            sorted = null;
        }
    }
    
    /**
     * return first row for which Objects.equals(get(columNumber), value);
     * @param index
     * @param value
     * @return
     */
    default Row getWhere(int index, String value){

        for (Row row : tsv()) {
            if(Objects.equals(row.get(index), value))
                return row;
        }
        return null;
    }

    /**
     * returns row for which Objects.equals(getCell(columName), value);
     * @param columName
     * @param value
     * @return
     */
    default Row getWhere(String columName, String value){
        return getWhere(tsv().indexOfColumn(columName), value);
    }
    
    /**
     * first row satisfying Objects.equals(rowNumber X index, value) will be removed and returned
     * and the following  will be shifted upward. (this will change rowNumber of any subsequent )
     * @param index 1 based index 
     * @param value to compare with
     * @return removed row (if removed, otherwise null)
     */
    default Row removeFirstWhere(int index, String value){
        Iterator<Row> itr = tsv().rows.iterator();
        
        while (itr.hasNext()) {
            Row row = itr.next();
            
            if(Objects.equals(row.get(index), value)){
                itr.remove();
                return row;
            }
        }
        return null;
    }

    /**
     * Removes all  satisfying Objects.equals(rowNumber X index, value)
     * (this will alter rowNumber of )
     * @param index 1 based index 
     * @param value to compare with cell
     */
    default void removeAllWhere(int index, String value){
        tsv().rows.removeIf(row -> Objects.equals(row.get(index), value));
    }

    /**
     *  Removes all  satisfying values.contains(cell = rowNumber X index);
     * (this will alter rowNumber of )
     * @param columnIndex 1 based index
     * @param values  
     */
    default void removeWhere(int columnIndex, Collection<String> values){
        Objects.requireNonNull(values);
        
        if(values.isEmpty())
            return;

        HashSet<String> set = new HashSet<>(values);
        tsv().rows.removeIf(row -> set.contains(row.get(columnIndex)));
    }

    /**
     * @see #removeWhere(int, String)
     * @param columnName of columnNumber 
     * @param value to compare with cell
     */
    default void removeFirstWhere(String columnName, String value){
        removeFirstWhere(tsv().indexOfColumn(columnName), value);
    }

    /**
     * @see #removeWhere(int, Collection)
     * @param columnName of columnNumber
     * @param values  
     */
    default void removeWhere(String columnName, Collection<String> values){
        removeWhere(tsv().indexOfColumn(columnName), values);
    }
    default void clear() {
        tsv().rows.clear();
    }
    default Stream<Row> stream() {
        return tsv().rows.stream();
    }
}
