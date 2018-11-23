package sam.io.fileutils;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Predicate;

/**
 * <h3> Simple PathFilter (Some additions to {@link PathMatcher}) </h3>
 * <p>follows some rules of https://git-scm.com/docs/gitignore </p>
 * <ul> 
 *   <li> any / is replaced with \ </li> 
 *   <li> A blank line matches no files. </li>
 *   <li> A line starting with # serves as a comment. </li> 
 *   <li> line is trimmed </li>
 *   <li> ! prefix nagates the check, if check is true </li>
 *   <li> if line ends with \, the path is also tested wit {@link Files#isDirectory(Path, java.nio.file.LinkOption...)} </li>
 *   <li> if path does not have any \, then check is performed against {@link Path#getFileName()} </li>
 *   <li> uses {@link PathMatcher#matches(Path)} for glob checks   </li>
 *   <li> if pattern starts with \ and {@link #PathFilter(Path, Iterable)}, root is not null, </li> 
 *     <li> then the pattern is converted as pattern = root.toString().concat(pattern);   </li>
 *     </ul>
 * @author Sameer
 *
 */
public class PathFilter implements Predicate<Path> {
	private Collection<Path> names = new HashSet<>();
	private Predicate<Path> simple = p -> false;
	private Predicate<Path> glob = p -> false;
	private PathFilter invert;
	
	public PathFilter(Iterable<String> iterable) {
		this(null, iterable);
	}
	
	public PathFilter(Path root, Iterable<String> iterable) {
		String rootS = root == null ? null : root.toString();
		ArrayList<String> invert = new ArrayList<>();
		
		for (String s : iterable) {
			s = s.trim();
			if(s.isEmpty() || s.charAt(0) == '#')
				continue;
			if(s.charAt(0) == '!') {
				invert.add(s.substring(1));
				continue;
			}
			
			s = s.replace('/', '\\');
			if(s.charAt(0) == '\\' && root != null)
				s = rootS.concat(s); 
			
			boolean  globSyntex = s.startsWith("glob:") || s.startsWith("regex:");
			boolean  star = contains(s, '*');
			boolean dircheck = s.charAt(s.length() - 1) == '\\';
			s = dircheck ? s.substring(0, s.length() - 1) : s;
			
			if(globSyntex || star || s.isEmpty()) {
				glob = glob.or(glob(globSyntex, dircheck, s));
			} else {
				Path sp = Paths.get(s);
				if(sp.getRoot() != null)
					simple = simple.or(equals(dircheck, sp));
				else if(sp.getNameCount() != 1)
					simple = simple.or(endwith(dircheck, sp));
				else {
					if(dircheck)
						simple = simple.or(p -> p.getFileName().equals(sp) && isdir(p));
					else
						names.add(sp);
				} 
			}
		}
		if(names.isEmpty())
			names = Collections.emptySet();
		if(!invert.isEmpty())
			this.invert = new PathFilter(root, invert);
	}
	private Predicate<Path> glob(boolean globSyntex, boolean dircheck, String s) {
		PathMatcher matcher = fs().getPathMatcher(globSyntex ? s : "glob:"+s);
		
		if(contains(s, '\\')) {
			if(dircheck)
				return p -> (matcher.matches(p) && isdir(p));
			return matcher::matches;
		} else {
			if(dircheck)
				return p -> (matcher.matches(p.getFileName()) && isdir(p));
			return  p -> matcher.matches(p.getFileName());
		}
	}
	private Predicate<Path> endwith(boolean dircheck, Path sp) {
		if(dircheck) 
			return (p -> p.endsWith(sp) && isdir(p));
		return p -> p.endsWith(sp);
	}
	private Predicate<Path> equals(boolean dircheck, Path sp) {
		if(dircheck) 
			return (p -> p.equals(sp) && isdir(p));
		return p -> p.equals(sp);
	}
	private boolean isdir(Path path) {
		return Files.isDirectory(path);
	}
	private boolean contains(String s, char c) {
		return s.indexOf(c) >= 0;
	}
	private FileSystem _fs;
	private FileSystem fs() {
		if(_fs != null) return _fs;
		return _fs = FileSystems.getDefault();
	}
	public boolean test(Path path) {
		if(names.contains(path.getFileName()) || simple.test(path) || glob.test(path)) {
			if(invert != null && invert.test(path))
				return false;
			
			return true;
		}
		return false;
	}
}
