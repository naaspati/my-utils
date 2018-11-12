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
			iterate0(this, consumer);
		}
		public <E> List<E> readList(IOExceptionFunction<DataInputStream, E> mapper) throws IOException {
			Objects.requireNonNull(source, "source not set");
			return iterate0(this, mapper);
		}
		public <K, V> Map<K,V> readMap(IOExceptionFunction<DataInputStream, K> keyReader, IOExceptionFunction<DataInputStream, V> valueReader) throws IOException {
			Objects.requireNonNull(source, "source not set");

			Map<K, V> map = new HashMap<>();
			IOExceptionConsumer<DataInputStream> cons = dis -> map.put(keyReader.apply(dis), valueReader.apply(dis));

			iterate0(this, cons) ;
			return map;
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
	public static <E> List<E> readList(Path path, IOExceptionFunction<DataInputStream, E> mapper) throws  IOException{
		return  new ReaderConfig().source(path).readList(mapper);
	}
	public static <K, V> Map<K,V> readMap(Path path, IOExceptionFunction<DataInputStream, K> keyReader, IOExceptionFunction<DataInputStream, V> valueReader) throws  IOException{
		return  new ReaderConfig().source(path).readMap(keyReader, valueReader);
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

	@SuppressWarnings("unchecked")
	private static <E> List<E> iterate0(ReaderConfig config, Object action) throws  IOException {
		Objects.requireNonNull(action);
		Objects.requireNonNull(config);

		IOExceptionFunction<DataInputStream, E> func = null;
		IOExceptionConsumer<DataInputStream> consumer = null;

		if(action instanceof IOExceptionFunction)
			func = (IOExceptionFunction<DataInputStream, E>) action;
		else if(action instanceof IOExceptionConsumer)
			consumer = (IOExceptionConsumer<DataInputStream>) action;
		else 
			throw new IllegalArgumentException("unknown action: "+action);

		try(Wrapper os2 = config.source();
				DataInputStream in = new DataInputStream(os2.get())) {
			int size = in.readInt();
			ArrayList<E> list = func == null ? null : new ArrayList<>(size);

			for (int i = 0; i < size; i++) {
				if(consumer != null)
					consumer.accept(in);
				else if(func != null)
					list.add(func.apply(in));
			}
			return list;
		}
	}

}
