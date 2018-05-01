package sam.fileutils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FilesWalker {
    public static class FileWalkResult {
        public final List<Path> files = new ArrayList<>();
        public final List<Path> dirs = new ArrayList<>();
    }
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
        if(path == null || Files.notExists(path))
            throw new IOException("Invalid Path: "+path);

        FileWalkResult fwr = new FileWalkResult(); 

        if(Files.isRegularFile(path))
            fwr.files.add(path);
        else{
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    fwr.files.add(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    fwr.dirs.add(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return fwr;
    }
}
