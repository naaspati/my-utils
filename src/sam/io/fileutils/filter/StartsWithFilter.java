package sam.io.fileutils.filter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

public class StartsWithFilter extends StringValuesFilter {
	protected final Predicate<Path> filter;

	public StartsWithFilter(String[] array) {
		super(array);
		filter = Filters.make(array, s -> {
			Path path = Paths.get(s);
			return (x -> x.startsWith(path));
		});
	}

	@Override
	public boolean test(Path t) {
		return filter.test(t);
	}

}
