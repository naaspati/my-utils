package sam.io.infile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.ConcurrentModificationException;
import java.util.concurrent.atomic.AtomicBoolean;

import sam.functions.IOExceptionConsumer;
import sam.functions.IOExceptionSupplier;

public class InFile implements AutoCloseable {
	private final AtomicBoolean inUse = new AtomicBoolean();
	private final AtomicBoolean closed = new AtomicBoolean();
	private final InFileImpl file;
	
	public InFile(Path path, boolean createIfNotExits) throws IOException {
		this.file = new InFileImpl(path, createIfNotExits);
	}

	private void checkClosed() throws ClosedChannelException {
		if(closed.get())
			throw new ClosedChannelException();
	}

	public Path getPath() {
		return file.getPath();
	}
	public long size() throws IOException {
		checkClosed();
		return file.size();
	}
	public long acutualSize() throws IOException {
		checkClosed();
		return file.acutualSize();
	}
	public DataMeta write(ByteBuffer buffer) throws IOException {
		checkClosed();
		lock();

		try {
			return file.write(buffer);	
		} finally {
			unlock();
		}
	}
	public DataMeta write(IOExceptionSupplier<ByteBuffer> buffers) throws IOException {
		checkClosed();
		lock();

		try {
			return file.write(buffers);	
		} finally {
			unlock();
		}
		
	}
	public int write2(IOExceptionSupplier<ByteBuffer> buffers) throws IOException {
		checkClosed();
		lock();

		try {
			return file.write2(buffers);	
		} finally {
			unlock();
		}
		
	}
	public ByteBuffer read(DataMeta meta, ByteBuffer buffer) throws IOException {
		checkClosed();
		lock();

		try {
			return file.read(meta, buffer);	
		} finally {
			unlock();
		}
		
	}
	public void read(DataMeta meta, ByteBuffer buffer, IOExceptionConsumer<ByteBuffer> bufferConsumer) throws IOException {
		checkClosed();
		lock();

		try {
			file.read(meta, buffer, bufferConsumer);	
		} finally {
			unlock();
		}
		
	}
	public void transfer(Iterable<DataMeta> metas, WritableByteChannel target, ByteBuffer buffer) throws IOException {
		checkClosed();
		lock();

		try {
			file.transfer(metas, target, buffer);
		} finally {
			unlock();
		}

	}

	/**
	 * never use inside try block, 
	 * if used finally block will unlock(), regardless lock was obtained
	 */
	private void lock() {
		if(!this.inUse.compareAndSet(false, true))
			throw new ConcurrentModificationException("already in use");
	}
	private void unlock() {
		this.inUse.set(false);
	}

	public void close() throws IOException {
		if(closed.get())
			return;

		lock();

		try {
			if(closed.get())
				return;

			file.close();
			closed.set(true);
		} finally {
			unlock();
		}
	}
}
