package sam.nopkg;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FileModResource extends ModResource<File> {
	public final StringModResource data;
	
	public FileModResource(Path save_path) {
		this.data = new StringModResource(save_path);
	}
	@Override
	protected void write(File e) throws IOException {
		data.write(e == null ? null : e.toString());
	}
	@Override
	protected File read() {
		String s = data.read();
		return s == null ? null : new File(s);
	}
};
