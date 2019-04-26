package sam.io.serilizers;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.NoSuchElementException;
import java.util.Objects;

import sam.io.HasBuffer;
import sam.io.IOUtils;
import sam.myutils.Checker;
import sam.myutils.ThrowException;

public class WriterImpl extends Writer {
	private final WritableByteChannel target;
	private final ByteBuffer buf;
	private final CharBuffer chars;
	private final boolean synced;
	private final Object lock;
	private final CharsetEncoder encoder;
	private volatile boolean flushed = true;
	
	public WriterImpl(WritableByteChannel target, CharBuffer chars, boolean syncronized, CharsetEncoder encoder) throws IOException {
	    this(target, HasBuffer.buffer(target), chars, syncronized, encoder);
	}
	public WriterImpl(WritableByteChannel target, ByteBuffer buffer, CharBuffer chars, boolean syncronized, CharsetEncoder encoder) throws IOException {
		Checker.requireNonNull("target, buffer, chars, syncronized, encoder", target, chars, syncronized, encoder);

		this.target = target;
		this.encoder = encoder;
		this.buf = Objects.requireNonNull(buffer);
		this.chars = chars;
		this.synced = syncronized;
		this.lock = syncronized ? new Object() : null;

		encoder.reset();
	}

	@Override
	public void write(int c) throws IOException {
		append((char)c);
	}
	@Override
	public void write(String str, int off, int len) throws IOException {
		append(str, off, off + len);
	}
	@Override
	public Writer append(CharSequence csq) throws IOException {
		append(csq, 0, csq.length());
		return this;
	}
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		append(new CharSequence() {
			@Override public CharSequence subSequence(int start, int end) { return ThrowException.illegalAccessError(); }
			@Override public int length() { return len; }

			@Override
			public char charAt(int index) {
				if(index >= len)
					throw new NoSuchElementException();

				return cbuf[off+index];
			}
		});
	}

	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException {
		if(synced) {
			synchronized (lock) { _append(csq, start, end); }
		} else {
			_append(csq, start, end);
		}
		return this;
	}
	@Override
	public Writer append(char c) throws IOException {
		if(synced) {
			synchronized (lock) { _append(c); }
		} else {
			_append(c);
		}
		return this;
	}

	private void _append(int c) throws IOException {
		if(!chars.hasRemaining())
			_write();

		chars.put((char) c);
	}
	private Writer _append(CharSequence csq, int start, int end) throws IOException {
		if(start > end)
			ThrowException.illegalArgumentException("start("+start+") > end("+end+")");

		while(start < end) {
			if(!chars.hasRemaining())
				_write();

			chars.put(csq.charAt(start++));
		}
		return this;
	}
	private void _write() throws IOException {
		_write(false);
	}
	private void _write(boolean end) throws IOException {
		chars.flip();

		while(chars.hasRemaining()) {
			CoderResult c = encoder.encode(chars, buf, end);
			flushed = false;

			if(c.isUnderflow())
				break;
			else if(c.isOverflow()) 
				writeTarget();
			else
				c.throwException();
		}
		chars.clear();
	}

	private void writeTarget() throws IOException {
		IOUtils.write(buf, target, true);
	}
	@Override
	public void flush() throws IOException {
		if(synced) {
			synchronized (lock) { _flush(); }
		} else {
			_flush();
		}
	}
	private void _flush() throws IOException {
		_write(true);
		
		if(flushed)
			return;
		
		flushed = true;
		
		while(true) {
			CoderResult c = encoder.flush(buf);
			writeTarget();

			if(c.isUnderflow()) 
				break;
			else if(!c.isOverflow())
				c.throwException();
		}
		encoder.reset();
	}
	@Override
	public void close() throws IOException {
		flush();
		target.close();
	}
};