package sam.cached.filetree.walk;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;

import sam.logging.Logger;
import sam.myutils.Checker;

public abstract class Walker<D extends F, F> {
	private static final Logger logger = Logger.getLogger(Walker.class);
	private final Predicate<F> skipFiles;

	public Walker(Predicate<F> skipFiles) throws IOException {
		this.skipFiles = skipFiles;
	}
	public void walkDir(D root) throws IOException {
		logger.debug("walking: {}", root);
		F[] children = walk(root);
		setChildren(children, root);
	}

	protected abstract void setChildren(F[] children, D root);
	protected abstract File fullPathFile(F parent);
	protected abstract F[] emptyArray();
	protected abstract F[] newArray(int length);
	protected abstract F newChild(D parent, String name);
	protected abstract boolean isDir(F p);
	
	private F[] walk(D parent) {
		File f = fullPathFile(parent);
		String[] files = f == null ? null : f.list();

		if(Checker.isEmpty(files))
			return emptyArray();

		F[] children = newArray(files.length);

		int n = 0;
		for (String s : files) {
			F p = newChild(parent, s);

			if(!skipFiles.test(p))
				children[n++] = p;
		}

		if(n != children.length)
			children = Arrays.copyOf(children, n);

		for (F p : children) {
			if(isDir(p)) {
				@SuppressWarnings("unchecked")
				D d = (D)p;
				setChildren(walk(d), d);
			}
		}
		return children;
	}
}
