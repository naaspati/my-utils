package sam.io.infile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.ConcurrentModificationException;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import sam.functions.IOExceptionSupplier;
import sam.io.BufferConsumer;
import sam.io.BufferSupplier;

/*
 * using delegate, rather than subclass, so that lock does not effect InFileImpl's internal access of methods
 */ 
public class InFile implements AutoCloseable {
	private final AtomicBoolean lock = new AtomicBoolean(false);
	private final AtomicBoolean closed = new AtomicBoolean();
	private final InFileImpl file;

	public InFile(Path path, boolean createIfNotExits) throws IOException {
		this.file = new InFileImpl(path, createIfNotExits);
	}

	private void checkClosed() throws ClosedChannelException {
		if(closed.get())
			throw new ClosedChannelException();
	}

	
	public DataMeta replace(DataMeta d, BufferSupplier buffers) throws IOException {
		return wrap(() -> file.replace(d, buffers));
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

	public IdentityHashMap<DataMeta, DataMeta> transferTo(Iterable<DataMeta> metas, InFile target) throws IOException {
		Objects.requireNonNull(target);
		Objects.requireNonNull(metas);
		
		if(file.equals(target.file))
			throw new IOException("source cannot be the target");
		
		return wrap(() -> target.wrap(() -> file.transferTo(metas, target.file)));
	}

	int write0(ByteBuffer buffer) throws IOException {
		return wrap(() -> file.write(buffer));
	}
	public DataMeta write(ByteBuffer buffer) throws IOException {
		return wrap(() -> file.write2(buffer));
	}
	public DataMeta write(BufferSupplier buffers) throws IOException {
		return wrap(() -> file.write(buffers));
	}
	public ByteBuffer read(DataMeta meta, ByteBuffer buffer) throws IOException {
		return wrap(() -> file.read(meta, buffer));
	}
	public void read(DataMeta meta, ByteBuffer buffer, BufferConsumer bufferConsumer) throws IOException {
		wrap(() -> {
			file.read(meta, buffer, bufferConsumer);
			return null;
		});
	}
	public long transferTo(Iterable<DataMeta> metas, WritableByteChannel target) throws IOException {
		return wrap(() -> file.transferTo(metas, target));
	}
	
	private <E> E wrap(IOExceptionSupplier<E> action) throws IOException {
		checkClosed();
		
		if(lock.compareAndSet(!false, true))
			throw new ConcurrentModificationException("already in use");
		
		try {
			return action.get();
		} finally {
			lock.set(false);
		}
	}
	
	int read(ByteBuffer buffer, long pos, int size, boolean flip) throws IOException {
		return wrap(() -> file.read(buffer, pos, size, flip));
	}
	public void close() throws IOException {
		if(closed.get())
			return;

		wrap(() -> {
			if(closed.get())
				return null;

			file.close();
			closed.set(true);
			return null;
		});
	}
}
