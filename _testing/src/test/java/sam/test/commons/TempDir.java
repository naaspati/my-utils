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

public class TempDir implements Closeable {
	private final Path tempDir;
	private final AtomicInteger counter;
	private final Logger logger;

	public TempDir(String dirname, Logger logger) throws IOException {
		tempDir = Files.createTempDirectory(dirname);
		counter = new AtomicInteger(0);
		this.logger = logger;

		if(logger != null)
			logger.info(() -> "CREATE: "+tempDir);
	}

	public void close() throws IOException {
		File file = tempDir == null ? null : tempDir.toFile();
		String[] files = file == null ? null : file.list();

		if(files != null) {
			for (String s : files) {
				if(!new File(file, s).delete()) {
					if(logger != null)
						logger.warning("failed to delete: "+file.getName()+"\\"+s);	
				}
			} 
			file.delete();
		}

		counter.set(0);
		if(logger != null)
			logger.info(() -> "DELETE: "+tempDir+(files == null ? "" : ", ["+String.join(",", files)+"]"));
	}

	public Path nextPath() {
		Path p = tempDir.resolve(String.valueOf(counter.incrementAndGet()));
		assertFalse(Files.exists(p));
		if(logger != null)
			logger.info(() -> "NextPath: "+p);
		return p;
	}

	public static void deleteQuietly(Path p, Logger logger) {
		if(p == null)
			return;
		
		try {
			Files.deleteIfExists(p);
		} catch (Exception e) {
			if(logger != null)
				logger.log(Level.WARNING, "failed to delete: "+p, e);
		}
	}

	public void deleteQuietly(Path p) {
		deleteQuietly(p, logger);
	}
}
