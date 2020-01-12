package sam.nopkg;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sam.logging.Logger;
public class FileLinesSet implements AutoCloseable {
	private static final Logger LOGGER = Logger.getLogger(FileLinesSet.class);

	private Set<String> _old, nnew;
	private final boolean gzipped;
	private final Path path;
	private static final String DATE_MARKER = "#DATE = ";
	private int mod;

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
			Set<String> o  = newSet();
			try(InputStream is = Files.newInputStream(path, READ);
					InputStream is2 = !gzipped ? is : new GZIPInputStream(is);
					InputStreamReader isr = new InputStreamReader(is2, "utf-8");
					BufferedReader reader = new BufferedReader(isr);
					) {
				reader.lines().forEach(s -> {
					if(!s.isEmpty() && !isDate(s))
						o.add(s);

				});
			} catch (IOException e) {
				throw new RuntimeException("failed to read: "+path, e);
			}
			_old = o.isEmpty() ? Collections.emptySet() : o;
			LOGGER.debug("lines: {}, inFile:\"{}\"",_old.size(),path);
		} else {
			_old = Collections.emptySet();
			LOGGER.warn("file not found: "+path);
		}

		return _old;
	}

	private boolean isDate(String s) {
		return !s.isEmpty() && s.charAt(0) == '#' && s.startsWith(DATE_MARKER);
	}

	protected Set<String> newSet() {
		return new HashSet<>();
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
	public boolean add(String s) {
		if(contains(s))
			return false;
		return nnew.add(s);
	}
	public boolean remove(String s) {
		boolean o = old().remove(s);
		if(o) 
			mod++;

		boolean n = nnew.remove(s);
		return o || n;
	}
	public boolean removeAll(Collection<String> s) {
		boolean o = old().removeAll(s);
		if(o) 
			mod++;

		boolean n = nnew.removeAll(s);
		return o || n;
	} 

	@Override
	public void close() throws IOException {
		if(!isModified())
			return;

		List<String> old = null;
		boolean truncate = false;

		if(mod != 0) {
			truncate = true;

			if(!_old.isEmpty() && Files.exists(path)) {
				Iterator<String> itr = Files.lines(path).iterator();
				old = new ArrayList<>();

				while (itr.hasNext()) {
					String s = itr.next();
					if(isDate(s))
						old.add(s);
					else if(_old.contains(s))
						old.add(s);
				}
			}
		}
		try(OutputStream os = Files.newOutputStream(path, CREATE, truncate ?  TRUNCATE_EXISTING : APPEND );
				OutputStream os2 = !gzipped ? os : new GZIPOutputStream(os);
				OutputStreamWriter osr = new OutputStreamWriter(os2, "utf-8");
				BufferedWriter write = new BufferedWriter(osr);
				) {

			if(old != null && !old.isEmpty()) {
				for (int i = 0; i < old.size(); i++) {
					String s = old.get(i);
					if(!(isDate(s) && (i == old.size() - 1 || isDate(old.get(i + 1)))))
						write.append(s).append('\n');
				}
			}

			System.out.println(old == null ? null : old.size());
			
			if(!nnew.isEmpty()) {
				write.append("#DATE = "+LocalDateTime.now()).append('\n');

				for (String s : nnew) 
					write.append(s).append('\n');				
			}


		}
	}

	public boolean isModified() {
		return !nnew.isEmpty() || mod != 0;
	}
}
