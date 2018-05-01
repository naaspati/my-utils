package sam.fileutils;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.function.Consumer;
import java.util.function.Function;

public class DirWatcher {
    public <T> void start(Path path, Consumer<Path> onupdate, Function<Throwable, Boolean> onError, Kind<T>[] watchTypes) {
        if(!Files.isDirectory(path))
            throw new IllegalArgumentException("path can only be a directory");

        try {
            WatchService wa = FileSystems.getDefault().newWatchService();
            WatchKey firstKey = path.register(wa, watchTypes);

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

                        onupdate.accept((Path)we.context());
                    }
                    if (!key2.reset())
                        return;
                } catch (Exception e) {
                    if(!onError.apply(e))
                        return;
                }
            }
        } catch (Exception e) {
            onError.apply(e);
        }
    }
}
