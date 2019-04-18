package sam.nopkg;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.*;
import java.util.Objects;

import static sam.io.IOConstants.*;
import sam.io.serilizers.DataReader;
import sam.io.serilizers.DataWriter;

public abstract class SerializeHelper<E> {
	protected final Path p;

	public SerializeHelper(Path p) {
		this.p = p;
	}

	public E read() throws IOException {
		if(Files.notExists(p))
			return readValueIfFileNotExist();

		try(FileChannel fc = FileChannel.open(p, READ);
				DataReader reader = new DataReader(fc, _buffer(fc.size()))) {
			return read(fc, reader);
		}
	}

	private ByteBuffer _buffer(long size) {
		ByteBuffer buf = buffer((int)size);
		Objects.requireNonNull(buf);
		
		if(size < 0)
			size = Integer.MAX_VALUE;

		if(buf == EMPTY_BUFFER)
			return ByteBuffer.allocate((int)Math.min(size, defaultBufferSize()));
		else {
			buf.clear();
			return buf;
		}
			
	}

	protected E read(FileChannel fc, DataReader reader) throws IOException {
		return read(reader);
	}

	public void write(E e) throws IOException {
		ByteBuffer buffer = buffer(-1);
		Objects.requireNonNull(buffer);

		if(e == null) {
			Files.deleteIfExists(p);
			return ;
		}

		buffer.clear();

		try(FileChannel fc = FileChannel.open(p, WRITE, CREATE, TRUNCATE_EXISTING);
				DataWriter writeer = new DataWriter(fc, _buffer(-1))) {
			write(fc, writeer, e);
		}
	}

	protected ByteBuffer buffer(int size) {
		return EMPTY_BUFFER;
	}

	protected void write(FileChannel fc, DataWriter writeer, E e) throws IOException {
		write(writeer, e);
	}

	protected abstract void write(DataWriter writer, E e) throws IOException ;
	protected abstract E read(DataReader reader) throws IOException ;
	protected abstract E readValueIfFileNotExist() throws IOException ;
}
