package sam.nopkg;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

public class SavedAsStringResource<E> extends SavedResource<E> {
	private static final Charset charset = StandardCharsets.UTF_8;
	public final Path save_path0;
	public final Function<E, String> toString;
	public final Function<String, E> fromString;
	
	public SavedAsStringResource(Path save_path, Function<String, E> fromString) {
		this(save_path, s -> s == null ? null : s.toString(), fromString);
	}
	public SavedAsStringResource(Path save_path, Function<E, String> toString, Function<String, E> fromString) {
		this.save_path0 = Objects.requireNonNull(save_path);
		this.toString = Objects.requireNonNull(toString);
		this.fromString = Objects.requireNonNull(fromString); 
	}
	@Override
	protected void write(E e) throws IOException {
		String s = toString.apply(e);
		
		if(s == null)
			Files.deleteIfExists(save_path0);
		else  {
			try(FileChannel c = FileChannel.open(save_path0, CREATE, TRUNCATE_EXISTING, WRITE)) {
				c.write(charset.encode(CharBuffer.wrap(s)));
			}
		}
	}
	@Override
	protected E read() {
		try {
			if(Files.notExists(save_path0))
				return null;
			else {
				try(FileChannel fc = FileChannel.open(save_path0, READ)) {
					return fromString.apply(charset.decode(fc.map(MapMode.READ_ONLY, 0, fc.size())).toString());					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
};
