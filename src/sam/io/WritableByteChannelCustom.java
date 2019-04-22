package sam.io;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.*;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

public interface WritableByteChannelCustom extends WritableByteChannel, HasBuffer {

	public static WritableByteChannel of(ByteBuffer target, ByteBuffer buffer) {
		return new WritableByteChannelCustom() {
			@Override
			public ByteBuffer buffer() {
				return buffer;
			}
			@Override
			public boolean isOpen() {
				return false;
			}
			@Override
			public void close() throws IOException {
			}

			@Override
			public int write(ByteBuffer src) throws IOException {
				int n = src.remaining();
				target.put(src);
				return n;
			}
		};
	}

	public static WritableByteChannel of(WritableByteChannel target, ByteBuffer buffer) {
		if(target instanceof WritableByteChannelCustom)
			return target;

		if(buffer == null)
			return target;
		else {
			return new WritableByteChannelCustom() {

				@Override
				public ByteBuffer buffer() {
					return buffer;
				}

				@Override
				public boolean isOpen() {
					return target.isOpen();
				}

				@Override
				public void close() throws IOException {
					target.close();
				}

				@Override
				public int write(ByteBuffer src) throws IOException {
					return target.write(src);
				}
			};
		}
	}
	public static WritableByteChannel of(OutputStream target, ByteBuffer buffer) {
		return new WritableByteChannelCustom() {
			@Override
			public ByteBuffer buffer() {
				return buffer;
			}
			@Override
			public boolean isOpen() {
				return true;
			}

			@Override
			public void close() throws IOException {
				target.close();
			}

			@Override
			public int write(ByteBuffer src) throws IOException {
				return IOUtils.write(src, target, false, false);
			}
		};
	}
	
	public static OutputStream newOutputStream(Path p, ByteBuffer buf, boolean append) throws IOException {
        FileChannel fc = FileChannel.open(p, WRITE, CREATE, append ? APPEND : TRUNCATE_EXISTING);
        
        return new OutputStream() {
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                if(len <= 0)
                    return;
                
                if(len > buf.remaining())
                    flush();
                
                if(len > buf.capacity())
                    IOUtils.write(ByteBuffer.wrap(b, off, len), fc, false);
                else 
                    buf.put(b, off, len);
            }

            @Override
            public void flush() throws IOException {
                IOUtils.write(buf, fc, true);
            }

            @Override
            public void close() throws IOException {
                flush();
                fc.close();
            }

            @Override
            public void write(int b) throws IOException {
                if(buf.remaining() < 1)
                    flush();
                
                buf.put((byte)b);
            }
        };
    }
}
