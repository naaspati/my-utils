package sam.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public interface GZipUtils {
	public static Stream<String> lines(Path path) throws IOException {
		GZIPInputStream gis = new GZIPInputStream(Files.newInputStream(path, StandardOpenOption.READ));
		InputStreamReader reader = new InputStreamReader(gis);
		BufferedReader br = new BufferedReader(reader);

		try {
			return br.lines().onClose(() -> {
				try {
					br.close();
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		} catch (Throwable e) {
			br.close();
			throw e;
		}
	}
	
	public static void write(Path path, Iterable<String> lines, StandardOpenOption...options) throws IOException {
		Objects.requireNonNull(path);
		Objects.requireNonNull(lines);
		
		try(
				GZIPOutputStream gis = new GZIPOutputStream(Files.newOutputStream(path, options));
				OutputStreamWriter reader = new OutputStreamWriter(gis);
				PrintWriter br = new PrintWriter(reader);
				) {
			
			for (String s : lines) 
				br.println(s);
		}
	}
}
