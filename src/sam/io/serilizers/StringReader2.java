package sam.io.serilizers;
import static java.nio.file.StandardOpenOption.READ;
import static sam.io.IOConstants.defaultCharset;
import static sam.io.IOConstants.defaultOnMalformedInput;
import static sam.io.IOConstants.defaultOnUnmappableCharacter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Path;
import java.util.Objects;

import sam.functions.IOExceptionFunction;
import sam.io.BufferFiller;

public class StringReader2 {
	private static final Charset DEFAULT_CHARSET = defaultCharset();

	public static ReaderConfig reader() {
		return new ReaderConfig();
	}
	public static class ReaderConfig {
		Object source;
		Charset charset;

		CodingErrorAction onMalformedInput;
		CodingErrorAction onUnmappableCharacter;

		private ReaderConfig() {}

		public ReaderConfig source(ReadableByteChannel source){ this.source=source;  return this; }
		public ReaderConfig source(InputStream source){ this.source=Channels.newChannel(source);  return this; }
		public ReaderConfig source(Path source){ 
			this.source= source;
			return this; 
		}
		public ReaderConfig source(File source){ 
			this.source= source.toPath();
			return this; 
		}
		public ReaderConfig charset(Charset charset){ this.charset=charset;  return this; }
		public ReaderConfig charset(String charset){ this.charset=Charset.forName(charset);  return this; }
		public ReaderConfig onMalformedInput(CodingErrorAction onMalformedInput){ this.onMalformedInput=onMalformedInput;  return this; }
		public ReaderConfig onUnmappableCharacter(CodingErrorAction onUnmappableCharacter){ this.onUnmappableCharacter=onUnmappableCharacter;  return this; }

		public StringBuilder read0() throws IOException {
			Objects.requireNonNull(source, "source not set");
			StringBuilder sb = (StringBuilder) apply(c -> getText0(c, this, new StringBuilder())); 
			return sb;
		}
		private <E> E apply(IOExceptionFunction<ReadableByteChannel, E> c) throws IOException {
			if(source instanceof ReadableByteChannel)
				return c.apply((ReadableByteChannel)source);

			try(FileChannel fc = FileChannel.open((Path)source, READ)) {
				return c.apply(fc);
			}
		}

		public String read(Path p) throws IOException {
			this.source = p;
			return read();
		}
		public String read() throws IOException {
			Objects.requireNonNull(source, "source not set");
			return apply(c -> getText(c, this));
		}
		private CharsetDecoder decoder() {
			return charset()
					.newDecoder()
					.onMalformedInput(onMalformedInput == null ? defaultOnMalformedInput() : onMalformedInput)
					.onUnmappableCharacter(onUnmappableCharacter == null ? defaultOnUnmappableCharacter() : onUnmappableCharacter);
		}
		public Charset charset() {
			return (charset == null ? DEFAULT_CHARSET : charset);
		}
	} 

	public static StringBuilder getText0(Path path, Charset charset) throws IOException {
		return reader().charset(charset).source(path).read0();
	}
	public static StringBuilder getText0(Path path, String charset) throws IOException {
		return getText0(path, Charset.forName(charset));
	}
	public static StringBuilder getText0(Path path) throws IOException {
		return getText0(path, defaultCharset());
	}

	public static String getText(Path path, Charset charset) throws IOException {
		return reader().charset(charset).read(path);
	}
	public static String getText(Path path, String charset) throws IOException {
		return getText(path, Charset.forName(charset));
	}
	public static String getText(Path path) throws IOException {
		return getText(path, defaultCharset());
	}

	public static String getText(ReadableByteChannel channel, ReaderConfig config) throws IOException {
		if(channel instanceof FileChannel) {
			FileChannel fc = (FileChannel) channel;
			long size = fc.size();
			if(size == 0)
				return "";
			
			return config.decoder().decode(fc.map(MapMode.READ_ONLY, fc.position(), size)).toString();
		}
		return getText0(channel, config, new StringBuilder()).toString();
	}
	public static StringBuilder getText0(ReadableByteChannel c, ReaderConfig config, StringBuilder sink) throws IOException {
		StringBuilder sb = new StringBuilder();
		StringIOUtils.read(BufferFiller.of(c), sink, config.decoder(), config.onUnmappableCharacter, config.onMalformedInput);
		return sb;
	}
}
