package sam.io.infile;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import sam.functions.IOExceptionConsumer;
import sam.functions.IOExceptionSupplier;
import sam.io.IOConstants;
import sam.io.IOUtils;
import sam.logging.Logger;
import sam.myutils.Checker;


class InFileImpl implements AutoCloseable {
	private static final Logger LOGGER = Logger.getLogger(InFileImpl.class);

	private static final int DEFAULT_BUFFER_SIZE = IOConstants.defaultBufferSize();
	private final FileChannel file;
	private final Path temp, filepath;
	private final AtomicLong position = new AtomicLong();

	public InFileImpl(Path path, boolean createIfNotExits) throws IOException {
		Objects.requireNonNull(path);
		this.filepath = path;

		if(!createIfNotExits && !Files.isRegularFile(path))
			throw new FileNotFoundException("file not found: "+path);

		this.temp = Files.createTempFile("InFile-", "-"+path.getFileName());

		if(Files.exists(path)) {
			Files.copy(path, temp, StandardCopyOption.REPLACE_EXISTING);
			LOGGER.debug("created a copy: \"{}\" -> \"{}\"", path, temp);
		} else {
			LOGGER.debug("created: \"{}\"", temp);
		}

		file = FileChannel.open(temp, READ, WRITE);
		position.set(file.size());
		file.position(position.get());
	}

	public Path getPath() {
		return filepath;
	}

	private long position() throws IOException {
		return position.get();
	}
	public long size() throws IOException {
		return position();
	}
	public long acutualSize() throws IOException {
		return file.size();
	}
	
	private int read(ByteBuffer buffer, long pos) throws IOException {
		return file.read(buffer, pos);
	}
	
	public DataMeta write(ByteBuffer buffer) throws IOException {
		return write(new IOExceptionSupplier<ByteBuffer>() {
			boolean b = true;

			@Override
			public ByteBuffer get() throws IOException {
				if(b) {
					b = false;
					return buffer;
				}
				return null;
			}
		});
	}

	/**
	 *  write every buffer supplied by buffers, until buffers returns null (end of input)
	 */
	public DataMeta write(IOExceptionSupplier<ByteBuffer> buffers) throws IOException {
		long pos = position();
		int size = write2(buffers);

		DataMeta d = new DataMeta(pos, size);

		LOGGER.debug("WRITTEN: {}", d);
		return d;
	}

	/**
	 *  write every buffer supplied by buffers, until buffers returns null (end of input)
	 */
	public int write2(IOExceptionSupplier<ByteBuffer> buffers) throws IOException {
		int size = 0;
		boolean success = false;

		try {
			ByteBuffer buffer; 
			int loops = 0;

			while((buffer = buffers.get()) != null) {
				if(!buffer.hasRemaining()) 
					LOGGER.debug("EMPTY buffer return by buffers");
				else 
					size += IOUtils.write(buffer,file, false);	
			}
			LOGGER.debug("WRITTEN: {} bytes, loops: {}", size, loops);
			success = true;
			position.addAndGet(size);
			return size;
		} finally {
			if(!success)
				position.set(file.size());
		}
	}

	public ByteBuffer read(DataMeta meta, ByteBuffer buffer) throws IOException {
		Objects.requireNonNull(meta);
		IOUtils.ensureCleared(buffer);

		if(meta.size == 0)
			return (buffer == null ? ByteBuffer.allocate(0) : buffer);

		if(buffer == null || buffer.capacity() < meta.size) {
			ByteBuffer old = buffer;
			buffer = ByteBuffer.allocate(meta.size);
			LOGGER.debug("Buffer created: {} -> buffer({})", old == null ? null : "buffer("+old.capacity()+")", meta.size);
		}

		buffer.clear();
		buffer.limit(meta.size);

		while(buffer.hasRemaining() && read(buffer, meta.position + buffer.position()) != -1) {
		}

		if(buffer.hasRemaining()) 
			throw new IOException(String.format("expected-to-read:%s, but-read:%s ", meta.size, buffer.position()));

		buffer.flip();

		LOGGER.debug("READ: {}", meta);
		return buffer;

	}

	public void read(DataMeta meta, ByteBuffer buffer, IOExceptionConsumer<ByteBuffer> bufferConsumer) throws IOException {
		int size = meta.size;
		if(size == 0)
			bufferConsumer.accept(buffer == null ? ByteBuffer.allocate(0) : buffer);
		else {
			if(meta.position + meta.size > size()) 
				throw new IOException("DataMeta out of bounds: (meta.position("+meta.position+") + meta.size("+meta.size+")) = ("+(meta.position + meta.size)+") > size("+size()+")");
			
			if(buffer == null) {
				buffer = ByteBuffer.allocate(Math.min(size, DEFAULT_BUFFER_SIZE));
				LOGGER.debug("Buffer created capacity: {}", buffer.capacity());
			}

			int loops = 0;
			long pos = meta.position;
			
			while(size > 0) {
				loops++;
				buffer.limit(buffer.position() + Math.min(buffer.remaining(), size));
				int n = read(buffer, pos);
				if(n == -1)
					throw new IOException("all bytes not found, remaining: "+size);

				pos += n;
				size -= n;
				buffer.flip();
				bufferConsumer.accept(buffer);

				if(size > 0 && !buffer.hasRemaining())
					throw new IOException("buffer not cosumed");
			}
			
			if(size != 0)
				throw new IOException("size("+size+") != 0");
			
			LOGGER.debug("READ bytes: {}, loops: {}", meta.size, loops);
		}
	}

	public void transfer(Iterable<DataMeta> metas, WritableByteChannel target, ByteBuffer buffer) throws IOException {
		Objects.requireNonNull(metas, "metas cannot be null");
		IOUtils.ensureCleared(buffer);

		List<DataMeta> list;
		if(metas instanceof List)
			list = (List<DataMeta>) metas;
		else if(metas instanceof Collection)
			list = Arrays.asList(((Collection<DataMeta>) metas).toArray(new DataMeta[0]));
		else {
			Iterator<DataMeta> itr = metas.iterator();

			if(!itr.hasNext())
				return;

			list = new ArrayList<>();
			itr.forEachRemaining(list::add);
		}

		transferList(list, target, buffer);
	}

	/**
	 * ordered and checked.
	 * i dont see any use for it now, maybe later
	 * -- NOTE: untested
	 * 
	 * possible uses: 
	 *   - when order is required
	 *     - compress infile
	 *     - tranfer to other infile
	 *     both of these need to modify DataMeta(s) thus old DataMeta(s) will needed to be changed 
	 * 
	 * @param metas
	 * @param target
	 * @param buffer
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	private long transferOrdered(Iterable<DataMeta> metas, WritableByteChannel target, ByteBuffer buffer) throws IOException {
		IOUtils.ensureCleared(buffer);

		Objects.requireNonNull(metas);
		List<DataMeta> list;

		if(metas instanceof Collection) {
			Collection col = (Collection)metas;
			list = col.isEmpty() ? Collections.emptyList() : new ArrayList<>(col);
		} else {
			Iterator<DataMeta> itr = metas.iterator();

			if(!itr.hasNext())
				list = Collections.emptyList();
			else {
				list = new ArrayList<>();
				itr.forEachRemaining(list::add);
			}
		}

		if(list.isEmpty()) 
			return 0;
		else if(list.size() == 1) 
			return IOUtils.write(read(list.get(0), buffer), target, false);
		else {
			list.sort(Comparator.comparingLong(d -> d.position));
			List<DataMeta> conflicts = null;

			for (int i = 0; i < list.size(); i++) {
				DataMeta current = list.get(i);
				if(i + 1 != list.size()) {
					DataMeta next = list.get(i);

					if(next.position < current.position + current.size) {
						if(conflicts == null)
							conflicts = new ArrayList<>();

						conflicts.add(current);
						conflicts.add(next);
					}
				}
			}
			if(Checker.isNotEmpty(conflicts))
				throw new IOException("DataMeta conflicts: "+conflicts);

			return transferList(list, target, buffer);
		}
	}

	private long transferList(List<DataMeta> list, WritableByteChannel target, ByteBuffer buffer) throws IOException {
		long size = position();

		if(list.stream().anyMatch(d -> d.position + d.size > size))
			throw new IOException("out of bounds: filesize:"+size+" "+list.stream().filter(d -> d.position + d.size > size).collect(Collectors.toList()));

		buffer = buffer(buffer);
		buffer.clear();
		AtomicLong read = new AtomicLong();

		int index = 0;
		while((index = transferSublist(list, target, buffer, read, index)) < list.size()){
		}

		return read.get();
	}
	private ByteBuffer buffer(ByteBuffer buffer) {
		if(buffer == null) {
			buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
			LOGGER.debug(() -> "buffer created: ByteBuffer.allocate("+DEFAULT_BUFFER_SIZE+")"); 
		}
		return buffer;
	}
	private int transferSublist(List<DataMeta> list, WritableByteChannel target, ByteBuffer buffer, AtomicLong transferCount, int index) throws IOException {
		IOUtils.ensureCleared(buffer);

		int nextIndex = index + 1;
		for (; nextIndex < list.size(); nextIndex++) {
			DataMeta current = list.get(nextIndex - 1);
			DataMeta next = list.get(nextIndex);
			if(current.position + current.size != next.position)
				break;
		}

		long initPos = list.get(index).position;
		long pos = initPos;
		long size = 0;
		for (int i = index; i < nextIndex; i++) 
			size += list.get(i).size();

		long size2 = size;
		int loops = 0;

		while(size > 0) {
			read(pos, size, buffer);
			int n = IOUtils.write(buffer, target, false);
			size -= n;
			pos  += n;
			loops++;
		}

		if(pos - initPos != size2)
			throw new IOException("expected to write: "+size2+", was-written:"+(pos - initPos));

		LOGGER.debug("WRITTEN: bytes-written:{}, loopCount:{}, range: [{}, {}], dataMeta: {}", size2, loops, index, nextIndex, list.subList(index, nextIndex));

		transferCount.addAndGet(size2);
		return nextIndex;
	}

	private void read(long pos, long size, ByteBuffer buffer) throws IOException {
		buffer.limit((int) Math.min(buffer.limit(), size));
		LOGGER.debug("READ file: position: {}, size: {}", pos, buffer.limit());
		read(buffer, pos);
		buffer.flip();
	}

	@Override
	public void close() throws IOException {
		file.close();
		Files.move(temp, filepath, StandardCopyOption.REPLACE_EXISTING);
		LOGGER.debug("moved: \"{}\" -> \"{}\"", temp, filepath);	
	}

}
