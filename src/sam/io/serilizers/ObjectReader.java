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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntConsumer;

public final class ObjectReader {

	public static <E> ReaderConfig reader(boolean gzip) {
		return new ReaderConfig();
	}
	public static class ReaderConfig {
		Object source;

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
		public <E> E read(IOExceptionFunction<DataInputStream, E> mapper) throws IOException {
			Objects.requireNonNull(source, "source not set");
			return read0(this, mapper);
		}
		public void iterate(IOExceptionConsumer<DataInputStream> consumer) throws  IOException {
			Objects.requireNonNull(source, "source not set");
			iterate0(this, null, consumer);
		}
		@SuppressWarnings("rawtypes")
		public <E> void readList(Collection<E> sink, IOExceptionFunction<DataInputStream, E> mapper) throws IOException {
			Objects.requireNonNull(source, "source not set");
			iterate0(this, size -> {
				if(sink instanceof ArrayList)
					((ArrayList)sink).ensureCapacity(size);
			}, d -> sink.add(mapper.apply(d)));
		}
		public <K, V> void readMap(Map<K, V> sink, IOExceptionFunction<DataInputStream, K> keyReader, IOExceptionFunction<DataInputStream, V> valueReader) throws IOException {
			Objects.requireNonNull(source, "source not set");
			iterate0(this, null, dis -> sink.put(keyReader.apply(dis), valueReader.apply(dis))) ;
		}
		private Wrapper source() throws IOException {
			return new Wrapper(source);
		}
	} 
	private static class Wrapper implements AutoCloseable {
		Object source;
		InputStream is;
		public Wrapper(Object source) {
			this.source = source;
		}
		public InputStream get() throws IOException {
			if(source instanceof InputStream)
				return (InputStream)source;
			else 
				return is = Files.newInputStream((Path)source, StandardOpenOption.READ);
		}
		@Override
		public void close() throws IOException {
			if(is != null) is.close();
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
	public static <E> E read(Path path, IOExceptionFunction<DataInputStream, E> mapper) throws  IOException{
		return  new ReaderConfig().source(path).read(mapper);
	}
	public static <E> ArrayList<E> readList(Path path, IOExceptionFunction<DataInputStream, E> mapper) throws  IOException{
		ArrayList<E> list = new ArrayList<>();
		read(list,path, mapper);
		return list;
	}
	public static <E> void read(Collection<E> sink, Path path, IOExceptionFunction<DataInputStream, E> mapper) throws  IOException{
		new ReaderConfig().source(path).readList(sink, mapper);
	}
	public static <K, V> HashMap<K,V> readMap(Path path, IOExceptionFunction<DataInputStream, K> keyReader, IOExceptionFunction<DataInputStream, V> valueReader) throws  IOException{
		HashMap<K, V> map = new HashMap<K, V>();
		read(map, path, keyReader, valueReader);
		return map;
	}
	public static <K, V> void read(Map<K, V> sink, Path path, IOExceptionFunction<DataInputStream, K> keyReader, IOExceptionFunction<DataInputStream, V> valueReader) throws  IOException{
		new ReaderConfig().source(path).readMap(sink, keyReader, valueReader);
	}
	public static void iterate(Path path, IOExceptionConsumer<DataInputStream> consumer) throws  IOException{
		new ReaderConfig().source(path).iterate(consumer);
	}
	private static <E> E read0(ReaderConfig config, IOExceptionFunction<DataInputStream, E> mapper) throws  IOException {
		try(Wrapper os2 = config.source();
				DataInputStream out = new DataInputStream(os2.get())) {
			return mapper.apply(out);
		}
	}
	private static Object read0( ReaderConfig config) throws  IOException, ClassNotFoundException {
		try(Wrapper os2 = config.source();
				ObjectInputStream out = new ObjectInputStream(os2.get())) {
			return out.readObject();
		}
	}
	private static <E> void iterate0(ReaderConfig config, IntConsumer sizeConsumer, IOExceptionConsumer<DataInputStream> consumer) throws  IOException {
		Objects.requireNonNull(config);

		try(Wrapper os2 = config.source();
				DataInputStream in = new DataInputStream(os2.get())) {
			int size = in.readInt();
			if(sizeConsumer != null)
				sizeConsumer.accept(size);

			for (int i = 0; i < size; i++) 
				consumer.accept(in);
		}
	}

}
