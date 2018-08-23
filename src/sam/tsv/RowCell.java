package sam.tsv;

public class RowCell {
    public final String columnName, value;

    public RowCell(String columnName, String value) {
        this.columnName = columnName;
        this.value = value;
    }
    public static RowCell of(String columnName, String value) {
        return new RowCell(columnName, value);
    }

}