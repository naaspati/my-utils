package sam.string;

import java.io.Writer;
import java.util.Objects;

/**
 * copy of StringWriter
 * @author Sameer
 *
 */
public class StringWriter2 extends Writer {
    private final StringBuilder sb;

    public StringWriter2(StringBuilder sb) {
        this.sb = Objects.requireNonNull(sb);
    }
    public StringWriter2() {
        this(new StringBuilder());
    }
    @Override
    public void write(int c) {
        sb.append((char)c);
    }

    @Override
    public void write(char[] cbuf) {
        sb.append(cbuf);
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        sb.append(cbuf, off, len);
    }

    @Override
    public void write(String str) {
        sb.append(str);
    }

    @Override
    public void write(String str, int off, int len) {
        sb.append(str, off, off + len);
    }

    @Override
    public StringWriter2 append(CharSequence csq) {
        sb.append(csq);
        return this;
    }

    @Override
    public StringWriter2 append(CharSequence csq, int start, int end) {
        sb.append(csq, start, end);
        return this;
    }

    @Override
    public StringWriter2 append(char c) {
        sb.append(c);
        return this;
    }
    @Override
    public String toString() {
        return sb.toString();
    }

    public void clear() {
        sb.setLength(0);
    }

    @Override public void flush() { }
    @Override public void close() { }

    public StringBuilder getBuilder() {
        return sb;
    }
}
