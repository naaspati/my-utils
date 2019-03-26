package sam.cached.filetree.walk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import sam.collection.ArrayIterator;
import sam.functions.IOExceptionConsumer;
import sam.myutils.Checker;

public class Dir extends PathWrap implements Iterable<PathWrap> {
	public static final PathWrap[] EMPTY = new PathWrap[0];
	private int id = -1;
	PathWrap[] children;

	private Dir(Dir parent, String name) {
		super(parent, name);
	}
	//root dir
	protected Dir(String name, Path fullpath, Path subpath, File fullpathFile, File subpathFile) {
		super(null, name, fullpath, subpath, fullpathFile, subpathFile);
	}
	public Dir(Dir parent, String name, File fullpathFile) {
		super(parent, name, null, null, fullpathFile, null);
	}
	public PathWrap resolve(String name) {
		File file = new File(fullpathFile(), name);
		
		if(file.isDirectory())
			return new Dir(this, name, file);
		else
			return new PathWrap(this, name);
	}
	
	PathWrap resolve0(String name, boolean isDir, long lastMod) {
		PathWrap p ;
		if(isDir)
			 p = new Dir(this, name);
		else
			p = new PathWrap(this, name);
		
		p.lastmod = lastMod;
		return p;
	}
	
	public int getId() {
		return id;
	}
	void setId(int id) {
		if(this.id != -1)
			throw new IllegalAccessError("id already set: "+this.id);
		this.id = id;
	}
	
	@Override
	public boolean isDir() {
		return true;
	}
	public int count() {
		return children.length;
	}
	@Override
	public Iterator<PathWrap> iterator() {
		if(Checker.isEmpty(children))
			return Collections.emptyIterator();
		else 
			return new ArrayIterator<>(children);
	}
	@Override
	public void forEach(Consumer<? super PathWrap> action) {
		for (PathWrap c : children) 
			action.accept(c);
	}
	public void forEach0(IOExceptionConsumer<? super PathWrap> action) throws IOException {
		for (PathWrap c : children) 
			action.accept(c);
	}
	@Override
	public Spliterator<PathWrap> spliterator() {
		return Arrays.spliterator(children);
	}
	private File file(File file, String name) {
		return new File(file, name);
	}
	private Path path(Path file, String name) {
		return file.resolve(name);
	}
	protected File fullpathFile(String name) {
		return file(fullpathFile(), name);
	}
	protected Path fullpath(String name) {
		return path(fullpath(), name);
	}
	protected File subpathFile(String name) {
		return file(subpathFile(), name);
	}
	protected Path subpath(String name) {
		return path(subpath(), name);
	}
	int deepCount() {
		if(children.length == 0)
			return 0;
		
		int n = children.length;
		
		for (PathWrap p : children) {
			if(p.isDir())
				n += ((Dir)p).deepCount();
		}
		
		return n;
	}
}
