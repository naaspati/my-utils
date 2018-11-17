package sam.config;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import sam.logging.MyLoggerFactory;

public final class LoadConfig {
	public static void load() throws URISyntaxException, IOException {
		String s = getProperty("config_file");
		if(s == null)
			s = getenv("config_file");
		if(s == null)
			s = ".config.properties";
		
		Path p = Paths.get(s);
		if(Files.notExists(p)) {
			p = null;
			InputStream u = ClassLoader.getSystemResourceAsStream(".config.properties");
			if(u != null) {
				load(u);
				return;
			} else {
				MyLoggerFactory.logger(LoadConfig.class).warning("\".config.properties\" not found");
			}
		} else {
			load(Files.newInputStream(p));
		}
	}
	public static void load(InputStream is) throws IOException {
		Properties2 p = new Properties2(is);
		p.setSystemLookup(true, true);
		
		HashMap<String, String> map = new HashMap<>();
		p.forEach((s,t) -> map.put(s, t));
		map.forEach(System::setProperty);
	}

}
