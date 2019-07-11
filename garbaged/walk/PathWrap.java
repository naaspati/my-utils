package sam.cached.filetree.walk;

import java.io.File;
import java.nio.file.Path;

public class PathWrap {
	public final Dir parent; 
	public final String name;

	private Path fullpath, subpath;
	private File fullpathFile, subpathFile;

	protected PathWrap(Dir parent, String name) {
		this.parent = parent;
		this.name = name;
	}

	//for root
	protected PathWrap(Dir parent, String name, Path fullpath, Path subpath, File fullpathFile, File subpathFile) {
		this.parent = parent;
		this.name = name;
		this.fullpath = fullpath;
		this.subpath = subpath;
		this.fullpathFile = fullpathFile;
		this.subpathFile = subpathFile;
	}
	
	public File fullpathFile() {
		if(fullpathFile == null)
			fullpathFile = parent.fullpathFile(name);

		return fullpathFile;
	}
	public File subpathFile() {
		if(subpathFile == null) 
			subpathFile = parent.subpathFile(name);
		
		return subpathFile;
	}
	public Path fullpath() {
		if(fullpath == null)
			fullpath = parent.fullpath(name);

		return fullpath;
	}
	public Path subpath() {
		if(subpath == null)
			subpath = parent.subpath(name);

		return subpath;
	}

	private int isDir = -1;
	public boolean isDir() {
		if(isDir == -1)
			isDir = fullpathFile().isDirectory() ? 1 : 0;

		return isDir == 1;
	}
	private int exists = -1;
	public boolean exists() {
		if(exists == -1)
			exists = fullpathFile().exists() ? 1 : 0;

		return exists == 1;
	}
	public String name() {
		return name;
	}
	
	long lastmod = -1;

	public long lastModified() {
		if(lastmod == -1)
			lastmod = fullpathFile().lastModified();
		return lastmod;
	}
}
