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

import sam.functions.IOExceptionBiConsumer;

import java.util.Objects;

public final class ObjectWriter {
	public static  WriterConfig writer() {
		return new WriterConfig();
	}
	
	public static class WriterConfig {
		Object target;
		
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
		public <E> void write(E object, IOExceptionBiConsumer<E, DataOutputStream> mapper) throws IOException {
			Objects.requireNonNull(object);
			Objects.requireNonNull(mapper);
			Objects.requireNonNull(target, "target not set");
			write0(object, this, mapper);
		}
		public <E> void writeList(Collection<E> list, IOExceptionBiConsumer<E, DataOutputStream> mapper) throws IOException {
			Objects.requireNonNull(target, "target not set");
			Objects.requireNonNull(list, "data cannot be null");
			Objects.requireNonNull(mapper, "mapper cannot be null");
			writeList0(list, this, mapper);
		}
		private WrapperW target() throws IOException {
			return new WrapperW(target);
		}
		public <K, V> void writeMap(Map<K, V> map, IOExceptionBiConsumer<DataOutputStream, K> keyWriter, IOExceptionBiConsumer<DataOutputStream, V> valueWriter) throws IOException {
			Objects.requireNonNull(map);
			Objects.requireNonNull(keyWriter);
			Objects.requireNonNull(valueWriter);
			
			Collection<Entry<K, V>> set = map.entrySet();
			writeList(set, (e, dis) -> {
				keyWriter.accept(dis, e.getKey());
				valueWriter.accept(dis, e.getValue());
			});
		}
	}
	
	private static class WrapperW implements AutoCloseable {
		Object target;
		OutputStream is;
		
		public WrapperW(Object target) {
			this.target = target;
		}
		public OutputStream get() throws IOException {
			if(target instanceof OutputStream)
				return (OutputStream)target;
			else 
				return is = Files.newOutputStream((Path)target, CREATE, WRITE, TRUNCATE_EXISTING);
		}
		@Override
		public void close() throws IOException {
			if(is != null) is.close();
		}
	}
	
    public static void write(Path path, Object object) throws  IOException{
        writer().target(path).write(object);
    }
    public static <E> void write(Path path, E data, IOExceptionBiConsumer<E, DataOutputStream> mapper) throws  IOException{
        new WriterConfig().target(path).write(data, mapper);
    }
    public static <E> void writeList(Path path, Collection<E> data, IOExceptionBiConsumer<E, DataOutputStream> mapper) throws  IOException{
        new WriterConfig().target(path).writeList(data, mapper);
    }
    public static <K,V> void writeMap(Path path, Map<K,V> data, IOExceptionBiConsumer<DataOutputStream, K> keyWriter, IOExceptionBiConsumer<DataOutputStream, V> valueWriter) throws  IOException{
        new WriterConfig().target(path).writeMap(data, keyWriter, valueWriter);
    }
    
    private static <E> void write0(E e, WriterConfig config, IOExceptionBiConsumer<E, DataOutputStream> mapper) throws  IOException {
        try(WrapperW w = config.target();
                DataOutputStream out = new DataOutputStream(w.get())) {
            mapper.accept(e, out);
        }
    }
    private static void write0(Object object, WriterConfig config) throws  IOException {
    	try(WrapperW w = config.target();
                ObjectOutputStream out = new ObjectOutputStream(w.get())) {
            out.writeObject(object);
        }
    }
    
    private static <E> void writeList0(Collection<E> data, WriterConfig config, IOExceptionBiConsumer<E, DataOutputStream> mapper) throws  IOException {
    	Objects.requireNonNull(data);
    	
    	try(WrapperW w = config.target();
                DataOutputStream out = new DataOutputStream(w.get())) {
        	out.writeInt(data.size());
        	
        	for (E e : data)
				mapper.accept(e, out);
        }
    }
    
}
