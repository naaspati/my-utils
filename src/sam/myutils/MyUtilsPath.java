package sam.myutils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public interface MyUtilsPath {
	public static Path subpath(Path child, Path parent) {
		Objects.requireNonNull(child);
		Objects.requireNonNull(parent);
		
		if(!child.startsWith(parent))
			throw new IllegalArgumentException("{error:\"child does'nt start with parent\", child:\""+child+"\", parent:\""+parent+"\" }") ;
		
		return child.subpath(parent.getNameCount(), child.getNameCount());
		
	}
	public static Path findPathNotExists(Path path) {
		if(Files.notExists(path))
			return path;

		final String filename = path.getFileName().toString();
		int index = filename.lastIndexOf('.');
		String name, ext = null;
		if(index > 0) {
			ext = filename.substring(index);
			if(ext.indexOf(' ') >= 0) {
				name = filename;
				ext = "";
			} else 
				name = filename.substring(0, index);
		} else {
			name = filename;
			ext = "";            
		}

		index = 1;
		path  = path.resolveSibling(name+"_1"+ext);
		while(Files.exists(path))
			path  = path.resolveSibling(name+"_"+(++index)+ext);

		return path;
	}

}
