package sam.sql.querymaker;

import java.util.Objects;
import java.util.function.Function;

interface Utils {
    static void checkArray(int[] values) {
        if(values == null || values.length == 0)
            throw new IllegalArgumentException("invalid data");
    }
    static void checkArray(double[] values) {
        if(values == null || values.length == 0)
            throw new IllegalArgumentException("invalid data");
    }
    static void checkArray(float[] values) {
        if(values == null || values.length == 0)
            throw new IllegalArgumentException("invalid data");
    }
    static void checkArray(char[] values) {
        if(values == null || values.length == 0)
            throw new IllegalArgumentException("invalid data");
    }
    static void checkArray(long[] values) {
        if(values == null || values.length == 0)
            throw new IllegalArgumentException("invalid data");
    }
    static <E> void checkArray(E[] values) {
        if(values == null || values.length == 0)
            throw new IllegalArgumentException("invalid data");
    }

    static StringBuilder append(StringBuilder sb, int[] values) {
        checkArray(values);

        if(values.length == 1)
            return append(sb,values[0]);

        for (int v : values)
            sb.append(v).append(',');

        removeLastCharSpace(sb);
        return sb;
    }
    static StringBuilder append(StringBuilder sb,char[] values, boolean quoted) {
        checkArray(values);

        if(values.length == 1)
            return append(sb,values[0], quoted);

        for (char v : values)
            quote(sb, v, quoted).append(',');

        removeLastCharSpace(sb);
        return sb;

    }
    static StringBuilder append(StringBuilder sb,double[] values) {
        checkArray(values);

        if(values.length == 1)
            return append(sb,values[0]);

        for (double v : values)
            sb.append(v).append(',');

        removeLastCharSpace(sb);
        return sb;
    }
    static StringBuilder append(StringBuilder sb,long[] values) {
        checkArray(values);

        if(values.length == 1)
            return append(sb,values[0]);

        for (long v : values)
            sb.append(v).append(',');

        removeLastCharSpace(sb);
        return sb;
    }
    static StringBuilder append(StringBuilder sb,float[] values) {
        checkArray(values);

        if(values.length == 1)
            return append(sb,values[0]);

        for (float v : values)
            sb.append(v).append(',');

        removeLastCharSpace(sb);
        return sb;
    }
    static <E> StringBuilder append(StringBuilder sb,E[] values, boolean quoted) {
        checkArray(values);

        if(values.length == 1)
            return append(sb,values[0], quoted);

        for (E v : values) 
            quote(sb,v, quoted).append(',');

        removeLastCharSpace(sb);
        return sb;
    }
    static <E> StringBuilder append(StringBuilder sb,Iterable<E> values, boolean quoted) {
        Objects.requireNonNull(values, "no values specified");

        int count = 0;
        for (E v : values) {
            quote(sb,v, quoted).append(',');
            count++;
        }

        if(count > 0)
            removeLastCharSpace(sb);
        else 
            throw new IllegalArgumentException("no values found in given iterable");
        return sb;
    }
    static <E, F> StringBuilder append(StringBuilder sb,Iterable<E> values, Function<E, F> valueMapper,  boolean quoted) {
        Objects.requireNonNull(values, "no values specified");

        int count = 0;
        for (E value : values) {
            quote(sb,valueMapper.apply(value), quoted).append(',');
            count++;
        }
        if(count > 0)
            removeLastCharSpace(sb);
        else 
            throw new IllegalArgumentException("no values found in given iterable");
        return sb;
    }

    static StringBuilder append(StringBuilder sb,int value) {
        return sb.append(value);
    }
    static StringBuilder append(StringBuilder sb,char value, boolean quoted) {
        return quote(sb,value, quoted);
    }
    static StringBuilder append(StringBuilder sb,double value) {
        return sb.append(value);
    }
    static StringBuilder append(StringBuilder sb,long value) {
        return sb.append(value);
    }
    static StringBuilder append(StringBuilder sb,float value) {
        return sb.append(value);
    }
    static <E> StringBuilder append(StringBuilder sb,E value, boolean quoted) {
        return quote(sb,value, quoted);
    }
    static StringBuilder setLastCharSpace(StringBuilder sb) {
        sb.setCharAt(sb.length() - 1, ' ');
        return sb;
    }

    static StringBuilder removeLastCharSpace(StringBuilder sb) {
        sb.setLength(sb.length() - 1);
        return sb;
    }
    static StringBuilder space(StringBuilder sb) {
        sb.append(' ');
        return sb;
    }
    static <E> StringBuilder quote(StringBuilder sb, E value, boolean quoted) {
        if(quoted)
            sb.append('\'').append(value).append('\'');
        else
            sb.append(value);
        return sb;
    }
    static StringBuilder quote(StringBuilder sb, char value, boolean quoted) {
        if(quoted)
            sb.append('\'').append(value).append('\'');
        else
            sb.append(value);
        return sb;
    }
    static StringBuilder appendAndSpace(StringBuilder sb, String string) {
        return sb.append(string).append(' ');
    }
    static StringBuilder startIn(StringBuilder sb, String columnName) {
        sb.append(columnName).append(" IN(");
        return sb;
    }
    static StringBuilder closeBracket(StringBuilder sb) {
        sb.append(") ");
        return sb;
    }
    static StringBuilder openBracket(StringBuilder sb) {
        sb.append("(");
        return sb;
    }

}
