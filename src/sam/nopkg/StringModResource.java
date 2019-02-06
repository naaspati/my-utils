package sam.nopkg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.*;

public class StringModResource extends ModResource<String> {
	public final Path save_path;
	
	public StringModResource(Path save_path) {
		this.save_path = save_path;
	}
	@Override
	protected void write(String e) throws IOException {
		if(e == null)
			Files.deleteIfExists(save_path);
		else 
			Files.write(save_path, e.getBytes("utf-8"), CREATE, TRUNCATE_EXISTING);
	}
	@Override
	protected String read() {
		try {
			return Files.notExists(save_path) ? null : new String(Files.readAllBytes(save_path), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
};
