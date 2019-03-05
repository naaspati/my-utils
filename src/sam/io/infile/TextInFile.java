package sam.io.infile;

import static java.nio.charset.CodingErrorAction.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Path;

import sam.io.BufferSupplier;
import sam.io.IOUtils;
import sam.io.serilizers.StringIOUtils;
import sam.logging.Logger;

public class TextInFile extends InFile {
	private static final Logger LOGGER = Logger.getLogger(TextInFile.class);

	public TextInFile(Path path, boolean createIfNotExits) throws IOException {
		super(path, createIfNotExits);
	}

	public void readText(DataMeta meta, ByteBuffer buffer, CharBuffer charBuffer, CharsetDecoder decoder, Appendable sink) throws IOException {
		readText(meta, buffer, charBuffer, decoder, sink, REPORT, REPORT);
	}
	public void readText(DataMeta meta, ByteBuffer buffer, CharBuffer charBuffer, CharsetDecoder decoder, Appendable sink, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		if(meta.size == 0)
			return;

		if(buffer == null) 
			buffer = ByteBuffer.allocate(Math.min(meta.size, BufferSupplier.DEFAULT_BUFFER_SIZE));
		
		IOUtils.ensureCleared(buffer);
		ByteBuffer bf = buffer;
		
		BufferSupplier fill = new BufferSupplier() {
			int size = meta.size;
			long pos = meta.position;
			
			boolean first = true;
			
			@Override public long size() throws IOException { return meta.size; }
			
			@Override
			public ByteBuffer next() throws IOException {
				if(!first) {
					if(bf.hasRemaining()) 
						bf.compact();
					 else
						bf.clear();	
				}
				
				first = false;
				
				int n = read(bf, pos, size, true);
				
				size -= n;
				pos  += n;
				
				return bf;
			}
			@Override
			public boolean isEndOfInput() throws IOException {
				return size == 0;
			}
		};
		
		StringIOUtils.read(fill, sink, decoder, charBuffer, onUnmappableCharacter, onMalformedInput);
	}
	
	public DataMeta write(CharSequence s, CharsetEncoder encoder, ByteBuffer buffer) throws IOException {
		return write(s, encoder, buffer, REPORT, REPORT);
	}
	public DataMeta write(CharSequence s, CharsetEncoder encoder, ByteBuffer buffer, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws IOException {
		long pos = size();
		
		if(s == null || s.length() == 0)
			return new DataMeta(pos, 0);
		
		int[] size = {0};
		StringIOUtils.write(b -> size[0] += write0(b), s, encoder, buffer, onUnmappableCharacter, onMalformedInput);
		
		DataMeta d = new DataMeta(pos, size[0]); 
		LOGGER.debug("WRITTEN: {}", d);
		return d;
	}
}
