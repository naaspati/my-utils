package sam.nopkg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

import sam.functions.IOExceptionBiConsumer;

public class SimpleSavedResource<E> extends SavedResource<E> {
	private final Function<Path, E> reader;
	private final IOExceptionBiConsumer<Path, E> writer;
	private final Path path;
	private final BiPredicate<E, E> isEqual;

	public SimpleSavedResource(String path, Function<Path, E> reader, IOExceptionBiConsumer<Path, E> writer) {
		this(Paths.get(path), reader, writer);
	}
	public SimpleSavedResource(Path path, Function<Path, E> reader, IOExceptionBiConsumer<Path, E> writer) {
		this(path, reader, writer, Objects::equals);
	}
	public SimpleSavedResource(String path, Function<Path, E> reader, IOExceptionBiConsumer<Path, E> writer, BiPredicate<E, E> isEqual) {
		this(Paths.get(path), reader, writer, isEqual);
	}
	public SimpleSavedResource(Path path, Function<Path, E> reader, IOExceptionBiConsumer<Path, E> writer, BiPredicate<E, E> isEqual) {
		this.reader = reader;
		this.writer = writer;
		this.path = path;
		this.isEqual = isEqual;
	}

	@Override
	protected E read() {
		return Files.notExists(path) ? null : reader.apply(path);
	}
	@Override
	protected void write(E e) throws IOException {
		if(e == null)
			Files.deleteIfExists(path);
		else
			writer.accept(path, e);
	}
	@Override
	protected boolean isEqual(E a, E b) {
		return isEqual.test(a, b);
	}

}
