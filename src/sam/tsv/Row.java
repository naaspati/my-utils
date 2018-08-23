package sam.tsv;

import java.util.Arrays;

public final class Row {
    /**
     * use created Tsv instance to create Rowbuilder 
     * @author Sameer
     */
    public static class Rowbuilder {
        private final Row row;
        Rowbuilder(int size, Tsv parent) {
            row = new Row(new String[size], parent);
        }
        public Rowbuilder set(int index, String value) {
            row.set(index, value);
            return this;
        }
        public Rowbuilder set(String columnName, String value) {
            row.set(columnName, value);
            return this;
        }
        public Row build() {
            return row;
        }
        /**
         * add to parent tsv
         */
        public Row add() {
            row.parent.addFromBuilder(row);
            return row;
        }
    } 

    String[] values;
    Tsv parent;
    private Object userData;

    Row(String[] values, Tsv parent ) {
        this.values = values;
        this.parent = parent;
    }
    public Object getUserData() {
        return userData;
    }
    public void setUserData(Object userData) {
        this.userData = userData;
    }
    public String get(int index) {
        return index < values.length ? values[index] : null;
    }
    public String get(String columnName) {
        return get(indexOf(columnName));
    }
    public int getInt(int index) {
        return Integer.parseInt(get(index));
    }
    public double getDouble(int index) {
        return Double.parseDouble(get(index));
    }
    public long getLong(int index) {
        return Long.parseLong(get(index));
    }
    public int getInt(String columnName) {
        return getInt(indexOf(columnName));
    }
    public double getDouble(String columnName) {
        return getDouble(indexOf(columnName));
    }
    public long getLong(String columnName) {
        return getLong(indexOf(columnName));
    }
    public void set(int index, String value) {
        if(index >= values.length)
            values = Arrays.copyOf(values, index + 1);

        values[index] = value;

    }
    public void set(String columnName, String value) {
        set(indexOf(columnName), value);
    }
    private int indexOf(String columnName) {
        return parent.indexOfColumn(columnName);
    }
    public int size() {
        return values.length;
    }
    @Override
    public String toString() {
        return Arrays.toString(values);
    }
    public String join(CharSequence delimiter) {
        return String.join(delimiter, values);
    }
    public StringBuilder join(String delimiter, String nullReplacement, StringBuilder collector) {
        for (int i = 0; i < values.length; i++) {
            String s = values[i];
            collector.append(s == null ? nullReplacement : s).append(i == values.length - 1 ? "" : delimiter);
        }
        return collector;
    }
    public void removeFromParent() {
        parent.remove(this);
    }
}