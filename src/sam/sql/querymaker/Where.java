package sam.sql.querymaker;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Where extends Appender {
    private final StringBuilder sb;
	private final QueryMakerBase caller;

    @Override
    public StringBuilder sb() {
        return sb;
    }
    
    Where(QueryMakerBase caller) {
    	this.caller = caller ; 
        this.sb = caller.sb;
        appendAndSpace("WHERE");
    }
    Where(StringBuilder sb) {
    	this.caller = null ; 
        this.sb = sb;
        appendAndSpace("WHERE");
    }
    private StringBuilder _appendEquals(String columnName) {
        sb.append(columnName).append('=');
        return sb;
    }
    public Where eqPlaceholder(String columnName) {
        _appendEquals(columnName).append('?');
        space();
        return this;
    }
    public Where eq(String columnName, int value) {
        _appendEquals(columnName).append(value);
        space();
        return this;
    }
    public Where eq(String columnName, char value, boolean quoted) {
        _appendEquals(columnName);
        quote(value, quoted);
        space();
        return this;
    }
    public Where eq(String columnName, double value) {
        _appendEquals(columnName).append(value);
        space();
        return this;
    }
    public <E> Where eq(String columnName, E value, boolean quoted) {
        _appendEquals(columnName);
        quote(value, quoted);
        space();
        return this;
    }
    public Where in(String columnName, int...values) {
        startIn(columnName);
        append(values);
        closeBracket(); 
        return this;
    }
    public Where inPlaceholder(String columnName, int count) {
        startIn(columnName);
        for (int i = 0; i < count; i++) 
            sb.append('?').append(i == count - 1 ? ' ':',');
        closeBracket(); 
        return this;
    }
    public Where in(String columnName, boolean quoted, char...values) {
        startIn(columnName);
        append(values,quoted);
        closeBracket();
        return this;
    }
    public Where in(String columnName, double...values) {
        startIn(columnName);
        append(values);
        closeBracket();
        return this;
    }
    public Where inSubSelect(String columnName, UnaryOperator<Select> subselect) {
    	startIn(columnName);
    	Select select = new Select(caller);
    	subselect.apply(select);
    	closeBracket();
        return this;
    }
    @SafeVarargs
    public final <E> Where in(String columnName, boolean quoted, E...values) {
        startIn(columnName);
        append(values, quoted);
        closeBracket();
        return this;
    }
    public <E> Where in(String columnName, Iterable<E> values) {
        return in(columnName, values, false);
    }
    public <E> Where in(String columnName, Iterable<E> values, boolean quoted) {
        startIn(columnName);
        append(values, quoted);
        closeBracket();
        return this;
    }
    public <E, F> Where in(String columnName, Iterable<E> values, Function<E, F> valueMapper, boolean quoted) {
        startIn(columnName);
        append(values, valueMapper, quoted);
        closeBracket();
        return this;
    }
    public <E> Where in(String columnName, Iterator<E> values) {
        return in(columnName, values, false);
    }
    public <E> Where in(String columnName, Iterator<E> values, boolean quoted) {
        startIn(columnName);
        append(values, quoted);
        closeBracket();
        return this;
    }
    public <E, F> Where in(String columnName, Iterator<E> values, Function<E, F> valueMapper, boolean quoted) {
        startIn(columnName);
        append(values, valueMapper, quoted);
        closeBracket();
        return this;
    }
    public Where condition(String condition) {
        appendAndSpace( condition);
        return this;
    }
    public Where or() {
        appendAndSpace("OR");
        return this;
    }
    public Where and() {
        appendAndSpace("AND");
        return this;
    }
    public Where not() {
        appendAndSpace("NOT");
        return this;
    }
}
