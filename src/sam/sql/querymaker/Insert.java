package sam.sql.querymaker;

import java.util.function.Function;

public class Insert extends QueryMakerBase {
    protected Insert(StringBuilder sb, QueryMaker maker, String tableName) {
        super(sb, maker, "INSERT INTO");
        appendAndSpace(tableName);
    }

    public String placeholders(String...columnNames) {
        columns(columnNames);
        return placeholders(columnNames.length);
    }
    public String placeholders(int count) {
        _append();
        while(count-- > 0) sb.append('?').append(',');
        removeLastChar();
        return closeBuild();
    }
    public Insert columns(String...columnNames) {
        checkArray(columnNames);

        openBracket(); 
        append(columnNames, false);
        closeBracket();
        space();

        return this;
    }
    private void _append() {
        sb.append("VALUES(");
    }
    private String closeBuild() {
        closeBracket();
        return build();
    } 
    public String values(int...values) {
        _append();
        super.append(values);
        return closeBuild();
    }
    public String values(boolean quoted, char...values) {
        _append();
        super.append(values, quoted);
        return closeBuild();
    }
    public String values(double...values) {
        _append();
        super.append(values);
        return closeBuild();
    }
    public String values(long...values) {
        _append();
        super.append(values);
        return closeBuild();
    }
    public String values(float...values) {
        _append();
        super.append(values);
        return closeBuild();
    }
    
    @SafeVarargs
    public final <E> String values(boolean quoted, E...values) {
        _append();
        super.append(values,quoted);
        return closeBuild();
    }
    public <E> String values(Iterable<E> values, boolean quoted) {
        _append();
        super.append(values, quoted);
        return closeBuild();
    }
    public <E, F> String values(Iterable<E> values, Function<E, F> valueMapper, boolean quoted) {
        _append();
        super.append(values, valueMapper, quoted);
        return closeBuild();
    }

}
