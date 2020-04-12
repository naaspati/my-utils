package sam.io.fileutils.filter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

public class EndsWithFilter extends StringValuesFilter {
	protected final Predicate<Path> filter;

	public EndsWithFilter(String[] array) {
		super(array);
		filter = Filters.make(array, s -> {
			Path path = Paths.get(s);
			return (x -> x.endsWith(path));
		});
	}

	@Override
	public boolean test(Path t) {
		return filter.test(t);
	}

}
