package sam.io.serilizers;


import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public final class ObjectReader {
	
	public static <E> ReaderConfig reader(boolean gzip) {
		return new ReaderConfig(gzip);
	}
	public static class ReaderConfig {
		Object source;
		final boolean gzip;

		private ReaderConfig(boolean gzip) {
			this.gzip = gzip;
		}
		public ReaderConfig source(InputStream source){ this.source=source;  return this; }
		public ReaderConfig source(Path source){ 
			this.source= source;
			return this; 
		}
		public ReaderConfig source(File source){ 
			this.source= source.toPath();
			return this; 
		}
		@SuppressWarnings("unchecked")
		public <E> E read() throws IOException, ClassNotFoundException {
			Objects.requireNonNull(source, "source not set");
			return (E)read0(this);
		}
		public <E> E read(OneObjectReader< E> mapper) throws IOException {
			Objects.requireNonNull(source, "source not set");
			return read0(this, mapper);
		}
		public void iterate(IOExceptionConsumer<DataInputStream> consumer) throws  IOException {
			Objects.requireNonNull(source, "source not set");
			iterate0(this, dis -> {consumer.accept(dis); return null;}, true);
		}
		public <E> List<E> readList(OneObjectReader< E> mapper) throws IOException {
			Objects.requireNonNull(source, "source not set");
			return iterate0(this, mapper, false);
		}
		public <K, V> Map<K,V> readMap(OneObjectReader< K> keyReader, OneObjectReader< V> valueReader) throws IOException {
			Objects.requireNonNull(source, "source not set");
			
			Map<K, V> map = new HashMap<>();
			
			iterate0(this, dis -> {
				map.put(keyReader.read(dis), valueReader.read(dis));
				return null;
			}, true) ;
			return map;
		}
		private InputStream source() throws IOException {
			if(source instanceof InputStream)
				return (InputStream)source;
			else
				return Files.newInputStream((Path)source, StandardOpenOption.READ);
		}
	} 
	@SuppressWarnings("unchecked")
	public static <R> R read(Path path) throws  IOException, ClassNotFoundException{
		return  (R) reader(false).source(path).read();
	}
	public static <R> R read(Path path, Class<R> cls) throws  IOException, ClassNotFoundException{
		return  read(path);
	}
	@SuppressWarnings("unchecked")
	public static <R> R readGzip(Path path) throws  IOException, ClassNotFoundException{
		return  (R) reader(true).source(path).read();
	}
	public static <R> R readGzip(Path path, Class<R> cls) throws  IOException, ClassNotFoundException{
		return  read(path);
	}

	public static <E> E readGzip(Path path, OneObjectReader< E> mapper) throws  IOException{
		return  new ReaderConfig(true).source(path).read(mapper);
	}
	public static <E> E read(Path path, OneObjectReader< E> mapper) throws  IOException{
		return  new ReaderConfig(false).source(path).read(mapper);
	}
	public static <E> List<E> readListGzip(Path path, OneObjectReader< E> mapper) throws  IOException{
		return  new ReaderConfig(true).source(path).readList(mapper);
	}
	public static <E> List<E> readList(Path path, OneObjectReader< E> mapper) throws  IOException{
		return  new ReaderConfig(false).source(path).readList(mapper);
	}
	public static <K, V> Map<K,V> readMap(Path path, OneObjectReader< K> keyReader, OneObjectReader< V> valueReader) throws  IOException{
		return  new ReaderConfig(false).source(path).readMap(keyReader, valueReader);
	}
	public static void iterateGzip(Path path, IOExceptionConsumer<DataInputStream> consumer) throws  IOException{
		new ReaderConfig(true).source(path).iterate(consumer);
	}
	public static void iterate(Path path, IOExceptionConsumer<DataInputStream> consumer) throws  IOException{
		new ReaderConfig(false).source(path).iterate(consumer);
	}
	private static <E> E read0(ReaderConfig config, OneObjectReader< E> mapper) throws  IOException {
		try(InputStream os2 = config.source();
				InputStream os3 = config.gzip ? new GZIPInputStream(os2) : os2;
				DataInputStream out = new DataInputStream(os3)) {
			return mapper.read(out);
		}
	}
	private static Object read0( ReaderConfig config) throws  IOException, ClassNotFoundException {
		try(InputStream os2 = config.source();
				InputStream os3 = config.gzip ? new GZIPInputStream(os2) : os2;
				ObjectInputStream out = new ObjectInputStream(os3)) {
			return out.readObject();
		}
	}

	private static <E> List<E> iterate0( ReaderConfig config, OneObjectReader< E> mapper, boolean iterateOnly) throws  IOException {
		try(InputStream in2 = config.source();
				InputStream in3 = config.gzip ? new GZIPInputStream(in2) : in2;
				DataInputStream in = new DataInputStream(in3)) {
			int size = in.readInt();
			ArrayList<E> list = iterateOnly ? null : new ArrayList<>(size);

			for (int i = 0; i < size; i++) {
				if(iterateOnly)
					mapper.read(in);
				else
					list.add(mapper.read(in));
			}
			return list;
		}
	}

}
