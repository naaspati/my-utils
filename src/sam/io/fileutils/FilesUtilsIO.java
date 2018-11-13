package sam.io.fileutils;

import static sam.io.BufferSize.DEFAULT_BUFFER_SIZE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

import sam.io.fileutils.FilesWalker.FileWalkResult;

public interface FilesUtilsIO {
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
	public static long pipe(InputStream in, OutputStream out) throws IOException  {
		int buffersize = in.available() + 5;
		if(buffersize < 20)
			buffersize = 20;
		if(buffersize > DEFAULT_BUFFER_SIZE)
			buffersize = DEFAULT_BUFFER_SIZE;

		long nread = 0L;//number of bytes read
		byte[] buf = new byte[buffersize];
		int n;

		while ((n = in.read(buf)) > 0) {
			out.write(buf, 0, n);
			nread += n;
		}
		return nread;
	}

	public static void deleteDir(Path dir) throws IOException {
		if(Files.notExists(dir)) return;

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
		Files.deleteIfExists(dir); //needed
	}

	/**
	 * delete a file or a dir(recursively)
	 * @param file
	 * @return
	 */
	public static boolean delete(File file)  {
		Objects.requireNonNull(file);

		if(!file.exists()) return true;
		if(file.isFile()) return file.delete();

		for (String s : file.list()) {
			File f = new File(file, s);
			if(!f.delete() && f.isDirectory())
				delete(f);
		}
		return file.delete();
	}
	public static FileLock createFileLock(String lockName) throws IOException {
		FileChannel c = FileChannel.open(Paths.get(lockName), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		FileLock fl =  c.tryLock();
		c.write(ByteBuffer.wrap(new byte[]{1}));
		return fl;
	}
}
