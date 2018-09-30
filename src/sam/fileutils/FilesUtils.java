package sam.fileutils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.function.Consumer;
import java.util.function.Function;

import sam.fileutils.FilesWalker.FileWalkResult;

public final class FilesUtils {
    public static final double VERSION = 1.21;

    private FilesUtils() {}
    
    public static void openFile(File file) throws IOException{ FileOpener.openFile(file); }
    public static void openFileNoError(File file){ FileOpenerNE.openFile(file); }
    public static void openFileLocationInExplorer(File file) throws IOException { FileOpener.openFileLocationInExplorer(file); }
    public static void openFileLocationInExplorerNoError(File file) { FileOpenerNE.openFileLocationInExplorer(file); }
    
    /**
     * read only one object from file
     * @return
     * @throws IOException 
     * @throws ClassNotFoundException
     *  
     */
    public static <R> R read(Path path) throws ClassNotFoundException, IOException{ return FilesUtilsObject.read(path); }
    public static <R> R read(Path path, Class<R> clazz) throws ClassNotFoundException, IOException{ return FilesUtilsObject.read(path, clazz); }
    public static void write(Object object, Path path) throws IOException{ FilesUtilsObject.write(object, path); }

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
            stringToFileWriter0 = StringToFileWriter.getWeakInstance();
        return stringToFileWriter0;
    }
    public static void appendFileAtTop(byte[] data, Path target) throws IOException { stringToFileWriter().  appendFileAtTop( data,  target) ;}


/** {@link FilesUtilsIO#listDirsFiles(Path)} */
 public static FileWalkResult listDirsFiles(Path path) throws IOException { return FilesUtilsIO.listDirsFiles(path); }
/** {@link FilesUtilsIO#pipe(InputStream,OutputStream)} */
 public long pipe(InputStream is, OutputStream os) throws IOException  { return FilesUtilsIO.pipe(is,os); }
/** {@link FileUtilsIO#pipe0(InputStream,OutputStream)} */
/** {@link FilesUtilsIO#deleteDir(Path)} */
 public static void deleteDir(Path dir) throws IOException {  FilesUtilsIO.deleteDir(dir); }
}
