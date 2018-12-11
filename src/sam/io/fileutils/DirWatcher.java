package sam.io.fileutils;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

@SuppressWarnings("rawtypes")
public abstract class DirWatcher implements Runnable {
	public final Path dir;
	private Kind[] watchTypes;
	
    public DirWatcher(Path dir, Kind...watchTypes) {
    	if(!Files.isDirectory(dir))
            throw new IllegalArgumentException("path can only be a directory");
    	
		this.dir = dir;
		this.watchTypes = watchTypes;
	}
    @Override
	public void run() {
        try {
            WatchService wa = FileSystems.getDefault().newWatchService();
            WatchKey firstKey = dir.register(wa, watchTypes);
            watchTypes = null;

            while (true) {
                try {
                    WatchKey key2 = wa.take();
                    if (key2 != firstKey)
                        continue;

                    /*
                     * 
                     * WatcherServices reports events twice because the underlying file is updated twice. 
                     * Once for the content and once for the file modified time. 
                     * These events happen within a short time span. 
                     * To solve this, sleep between the poll() or take() calls and the key.pollEvents() call.
                     * 
                     */
                    Thread.sleep(100);
                    for (WatchEvent<?> we : key2.pollEvents()) {
                        if (we.kind() == StandardWatchEventKinds.OVERFLOW)
                            continue;

                        onEvent((Path)we.context(), we);
                    }
                    if (!key2.reset())
                        return;
                } catch (Exception e) {
                    if(!onErrorContinue(e))
                        return;
                }
            }
        } catch (Exception e) {
        	failed(e);
        }
    }
	
	/**
	 * process failed completely
	 * @param e
	 */
	protected abstract void failed(Exception e);
	
	/**
	 * possibly error while polling 
	 * @param e
	 * @return return return if you still want to continue; 
	 */
	protected abstract boolean onErrorContinue(Exception e);
	
	protected abstract void onEvent(Path context, WatchEvent<?> we);
}
