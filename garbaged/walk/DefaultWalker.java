package sam.cached.filetree.walk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Predicate;

import sam.functions.IOExceptionConsumer;

public class DefaultWalker extends Walker<Dir, PathWrap> {

	public DefaultWalker(Predicate<PathWrap> skipFiles) throws IOException {
		super(skipFiles);
	}

	public static RootDir walkDir(Path rootPath, Predicate<PathWrap> skipFiles) throws IOException {
		return new DefaultWalker(skipFiles).walkDir(rootPath);
	}

	private int id = 0;
	public RootDir walkDir(Path rootPath) throws IOException {
		if(!Files.isDirectory(rootPath))
			throw new IllegalArgumentException((Files.notExists(rootPath) ? "not found: " : "not a directory: ")+rootPath);

		RootDir root = new RootDir(rootPath);
		root.setId(id++);
		super.walkDir(root);
		return root;
	} 

	@Override
	protected void setChildren(PathWrap[] children, Dir root) {
		root.children = children;
	}
	@Override
	protected File fullPathFile(PathWrap d) {
		return d.fullpathFile();
	}

	@Override
	protected PathWrap[] emptyArray() {
		return Dir.EMPTY;
	}
	@Override
	protected PathWrap[] newArray(int length) {
		return new PathWrap[length];
	}
	@Override
	protected boolean isDir(PathWrap p) {
		return p.isDir();
	}
	@Override
	protected PathWrap newChild(Dir parent, String name) {
		PathWrap w = parent.resolve(name);
		if(w.isDir())
			((Dir)w).setId(id++);
		return w;
	}
	
	public static void walk(Dir dir, Consumer<PathWrap> consumer) {
		dir.forEach(consumer);
		dir.forEach(c -> {
			if(c.isDir())
				walk((Dir)c, consumer);
		});
	}

	public static void walk0(Dir dir, IOExceptionConsumer<PathWrap> consumer) throws IOException {
		dir.forEach0(consumer);
		dir.forEach0(c -> {
			if(c.isDir())
				walk0((Dir)c, consumer);
		});
	}
}
