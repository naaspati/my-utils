package sam.io.infile;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import sam.io.BufferConsumer;
import sam.io.BufferSupplier;
import sam.io.IOConstants;
import sam.io.IOUtils;
import sam.logging.Logger;


class InFileImpl implements AutoCloseable {
	private static final Logger LOGGER = Logger.getLogger(InFileImpl.class);
	private static final boolean DEBUG_ENABLED = LOGGER.isDebugEnabled();

	private static final int DEFAULT_BUFFER_SIZE = IOConstants.defaultBufferSize();
	private final FileChannel file;
	private final Path filepath;
	private final FileLock lock;
	private final AtomicLong position = new AtomicLong();
	private volatile int mod = 0;

	public InFileImpl(Path path, boolean createIfNotExits) throws IOException {
		Objects.requireNonNull(path);
		this.filepath = path;

		file =  createIfNotExits ? FileChannel.open(path, CREATE, READ, WRITE) : FileChannel.open(path, READ, WRITE);
		lock = file.tryLock();
		if(lock == null) {
			file.close();
			throw new IOException("failed to accuire lock");
		}

		long pos = file.size();
		position.set(pos);
		file.position(pos);
	}

	public Path getPath() {
		return filepath;
	}
	private void resetPosition() throws IOException {
		long n = acutualSize();
		LOGGER.warn("Position reset: {} -> {}", position.get(), n);
		position.set(n);
		mod++;
	}
	long position() throws IOException {
		return position.get();
	}
	public long size() throws IOException {
		return position();
	}
	public long acutualSize() throws IOException {
		return file.size();
	}
	int read(ByteBuffer buffer, long pos, int size, boolean flip) throws IOException {
		buffer.limit(buffer.position() + Math.min(buffer.remaining(), size));
		return read(buffer, pos, flip);
	}
	int read(ByteBuffer buffer, long pos, boolean flip) throws IOException {
		int n = file.read(buffer, pos);
		if(flip)
			buffer.flip();

		if(DEBUG_ENABLED)
			LOGGER.debug("READ: pos: {}, size: {}", pos, buffer.limit());

		return n;
	}

	DataMeta write2(ByteBuffer buffer) throws IOException {
		long pos = position();
		int size = write(buffer);
		return new DataMeta(pos, size);
	}
	int write(ByteBuffer buffer) throws IOException {
		if(!buffer.hasRemaining()) {
			buffer.clear();
			return 0;
		}

		mod++;
		boolean success = false;
		try {
			long pos = DEBUG_ENABLED ? position() : -1;

			int n = IOUtils.write(buffer, file, false);
			success = true;
			position.addAndGet(n);

			if(DEBUG_ENABLED)
				LOGGER.debug("WRITTEN: pos: {}, size: {}", pos, n);
			return n;
		} finally {
			if(!success)
				resetPosition();
		}
	}
	/**
	 *  write every buffer supplied by buffers, until buffers returns null (end of input)
	 */
	public DataMeta write(BufferSupplier buffers) throws IOException {
		long pos = position();
		int size = write2(buffers);

		DataMeta d = new DataMeta(pos, size);

		LOGGER.debug("WRITTEN: {}", d);
		return d;
	}

	public DataMeta replace(DataMeta d, ByteBuffer buffer) throws IOException {
		Objects.requireNonNull(d);
		Objects.requireNonNull(buffer);
		verifyDataMeta(d);

		if(buffer.remaining() > d.size)
			throw new IOException("new size ("+(buffer.remaining())+"), exceeds old size ("+d.size+")");

		if(d.size == 0)
			return d;
		if(!buffer.hasRemaining())
			return new DataMeta(d.position, 0);

		DataMeta dm = new DataMeta(d.position, buffer.remaining());

		long pos = d.position;
		while(buffer.hasRemaining()) 
			pos += file.write(buffer, pos);

		LOGGER.debug("REPLACED: {} -> {}", d, dm);
		return dm;
	}

	public DataMeta replace(DataMeta d, BufferSupplier buffers) throws IOException {
		Objects.requireNonNull(d);
		Objects.requireNonNull(buffers);

		long pos = 0;
		int loops = 0;

		while(true) {
			loops++;
			ByteBuffer buffer = buffers.next();
			if(buffer == null && buffers.isEndOfInput())
				break;

			if(!buffer.hasRemaining()) { 
				LOGGER.debug("EMPTY buffer return by buffers");
				buffer.clear();
			} else {
				if(buffer.remaining() + pos > d.size + d.position)
					throw new IOException("new size ("+(buffer.remaining() + pos - d.position)+"), exceeds old size ("+d.size+")");

				pos += IOUtils.write(buffer, pos, file, false);
			}

			if(buffers.isEndOfInput())
				break;
		}

		DataMeta d2 = new DataMeta(d.position, (int) (pos - d.position));
		LOGGER.debug("REPLACED: {} -> {}, loops: {}", d, d2, loops);

		return d2;
	}

	/**
	 *  write every buffer supplied by buffers, until buffers returns null (end of input)
	 */
	private int write2(BufferSupplier buffers) throws IOException {
		int size = 0;
		boolean success = false;

		mod++;

		try {
			int loops = 0;

			while(true) {
				ByteBuffer buffer = buffers.next();
				if(buffer == null && buffers.isEndOfInput())
					break;

				if(!buffer.hasRemaining()) { 
					LOGGER.debug("EMPTY buffer return by buffers");
					buffer.clear();
				} else {
					size += IOUtils.write(buffer, file, false);
				}

				if(buffers.isEndOfInput())
					break;
			}

			LOGGER.debug("WRITTEN: {} bytes, loops: {}", size, loops);
			success = true;
			position.addAndGet(size);
			return size;
		} finally {
			if(!success)
				resetPosition();
		}
	}

	public ByteBuffer read(DataMeta meta, ByteBuffer buffer) throws IOException {
		Objects.requireNonNull(meta);
		IOUtils.ensureCleared(buffer);
		verifyDataMeta(meta);

		if(meta.size == 0)
			return (buffer == null ? IOConstants.EMPTY_BUFFER : buffer);

		if(buffer == null || buffer.capacity() < meta.size) {
			ByteBuffer old = buffer;
			buffer = ByteBuffer.allocate(meta.size);
			if(DEBUG_ENABLED)
				LOGGER.debug("Buffer created: {} -> buffer({})", old == null ? null : "buffer("+old.capacity()+")", meta.size);
		}

		buffer.clear();
		buffer.limit(meta.size);

		while(buffer.hasRemaining() && read(buffer, meta.position + buffer.position(), false) != -1) {
		}

		if(buffer.hasRemaining()) 
			throw new IOException(String.format("expected-to-read:%s, but-read:%s ", meta.size, buffer.position()));

		buffer.flip();

		LOGGER.debug("READ: {}", meta);
		return buffer;

	}

	public void read(DataMeta meta, ByteBuffer buffer, BufferConsumer bufferConsumer) throws IOException {
		int size = meta.size;
		if(size == 0)
			bufferConsumer.consume(buffer == null ? IOConstants.EMPTY_BUFFER : buffer);
		else {
			verifyDataMeta(meta);
			if(buffer == null) {
				buffer = ByteBuffer.allocate(Math.min(size, DEFAULT_BUFFER_SIZE));
				LOGGER.debug("Buffer created capacity: {}", buffer.capacity());
			}

			int loops = 0;
			long pos = meta.position;

			while(size > 0) {
				loops++;
				int n = read(buffer, pos, size, true);
				if(n == -1)
					throw new IOException("all bytes not found, remaining: "+size);

				pos += n;
				size -= n;

				bufferConsumer.consume(buffer);

				if(size > 0 && !buffer.hasRemaining())
					throw new IOException("buffer not cosumed");
			}

			if(size != 0)
				throw new IOException("size("+size+") != 0");

			LOGGER.debug("READ bytes: {}, loops: {}", meta.size, loops);
		}
	}

	private void verifyDataMetas(List<DataMeta> list) throws IOException {
		long size = position();

		if(list.stream().anyMatch(d -> d.position + d.size > size))
			throw new IOException("out of bounds: filesize:"+size+" "+list.stream().filter(d -> d.position + d.size > size).collect(Collectors.toList()));
	}
	private void verifyDataMeta(DataMeta d) throws IOException {
		long size = position();

		if(d.position + d.size > size)
			throw new IOException("DataMeta out of bounds: "+d+", size: "+size);
	}

	public IdentityHashMap<DataMeta, DataMeta> transferTo(Iterable<DataMeta> metas, InFileImpl target) throws IOException {
		Objects.requireNonNull(metas);

		if(this.equals(target))
			throw new IOException("source cannot be the target");

		List<DataMeta> list = toList(metas);
		if(list.isEmpty())
			return new IdentityHashMap<>();

		verifyDataMetas(list);

		long tpos = target.position();
		boolean success = false;
		mod++;

		try {
			transferList(list, target.file);
			IdentityHashMap<DataMeta, DataMeta> map = new IdentityHashMap<>();

			for (DataMeta old : list) {
				DataMeta neww = new DataMeta(tpos, old.size);
				map.put(old, neww);
				tpos += old.size;
			}

			if(DEBUG_ENABLED) {
				StringBuilder sb = new StringBuilder();
				list.forEach(c -> sb.append("transfered: ").append(c).append(" -> ").append(map.get(c)).append('\n'));
				LOGGER.debug(() -> sb.toString());
			}

			target.position.set(tpos);
			success = true;
			return map;
		} finally {
			if(!success)
				target.resetPosition();
		}

	}

	public long transferTo(Iterable<DataMeta> metas, WritableByteChannel target) throws IOException {
		Objects.requireNonNull(metas, "metas cannot be null");

		List<DataMeta> list = toList(metas);
		return transferList(list, target);
	}

	@SuppressWarnings("rawtypes")
	private List<DataMeta> toList(Iterable<DataMeta> metas) {
		if(metas instanceof Collection && ((Collection) metas).isEmpty())
			return Collections.emptyList();

		if(metas instanceof List)
			return (List<DataMeta>) metas;
		else if(metas instanceof Collection)
			return Arrays.asList(((Collection<DataMeta>) metas).toArray(new DataMeta[0]));
		else {
			Iterator<DataMeta> itr = metas.iterator();

			if(!itr.hasNext())
				return Collections.emptyList();

			List<DataMeta> list = new ArrayList<>();
			itr.forEachRemaining(list::add);
			return list;
		}
	}

	private long transferList(List<DataMeta> list, WritableByteChannel target) throws IOException {
		mod++;

		if(list.isEmpty())
			return 0;
		else if(list.size() == 1) {
			DataMeta d = list.get(0);
			verifyDataMeta(d);
			return transfer(d.position, d.size, target);
		} else {

			AtomicLong read = new AtomicLong();

			int index = 0;
			while((index = transferSublist(list, target, read, index)) < list.size()){
			}

			return read.get();	
		}
	}

	private int transferSublist(List<DataMeta> list, WritableByteChannel target, AtomicLong transferCount, int index) throws IOException {
		int nextIndex = index + 1;
		for (; nextIndex < list.size(); nextIndex++) {
			DataMeta current = list.get(nextIndex - 1);
			DataMeta next = list.get(nextIndex);
			if(current.position + current.size != next.position)
				break;
		}

		long size = 0;
		for (int i = index; i < nextIndex; i++) 
			size += list.get(i).size();

		long size2 = transfer(list.get(index).position, size, target);
		transferCount.addAndGet(size2);

		if(DEBUG_ENABLED) {
			if(index + 1 == nextIndex)
				LOGGER.debug("WRITTEN: bytes-written:{}, index: {}, dataMeta: {}", size2, index, list.get(index));		
			else
				LOGGER.debug("WRITTEN: bytes-written:{}, range: [{}, {}], dataMeta: {}", size2, index, nextIndex, list.subList(index, nextIndex));	
		}
		return nextIndex;
	}

	private long transfer(long pos, long size, WritableByteChannel target) throws IOException {
		long initPos = pos;
		long size2 = size;

		mod++;

		while(size > 0) {
			long n = file.transferTo(pos, size, target);
			size -= n;
			pos  += n;
		}

		if(size != 0)
			throw new IOException("expected to write: "+size2+", was-written:"+(pos - initPos));

		return pos - initPos;
	}

	@Override
	public void close() throws IOException {
		lock.release();
		file.close();
	}

	public BufferSupplier supplier(DataMeta meta, ByteBuffer buffer) throws IOException {
		Objects.requireNonNull(meta);
		verifyDataMeta(meta);

		if(meta.size == 0)
			return BufferSupplier.EMPTY;

		if(buffer == null) 
			buffer = ByteBuffer.allocate(Math.min(meta.size, BufferSupplier.DEFAULT_BUFFER_SIZE));

		ByteBuffer buf = buffer;
		int mod = this.mod;

		return new BufferSupplier() {
			int size = meta.size;
			long pos = meta.position;

			@Override 
			public long size() throws IOException {
				checkMod(mod);
				return meta.size; 
			}

			@Override
			public ByteBuffer next() throws IOException {
				ensureBufferNotFull(buf);
				
				checkMod(mod);
				int n = read(buf, pos, size, true);

				size -= n;
				pos  += n;

				return buf;
			}
			
			@Override
			public boolean isEndOfInput() throws IOException {
				checkMod(mod);
				return size == 0;
			}
		};
	}

	private void checkMod(int mod2) {
		if(this.mod != mod2)
			throw new ConcurrentModificationException();
	}
}
