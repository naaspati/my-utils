package sam.io.infile;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Deprecated //very small loading unloading not effiecient
public final class MetasFile implements AutoCloseable {
	private static final long serial = 2167575924260531280L;

	private static final int BYTES = Long.BYTES + Integer.BYTES;

	private final FileChannel file;
	private final FileLock lock;

	private AtomicLong size = new AtomicLong();
	private final ByteBuffer buffer = ByteBuffer.allocate(BYTES);

	public MetasFile(Path path) throws IOException {
		file = FileChannel.open(path, READ, WRITE, CREATE);
		lock = file.tryLock();

		if(lock == null)
			throw new IOException("failed to acquire lock");

		size.set(file.size());

		if(size.get() != 0) {
			buffer.clear();
			int n = file.read(buffer);
			buffer.flip();

			if(n < BYTES)
				throw new IOException("not MetasFile");
			buffer.getInt();
			if(buffer.getLong() != serial)
				throw new IOException("not MetasFile");
		} else {
			buffer.clear();
			buffer.putInt(0);
			buffer.putLong(serial);
			buffer.flip();
			file.write(buffer);

			size.addAndGet(BYTES);
		}
	}

	public void setMeta(int meta) throws IOException {
		buffer.clear();
		buffer.putInt(meta);
		buffer.flip();
		file.write(buffer, 0);
	}
	public int getMeta() throws IOException {
		buffer.clear();
		buffer.limit(Integer.BYTES);
		file.read(buffer, 0);
		buffer.flip();
		
		return buffer.getInt();
	}

	public DataMeta get(int id, boolean throwIfNotFound) throws IOException {
		long pos = pos(id);

		if(pos + BYTES > size.get()) {
			if(throwIfNotFound)
				throw new IOException("out of bounds: pos ("+pos+") + BYTES ("+BYTES+") > size.get("+size.get()+")");
			return null;
		}

		buffer.clear();
		file.read(buffer, pos);
		buffer.flip();
		return new DataMeta(buffer.getLong(), buffer.getInt()); 
	}
	private long pos(int id) {
		if(id < 0)
			throw new IllegalArgumentException("id("+id+") < 0");

		return (id + 1) * BYTES;
	}

	public void set(int id, DataMeta d) throws IOException {
		Objects.requireNonNull(d);
		long pos = pos(id);

		long size = this.size.get();

		if(pos > size * 2)
			throw new IOException("out of bounds: pos("+pos+") > size.get() * 2 = "+(size * 2)+""); 

		buffer.clear();
		buffer.putLong(d.position);
		buffer.putInt(d.size);
		buffer.flip();

		file.write(buffer, pos);

		if(pos > size)
			this.size.set(pos + BYTES);
	}
	public long size() {
		return size.get();
	}
	@Override
	public void close() throws Exception {
		file.close();
		lock.release();
	}
}
