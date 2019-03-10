package sam.test.commons;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TempDir implements Closeable {
	private final Path tempDir;
	private final AtomicInteger counter;
	
	protected abstract Logger logger();
	
	public TempDir(String dirname) throws IOException {
		tempDir = Files.createTempDirectory(dirname);
		counter = new AtomicInteger(0);

		logger().info(() -> "CREATE: "+tempDir);
	}
	
	
	public void close() throws IOException {
		File file = tempDir == null ? null : tempDir.toFile();
		String[] files = file == null ? null : file.list();

		if(files != null) {
			for (String s : files) {
				if(!new File(file, s).delete())
					logger().warning("failed to delete: "+file.getName()+"\\"+s);
			} 
		}

		counter.set(0);
		logger().info(() -> "DELETE: "+tempDir+(files == null ? "" : ", ["+String.join(",", files)+"]"));
	}

	public Path nextPath() {
		Path p = tempDir.resolve(String.valueOf(counter.incrementAndGet()));
		assertFalse(Files.exists(p));
		logger().info(() -> "NextPath: "+p);
		return p;
	}

	public void deleteQuietly(Path p) {
		if(p == null)
			return;
		try {
			Files.deleteIfExists(p);
		} catch (Exception e) {
			logger().log(Level.WARNING, "failed to delete: "+p, e);
		}
	}
}
