package sam.io.fileutils.filter;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.function.Predicate;

import sam.config.Constants;
import sam.string.StringUtils;

public class GlobFilter extends StringValuesFilter {
	private final Predicate<Path> filter;
	
	public GlobFilter(String[] array) {
		super(array);
		if(array == null || array.length == 0) {
			this.filter = Constants.falseAlways();
		} else {
			FileSystem fs = FileSystems.getDefault();
			this.filter = Filters.make(array, s -> {
				PathMatcher rgx = fs.getPathMatcher("glob:".concat(s));
				return StringUtils.contains(s, '/') ? (x -> rgx.matches(x)) : (x -> rgx.matches(x.getFileName()));
			});
		}
	}
	
	@Override
	public boolean test(Path t) {
		return filter.test(t);
	}
}
