package sam.io.infile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Path;

import sam.functions.IOExceptionConsumer;
import sam.io.BufferConsumer;
import sam.io.BufferSupplier;
import sam.io.serilizers.StringIOUtils;
import sam.io.serilizers.WriterImpl;
import sam.logging.Logger;

public class TextInFile extends InFile {
	private static final Logger LOGGER = Logger.getLogger(TextInFile.class);

	public TextInFile(Path path, boolean createIfNotExits) throws IOException {
		super(path, createIfNotExits);
	}

	public void readText(DataMeta meta, ByteBuffer buffer, CharBuffer charBuffer, CharsetDecoder decoder, Appendable sink) throws IOException {
		readText(meta, buffer, charBuffer, decoder, sink, null, null, ' ', null);
	}
	public void readText(DataMeta meta, ByteBuffer buffer, CharBuffer charBuffer, CharsetDecoder decoder, IOExceptionConsumer<CharBuffer> consumer) throws IOException {
		readText(meta, buffer, charBuffer, decoder, null, consumer, null, ' ', null);
	}
	public void collect(DataMeta meta, ByteBuffer buffer, CharBuffer charBuffer, CharsetDecoder decoder, IOExceptionConsumer<String> consumer, char separator, StringBuilder sbBuffer) throws IOException {
		readText(meta, buffer, charBuffer, decoder, null, null, consumer, separator, sbBuffer);
	}
	private void readText(DataMeta meta, ByteBuffer buffer, CharBuffer charBuffer, CharsetDecoder decoder, Appendable sink, IOExceptionConsumer<CharBuffer> consumer, IOExceptionConsumer<String> collector, char separator, StringBuilder sb) throws IOException {
		if(meta.size == 0)
			return;

		BufferSupplier fill = supplier(meta, buffer);

		if(collector != null)
			StringIOUtils.collect(fill, separator, collector, decoder, charBuffer, sb);
		else if(consumer != null)
			StringIOUtils.read(fill, consumer, decoder, charBuffer);	
		else 
			StringIOUtils.read(fill, sink, decoder, charBuffer);
	}

	public DataMeta write(CharSequence s, CharsetEncoder encoder, ByteBuffer buffer) throws IOException {
		long pos = size();

		if(s == null || s.length() == 0)
			return new DataMeta(pos, 0);

		int[] size = {0};
		StringIOUtils.write(consumer(size), s, encoder, buffer);

		DataMeta d = new DataMeta(pos, size[0]); 
		LOGGER.debug("WRITTEN: {}", d);
		return d;
	}
	
	private BufferConsumer consumer(int[] size) {
		return b -> size[0] += write0(b);
	}

	public DataMeta write(IOExceptionConsumer<WriterImpl> writerConsumer, CharsetEncoder encoder, ByteBuffer buffer, CharBuffer charBuffer) throws IOException {
		long pos = size();
		int[] size = {0};
		
		try(WriterImpl w = new WriterImpl(consumer(size), buffer, charBuffer, false, encoder)) {
			writerConsumer.accept(w);
		}
		
		DataMeta d = new DataMeta(pos, size[0]); 
		LOGGER.debug("WRITTEN: {}", d);
		return d;
	}
}