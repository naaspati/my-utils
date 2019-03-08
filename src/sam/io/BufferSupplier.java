package sam.io;

import static sam.io.IOConstants.defaultBufferSize;

import java.io.IOException;
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

		IOUtils.ensureCleared(buffer);
		IOUtils.setFilled(buffer);
		ByteBuffer buf = buffer(buffer, size);

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

		final long size;

		if(channel instanceof FileChannel) {
			FileChannel f = (FileChannel) channel;
			size = f.size() - f.position();

			if(size == 0)
				return EMPTY;
		} else {
			size = -1;
		}

		ByteBuffer bf = buffer(buffer, (int)size);
		IOUtils.ensureCleared(bf);
		IOUtils.setFilled(bf);

		return new BufferSupplier() {
			int n = 0;

			@Override public long size() throws IOException { return size; }

			@Override
			public ByteBuffer next() throws IOException {
				IOUtils.compactOrClear(buffer);
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