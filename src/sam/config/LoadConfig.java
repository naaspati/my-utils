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

import sam.logging.Logger;

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
				logger().debug("config_file: {}", u);
				load(u.openStream());
				return;
			} else {
				logger().warn("\".config.properties\" not found");
			}
		} else {
			load(Files.newInputStream(p));
			logger().debug("config_file: {}", p);
		}
	}
	private static Logger logger() {
		return Logger.getLogger(LoadConfig.class);
	}
	public static void load(InputStream is) throws IOException {
		Properties2 p = new Properties2(is, false);
		p.setSystemLookup(true, true);
		
		HashMap<String, String> map = new HashMap<>();
		p.forEach((s,t) -> map.put(s, t));
		
		if(map.isEmpty())
			return;
		
		map.forEach(System::setProperty);
		
		Logger logger = logger();
		if(logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder().append('\n');
			map.forEach((s,t) -> sb.append("  ").append(s).append('=').append('"').append(t).append('"').append('\n'));
			logger.debug(sb.substring(0, sb.length() - 1));
		}
	}

}
