package sam.sql;

import java.util.PrimitiveIterator.OfInt;
import java.util.function.Consumer;

import com.almworks.sqlite4java.SQLiteException;

import sam.collection.PrimitiveIterators;
import sam.myutils.Checker;

public interface QueryHelper {
    /**
     * closed sql with ");\n"
     * 
     * @param tableName
     * @param columnNames
     * @return
     * @throws SQLiteException
     */
    public static String insertSQL(String tableName, String... columnNames) {
        if (Checker.isEmpty(columnNames))
            throw new IllegalArgumentException("no column names specified");

        StringBuilder sb = new StringBuilder().append("INSERT INTO ").append(tableName).append("(");

        for (String s : columnNames)
            sb.append(s).append(',');

        sb.setLength(sb.length() - 1);
        sb.append(") VALUES(");

        for (int i = 0; i < columnNames.length; i++)
            sb.append('?').append(',');

        sb.setLength(sb.length() - 1);
        sb.append(");\n");

        return sb.toString();
    }

    /**
     * not closed, can be appended
     * 
     * @param tableName
     * @param columnNames
     * @return
     * @throws SQLiteException
     */
    public static StringBuilder selectSQL(String tableName, String... columnNames) {
        if (Checker.isEmptyTrimmed(tableName))
            throw new IllegalArgumentException("invalid tablename: tablename cannnot be empty");

        if (Checker.isEmpty(columnNames))
            throw new IllegalArgumentException("no column names specified");

        StringBuilder sb = new StringBuilder().append("SELECT ");

        for (String s : columnNames)
            sb.append(s).append(',');
        sb.setLength(sb.length() - 1);

        sb.append(" FROM ").append(tableName);

        return sb;
    }

    public static StringBuilder selectWhereFieldInSQL(String tableName, String field, Iterable<Integer> values,
            String... columnNames) {
        return selectWhereFieldInSQL(tableName, field, PrimitiveIterators.of(values.iterator()), columnNames);
    }

    public static StringBuilder selectWhereFieldInSQL(String tableName, String field, int[] values,
            String... columnNames) {
        return selectWhereFieldInSQL(tableName, field, PrimitiveIterators.of(values), columnNames);
    }

    public static StringBuilder selectWhereFieldInSQL(String tableName, String field, OfInt values,
            String... columnNames) {
        return selectWhereFieldInSQL(tableName, field,
                sb -> values.forEachRemaining((int n) -> sb.append(n).append(',')), columnNames);
    }

    public static StringBuilder selectWhereFieldEqSQL(String tableName, String field, Object value,
            String[] columnNames) {
        return whereSql(tableName, field, columnNames, "=").append(value);
    }

    public static StringBuilder updatePreparedSql(String tableName, String... columnNames) {
        if (Checker.isEmptyTrimmed(tableName))
            throw new IllegalArgumentException("tablename not specified");
        if (Checker.isEmpty(columnNames))
            throw new IllegalArgumentException("columnNames not specified");

        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(tableName).append(" SET ");
        for (String s : columnNames)
            sb.append(s).append("=?,");
        sb.setCharAt(sb.length() - 1, ' ');
        return sb;
    }

    public static StringBuilder selectWhereFieldInSQL(String tableName, String field, Consumer<StringBuilder> appender,
            String[] columnNames) {
        StringBuilder sb = whereSql(tableName, field, columnNames, "IN(");
        appender.accept(sb);
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.setCharAt(sb.length() - 1, ')');
        } else {
            sb.append(')');
        }
        return sb;
    }

    static StringBuilder whereSql(String tableName, String field, String[] columnNames, String operator) {
        if (Checker.isEmptyTrimmed(field))
            throw new IllegalArgumentException("invalid field: field cannnot be empty");

        return selectSQL(tableName, columnNames).append(" WHERE ").append(field).append(' ').append(operator);
    }
}
