package sam.io.infile;

import static java.nio.charset.CodingErrorAction.REPORT;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.junit.platform.commons.JUnitException;

import com.thedeanda.lorem.LoremIpsum;

import sam.functions.IOExceptionBiConsumer;
import sam.myutils.MyUtilsBytes;


public class TextInFileTestUtils {
	protected static int DEFAULT_SAMPLE_SIZE = Optional.ofNullable(System.getProperty("DEFAULT_SAMPLE_SIZE")).map(Integer::parseInt).orElse(100);
	protected static final Logger LOGGER = Logger.getLogger(TextInFileTestUtils.class.getName());

	public static class Appen implements Appendable {
		final StringBuilder sb;
		int loops;

		public Appen() {
			this.sb = new StringBuilder();
		}
		public Appen(int size) {
			this.sb = new StringBuilder(size);
		}
		@Override
		public Appendable append(CharSequence csq, int start, int end) throws IOException {
			loops++;
			return sb.append(csq, start, end);
		}
		@Override
		public Appendable append(char c) throws IOException {
			return sb.append(c);
		}
		@Override
		public Appendable append(CharSequence csq) throws IOException {
			loops++;
			return sb.append(csq);
		}
		@Override
		public String toString() {
			return sb.toString();
		}
	}

	public static class Temp implements AutoCloseable {
		final LoremIpsum random = LoremIpsum.getInstance();
		final CharsetEncoder encoder = UTF_8.newEncoder();
		final CharsetDecoder decoder = UTF_8.newDecoder();
		final TextInFile file;

		public Temp(Path path, boolean b) throws IOException {
			file = new TextInFile(path, b);
		}

		public DataMeta write(String s) throws IOException {
			return write(s, null);
		}
		public DataMeta write(String s, ByteBuffer buffer) throws IOException {
			return file.write(s, encoder, buffer);
		}
		@Override
		public void close() throws IOException {
			file.close();
		}

		public long acutualSize() throws IOException {
			return file.acutualSize();
		}

		public long size() throws IOException {
			return file.size();
		}
		public void readText(DataMeta meta, ByteBuffer buffer, CharBuffer chars, Appendable sink) throws IOException {
			file.readText(meta, buffer, chars, decoder, sink);
		}

		public String readText(DataMeta meta) throws IOException {
			StringBuilder sb = new StringBuilder();
			file.readText(meta, null, null, decoder, sb);
			return sb.toString();
		}
		public void readText(DataMeta d, StringBuilder sb, ByteBuffer buffer, CharBuffer chars) throws IOException {
			file.readText(d, buffer, chars, decoder, sb);
		}

		public IdentityHashMap<DataMeta, DataMeta> transferTo(List<DataMeta> metas, Temp file) throws IOException {
			return this.file.transferTo(metas, file.file);
		}

		public long transferTo(List<DataMeta> metas, FileChannel target) throws IOException {
			return file.transferTo(metas, target);
		}

	}

	protected void logMethod() {
		LOGGER.info("\n--------------------------\n"+Thread.currentThread().getStackTrace()[2]+"\n--------------------------\n");
	}
	protected void logMethod(String msg) {
		LOGGER.info("\n--------------------------\n"+msg+"\n"+Thread.currentThread().getStackTrace()[2]+"\n--------------------------\n");
	}

	protected IdentityHashMap<DataMeta, String> write(int minParagraphSize, int maxParagraphSize, int sampleSize, Temp file) throws IOException {
		long pos = 0;
		final IdentityHashMap<DataMeta, String> map = new IdentityHashMap<>();
		final ByteBuffer buffer = ByteBuffer.allocate(8124);

		for (int i = 0; i < sampleSize; i++) {
			String s = file.random.getParagraphs(minParagraphSize, maxParagraphSize);
			DataMeta m = file.write(s, buffer);
			map.put(new DM(i,m), s);

			assertEquals(pos, m.position, () -> m.toString());
			pos = m.position + m.size;
		}

		return map;
	}

	private class DM extends DataMeta {
		public int index;

		public DM(int index, DataMeta d) {
			super(d.position, d.size);
			this.index = index;
		}
		@Override
		public String toString() {
			return "DM(i: "+index+", p:"+position+", s:"+MyUtilsBytes.bytesToHumanReadableUnits(size, false)+")";
		}
	}

	protected List<DataMeta> shuffled(IdentityHashMap<DataMeta, String> map) {
		List<DataMeta> list = new ArrayList<>(map.keySet());
		Collections.shuffle(list);
		return list;
	}
	protected void common(int minParagraphSize, int maxParagraphSize, IOExceptionBiConsumer<IdentityHashMap<DataMeta, String>, Temp> consumer) throws IOException {
		if(DEFAULT_SAMPLE_SIZE < 10)
			throw new JUnitException("sampleSize("+DEFAULT_SAMPLE_SIZE+") < 10");

		common(minParagraphSize, maxParagraphSize, DEFAULT_SAMPLE_SIZE, consumer);
	}
	protected void common(int minParagraphSize, int maxParagraphSize, int sampleSize, IOExceptionBiConsumer<IdentityHashMap<DataMeta, String>, Temp> consumer) throws IOException {
		if(sampleSize < 100)
			LOGGER.warning("very small sampleSize: "+sampleSize);
		if(sampleSize < 1)
			throw new JUnitException("sampleSize("+sampleSize+")");

		Path path = Files.createTempFile("TEST-INFILE", null);
		try(Temp file = new Temp(path, false)) {

			IdentityHashMap<DataMeta, String> map = write(minParagraphSize, maxParagraphSize, sampleSize, file);

			assertEquals(file.acutualSize(), file.size());

			System.out.println("minParagraphSize : "+minParagraphSize+", maxParagraphSize: "+maxParagraphSize+"\nsize: "+MyUtilsBytes.bytesToHumanReadableUnits(file.size(), false));
			consumer.accept(map, file);
		} finally {
			if(path != null) {
				Files.deleteIfExists(path);
			}
		}
	}

	protected void equalAssert(CharSequence expected, Appen actual) {
		assertEquals(expected.length(), actual.sb.length());
		assertEquals(CharBuffer.wrap(expected), CharBuffer.wrap(actual.sb));
	}
}
