package sam.io.infile;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.file.Path;

import sam.io.IOUtils;


public class InFile implements AutoCloseable {
	private final FileChannel file;
	private long size;
	
	public InFile(Path path, boolean createInNotExits) throws IOException {
		this.file = createInNotExits ? FileChannel.open(path, CREATE, READ, WRITE) : FileChannel.open(path, READ, WRITE);
		this.size = file.size();
		file.position(0);
	}
	
	public long position() throws IOException { return file.position(); }
	public int write(InputStream is, byte[] buffer) throws IOException {
		return (int) IOUtils.pipe(is, file, buffer);
	} 
	
	@Override
	public void close() throws IOException {
		file.close();
	}
	public DataMeta write(CharSequence data, ByteBuffer buffer, CharsetEncoder encoder) throws IOException {
		CharBuffer chars = CharBuffer.wrap(data);
		
		long pos = this.size;
		int size = 0;
		encoder.reset();
		
		while(true) {
			CoderResult cr = encoder.encode(chars, buffer, true);
			check(cr);
			
			size += write(buffer, file, true);
			
			if(!chars.hasRemaining()) {
				while(true) {
					cr = encoder.flush(buffer);
					check(cr);
					size += write(buffer, file, true);
					
					if(cr.isUnderflow()) {
						this.size += size;
						return new DataMeta(pos, size);
					}
				}
			}
		}
	}
	
	public void read(ByteBuffer buffer, DataMeta meta) throws IOException {
		buffer.limit(meta.size);
		file.position(meta.position);
		while(file.read(buffer) != -1 && buffer.hasRemaining()) { }
	}
	
	private long write(ByteBuffer buffer, FileChannel file, boolean flip) throws IOException {
		return IOUtils.write(buffer, file, flip);
	}

	private static void check(CoderResult c) throws CharacterCodingException {
		if(!(c.isUnderflow() || c.isOverflow()))
			c.throwException();
	}

}
