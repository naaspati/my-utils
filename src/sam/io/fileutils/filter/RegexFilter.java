package sam.io.fileutils.filter;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.function.Predicate;

import sam.config.Constants;

public class RegexFilter extends StringValuesFilter {
	private final Predicate<Path> filter;
	
	public RegexFilter(String[] array) {
		super(array);
		if(array == null || array.length == 0) {
			this.filter = Constants.falseAlways();
		} else {
			FileSystem fs = FileSystems.getDefault();
			
			filter = Filters.make(array, s -> {
				PathMatcher m = fs.getPathMatcher("regex:".concat(s.replace("/", "\\\\")));
				return x -> m.matches(x);
			});
		}
	}
	
	@Override
	public boolean test(Path t) {
		return filter.test(t);
	}
}
