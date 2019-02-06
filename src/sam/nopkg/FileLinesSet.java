package sam.nopkg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sam.logging.MyLoggerFactory;

public class FileLinesSet implements AutoCloseable {
	private static final Logger LOGGER = MyLoggerFactory.logger(FileLinesSet.class);
	
	private Set<String> _old, nnew;
	private final boolean gzipped;
	private final Path path;
	private static final String DATE_MARKER = "#DATE = ";

	public FileLinesSet(Path path, boolean gzipped) {
		this.gzipped = gzipped;
		this.path = path;
		nnew = new LinkedHashSet<>();
	}
	
	public Set<String> getOld() {
		return old();
	}
	private Set<String> old() {
		if(_old != null)
			return _old;
		
		if(Files.exists(path)) {
			Set<String> o  = new HashSet<>();
			try(InputStream is = Files.newInputStream(path, StandardOpenOption.READ);
					InputStream is2 = !gzipped ? is : new GZIPInputStream(is);
					InputStreamReader isr = new InputStreamReader(is2, "utf-8");
					BufferedReader reader = new BufferedReader(isr);
					) {
				reader.lines().forEach(s -> {
					if(!s.isEmpty() && (s.charAt(0) != '#' || !s.startsWith(DATE_MARKER)))
						o.add(s);
						
				});
			} catch (IOException e) {
				throw new RuntimeException("failed to read: "+path, e);
			}
			_old = o.isEmpty() ? Collections.emptySet() : o;
			LOGGER.fine(() -> "lines: "+_old.size()+", in file: \""+path+"\"");
		} else {
			_old = Collections.emptySet();
			LOGGER.warning("file not found: "+path);
		}
		
		return _old;
	}

	public Set<String> getNew() {
		return nnew;
	}
	public boolean isGzipped() {
		return gzipped;
	}
	
	public boolean contains(String s) {
		return nnew.contains(s) || old().contains(s);
	}
	public void add(String s) {
		nnew.add(s);
	}

	@Override
	public void close() throws Exception {
		if(nnew.isEmpty())
			return;
		
		try(OutputStream os = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND );
				OutputStream os2 = !gzipped ? os : new GZIPOutputStream(os);
				OutputStreamWriter osr = new OutputStreamWriter(os2, "utf-8");
				BufferedWriter write = new BufferedWriter(osr);
				) {
			write.append("#DATE = "+LocalDateTime.now()).append('\n');
			
			for (String s : nnew) 
				write.append(s).append('\n');
		}
	}
}
