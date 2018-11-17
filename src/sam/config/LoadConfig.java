package sam.config;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Logger;

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
			URL u = ClassLoader.getSystemResource(".config.properties");
			if(u != null) {
				Logger.getLogger(LoadConfig.class.getName()).fine("config_file: "+u);
				load(u.openStream());
				return;
			} else {
				Logger.getLogger(LoadConfig.class.getName()).warning("\".config.properties\" not found");
			}
		} else {
			Logger.getLogger(LoadConfig.class.getName()).fine("config_file: "+p);
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
