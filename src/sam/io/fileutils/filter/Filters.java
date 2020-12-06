package sam.io.fileutils.filter;

import static sam.config.Constants.FALSE_ALWAYS;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;


@SuppressWarnings("unchecked")
public class Filters implements Filter {
	
	protected static final String PATH = "path";
	protected static final String FILENAME = "filename";
	protected static final String STARTSWITH = "startsWith";
	protected static final String ENDSWITH = "endsWith";
	protected static final String GLOB = "glob";
	protected static final String REGEX = "regex";
	protected static final String INVERT = "invert";


	protected Predicate<Path>  path = FALSE_ALWAYS;
	protected Predicate<Path>  filename = FALSE_ALWAYS;
	protected Predicate<Path>  startsWith = FALSE_ALWAYS;
	protected Predicate<Path>  endsWith = FALSE_ALWAYS;
	protected Predicate<Path>  glob = FALSE_ALWAYS;
	protected Predicate<Path>  regex = FALSE_ALWAYS;
	protected Predicate<Path>  invert;

	@Override
	public boolean test(Path p) {
		if (invert != null && invert.test(p)) 
			return false;
		
		return  matchedAt(p) != null;
	}
	
	public Map<String, Object> asMap() {
		HashMap<String, Object> map = new HashMap<>();
		if(invert instanceof Filters)
			map.put("invert", ((Filters)invert).asMap());
		
		for (Field f : getClass().getDeclaredFields()) {
			if(Modifier.isStatic(f.getModifiers()))
				continue;
			try {
				Object o = f.get(this);
				if(o instanceof StringValuesFilter)
					map.put(f.getName(), ((StringValuesFilter)o).values());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}			
		}
		return  map;
	}
	
	public Predicate<Path> matchedAt(Path p) {
		if (invert != null && invert.test(p)) 
			return null;
		
		if(path.test(p)) return path;
		if(filename.test(p.getFileName())) return filename;
		if(startsWith.test(p)) return startsWith;
		if(endsWith.test(p)) return endsWith;
		if(glob.test(p)) return glob;
		if(regex.test(p)) return regex;
		
		return null;
	}
	
	public Predicate<Path> getFileName() {
		return filename;
	}

	public void setFileName(Predicate<Path> filename) {
		this.filename = filename;
	}

	public Predicate<Path> getGlob() {
		return glob;
	}

	public void setGlob(Predicate<Path> glob) {
		this.glob = glob;
	}

	public Predicate<Path> getRegex() {
		return regex;
	}

	public void setRegex(Predicate<Path> regex) {
		this.regex = regex;
	}

	public Predicate<Path> getPath() {
		return path;
	}

	public void setPath(Predicate<Path> path) {
		this.path = path;
	}

	public Predicate<Path> getStartsWith() {
		return startsWith;
	}

	public void setStartsWith(Predicate<Path> startsWith) {
		this.startsWith = startsWith;
	}

	public Predicate<Path> getEndsWith() {
		return endsWith;
	}

	public void setEndsWith(Predicate<Path> endsWith) {
		this.endsWith = endsWith;
	}

	public Predicate<Path> getInvert() {
		return invert;
	}

	public void setFileName(String[] name) {
		this.filename = create(name, ContainsFilter::new);
	}

	public void setGlob(String[] glob) {
		this.glob = create(glob, GlobFilter::new);
	}

	public void setRegex(String[] regex) {
		this.regex = create(regex, RegexFilter::new);
	}

	public void setPath(String[] path) {
		this.path = create(path, ContainsFilter::new);
	}

	public void setStartsWith(String[] startsWith) {
		this.startsWith = create(startsWith, StartsWithFilter::new);
	}

	public void setEndsWith(String[] endsWith) {
		this.endsWith = create(endsWith, EndsWithFilter::new);
	}
	
	public void setInvert(Filters invert) {
		this.invert = invert;
	}

	public void setInvert(Predicate<Path> invert) {
		this.invert = invert;
	}
	
	protected static Predicate<Path> create(String[] array, Function<String[], Filter> mapper) {
		return array == null || array.length == 0 ? FALSE_ALWAYS : mapper.apply(array);
	}

	protected static <T> Predicate<T> make(String[] array, Function<String, Predicate<T>> mapper) {
		if(array == null || array.length == 0)
			return FALSE_ALWAYS;
		else {
			Predicate<T> p = mapper.apply(array[0]);
			for (int i = 1; i < array.length; i++)
				p = p.or(mapper.apply(array[i]));
			return p;
		} 		
	}
}
