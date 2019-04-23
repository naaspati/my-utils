package sam.io.fileutils;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
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

import sam.io.IOUtils;
import sam.io.fileutils.FilesWalker.FileWalkResult;
import sam.myutils.Checker;

public final class FilesUtilsIO {
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
	@SuppressWarnings("rawtypes")
	public static void writeAsString(Path path, Iterable itr, StandardOpenOption...options) throws IOException {
		try(BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8, options)) {
			for (Object o : itr) {
				w.append(o.toString());
				w.newLine();
			}
		}
	}

	public static OutputStream newOutputStream(Path p, ByteBuffer buf, boolean append) throws IOException {
		FileChannel fc = FileChannel.open(p, WRITE, CREATE, append ? APPEND : TRUNCATE_EXISTING);
		return newOutputStream(fc, buf);
	}

	public static OutputStream newOutputStream(WritableByteChannel fc, ByteBuffer buf) throws IOException {
		return new OutputStream() {
			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				if(len <= 0)
					return;

				if(len > buf.remaining())
					flush();

				if(len > buf.capacity())
					IOUtils.write(ByteBuffer.wrap(b, off, len), fc, false);
				else 
					buf.put(b, off, len);
			}

			@Override
			public void flush() throws IOException {
				IOUtils.write(buf, fc, true);
			}

			@Override
			public void close() throws IOException {
				flush();
				fc.close();
			}

			@Override
			public void write(int b) throws IOException {
				if(buf.remaining() < 1)
					flush();

				buf.put((byte)b);
			}
		};
	}

	public static InputStream newInputStream(Path p, ByteBuffer buf) throws IOException {
		return newInputStream(FileChannel.open(p, READ), buf);
	}

	public static InputStream newInputStream(ReadableByteChannel fc, ByteBuffer buf) throws IOException {
		IOUtils.read(buf, true, fc);

		return new InputStream() {
			@Override
			public long skip(long n) throws IOException {
				throw new IOException();
			}
			@Override
			public int available() throws IOException {
				throw new IOException();
			}
			@Override
			public synchronized void mark(int readlimit) {
			}
			@Override
			public synchronized void reset() throws IOException {
				throw new IOException();
			}
			@Override
			public boolean markSupported() {
				return false;
			}

			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				Objects.requireNonNull(b);
				Checker.assertFalseArgumentError(off < 0, "off < 0");
				Checker.assertFalseArgumentError(len < 0, "len < 0");

				if(len == 0)
					return 0;

				if(len <= buf.remaining()) 
					return copy(buf, b, off, len);
				else {
					int read = 0;

					while(len > 0) {
						if(!buf.hasRemaining() && IOUtils.read(buf, true, fc) < 0)
							return read == 0 ? -1 : read;

						int n = copy(buf, b, off, Math.min(len, buf.remaining()));

						len -= n;
						off += n;
						read += n;
					}

					return read;
				}
			}

			@Override
			public void close() throws IOException {
				fc.close();
			}

			@Override
			public int read() throws IOException {
				if(!buf.hasRemaining() && IOUtils.read(buf, true, fc) < 0)
					return -1;

				return buf.get();
			}
		};
	}
	public static int copy(ByteBuffer buf, byte[] b, int off, int len) {
		System.arraycopy(buf.array(), buf.position(), b, off, len);
		buf.position(buf.position() + len);
		return len;
	}
}
