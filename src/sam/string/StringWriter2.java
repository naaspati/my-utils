package sam.string;

import java.io.IOException;
import java.io.StringWriter;

/**
 * copy of StringWriter
 * @author Sameer
 *
 */
public class StringWriter2 extends StringWriter {
	private StringBuilder builder;

	public StringWriter2(StringBuilder builder) {
    	this.builder = builder;
    }
    public StringWriter2() {
    	builder = new StringBuilder();
    }
    public StringWriter2(int initialSize) {
    	builder = new StringBuilder(initialSize);
    }
    @Override
	public void write(char[] cbuf) throws IOException {
    	builder.append(cbuf);
	}
    public void write(int c) {
        builder.append((char) c);
    }
    public void write(char cbuf[], int off, int len) {
        builder.append(cbuf, off, len);
    }
    public void write(String str) {
        builder.append(str);
    }
    public void write(String str, int off, int len)  {
        builder.append(str.substring(off, off + len));
    }
    public StringWriter2 append(CharSequence csq) {
        if (csq == null)
            write("null");
        else
            write(csq.toString());
        return this;
    }
    public StringWriter2 append(CharSequence csq, int start, int end) {
        CharSequence cs = (csq == null ? "null" : csq);
        write(cs.subSequence(start, end).toString());
        return this;
    }
    public StringWriter2 append(char c) {
        write(c);
        return this;
    }

    public String toString() {
        return builder.toString();
    }

    public StringBuilder getBuilder() {
    	return builder;
    }
    @Deprecated
    public StringBuffer getBuffer() {
    	throw new IllegalAccessError("use getBuilder()");
    }
    public void flush() { }
    public void close() { }
    
    public void clear() {
    	builder.setLength(0);
    }

}
