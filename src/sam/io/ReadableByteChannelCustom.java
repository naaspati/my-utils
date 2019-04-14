package sam.io;

import static sam.io.IOConstants.defaultBufferSize;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;

import sam.myutils.Checker;

public interface ReadableByteChannelCustom extends ReadableByteChannel, HasBuffer, HasSize {

	public static final int DEFAULT_BUFFER_SIZE = defaultBufferSize();

	public static final ReadableByteChannelCustom EMPTY = new ReadableByteChannelCustom() {
		@Override 
		public long size() throws IOException { 
			return 0; 
		}
		@Override
		public int read(ByteBuffer dst) throws IOException {
			return -1;
		}
		@Override
		public boolean isOpen() {
			return true;
		}
		@Override
		public void close() throws IOException { }
		
		@Override
		public ByteBuffer buffer() {
			return IOConstants.EMPTY_BUFFER;
		}
	};
	
	public static ReadableByteChannelCustom of(ByteBuffer buf) {
		if(!buf.hasRemaining())
			return EMPTY;
		
		ByteBuffer orig = buf;
		
		if(buf.position() != 0)
			buf = ByteBuffer.wrap(buf.array(), buf.position(), buf.remaining());
		
		ByteBuffer buf2 = buf;
		
		int size = buf.remaining();
		return new ReadableByteChannelCustom() {
			boolean first = true;
			ByteBuffer copy = buf2.duplicate();
			
			@Override
			public long size() throws IOException {
				return size;
			}
			
			@Override
			public ByteBuffer buffer() {
				return buf2;
			}
			
			@Override
			public boolean isOpen() {
				return true;
			}
			
			@Override
			public void close() throws IOException {
			}
			
			@Override
			public int read(ByteBuffer dst) throws IOException {
				if(!first)
					return -1;
				
				if(dst != buf2)
					throw new IOException();
				if(!dst.equals(copy))
					throw new IOException();

				first = false;
				int n = dst.remaining();
				dst.position(dst.limit());
				
				if(orig != dst)
					orig.position(orig.limit());
				
				return n;
			}
		};
	}

	public static ReadableByteChannelCustom of(FileChannel fc, ByteBuffer buffer, long position, int size) throws IOException {
		Objects.requireNonNull(fc);
		Checker.assertTrue(position >= 0, () -> new IllegalArgumentException("negative position: "+position));
		Checker.assertTrue(size >= 0, () -> new IllegalArgumentException("negative size: "+size));

		if(position + size > fc.size())
			throw new IOException(String.format("position(%s) + size(%s) = (%s) > file.size(%s)", position, size, position + size, fc.size()));
		
		return new ReadableByteChannelCustom2(fc, size, buffer) {
			long pos = position;
			int remaining = size;
			
			@Override
			public int read(ByteBuffer dst) throws IOException {
				int n = IOUtils.read(dst, pos, remaining, fc, false);

				if(n != -1) {
					pos        += n;
					remaining  -= n;	
				}

				return remaining == 0 ? -1 : n;
			}
		};
	}

	public static ReadableByteChannel of(ReadableByteChannel channel, ByteBuffer buffer) throws IOException {
		Objects.requireNonNull(channel);
		
		if(channel instanceof ReadableByteChannelCustom)
			return channel; 

		if(channel instanceof FileChannel) {
			FileChannel f = (FileChannel) channel;
			return of(f, buffer, f.position(), (int) (f.size() - f.position()));
		} else
			return new ReadableByteChannelCustom2(channel, -1, buffer);
	}
	
	public static class ReadableByteChannelCustom2 implements ReadableByteChannelCustom {
		private final ReadableByteChannel c;
		private final long size;
		private final ByteBuffer buffer;

		public ReadableByteChannelCustom2(ReadableByteChannel c, long size, ByteBuffer buffer) {
			this.c = c;
			this.size = size;
			this.buffer = buffer;
		}

		@Override
		public int read(ByteBuffer dst) throws IOException {
			return c.read(dst);
		}

		@Override
		public boolean isOpen() {
			return c.isOpen();
		}

		@Override
		public void close() throws IOException {
			c.close();
		}
		@Override
		public long size() throws IOException {
			return size;
		}
		@Override
		public ByteBuffer buffer() {
			return buffer;
		}
		
	}

	public static ReadableByteChannel of(InputStream is, ByteBuffer buffer) throws IOException {
		
		return new ReadableByteChannelCustom() {
			@Override
			public long size() throws IOException {
				return is.available();
			}
			@Override
			public int read(ByteBuffer dst) throws IOException {
				int n = is.read(dst.array(), dst.position(), dst.remaining());
				
				if(n != -1)
					dst.position(dst.position() + n);
				return n;
			}
			@Override
			public boolean isOpen() {
				return true;
			}
			@Override
			public void close() throws IOException {
				is.close();
			}
			@Override
			public ByteBuffer buffer() {
				return buffer;
			}
		};
	}
	
}
