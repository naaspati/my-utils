package sam.nopkg;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import sam.io.IOConstants;
import sam.io.IOUtils;
import sam.logging.Logger;
import sam.reference.ReferenceUtils;
import sam.reference.WeakAndLazy;

public final class Resources implements AutoCloseable {
	private static final AtomicInteger count = new AtomicInteger(0);
	private static final AtomicReference<WeakReference<Resources>> reference = new AtomicReference<>();
	private static final Logger LOGGER = Logger.getLogger(Resources.class);
	private static final boolean DEBUG = LOGGER.isDebugEnabled();

	private Resources() { }

	public static Resources get() throws IOException {
		Resources resource = ReferenceUtils.get(reference.getAndSet(null));
		if(resource == null) {
			resource = new Resources();
			int n = count.incrementAndGet();
			if(DEBUG)
				LOGGER.debug(n+": Resource created for: "+Thread.currentThread().getStackTrace()[2]);
		}

		resource.open();
		if(DEBUG)
			LOGGER.debug("Resource opened at: "+Thread.currentThread().getStackTrace()[2]);
		return resource;
	}

	private byte[] bytes;
	private ByteBuffer buffer;
	private CharBuffer chars;
	private Charset charset;
	private CharsetDecoder decoder;
	private CharsetEncoder encoder;
	private final WeakAndLazy<StringBuilder> wsink = new WeakAndLazy<>(StringBuilder::new);

	private void open() throws IOException {
		IOUtils.ensureCleared(buffer);
		IOUtils.ensureCleared(chars);
		boolean[] b = {false};
		wsink.ifPresent(s -> b[0] = s.length() != 0);

		if(b[0])
			throw new IOException();
	}

	public CharBuffer chars() {
		if(chars == null)
			chars = CharBuffer.allocate(100);

		return chars;
	}

	/**
	 * returns ByteBuffer.wrap(bytes()) 
	 * @return
	 */
	public ByteBuffer buffer() {
		if(buffer == null)
			buffer = ByteBuffer.wrap(bytes());

		return buffer;
	}

	public Charset charset() {
		if(charset == null)
			charset = IOConstants.defaultCharset();
		return charset;
	}
	public byte[] bytes() {
		if(bytes == null)
			bytes = new byte[IOConstants.defaultBufferSize()];
		return bytes;
	}
	public CharsetEncoder encoder() {
		if (encoder == null)
			encoder = IOConstants.newEncoder(charset());
		return encoder;
	}
	public CharsetDecoder decoder() {
		if (decoder == null)
			decoder = IOConstants.newDecoder(charset());
		return decoder;
	}

	@Override
	public void close() throws IOException {
		clear(buffer);
		clear(chars);
		wsink.ifPresent(s -> s.setLength(0));
		
		if(charset != null) {
			if(decoder != null)
				decoder.reset();
			if(encoder != null)
				encoder.reset();	
		}
		reference.compareAndSet(null, new WeakReference<>(this));
	}
	
	private void clear(Buffer b) {
		if(b != null)
			b.clear();
	}
	public StringBuilder sb() {
		return wsink.get();
	}
}
