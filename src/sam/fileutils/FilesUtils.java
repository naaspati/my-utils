package sam.fileutils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;
import java.util.function.Function;

import sam.fileutils.FilesWalker.FileWalkResult;


public final class FilesUtils {
    public static final double VERSION = 1.21;
   private final static int BUFFER_SIZE;
    static {
        String s = System.getenv("FILES_UTILS_BUFFER_SIZE");
        s = s != null ? s : System.getProperty("FILES_UTILS_BUFFER_SIZE", "8192");

        BUFFER_SIZE = Integer.parseInt(s);
        if(BUFFER_SIZE < 8192)
            throw new RuntimeException("minimum buffer size can be: 8192, but given "+BUFFER_SIZE);
    }

    private FilesUtils() {}

    /**
     * <pre>
     * return a HashMap(String -> Arraylist(Path))
     * 
     * HashMap hash two keys "DIR" and "FILE"
     * 
     * "FILE" -> ArrayList(Path if Path points to a regular File)
     * "DIR" -> ArrayList(Path if Path points to a Directory)
     * </pre>
     * @param path
     * @return
     * @throws IOException
     */
    public static FileWalkResult listDirsFiles(Path path) throws IOException {
        return FilesWalker.listDirsFiles(path);
    }
    /**
     * Reads all bytes from an input stream and writes them to an output stream.
     * and return number of bytes read 
     */
    public static long pipe(InputStream is, OutputStream os) throws IOException  {
        long nread = 0L;//number of bytes read
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = is.read(buf)) > 0) {
            os.write(buf, 0, n);
            nread += n;
        }
        return nread;
    }

    private static FileOpener fileOpener0;
    private static FileOpener fileOpener() {
        if(fileOpener0 == null)
            fileOpener0 = new FileOpener();
        return fileOpener0;
    }
    
    public static void openFile(File file) throws IOException{ fileOpener().openFile(file); }
    public static void openFileNoError(File file){ fileOpener().openFileNoError(file); }
    public static void openFileLocationInExplorer(File file) throws IOException { fileOpener().openFileLocationInExplorer(file); }
    public static void openFileLocationInExplorerNoError(File file) { fileOpener().openFileLocationInExplorerNoError(file); }

    private static ReadWriteObject readWriteObject0;
    private static ReadWriteObject readWriteObject() {
        if(readWriteObject0 == null)
            readWriteObject0 = new ReadWriteObject(); 
        return readWriteObject0;
    }
    
    /**
     * read only one object from file
     * @return
     * @throws IOException 
     * @throws ClassNotFoundException
     *  
     */
    public static <R> R readObjectFromFile(Path path) throws ClassNotFoundException, IOException{ return readWriteObject().readObjectFromFile(path); }
    public static <R> R readObjectFromFile(Path path, Class<R> clazz) throws ClassNotFoundException, IOException{ return readWriteObject().readObjectFromFile(path, clazz); }
    public static void writeObjectToFile(Object object, Path path) throws IOException{ readWriteObject().writeObjectToFile(object, path); }
    
    public static Path findPathNotExists(Path path) {
        if(Files.notExists(path))
            return path;

        final String filename = path.getFileName().toString();
        int index = filename.lastIndexOf('.');
        String name, ext = null;
        if(index > 0) {
            ext = filename.substring(index);
            if(ext.indexOf(' ') >= 0) {
                name = filename;
                ext = "";
            } else 
                name = filename.substring(0, index);
        } else {
            name = filename;
            ext = "";            
        }

        index = 1;
        path  = path.resolveSibling(name+"_1"+ext);
        while(Files.exists(path))
            path  = path.resolveSibling(name+"_"+(++index)+ext);

        return path;
    }

    public static void deleteDir(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir2, IOException exc) throws IOException {
                Files.delete(dir2);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 
     * @param path dir to watch
     * @param onupdate
     * @param onError
     * @param watchTypes
     */
    @SafeVarargs
    public static <T> void watchDir(Path path, Consumer<Path> onupdate, Function<Throwable, Boolean> onError, WatchEvent.Kind<T>...watchTypes) {
        new DirWatcher().start(path, onupdate, onError, watchTypes);
    }
    
    public static void write(Path path, CharSequence data, Charset charset, boolean append, CodingErrorAction onMalformedInput,CodingErrorAction onUnmappableCharacter) throws IOException { stringToFileWriter().  write( path,  data,  charset, append,  onMalformedInput, onUnmappableCharacter) ;}
    public static void write(Path path, CharSequence data, boolean append) throws IOException { stringToFileWriter().  write( path,  data,append) ;}
    public static void write(Path path, CharSequence data, Charset charset, boolean append) throws IOException { stringToFileWriter().  write( path,  data,  charset, append) ;}
    public static StringBuilder textOf(Path path, Charset charset, CodingErrorAction onMalformedInput,CodingErrorAction onUnmappableCharacter) throws IOException {return stringToFileWriter().  textOf( path,  charset,  onMalformedInput, onUnmappableCharacter) ;}
    public static StringBuilder textOf(Path path, Charset charset) throws IOException {return stringToFileWriter().  textOf( path,  charset) ;}
    public static StringBuilder textOf(Path path) throws IOException {return stringToFileWriter().  textOf( path) ;}
 
    private static StringToFileWriter stringToFileWriter0;
    private static StringToFileWriter stringToFileWriter() {
        if(stringToFileWriter0 == null)
            stringToFileWriter0 = new StringToFileWriter(BUFFER_SIZE);
        return stringToFileWriter0;
    }
    public static void appendFileAtTop(byte[] data, Path target) throws IOException { stringToFileWriter().  appendFileAtTop( data,  target) ;}
}
