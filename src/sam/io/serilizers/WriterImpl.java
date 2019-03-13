package sam.io.serilizers;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.NoSuchElementException;

import sam.io.BufferConsumer;
import sam.io.IOUtils;
import sam.myutils.Checker;
import sam.myutils.ThrowException;

public class WriterImpl extends Writer {
	private final BufferConsumer consumer;
	private final ByteBuffer buffer;
	private final CharBuffer chars;
	private final boolean synced;
	private final Object lock;
	private final CharsetEncoder encoder;
	private volatile boolean flushed = true;

	public WriterImpl(WritableByteChannel target, ByteBuffer buffer, CharBuffer chars, boolean syncronized, CharsetEncoder encoder) throws IOException {
		this(b -> IOUtils.write(b, target, false), buffer, chars, syncronized, encoder);
	}
	public WriterImpl(BufferConsumer consumer, ByteBuffer buffer, CharBuffer chars, boolean syncronized, CharsetEncoder encoder) throws IOException {
		Checker.requireNonNull("consumer, buffer, chars, syncronized, encoder", consumer, buffer, chars, syncronized, encoder);

		this.consumer = consumer;
		this.encoder = encoder;
		this.buffer = buffer;
		this.chars = chars;
		this.synced = syncronized;
		this.lock = syncronized ? new Object() : null;

		encoder.reset();
		
		IOUtils.ensureCleared(buffer);
		IOUtils.ensureCleared(chars);
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
			CoderResult c = encoder.encode(chars, buffer, end);
			flushed = false;

			if(c.isUnderflow())
				break;
			else if(c.isOverflow()) 
				consume();
			else
				c.throwException();
		}
		chars.clear();
	}

	private void consume() throws IOException {
		buffer.flip();
		consumer.consume(buffer);
		if(!buffer.hasRemaining())
			throw new IOException("buffer not consumed");
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
			CoderResult c = encoder.flush(buffer);
			consume();

			if(c.isUnderflow()) 
				break;
		}
		encoder.reset();
	}
	@Override
	public void close() throws IOException {
		flush();
	}
};