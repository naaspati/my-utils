package sam.fileutils;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sam.io.InputStreamSource;

public class ReadWriteObject {
    @SuppressWarnings("unchecked")
    public <R> R readObjectFromFile(Path path) throws ClassNotFoundException, IOException{
        return  (R) read(new InputStreamSource(path));
    }
    @SuppressWarnings("unchecked")
    public <R> R readObjectFromFile(Path path, Class<R> cls) throws ClassNotFoundException, IOException{
        return  (R) read(new InputStreamSource(path));
    }
    public Object read(InputStreamSource source) throws ClassNotFoundException, IOException {
        try(InputStream os = source.getInputStream();
                GZIPInputStream gos = new GZIPInputStream(os);
                ObjectInputStream out = new ObjectInputStream(gos)) {
            return out.readObject();
        }
    }
    public void writeObjectToFile(Object object, Path path) throws IOException{

        if(path == null)
            throw new NullPointerException("path is null");

        if(path.getNameCount() != 1 && Files.notExists(path.getParent()))
            Files.createDirectories(path.getParent());

        try(OutputStream os = Files.newOutputStream(path, StandardOpenOption.CREATE,  StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
                GZIPOutputStream gos = new GZIPOutputStream(os);
                ObjectOutputStream out = new ObjectOutputStream(gos)) {
            out.writeObject(object);
            out.flush();
        }
    }

}
