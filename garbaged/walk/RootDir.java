package sam.cached.filetree.walk;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RootDir extends Dir {
	protected RootDir(Path root) {
		super(root.getFileName().toString(), root, Paths.get(""), root.toFile(), new File(""));
	}
	@Override
	protected File subpathFile(String name) {
		return new File(name);
	}
	@Override
	protected Path subpath(String name) {
		return Paths.get(name);
	}
}
