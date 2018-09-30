package sam.fileutils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import sam.fileutils.FilesWalker.FileWalkResult;

public final class FilesUtilsIO {
	private static WeakReference<FilesUtilsIO> instance = new WeakReference<FilesUtilsIO>(null);

	public static FilesUtilsIO getWeakInstance() {
		FilesUtilsIO w = instance.get();
		if (w == null)
			instance = new WeakReference<FilesUtilsIO>(w = new FilesUtilsIO());

		return w;
	}
	private int bufferSize;
	public FilesUtilsIO() {
		this.bufferSize = new BufferSize().get();
	}
	public FilesUtilsIO(int buffersize) {
		this.bufferSize = buffersize;
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
		return FilesWalker.listDirsFiles(path);
	}

	public static long pipe(InputStream is, OutputStream os) throws IOException  {
		return getWeakInstance().pipe0(is, os);
	}
	/**
	 * Reads all bytes from an input stream and writes them to an output stream.
	 * and return number of bytes read 
	 */
	public long pipe0(InputStream is, OutputStream os) throws IOException  {
		long nread = 0L;//number of bytes read
		byte[] buf = new byte[bufferSize];
		int n;
		while ((n = is.read(buf)) > 0) {
			os.write(buf, 0, n);
			nread += n;
		}
		return nread;
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
	



}
