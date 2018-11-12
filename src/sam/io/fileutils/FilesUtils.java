package sam.io.fileutils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.function.Consumer;
import java.util.function.Function;

import sam.io.fileutils.FilesWalker.FileWalkResult;

public final class FilesUtils {
    public static final double VERSION = 1.21;

    private FilesUtils() {}
    
    public static void openFile(File file) throws IOException{ FileOpener.openFile(file); }
    public static void openFileNoError(File file){ FileOpenerNE.openFile(file); }
    public static void openFileLocationInExplorer(File file) throws IOException { FileOpener.openFileLocationInExplorer(file); }
    public static void openFileLocationInExplorerNoError(File file) { FileOpenerNE.openFileLocationInExplorer(file); }

    /**
     * 
     * @param path dir to watch
     * @param onupdate
     * @param onError
     * @param watchTypes
     */
    @SafeVarargs
    public static <T> void watchDir(Path path, Consumer<Path> onupdate, Function<Throwable, Boolean> onError, WatchEvent.Kind<T>...watchTypes) {
        new DirWatcher(path, onupdate, onError, watchTypes).run();
    }
    
/** {@link FilesUtilsIO#listDirsFiles(Path)} */
 public static FileWalkResult listDirsFiles(Path path) throws IOException { return FilesUtilsIO.listDirsFiles(path); }
/** {@link FilesUtilsIO#pipe(InputStream,OutputStream)} */
 public long pipe(InputStream is, OutputStream os) throws IOException  { return FilesUtilsIO.pipe(is,os); }
/** {@link FileUtilsIO#pipe0(InputStream,OutputStream)} */
/** {@link FilesUtilsIO#deleteDir(Path)} */
 public static void deleteDir(Path dir) throws IOException {  FilesUtilsIO.deleteDir(dir); }
}
