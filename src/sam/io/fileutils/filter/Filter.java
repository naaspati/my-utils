package sam.io.fileutils.filter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface Filter extends java.nio.file.DirectoryStream.Filter<Path>, Predicate<Path> {
	@Override
	default boolean accept(Path entry) throws IOException {
		return test(entry);
	}
	@Override
	default Filter and(Predicate<? super Path> other) {
		Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
	}
	@Override
	default Filter negate() {
		return (t) -> !test(t);
	}
	@Override
	default Filter or(Predicate<? super Path> other) {
		Objects.requireNonNull(other);
        return (t) -> test(t) || other.test(t);
	}
}
