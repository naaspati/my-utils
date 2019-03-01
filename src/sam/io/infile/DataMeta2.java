package sam.io.infile;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

import sam.io.BufferSupplier;
import sam.io.IOUtils;

/**
 * this contains a third field (id), which can be used as DataMeta identifier
 */
public class DataMeta2 extends DataMeta {
//	private static final Logger LOGGER = Logger.getLogger(DataMeta2.class);

	private static final long marker = -6869250468519032486L;

	public static final int BYTES = 
			Integer.BYTES + // for id
			Long.BYTES + // for pos
			Integer.BYTES; // for size

	public final int id;

	public DataMeta2(int id, long position, int size) {
		super(position, size);
		this.id = id;
	}
	public DataMeta2(int id, DataMeta d) {
		this(id, d.position, d.size);
	}

	public final int id() { return id; }

	private static void ensureBufferCapacity(ByteBuffer buffer) throws IOException {
		if(buffer != null && buffer.capacity() < BYTES * 2)
			throw new IOException("buffer.capacity("+buffer.capacity()+") < BYTES * 2 ("+BYTES * 2+")");
	}
	public static void save(Collection<? extends DataMeta2> metas, Path p, ByteBuffer buffer) throws IOException {
		Objects.requireNonNull(metas);
		Objects.requireNonNull(p);
		IOUtils.ensureCleared(buffer);

		ensureBufferCapacity(buffer);

		try(FileChannel fc = FileChannel.open(p, CREATE, WRITE, TRUNCATE_EXISTING)) {
			write(metas, buffer, fc);
		}
	}
	public static void write(Collection<? extends DataMeta2> metas, ByteBuffer buffer, WritableByteChannel fc) throws IOException {
		ensureBufferCapacity(buffer);
		buffer = buffer(buffer, metas.size() * BYTES + Long.BYTES + Integer.BYTES);

		buffer.putLong(marker);
		buffer.putInt(metas.size());

		if(!metas.isEmpty()) {
			for (DataMeta2 d : metas) {
				if(buffer.remaining() < BYTES)   
					IOUtils.write(buffer, fc, true);
				

				buffer.putInt(d.id);
				buffer.putLong(d.position);
				buffer.putInt(d.size);
			}
		}
		
		IOUtils.write(buffer, fc, true);
	}
	public static DataMeta2[] read(Path p, ByteBuffer buffer) throws IOException {
		Objects.requireNonNull(p);
		IOUtils.ensureCleared(buffer);

		ensureBufferCapacity(buffer);

		try(FileChannel fc = FileChannel.open(p, READ)) {
			long filesize = fc.size();
			if(filesize == 0)
				return new DataMeta2[0];

			buffer = buffer(buffer, (int)filesize);

			return read(fc, buffer);
		}
	}

	private static ByteBuffer buffer(ByteBuffer buffer, int maxsize) {
		if(buffer != null)
			return buffer;

		return ByteBuffer.allocate((int)Math.min(maxsize, BufferSupplier.DEFAULT_BUFFER_SIZE));
	}
	public static DataMeta2[] read(ReadableByteChannel fc, ByteBuffer buffer) throws IOException {
		ByteBuffer b = ByteBuffer.allocate(Long.BYTES);
		int n = fc.read(b);
		b.flip();

		if(n != Long.BYTES || marker != b.getLong())
			throw new IOException("invalid source");

		b.clear();
		b.limit(Integer.BYTES);

		n = fc.read(b);
		b.flip();

		if(n != Integer.BYTES)
			throw new IOException("invalid source");

		int count = b.getInt();
		if(count == 0)
			return new DataMeta2[0];

		int size = count * BYTES;
		buffer = buffer(buffer, size);
		
		if(buffer.capacity() < size && buffer.capacity() < BYTES * 2)
			throw new IOException("buffer.capacity("+buffer.capacity()+") < BYTES * 2 ("+(BYTES * 2)+")");
		
		DataMeta2[] list = new DataMeta2[count];
		int index = 0;

		while(size > 0) {
			buffer.limit(buffer.position() + Math.min(buffer.remaining(), size));

			n = fc.read(buffer);
			buffer.flip();

			if(n == -1) {
				if(buffer.hasRemaining())
					throw new IOException("not all data found: remaining: "+buffer.remaining()+", needed-more-bytes: "+size);
				break;
			}

			size -= n;

			while(buffer.remaining() >= BYTES) 
				list[index++] = new DataMeta2(buffer.getInt(), buffer.getLong(), buffer.getInt());

			if(buffer.hasRemaining())
				buffer.compact();
			else
				buffer.clear();
		}
		
		//LOGGER.debug("READ loops: {}, metas.size: {}, bytes: {}",loops, list.size(), bytes);
		return list;
	}
	@Override
	public int hashCode() {
		return id;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj  == null || !(obj instanceof DataMeta2))
			return false;

		DataMeta2 d = (DataMeta2) obj;

		return 
				this.id == d.id &&
				this.position == d.position &&
				this.size == d.size ;
	}

	@Override
	public String toString() {
		return "DataMeta2 [id="+id+", pos=" + position + ", size=" + size + "]";
	}
}
