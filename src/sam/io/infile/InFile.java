package sam.io.infile;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import sam.io.IOConstants;
import sam.io.IOUtils;
import sam.logging.Logger;


public class InFile implements AutoCloseable {
	private static final Logger LOGGER = Logger.getLogger(InFile.class);

	private static final int DEFAULT_BUFFER_SIZE = IOConstants.defaultBufferSize();
	private final FileChannel file;
	private final Path temp, filepath;
	
	public InFile(Path path, boolean createIfNotExits) throws IOException {
		Objects.requireNonNull(path);
		this.filepath = path;
		
		if(!createIfNotExits && !Files.isRegularFile(path))
			throw new FileNotFoundException("file not found: "+path);
		
		this.temp = Files.createTempFile(null, null);
		if(Files.exists(path))
			Files.copy(path, temp, StandardCopyOption.REPLACE_EXISTING);
		
		file = FileChannel.open(temp, READ, WRITE);
		file.position(file.size());
		
		System.out.println(temp);
	}
	
	public long position() throws IOException { return file.position(); }
	public int write(InputStream is, byte[] buffer) throws IOException {
		return (int) IOUtils.pipe(is, file, buffer);
	}
	public ByteBuffer read(DataMeta meta, ByteBuffer buffer) throws IOException {
		synchronized (file) {
			Objects.requireNonNull(meta);
			
			if(meta.size == 0)
				return buffer == null ? ByteBuffer.allocate(0) : buffer;

			if(buffer != null && (buffer.position() != 0 || buffer.limit() != buffer.capacity()))
				throw new IOException("defaultBuffer.position("+buffer.position()+") != 0 || defaultBuffer.limit("+buffer.limit()+") != defaultBuffer.capacity("+buffer.capacity()+")");
				
			if(buffer == null || buffer.capacity() < meta.size) {
				int oldsize = buffer == null ? -1 : buffer.capacity();
				buffer = ByteBuffer.allocate(meta.size);
				
				LOGGER.debug("Buffer created: {} -> buffer({})", oldsize == -1 ? null : "buffer("+oldsize+")", meta.size);
			}
			
			buffer.clear();
			buffer.limit(meta.size);
			
			while(buffer.hasRemaining() && file.read(buffer, meta.position + buffer.position()) != -1) {
			}
			
			if(buffer.hasRemaining()) 
				throw new IOException(String.format("expected-to-read:%s, but-read:%s ", meta.size, buffer.position()));
			
			buffer.flip();
			return buffer;
		}
	} 
	
	@Override
	public void close() throws IOException {
		file.close();
		Files.move(temp, filepath, StandardCopyOption.REPLACE_EXISTING);
	}
}
