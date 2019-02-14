package sam.io.fileutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.stream.Stream;

import sam.io.IOConstants;
import sam.io.fileutils.FilesWalker.FileWalkResult;
import sam.myutils.Checker;
import sam.myutils.ThrowException;

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
		int dbs = IOConstants.defaultBufferSize();

		int buffersize = in.available() + 5;
		if(buffersize < 20)
			buffersize = 20;
		if(buffersize > dbs)
			buffersize = dbs;

		long nread = 0L;//number of bytes read
		byte[] buf = new byte[buffersize];
		int n;

		while ((n = in.read(buf)) > 0) {
			out.write(buf, 0, n);
			nread += n;
		}
		return nread;
	}

	public static long pipe(InputStream is, Path path, byte[] buffer) throws IOException {
		Checker.requireNonNull("is path buffer", is, path, buffer);
		if(buffer.length == 0)
			ThrowException.illegalArgumentException("buffer.length == 0");

		try(OutputStream out = Files.newOutputStream(path)) {
			int n = 0;
			long size = 0;
			while((n = is.read(buffer)) > 0) {
				out.write(buffer, 0, n);
				size += n;
			}
			return size;
		}
	}
	public static long pipe(InputStream in, WritableByteChannel out, byte[] buffer) throws IOException {
		int n = 0;
		long size = 0;
		while((n = in.read(buffer)) > 0) {
			out.write(ByteBuffer.wrap(buffer, 0, n));
			size += n;
		}
		return size;
	}

	public static int write(ByteBuffer buffer, WritableByteChannel channel, boolean flip) throws IOException {
		if(flip)
			buffer.flip();

		int n = 0;
		while(buffer.hasRemaining())
			n += channel.write(buffer);

		buffer.clear();
		return n;
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

	@FunctionalInterface
	public static interface WalkResult<E> {
		public FileVisitResult apply(Path path, E e) throws IOException;
	}
	@FunctionalInterface
	public static interface WalkConsumer<E> {
		public void apply(Path path, E e) throws IOException;
	}

	public static class Walker {
		private WalkResult<BasicFileAttributes> visitFile;
		private WalkResult<IOException> visitFileFailed;
		private WalkResult<BasicFileAttributes> preVisitDirectory;
		private WalkResult<IOException> postVisitDirectory;

		public Walker visitFile(WalkResult<BasicFileAttributes> visitFile) {
			this.visitFile = visitFile;
			return this;
		}
		public Walker visitFileFailed(WalkResult<IOException> visitFileFailed) {
			this.visitFileFailed = visitFileFailed;
			return this;
		}
		public Walker preVisitDirectory(WalkResult<BasicFileAttributes> preVisitDirectory) {
			this.preVisitDirectory = preVisitDirectory;
			return this;
		}
		public Walker postVisitDirectory(WalkResult<IOException> postVisitDirectory) {
			this.postVisitDirectory = postVisitDirectory;
			return this;
		}
		private <E> WalkResult<E> wrap(WalkConsumer<E> c){
			return (s,t) -> {
				c.apply(s, t);
				return FileVisitResult.CONTINUE;
			};
		} 
		public Walker visitFile(WalkConsumer<BasicFileAttributes> visitFile) {
			this.visitFile = wrap(visitFile);
			return this;
		}
		public Walker visitFileFailed(WalkConsumer<IOException> visitFileFailed) {
			this.visitFileFailed = wrap(visitFileFailed);
			return this;
		}
		public Walker preVisitDirectory(WalkConsumer<BasicFileAttributes> preVisitDirectory) {
			this.preVisitDirectory = wrap(preVisitDirectory);
			return this;
		}
		public Walker postVisitDirectory(WalkConsumer<IOException> postVisitDirectory) {
			this.postVisitDirectory = wrap(postVisitDirectory);
			return this;
		}

		public void start(Path start) throws IOException {
			if(Stream.of(
					visitFile,
					visitFileFailed,
					postVisitDirectory,
					preVisitDirectory).allMatch(Objects::isNull)){
				throw new NullPointerException("no visitor specified");
			}

			Files.walkFileTree(start, new FileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					if(preVisitDirectory != null)
						return preVisitDirectory.apply(dir, attrs);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if(visitFile != null)
						return visitFile.apply(file, attrs);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					if(visitFileFailed != null)
						return visitFileFailed.apply(file, exc);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					if(postVisitDirectory != null)
						return postVisitDirectory.apply(dir, exc);
					return FileVisitResult.CONTINUE;
				}
			});

		}
	}

	public static Walker walker() {
		return new Walker();
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
	public static FileLock createFileLock(Path lockFile) throws IOException {
		FileChannel c = FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		FileLock fl =  c.tryLock();
		c.write(ByteBuffer.wrap(new byte[]{1}));
		return fl;
	}
	@SuppressWarnings("rawtypes")
	public static void writeAsString(Path path, Iterable itr, StandardOpenOption...options) throws IOException {
		try(BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8, options)) {
			for (Object o : itr) {
				w.append(o.toString());
				w.newLine();
			}
		}
	}
}
