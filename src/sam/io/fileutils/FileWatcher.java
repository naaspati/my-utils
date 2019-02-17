package sam.io.fileutils;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public abstract class FileWatcher extends DirWatcher {
	private final Path name;

	public FileWatcher(Path path) {
		super(path.getParent(), StandardWatchEventKinds.ENTRY_MODIFY);
		this.name = path.getFileName();
	}
	@Override
	protected void failed(Exception e) {
		e.printStackTrace();
	}
	@Override
	protected boolean onErrorContinue(Exception e) {
		return true;
	}
	@Override
	protected void onEvent(Path context, WatchEvent<?> we) {
		if(name.equals(context))
			onModify();
	}
	protected abstract void onModify();
}
