package sam.io.serilizers;

import static java.nio.charset.CodingErrorAction.REPORT;
import static java.nio.file.StandardOpenOption.READ;
import static sam.io.BufferSize.DEFAULT_BUFFER_SIZE;
import static sam.io.DefaultCharset.DEFAULT_CHARSET;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Logger;

import sam.logging.MyLoggerFactory;

public class StringReader2 {
	private static final Logger LOGGER = MyLoggerFactory.logger(StringReader2.class.getSimpleName());
	
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
			return getText0(this);
		}
		public String read(Path p) throws IOException {
			this.source = p;
			return read();
		}
		public String read() throws IOException {
			Objects.requireNonNull(source, "source not set");
			return getText(this);
		}
		private CharsetDecoder decoder() {
			return charset()
			.newDecoder()
			.onMalformedInput(onMalformedInput == null ? REPORT : onMalformedInput)
			.onUnmappableCharacter(onUnmappableCharacter == null ? REPORT : onUnmappableCharacter);
		}
		public ReadableByteChannel channel() throws IOException {
			if(source instanceof ReadableByteChannel)
				return (ReadableByteChannel)source;
			
			return FileChannel.open((Path)source, READ);
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
		return getText0(path, DEFAULT_CHARSET);
	}
	
	public static String getText(Path path, Charset charset) throws IOException {
		return reader().charset(charset).read(path);
	}
	public static String getText(Path path, String charset) throws IOException {
		return getText(path, Charset.forName(charset));
	}
	public static String getText(Path path) throws IOException {
		return getText(path, DEFAULT_CHARSET);
	}
	
	public static String getText(ReaderConfig config) throws IOException {
		CharsetDecoder decoder = config.decoder();

		ReadableByteChannel channel2 = config.channel();
		if(channel2 instanceof FileChannel && ((FileChannel)channel2).size() == 0)
			return "";
			
		long[] buffersize = buffersize(channel2);
		ByteBuffer bytes = ByteBuffer.allocate((int)buffersize[1]);
		String result = null;

		int loops = 0;

		try(ReadableByteChannel channel = channel2;){
			while(true) {
				int n = channel.read(bytes);
				bytes.flip();

				if(n == -1) break;
				loops++;

				CharBuffer cb = decoder.decode(bytes);
				if(result == null)
					result = cb.toString();
				else
					result = concat(result, cb);
				bytes.clear();
			}
		}

		int loops2 = loops;
		LOGGER.fine(() -> "READ { charset:"+config.charset()+", fileSize:"+buffersize[0]+", ByteBuffer.capacity:"+bytes.capacity()+", loopCount:"+loops2+"}");
		return result;
	}
	private static String concat(String result, CharBuffer cb) {
		if(result.length() == 0) return cb.toString();
		if(cb.length() == 0) return result;
		
		char[] chars = new char[result.length() + cb.length()];
		int n = 0;
		for (int i = 0; i < result.length(); i++) 
			chars[n++] = result.charAt(i);
		
		for (int i = 0; i < cb.length(); i++) 
			chars[n++] = cb.charAt(i);
		
		return new String(chars);
	}
	public static StringBuilder getText0(ReaderConfig config) throws IOException {
		CharsetDecoder decoder = config.decoder();

		double d = decoder.averageCharsPerByte();
		int bytesPer = (int) Math.round(d);
		ReadableByteChannel channel2 = config.channel();
		if(channel2 instanceof FileChannel && ((FileChannel)channel2).size() == 0)
			return new StringBuilder();
		
		long[] bs = buffersize(channel2);
		int buffersize = (int) bs[1];

		CharBuffer chars = CharBuffer.allocate(buffersize/bytesPer > 100 ? 100 : buffersize/bytesPer);
		ByteBuffer bytes = ByteBuffer.allocate(buffersize);
		StringBuilder sb = new StringBuilder((int) (bs[0]/bytesPer)+10);
		int startCapacity = sb.capacity();

		int bytesloops = 0;
		int charloops = 0;

		try(ReadableByteChannel channel = channel2) {
			while(true) {
				bytesloops++;

				int n = channel.read(bytes);
				bytes.flip();

				charloops += read(config,decoder, chars, bytes, sb, n);

				if(n == -1) {
					while(true) {
						CoderResult c = decoder.flush(chars);
						checkResult(config,c);
						append(sb, chars);

						if(c.isUnderflow()) break;
					}
					break;
				}
				if(bytes.hasRemaining())
					bytes.compact();
				else
					bytes.clear();
			}
		}

		int loops2 = bytesloops;
		int loops3 = charloops;
		LOGGER.fine(() -> "READ { charset:"+config.charset()+", fileSize:"+bs[0]+", Stringbuilder.length:"+sb.length()+", Stringbuilder.capacity("+startCapacity+"->"+sb.capacity()+ "), ByteBuffer.capacity:"+bytes.capacity()+", averageBytesPerChar: "+d+", CharBuffer.capacity: "+chars.capacity()+", byteLoopCount:"+loops2+", charLoopCount:"+loops3+"}");
		return sb;
	}
	private static long[] buffersize(ReadableByteChannel channel2) throws IOException {
		long size, buffersize;
		
		if(channel2 instanceof FileChannel) {
			size = ((FileChannel)channel2).size();
			buffersize = (int)(size < DEFAULT_BUFFER_SIZE ? size : DEFAULT_BUFFER_SIZE);
		} else {
			size = -1;
			buffersize = DEFAULT_BUFFER_SIZE;
		}
		if(buffersize < 20)
			buffersize = 20;
		
		return new long[] {size, buffersize};
	}
	private static int read(ReaderConfig w, CharsetDecoder decoder, CharBuffer chars, ByteBuffer bytes, StringBuilder sb, int n) throws CharacterCodingException {
		int cn = 0;
		while(true) {
			cn++;
			CoderResult c  = decoder.decode(bytes, chars, n == -1);
			checkResult(w, c);

			append(sb, chars);
			if(c.isUnderflow()) break;
			c  = decoder.decode(bytes, chars, n == -1);
		}
		return cn;
	}
	private static void append(StringBuilder sb, CharBuffer chars) {
		chars.flip();
		sb.append(chars);
		chars.clear();
	}
	private static void checkResult(ReaderConfig w, CoderResult c) throws CharacterCodingException {
		if((c.isUnmappable() && w.onUnmappableCharacter == CodingErrorAction.REPORT) || (c.isMalformed() && w.onMalformedInput == CodingErrorAction.REPORT))
			c.throwException();
	}

}
