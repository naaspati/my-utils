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
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public final class ObjectReader2 {
	public static <E> ReaderConfig<E> reader(boolean gzip) {
		return new ReaderConfig<E>(gzip);
	}
	public static <E> ReaderConfig<E> reader(Class<E> cls, boolean gzip) {
		return new ReaderConfig<E>(gzip);
	}
	public static class ReaderConfig<E> {
		Object source;
		final boolean gzip;

		private ReaderConfig(boolean gzip) {
			this.gzip = gzip;
		}
		public ReaderConfig<E> source(InputStream source){ this.source=source;  return this; }
		public ReaderConfig<E> source(Path source){ 
			this.source= source;
			return this; 
		}
		public ReaderConfig<E> source(File source){ 
			this.source= source.toPath();
			return this; 
		}
		@SuppressWarnings("unchecked")
		public E read() throws IOException, ClassNotFoundException {
			Objects.requireNonNull(source, "source not set");
			return (E)read0(this);
		}
		public E read(IOExceptionFunction<DataInputStream, E> mapper) throws IOException, ClassNotFoundException {
			Objects.requireNonNull(source, "source not set");
			return read0(this, mapper);
		}
		public List<E> readList(IOExceptionFunction<DataInputStream, E> mapper) throws IOException, ClassNotFoundException {
			Objects.requireNonNull(source, "source not set");
			return readList0(this, mapper);
		}
		private InputStream source() throws IOException {
			if(source instanceof InputStream)
				return (InputStream)source;
			else
				Files.newInputStream((Path)source, StandardOpenOption.READ);
			return null;
		}
	} 
    @SuppressWarnings("unchecked")
    public static <R> R read(Path path) throws ClassNotFoundException, IOException{
        return  (R) reader(false).source(path).read();
    }
    public static <R> R read(Path path, Class<R> cls) throws ClassNotFoundException, IOException{
        return  read(path);
    }
    @SuppressWarnings("unchecked")
    public static <R> R readGzip(Path path) throws ClassNotFoundException, IOException{
        return  (R) reader(true).source(path).read();
    }
    public static <R> R readGzip(Path path, Class<R> cls) throws ClassNotFoundException, IOException{
        return  read(path);
    }
    
    public static <E> E readGzip(Path path, IOExceptionFunction<DataInputStream, E> mapper) throws ClassNotFoundException, IOException{
        return  new ReaderConfig<E>(true).source(path).read(mapper);
    }
    public static <E> E read(Path path, IOExceptionFunction<DataInputStream, E> mapper) throws ClassNotFoundException, IOException{
        return  new ReaderConfig<E>(false).source(path).read(mapper);
    }
    
    private static <E> E read0(ReaderConfig<E> config, IOExceptionFunction<DataInputStream, E> mapper) throws ClassNotFoundException, IOException {
        try(InputStream os2 = config.source();
        		InputStream os3 = config.gzip ? new GZIPInputStream(os2) : os2;
                DataInputStream out = new DataInputStream(os3)) {
            return mapper.apply(out);
        }
    }
    private static Object read0(@SuppressWarnings("rawtypes") ReaderConfig config) throws ClassNotFoundException, IOException {
        try(InputStream os2 = config.source();
        		InputStream os3 = config.gzip ? new GZIPInputStream(os2) : os2;
                ObjectInputStream out = new ObjectInputStream(os3)) {
            return out.readObject();
        }
    }
    private static <E> List<E> readList0(@SuppressWarnings("rawtypes") ReaderConfig config, IOExceptionFunction<DataInputStream, E> mapper) throws ClassNotFoundException, IOException {
        try(InputStream in2 = config.source();
        		InputStream in3 = config.gzip ? new GZIPInputStream(in2) : in2;
                DataInputStream in = new DataInputStream(in3)) {
        	int size = in.readInt();
        	ArrayList<E> list = new ArrayList<>(size);
        	
        	for (int i = 0; i < size; i++)
				list.add(mapper.apply(in));
        	
            return list;
        }
    }
    
}
