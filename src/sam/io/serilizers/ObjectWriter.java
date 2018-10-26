package sam.io.serilizers;


import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

public final class ObjectWriter {
	public static final ObjectSerializer<Integer> INT_SERIALIZER = (dos, e) -> dos.writeInt(e);
	public static final ObjectSerializer<Long> LONG_SERIALIZER = (dos, e) -> dos.writeLong(e);
	
	public static  WriterConfig writer(boolean gzip) {
		return new WriterConfig(gzip);
	}
	public static class WriterConfig {
		Object target;
		final boolean gzip;

		private WriterConfig(boolean gzip) {
			this.gzip = gzip;
		}
		public WriterConfig target(OutputStream target){ this.target=target;  return this; }
		public WriterConfig target(Path target){ 
			this.target= target;
			return this; 
		}
		public WriterConfig target(File target){ 
			this.target= target.toPath();
			return this; 
		}
		public void write(Object object) throws IOException {
			Objects.requireNonNull(target, "target not set");
			write0(object, this);
		}
		public <E> void write(E object, ObjectSerializer< E> mapper) throws IOException {
			Objects.requireNonNull(object);
			Objects.requireNonNull(mapper);
			Objects.requireNonNull(target, "target not set");
			write0(object, this, mapper);
		}
		public <E> void writeList(Collection<E> list, ObjectSerializer< E> mapper) throws IOException {
			Objects.requireNonNull(target, "target not set");
			Objects.requireNonNull(list, "data cannot be null");
			Objects.requireNonNull(mapper, "mapper cannot be null");
			writeList0(list, this, mapper);
		}
		private OutputStream target() throws IOException {
			if(target instanceof OutputStream)
				return (OutputStream)target;
			else
				return Files.newOutputStream((Path)target, CREATE, WRITE, TRUNCATE_EXISTING);
		}
		public <K,V> void writeMap(Map<K, V> data, ObjectSerializer<K> keyWriter, ObjectSerializer<V> valueWriter) throws IOException {
			Objects.requireNonNull(data);
			Objects.requireNonNull(keyWriter);
			Objects.requireNonNull(valueWriter);
			
			Collection<Entry<K, V>> set = data.entrySet();
			writeList(set, (dos, e) -> {
				keyWriter.write(dos, e.getKey());
				valueWriter.write(dos, e.getValue());
			});
		}
	} 
    public static void write(Path path, Object object) throws  IOException{
        writer(false).target(path).write(object);
    }
    public static void writeGzip(Path path, Object object) throws  IOException{
         writer(true).target(path).write(object);
    }
    public static <E> void writeGzip(Path path, E data, ObjectSerializer<E> mapper) throws  IOException{
        new WriterConfig(true).target(path).write(data, mapper);
    }
    public static <E> void write(Path path, E data, ObjectSerializer<E> mapper) throws  IOException{
        new WriterConfig(false).target(path).write(data, mapper);
    }
    public static <E> void writeGzip(Path path, Collection<E> data, ObjectSerializer<E> mapper) throws  IOException{
        new WriterConfig(true).target(path).writeList(data, mapper);
    }
    public static <E> void writeList(Path path, Collection<E> data, ObjectSerializer<E> mapper) throws  IOException{
        new WriterConfig(false).target(path).writeList(data, mapper);
    }
    public static <K,V> void writeMap(Path path, Map<K,V> data, ObjectSerializer<K> keyWriter, ObjectSerializer<V> valueWriter) throws  IOException{
        new WriterConfig(false).target(path).writeMap(data, keyWriter, valueWriter);
    }
    private static <E> void write0(E e, WriterConfig config, ObjectSerializer<E> mapper) throws  IOException {
        try(OutputStream os2 = config.target();
        		OutputStream os3 = config.gzip ? new GZIPOutputStream(os2) : os2;
                DataOutputStream out = new DataOutputStream(os3)) {
            mapper.write(out, e);
        }
    }
    private static void write0(Object object, WriterConfig config) throws  IOException {
        try(OutputStream os2 = config.target();
        		OutputStream os3 = config.gzip ? new GZIPOutputStream(os2) : os2;
                ObjectOutputStream out = new ObjectOutputStream(os3)) {
            out.writeObject(object);
        }
    }
    
    private static <E> void writeList0(Collection<E> data, WriterConfig config, ObjectSerializer<E> mapper) throws  IOException {
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
