package sam.myutils;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Function;

public interface MyUtilsPath {
	public static Path subpath(Path child, Path parent) {
		Objects.requireNonNull(child);
		Objects.requireNonNull(parent);
		
		if(!child.startsWith(parent))
			throw new IllegalArgumentException("{error:\"child does'nt start with parent\", child:\""+child+"\", parent:\""+parent+"\" }") ;
		
		return child.subpath(parent.getNameCount(), child.getNameCount());
	}
	
	public static Function<Path, Path> subpather(Path parent){
		Objects.requireNonNull(parent);
		int count = parent.getNameCount();
		
		return child -> {
			Objects.requireNonNull(child);
			
			if(child.getNameCount() < count || !child.startsWith(parent))
				throw new IllegalArgumentException("{error:\"child does'nt start with parent\", child:\""+child+"\", parent:\""+parent+"\" }") ;
			
			return child.subpath(count, child.getNameCount());
		};
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
	public static Path resolveToClassLoaderPath(String resourceName) throws URISyntaxException {
		URL u = ClassLoader.getSystemResource(resourceName);
		if(u == null)
			return Paths.get(ClassLoader.getSystemResource(".").toURI()).resolve(resourceName);
		else
			return Paths.get(u.toURI());	
	}
}
