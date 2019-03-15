package sam.io;

import static sam.io.IOConstants.defaultBufferSize;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;

import sam.logging.Logger;
import sam.myutils.Checker;

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
	public void onComplete() {
	}

	public static final int DEFAULT_BUFFER_SIZE = defaultBufferSize();

	public static final BufferSupplier EMPTY = new BufferSupplier() {
		@Override public long size() throws IOException { return 0; }
		@Override public ByteBuffer next() throws IOException { return null; }
		@Override public boolean isEndOfInput() throws IOException { return true; }
		@Override public boolean isEmpty() { return true; }
	};

	public static BufferSupplier of(FileChannel fc, ByteBuffer buffer, long position, int size) throws IOException {
		Objects.requireNonNull(fc);
		Checker.assertTrue(position >= 0, () -> new IllegalArgumentException("negative position: "+position));
		Checker.assertTrue(size >= 0, () -> new IllegalArgumentException("negative size: "+size));

		if(position + size > fc.size())
			throw new IOException(String.format("position(%s) + size(%s) = (%s) > file.size(%s)", position, size, position + size, fc.size()));

		ByteBuffer buf = buffer(buffer, size);
		IOUtils.ensureCleared(buf);
		IOUtils.setFilled(buf);
		
		if(size == 0)
			return EMPTY;

		return new BufferSupplier() {
			long pos = position;
			int remaining = size;

			@Override
			public ByteBuffer next() throws IOException {
				IOUtils.compactOrClear(buf);

				int n = IOUtils.read(buf, pos, remaining, fc);
				pos        += n;
				remaining  -= n;

				return buf;
			}
			@Override
			public long size() throws IOException {
				return size;
			}
			
			@Override
			public boolean isEndOfInput() throws IOException {
				return remaining <= 0;
			}
		};
	}

	private static ByteBuffer buffer(ByteBuffer buffer, int size) {
		if(buffer == null) {
			int n = size < 0 ? DEFAULT_BUFFER_SIZE : size;
			buffer = ByteBuffer.allocate(Math.min(DEFAULT_BUFFER_SIZE, n));
			LOGGER.debug("ByteBuffer({}) created", buffer.capacity());
		}
		return buffer;
	}
	public static BufferSupplier of(ReadableByteChannel channel, ByteBuffer buffer) throws IOException {
		Objects.requireNonNull(channel);
		
		if(channel instanceof FileChannel) {
			FileChannel f = (FileChannel) channel;
			return of(f, buffer, f.position(), (int) (f.size() - f.position()));
		}

		ByteBuffer buf = buffer(buffer, -1);
		IOUtils.ensureCleared(buf);
		IOUtils.setFilled(buf);

		return new BufferSupplier() {
			int n = 0;

			@Override public long size() throws IOException { return -1; }

			@Override
			public ByteBuffer next() throws IOException {
				IOUtils.compactOrClear(buf);
				n = channel.read(buf);
				buf.flip();

				return buf;
			}

			@Override
			public boolean isEndOfInput() throws IOException {
				return n == -1;
			}
		};
	}
	
	public static BufferSupplier of(InputStream gis, ByteBuffer buffer) throws IOException {
		IOUtils.ensureCleared(buffer);
		buffer = buffer(buffer, DEFAULT_BUFFER_SIZE);
		
		ByteBuffer buf = buffer;
		
		return new BufferSupplier() {
			int n = 0;
			boolean first = true;
			
			@Override
			public ByteBuffer next() throws IOException {
				if(!first)
					IOUtils.compactOrClear(buf);

				first = false;
				
				n = gis.read(buf.array(), buf.position(), buf.remaining());
				if(n != -1) {
					buf.limit(buf.position() + n);
					buf.position(0);
				} else {
					buf.flip();
				} 
				return buf;
			}
			
			@Override
			public boolean isEndOfInput() throws IOException {
				return n == -1;
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