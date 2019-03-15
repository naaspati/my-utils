package sam.io.serilizers;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static sam.io.IOUtils.ensureCleared;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Path;
import java.util.Objects;

import sam.io.BufferConsumer;
import sam.io.IOConstants;


public class StringWriter2 {
	private CharsetEncoder encoder;
	private boolean append;
	private ByteBuffer buffer;

	public void encoder(Charset charset) {
		this.encoder = IOConstants.newEncoder(charset);
	}
	public StringWriter2 encoder(CharsetEncoder encoder) {
		this.encoder = Objects.requireNonNull(encoder);
		return this;
	}

	public StringWriter2 buffer(ByteBuffer buffer) { 
		this.buffer = buffer; 
		return this; 
	}
	public StringWriter2 append(boolean append) { 
		this.append = append; 
		return this; 
	}

	public void encoder(Charset charset, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) {
		this.encoder = charset.newEncoder()
				.onMalformedInput(onMalformedInput)
				.onUnmappableCharacter(onUnmappableCharacter);
	}

	public void write(CharSequence data, WritableByteChannel target) throws IOException {
		ensureCleared(buffer);
		StringIOUtils.write(BufferConsumer.of(target, false), data, encoder, buffer);

		if(buffer != null)
			buffer.clear();
	}
	public void write(CharSequence data, Path target) throws IOException {
		try(FileChannel fc = FileChannel.open(target, CREATE, WRITE,  append ? APPEND : TRUNCATE_EXISTING)) {
			write(data, fc);
		}
	}
	public static void setText(Path path, CharSequence data) throws IOException{
		new StringWriter2().write(data, path);
	}
	public static void appendText(Path path, CharSequence data) throws IOException{
		new StringWriter2().append(true).write(data, path);
	}

}
