package sam.nopkg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

import static java.nio.file.StandardOpenOption.*;

public class ModResourceSavedAsString<E> extends ModResource<E> {
	public final Path save_path;
	public final Function<E, String> toString;
	public final Function<String, E> fromString;
	
	public ModResourceSavedAsString(Path save_path, Function<String, E> fromString) {
		this(save_path, s -> s == null ? null : s.toString(), fromString);
	}
	public ModResourceSavedAsString(Path save_path, Function<E, String> toString, Function<String, E> fromString) {
		this.save_path = Objects.requireNonNull(save_path);
		this.toString = Objects.requireNonNull(toString);
		this.fromString = Objects.requireNonNull(fromString); 
	}
	@Override
	protected void write(E e) throws IOException {
		String s = toString.apply(e);
		
		if(s == null)
			Files.deleteIfExists(save_path);
		else 
			Files.write(save_path, s.getBytes("utf-8"), CREATE, TRUNCATE_EXISTING);
	}
	@Override
	protected E read() {
		
		try {
			if(Files.notExists(save_path))
				return null;
			else
				return fromString.apply(new String(Files.readAllBytes(save_path), "utf-8"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
};
