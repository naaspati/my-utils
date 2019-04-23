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
}
