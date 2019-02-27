package sam.myutils;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

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
	public static Path resolveToClassLoaderPath(String resourceName) {
		return resolveToClassLoaderPath(ClassLoader.getSystemClassLoader(),resourceName);		
	}
	public static Path resolveToClassLoaderPath0(ClassLoader loader, String resourceName) throws URISyntaxException {
		URL u = loader.getResource(resourceName);
		
		if(u == null) {
			u = ClassLoader.getSystemResource(".");
			if(u == null) return null;
			return Paths.get(u.toURI()).resolve(resourceName);
		} else
			return Paths.get(u.toURI());
	}
	public static Path resolveToClassLoaderPath(ClassLoader loader, String resourceName) {
		try {
			return resolveToClassLoaderPath0(loader, resourceName);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static final Path TEMP_DIR = AccessController.doPrivileged((PrivilegedAction<Path>)() -> {
		Path p = Paths.get(System.getProperty("java.io.tmpdir"));
		if(Files.notExists(p))
			throw new RuntimeException("not found: "+p);
		return p;
	});
			
	
	public static String pathFormattedDateTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH.mm"));
	}

	public static final Path SELF_DIR = AccessController.doPrivileged((PrivilegedAction<Path>)() -> {
				return Optional.ofNullable(System2.lookup("SELF_DIR")).map(Paths::get).filter(f -> {
					if(Files.notExists(f))
						throw new IllegalStateException("SELF_DIR not found: "+f);
					return true;
				}).orElse(null);
			});
	
	public static Path selfDir() {
		if(SELF_DIR == null)
			throw new RuntimeException("SELF_DIR not found/set: "+SELF_DIR);
		
		return SELF_DIR;
	}
}
