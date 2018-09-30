package sam.fileutils;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class FilesUtilsObjectNew {
	private static InputStream is(Path path) throws IOException {
		return Files.newInputStream(path, StandardOpenOption.READ);
	}
	private static OutputStream os(Path path) throws IOException {
		return Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}
    @SuppressWarnings("unchecked")
    public static <R> R read(Path path) throws ClassNotFoundException, IOException{
        return  (R) read0(is(path), false);
    }
    public static <R> R read(Path path, Class<R> cls) throws ClassNotFoundException, IOException{
        return  cls.cast(read0(is(path), false));
    }
    @SuppressWarnings("unchecked")
    public static <R> R readGzip(Path path) throws ClassNotFoundException, IOException{
        return  (R) read0(is(path), true);
    }
    public static <R> R readGzip(Path path, Class<R> cls) throws ClassNotFoundException, IOException{
        return  cls.cast(read0(is(path), true));
    }
    public static Object read0(InputStream source, boolean gzip) throws ClassNotFoundException, IOException {
        try(InputStream os2 = source;
        		InputStream os3 = !gzip ? os2 : new GZIPInputStream(os2);
                ObjectInputStream out = new ObjectInputStream(os3)) {
            return out.readObject();
        }
    }
    private static void check(Path path) throws IOException {
    	if(path == null)
            throw new NullPointerException("path is null");

        if(path.getNameCount() != 1 && Files.notExists(path.getParent()))
            Files.createDirectories(path.getParent());
    }
    public static void write(Object object, Path path) throws IOException{
    	check(path);
        write(object, os(path));
    }
    public static void write(Object object, OutputStream os) throws IOException {
    	write0(object, os, false);
	}
    public static void writeGzip(Object object, Path path) throws IOException{
    	check(path);
        writeGzip(object, os(path));
    }
    public static void writeGzip(Object object, OutputStream os) throws IOException {
    	write0(object, os, true);
	}
	private static void write0(Object object, OutputStream os, boolean gzip) throws IOException {
    	try(OutputStream os2 = os; 
    			OutputStream os3 = !gzip ? os2 : new GZIPOutputStream(os2);
                ObjectOutputStream out = new ObjectOutputStream(os3)) {
            out.writeObject(object);
            out.flush();
        }
    } 
	public static <E> List<E> readList(Path path, IOExceptionFunction<DataInputStream, E> mapper) throws IOException {
		return readList(is(path), mapper);
	}
	public static <E> List<E> readList(InputStream is, IOExceptionFunction<DataInputStream, E> mapper) throws IOException {
		try(InputStream is2 = is;
				DataInputStream dis = new DataInputStream(is2);) {
			int size = dis.readInt();
			List<E> list = new ArrayList<>();
			
			for (int i = 0; i < size; i++)
				list.add(mapper.apply(dis));
			
			return list;
		}
	}
	public static <E> void write(Path path, Collection<E> data, ObjectWriter<E> writer) throws IOException {
		write(os(path), data, writer);
	}
	public static void write(Path path, Collection<WriteableObject> data) throws IOException {
		write(os(path), data, (dos, e) -> e.write(dos));
	}
	public static <E> void write(OutputStream os, Collection<E> data, ObjectWriter<E> writer) throws IOException {
		Objects.requireNonNull(data);
		Objects.requireNonNull(os);
		Objects.requireNonNull(writer);
		
		try(OutputStream is2 = os;
				DataOutputStream dis = new DataOutputStream(is2);) {
			dis.writeInt(data.size());
			List<E> list = new ArrayList<>();
			
			for (E e : list) 
				writer.write(dis, e);
		}
	}
}
