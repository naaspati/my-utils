package sam.sql.querymaker;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

abstract class Appender extends Checker {
    abstract StringBuilder sb();

    protected StringBuilder append( int[] values) {
        checkArray(values);

        if(values.length == 1)
            return append(values[0]);

        for (int v : values)
            sb().append(v).append(',');

        removeLastChar();
        return sb();
    }
    protected StringBuilder append(char[] values, boolean quoted) {
        checkArray(values);

        if(values.length == 1)
            return append(values[0], quoted);

        for (char v : values)
            quote( v, quoted).append(',');

        removeLastChar();
        return sb();

    }
    protected StringBuilder append(double[] values) {
        checkArray(values);

        if(values.length == 1)
            return append(values[0]);

        for (double v : values)
            sb().append(v).append(',');

        removeLastChar();
        return sb();
    }
    protected StringBuilder append(long[] values) {
        checkArray(values);

        if(values.length == 1)
            return append(values[0]);

        for (long v : values)
            sb().append(v).append(',');

        removeLastChar();
        return sb();
    }
    protected StringBuilder append(float[] values) {
        checkArray(values);

        if(values.length == 1)
            return append(values[0]);

        for (float v : values)
            sb().append(v).append(',');

        removeLastChar();
        return sb();
    }
    protected <E> StringBuilder append(E[] values, boolean quoted) {
        checkArray(values);

        if(values.length == 1)
            return append(values[0], quoted);

        for (E v : values) 
            quote(v, quoted).append(',');

        removeLastChar();
        return sb();
    }
    protected <E> StringBuilder append(Iterable<E> values, boolean quoted) {
        return append(values.iterator(),  quoted);
    }
    protected <E, F> StringBuilder append(Iterable<E> values, Function<E, F> valueMapper,  boolean quoted) {
        return append(values.iterator(), valueMapper,  quoted); 
    }

    protected <E> StringBuilder append(Iterator<E> values, boolean quoted) {
        Objects.requireNonNull(values, "no values specified");
        
        if(!values.hasNext())
            throw new IllegalArgumentException("no values found in given iterator/iterable");

        while (values.hasNext()) {
            quote(values.next(), quoted);
            if(values.hasNext())
                sb().append(',');
        }
        return sb();
    }
    protected <E, F> StringBuilder append(Iterator<E> values, Function<E, F> valueMapper,  boolean quoted) {
        Objects.requireNonNull(values, "no values specified");
        
        if(!values.hasNext())
            throw new IllegalArgumentException("no values found in given iterator/iterable");

        while (values.hasNext()) {
            quote(valueMapper.apply(values.next()), quoted);
            if(values.hasNext())
                sb().append(',');
        }
        return sb();
    }

    protected StringBuilder append(int value) {
        return sb().append(value);
    }
    protected StringBuilder append(char value, boolean quoted) {
        return quote(value, quoted);
    }
    protected StringBuilder append(double value) {
        return sb().append(value);
    }
    protected StringBuilder append(long value) {
        return sb().append(value);
    }
    protected StringBuilder append(float value) {
        return sb().append(value);
    }
    protected <E> StringBuilder append(E value, boolean quoted) {
        return quote(value, quoted);
    }
    protected StringBuilder setLastCharSpace() {
        sb().setCharAt(sb().length() - 1, ' ');
        return sb();
    }

    protected StringBuilder removeLastChar() {
        sb().setLength(sb().length() - 1);
        return sb();
    }
    protected StringBuilder space() {
        sb().append(' ');
        return sb();
    }
    protected <E> StringBuilder quote( E value, boolean quoted) {
        if(quoted)
            sb().append('\'').append(value).append('\'');
        else
            sb().append(value);
        return sb();
    }
    protected StringBuilder quote( char value, boolean quoted) {
        if(quoted)
            sb().append('\'').append(value).append('\'');
        else
            sb().append(value);
        return sb();
    }
    protected StringBuilder appendAndSpace( String string) {
        return sb().append(string).append(' ');
    }
    protected StringBuilder startIn(String columnName) {
        sb().append(columnName).append(" IN(");
        return sb();
    }
    protected StringBuilder closeBracket() {
        sb().append(") ");
        return sb();
    }
    protected StringBuilder openBracket() {
        sb().append("(");
        return sb();
    }
}
