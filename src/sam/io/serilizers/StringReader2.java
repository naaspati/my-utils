package sam.io.serilizers;

import static java.nio.file.StandardOpenOption.READ;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Path;
import java.util.Objects;

import sam.io.BufferSupplier;
import sam.io.IOConstants;


public class StringReader2 {
	private CharsetDecoder decoder;
	private ByteBuffer buffer;
	private CharBuffer chars;

	public void decoder(Charset charset) {
		this.decoder = IOConstants.newDecoder(charset);
	}
	public void decoder(CharsetDecoder decoder) {
		this.decoder = Objects.requireNonNull(decoder);
	}
	
	public void buffer(ByteBuffer buffer) { this.buffer = buffer; }
	public void charBuffer(CharBuffer chars) { this.chars = chars; }
	
	public void decoder(Charset charset, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) {
		this.decoder = charset.newDecoder()
				.onMalformedInput(onMalformedInput)
				.onUnmappableCharacter(onUnmappableCharacter);
	}
	
	public void read(ReadableByteChannel source, Appendable sink) throws IOException {
		StringIOUtils.read(BufferSupplier.of(source, buffer), sink, decoder, chars);
		
		if(buffer != null)
			buffer.clear();
		if(chars != null)
			chars.clear();
	}
	public void read(Path source, Appendable sink) throws IOException {
		try(FileChannel fc = FileChannel.open(source, READ)) {
			read(fc, sink);
		}
	}
	public StringBuilder getText(ReadableByteChannel source) throws IOException {
		StringBuilder sb = new StringBuilder();
		read(source, sb);
		return sb;
	}
	public StringBuilder getText(Path source) throws IOException {
		try(FileChannel fc = FileChannel.open(source, READ)) {
			return getText(fc);
		}
	}
}
