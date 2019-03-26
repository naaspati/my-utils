package sam.cached.filetree.walk;

import static org.junit.Assert.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import sam.books.BooksDBMinimal;
import sam.collection.ArraysUtils;

class WalkerTest {

	@Test
	void test() throws IOException {
		Set<Path> skipFiles = skipFiles();
		RootDir rootDir = walk(skipFiles);

		Map<Path, PathWrap> actual = new HashMap<>();
		DefaultWalker.walk(rootDir, e -> actual.put(e.subpath(), e));

		print(rootDir);

		final Path root = rootDir.fullpath();
		int namecount = root.getNameCount();

		ArrayList<Path> expected = new ArrayList<>(actual.size());
		Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
			boolean first = true;

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if(first) {
					first = false;
					return FileVisitResult.CONTINUE;
				} else
					return put(dir, true);
			}
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				return put(file, false);
			}

			private FileVisitResult put(Path file, boolean dir) {
				Path subpath = file.subpath(namecount, file.getNameCount());
				if(skipFiles.contains(subpath)) {
					System.out.println("skipped: "+subpath);
					return dir ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
				}
				expected.add(file);
				return FileVisitResult.CONTINUE;
			}
		});

		assertEquals(expected.size(), actual.size());
		assertEquals(rootDir.deepCount(), actual.size());

		for (Path full : expected) {
			Path subpath = full.subpath(namecount, full.getNameCount());
			PathWrap p = actual.get(subpath);

			assertNotNull(p, () -> full.toString());

			assertEquals(full, p.fullpath());
			assertEquals(full.toFile(), p.fullpathFile());
			assertEquals(subpath, p.subpath());
			assertEquals(subpath.toFile(), p.subpathFile());
			assertEquals(full.getFileName().toString(), p.name);
		}
	}

	private Set<Path> skipFiles() throws IOException {
		return Files.lines(Paths.get("D:\\Core Files\\Adobe\\ProgrammingComputer Books\\non-book materials\\Booklist app\\booklist_update\\ignore-subpaths.txt")).map(Paths::get).collect(Collectors.toSet());
	}

	private RootDir walk(Set<Path> skipFiles) throws IOException {
		DefaultWalker w = new DefaultWalker(p -> p != null && skipFiles.contains(p.subpath()));
		RootDir rootDir = w.walkDir(BooksDBMinimal.ROOT);
		return rootDir;
	}

	private int compares;

	@Test
	public void serializeTest() throws IOException {
		Path p = Files.createTempFile(null, null);
		try {
			serializeTest0(p);
		} finally {
			Files.deleteIfExists(p);
		}
	}

	public void serializeTest0(Path p) throws IOException {
		RootDir write = walk(skipFiles());

		Random r = new Random();
		int[] k = {0};
		DefaultWalker.walk(write, d -> {
			d.lastmod = r.nextLong();
			k[0]++;
		});


		RootDirSerializer.save(write, p);

		RootDir read = RootDirSerializer.read(p);

		ArrayList<Dir> write_dirs = new ArrayList<>();
		ArrayList<PathWrap> write_files = new ArrayList<>();

		fill(write, write_dirs, write_files);

		ArrayList<Dir> read_dirs = new ArrayList<>();
		ArrayList<PathWrap> read_files = new ArrayList<>();

		fill(read, read_dirs, read_files);

		compares = 0;
		assertEquals(write_dirs.size(), read_dirs.size());
		assertEquals(write_files.size(), read_files.size());

		compare(write, read);

		for (int i = 0; i < write_dirs.size(); i++) 
			compare(write_dirs.get(i), read_dirs.get(i));

		read_files.sort(Comparator.comparing(d -> d.name));
		write_files.sort(Comparator.comparing(d -> d.name));

		for (int i = 0; i < write_files.size(); i++) 
			compare0(write_files.get(i), read_files.get(i));

		System.out.printf("filesCount: %s, dirsCount: %s, deepCount: %s,  compares: %s\n", write_files.size(), write_dirs.size(), write.deepCount(), compares);
	}

	private void compare(Dir a, Dir b) {
		if(a == null && b == null)
			return;

		compare0(a, b);
		assertEquals(a.count(), b.count());
		Arrays.sort(a.children, Comparator.comparing(d -> d.name));
		Arrays.sort(b.children, Comparator.comparing(d -> d.name));

		for (int i = 0; i < a.count(); i++) {
			boolean success = false;
			try {
				compare0(a.children[i], b.children[i]);
				success = true;
			} finally {
				if(!success) 
					System.out.printf("n: %s\n%s\n------------\n%s\n", i, join(a), join(b));
			}
		}
	}
	private String join(Dir a) {
		return String.join("\n", ArraysUtils.map(a.children, new String[a.count()], d -> d.subpath().toString()));
	}

	private void compare0(PathWrap a, PathWrap b) {
		if(a == null && b == null)
			return;

		compares++;
		assertNotSame(a, b);

		assertEquals(a.fullpath(), b.fullpath());
		assertEquals(a.fullpathFile(), b.fullpathFile());
		assertEquals(a.subpath(), b.subpath());
		assertEquals(a.subpathFile(), b.subpathFile());
		assertEquals(a.lastmod, b.lastmod);
	}

	private void fill(RootDir root, List<Dir> dirs, List<PathWrap> files) {
		DefaultWalker.walk(root, w -> {
			if(w.isDir())
				dirs.add((Dir) w);
			else
				files.add(w);
		});
	}

	private void print(Dir dir) {
		System.out.println(dir.getId()+": "+dir.subpath());

		for (PathWrap p : dir) 
			System.out.println("  "+p.subpath());

		System.out.println();

		for (PathWrap p : dir) {
			if(p.isDir())
				print((Dir)p);
		}
	}

}
