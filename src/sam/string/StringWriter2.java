package sam.string;

import java.io.IOException;
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
	public void write(int c) throws IOException {
		sb.append((char)c);
	}

	@Override
	public void write(char[] cbuf) throws IOException {
		sb.append(cbuf);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		sb.append(cbuf, off, len);
	}

	@Override
	public void write(String str) throws IOException {
		sb.append(str);
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		sb.append(str, off, off + len);
	}

	@Override
	public Writer append(CharSequence csq) throws IOException {
		sb.append(csq);
		return this;
	}

	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException {
		sb.append(csq, start, end);
		return this;
	}

	@Override
	public Writer append(char c) throws IOException {
		sb.append(c);
		return this;
	}

	@Override public void flush() throws IOException { }
	@Override public void close() throws IOException { }
	
	public StringBuilder getBuilder() {
		return sb;
	}
}
