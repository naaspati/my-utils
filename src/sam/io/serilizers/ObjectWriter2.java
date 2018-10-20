package sam.io.serilizers;


import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

public final class ObjectWriter2 {
	public static <E> WriterConfig<E> writer(boolean gzip) {
		return new WriterConfig<E>(gzip);
	}
	public static <E> WriterConfig<E> writer(Class<E> cls, boolean gzip) {
		return new WriterConfig<E>(gzip);
	}
	public static class WriterConfig<E> {
		Object target;
		final boolean gzip;

		private WriterConfig(boolean gzip) {
			this.gzip = gzip;
		}
		public WriterConfig<E> target(OutputStream target){ this.target=target;  return this; }
		public WriterConfig<E> target(Path target){ 
			this.target= target;
			return this; 
		}
		public WriterConfig<E> target(File target){ 
			this.target= target.toPath();
			return this; 
		}
		public void write(Object object) throws IOException, ClassNotFoundException {
			Objects.requireNonNull(target, "target not set");
			write0(object, this);
		}
		public void write(E object, ObjectSerializer< E> mapper) throws IOException, ClassNotFoundException {
			Objects.requireNonNull(target, "target not set");
			write0(object, this, mapper);
		}
		public void write(Collection<E> list, ObjectSerializer< E> mapper) throws IOException, ClassNotFoundException {
			Objects.requireNonNull(target, "target not set");
			writeList0(list, this, mapper);
		}
		private OutputStream target() throws IOException {
			if(target instanceof OutputStream)
				return (OutputStream)target;
			else
				Files.newOutputStream((Path)target, StandardOpenOption.READ);
			return null;
		}
	} 
    public static void write(Path path, Object object) throws ClassNotFoundException, IOException{
        writer(false).target(path).write(object);
    }
    public static void writeGzip(Path path, Object object) throws ClassNotFoundException, IOException{
         writer(true).target(path).write(object);
    }
    public static <E> void writeGzip(Path path, E data, ObjectSerializer<E> mapper) throws ClassNotFoundException, IOException{
        new WriterConfig<E>(true).target(path).write(data, mapper);
    }
    public static <E> void write(Path path, E data, ObjectSerializer<E> mapper) throws ClassNotFoundException, IOException{
        new WriterConfig<E>(false).target(path).write(data, mapper);
    }
    public static <E> void writeGzip(Path path, Collection<E> data, ObjectSerializer<E> mapper) throws ClassNotFoundException, IOException{
        new WriterConfig<E>(true).target(path).write(data, mapper);
    }
    public static <E> void write(Path path, Collection<E> data, ObjectSerializer<E> mapper) throws ClassNotFoundException, IOException{
        new WriterConfig<E>(false).target(path).write(data, mapper);
    }
    private static <E> void write0(E e, WriterConfig<E> config, ObjectSerializer<E> mapper) throws ClassNotFoundException, IOException {
        try(OutputStream os2 = config.target();
        		OutputStream os3 = config.gzip ? new GZIPOutputStream(os2) : os2;
                DataOutputStream out = new DataOutputStream(os3)) {
            mapper.write(out, e);
        }
    }
    private static void write0(Object object, @SuppressWarnings("rawtypes") WriterConfig config) throws ClassNotFoundException, IOException {
        try(OutputStream os2 = config.target();
        		OutputStream os3 = config.gzip ? new GZIPOutputStream(os2) : os2;
                ObjectOutputStream out = new ObjectOutputStream(os3)) {
            out.writeObject(object);
        }
    }
    private static <E> void writeList0(Collection<E> data, WriterConfig<E> config, ObjectSerializer<E> mapper) throws ClassNotFoundException, IOException {
    	Objects.requireNonNull(data);
    	
        try(OutputStream in2 = config.target();
        		OutputStream in3 = config.gzip ? new GZIPOutputStream(in2) : in2;
                DataOutputStream in = new DataOutputStream(in3)) {
        	in.writeInt(data.size());
        	
        	for (E e : data) {
				mapper.write(in, e);
			}
        }
    }
    
}
