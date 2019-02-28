package sam.io;

import static sam.io.IOConstants.defaultBufferSize;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;

import sam.logging.Logger;

public abstract class BufferSupplier {
	private static final Logger LOGGER = Logger.getLogger(BufferSupplier.class);
	
	public abstract ByteBuffer next() throws IOException;
	public long size() throws IOException {
		return -1;
	}
	public abstract boolean isEndOfInput() throws IOException;
	public boolean isEmpty() {
		return false;
	}
	
	public static final int DEFAULT_BUFFER_SIZE = defaultBufferSize();
	
	public static final BufferSupplier EMPTY = new BufferSupplier() {
		@Override public long size() throws IOException { return 0; }
		@Override public ByteBuffer next() throws IOException { return null; }
		@Override public boolean isEndOfInput() throws IOException { return true; }
		@Override public boolean isEmpty() { return true; }
	};
	
	public static BufferSupplier of(ReadableByteChannel channel, ByteBuffer buffer) throws IOException {
		Objects.requireNonNull(channel);

		final long size;
		
		if(channel instanceof FileChannel) {
			FileChannel f = (FileChannel) channel;
			size = f.size() - f.position();
			
			if(size == 0)
				return EMPTY;
		} else {
			size = -1;
		}
		
		if(buffer == null) {
			int n = size < 0 ? DEFAULT_BUFFER_SIZE : (int)size;
			buffer = ByteBuffer.allocate(Math.min(DEFAULT_BUFFER_SIZE, n));
			LOGGER.debug("ByteBuffer({}) created", buffer.capacity());
		}
		
		ByteBuffer bf = buffer;
		IOUtils.ensureCleared(bf);
		
		return new BufferSupplier() {
			int n = 0;
			boolean first = true;
			
			@Override public long size() throws IOException { return size; }
			
			@Override
			public ByteBuffer next() throws IOException {
				if(!first) {
					if(bf.hasRemaining()) {
						bf.compact();
						LOGGER.debug("bf.compact()");
					} else
						bf.clear();	
				}
				
				first = false;
				
				n = channel.read(bf);
				bf.flip();
				
				return bf;
			}
			
			@Override
			public boolean isEndOfInput() throws IOException {
				return n == -1;
			}
			@Override
			public boolean isEmpty() {
				return size == 0;
			}
		};
	}
	public static BufferSupplier of(ByteBuffer buf) {
		Objects.requireNonNull(buf);
		
		int size = buf.remaining();
		
		if(size == 0)
			return EMPTY;
		
		return new BufferSupplier() {
			@Override public long size() throws IOException { return size; }
			@Override public ByteBuffer next() throws IOException { return buf; }
			@Override public boolean isEndOfInput() throws IOException { return true; }
			@Override public boolean isEmpty() { return size == 0; }
		};
	}
}