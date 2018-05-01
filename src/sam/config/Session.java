package sam.config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Session {
	public static final double VERSION = 0.401;

	private static volatile Map<String, String> store;
	private static Path path;
	private static boolean modified = false;

	public static void setPath(Path path) {
		Objects.requireNonNull(path, "session path cannot be null");
		Session.path = path;
	}
	public static Path getPath() {
		return path;
	}
	private static volatile boolean threadSafe; 
	public static void makeThreadSafe() {
		if(threadSafe)
			return;

		threadSafe = true;
		init();
		store = Collections.synchronizedMap(store);
	}

	public static void reset() {
		store = null;
		if(threadSafe) {
			threadSafe = false;
			makeThreadSafe();
		}
		else
			init();
	}
	private static void init(){
		if(store == null) {
			synchronized (Session.class) {
				if(store == null) {
					store = new LinkedHashMap<>();
					try {
						if(path == null)
							path = Paths.get(ClassLoader.getSystemResource(".").toURI());
						if(Files.isDirectory(path))
							path  = path.resolve("session.properties");

						if(Files.notExists(path))
							logger().warning(path.getFileName()+" not found, at path:"+path+"  thus will be created");
						else {
							Files.lines(path)
							.filter(s ->  !s.trim().startsWith("#") && s.indexOf('=') > 0)
							.map(Session::parseKeyValue)
							.filter(s -> s.length != 0)
							.forEach(s -> store.put(s[0], s[1]));							
						}
					} catch (IOException|NullPointerException | URISyntaxException e) {
						logger().severe("failed to read session.properties, path: "+path+"  "+e);
					}	

					Runtime.getRuntime().addShutdownHook(new Thread(() -> {
						try {
							save();
						} catch (IOException | URISyntaxException e) {
							logger().severe("failed to save session:"+path+"  "+e);
						}
					}));
				}
			}
		}
	}
	private static String[] parseKeyValue(String s) {
		int n = s.indexOf('=');
		String key = s.substring(0, n);
		int count = countPrecedingBackwordSlashes(s.toCharArray(), n);
		while(count%2 != 0) {
			n = s.indexOf('=', n+1);
			if(n == -1) {
				logger().severe("bad entry found: "+s);
				return new String[0];
			}
			key = s.substring(0, n);
			count = countPrecedingBackwordSlashes(s.toCharArray(), n);
		}
		String value = unescapeString(key.length() == s.length() - 1 ? "" : s.substring(n + 1));
		key = unescapeKey(key);
		
		return new String[] {key, value};
	}

	private static String unescapeKey(String key) {
		return unescapeString(key.replace("\\=", "="));
	}

	private static String unescapeString(String string) {
		if(string == null || string.isEmpty())
			return string;

		if(string.indexOf('\\') == -1)
			return string;

		char[] chars = string.toCharArray();
		StringBuilder sb = new StringBuilder(chars.length);

		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if((c == 'n' || c == 'r') && i != 0 && countPrecedingBackwordSlashes(chars, i)%2 != 0)
				sb.setCharAt(sb.length() - 1, c == 'n' ? '\n' : '\r');
			else
				sb.append(c);
		}
		return sb.toString();
	}
	private static String escapeKey(String key) {
		return escapeString(key.replace("=", "\\="));
	}
	private static String escapeString(String string) {
		if(string == null || string.isEmpty())
			return string;
		
		if(string.indexOf('\r') == -1 && string.indexOf('\n') == -1)
			return string;
		
		//string.replace("\r", "\\r").replace("\n", "\\n")
		
		char[] chars = string.toCharArray();
		StringBuilder sb = new StringBuilder(chars.length);
		
		for (char c : chars) {
			if(c == '\r' || c == '\n')
				sb.append(c == '\r' ? "\\r" : "\\n");
			else
				sb.append(c);
		}
		return sb.toString();
	}
	
	private static int countPrecedingBackwordSlashes(char[] chars, int startAt) {
		if(chars[startAt - 1] == '\\') {
			int i = 2;
			while(chars[startAt - i] == '\\') {i++;};
			return i - 1;
		}
		else
			return 0;
	}
	private static Logger logger() {
		return Logger.getLogger(Session.class.getCanonicalName());
	}

	public static String get(String key) {
		init();
		return store.get(key);
	}
	public static String get(String key, String defaultValue) {
		String s = get(key);
		return s == null ? defaultValue : s;
	}
	public static <R> R get(String key, R defaultValue, Function<String, R> parser) {
		String s = get(key);
		return s == null ? defaultValue : parser.apply(s); 
	}
	public static boolean has(String key) {
		init();
		return store.containsKey(key);
	}
	public static void remove(String key) {
		init();
		modified = true;
		store.remove(key);
	}

	public static void put(String key, String value) {
		if(key == null)
			throw new NullPointerException("key cannot be a null"); 

		if(key.trim().isEmpty())
			throw new IllegalArgumentException("key cannot be a empty string");
		
		if(value == null)
			throw new NullPointerException("value cannot be a null");
			
		init();
		String oldValue = store.get(key);
		modified = modified || !Objects.equals(oldValue, value);
		store.put(key, value);
	}
	public static void putIfAbsent(String key, String value) {
		if(has(key))
			return;
		put(key, value);
	}

	public static void forEach(BiConsumer<String, String> action) {
		store.forEach(action);
	}
	
	public static void save() throws IOException, URISyntaxException {
		if(!modified)
			return;

		modified = false;

		String date = "# --TIME -> "+LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))+"  VERSION: "+VERSION;

		if(path == null)
			path = Paths.get(ClassLoader.getSystemResource(".").toURI()).resolve("session.properties");

		if(Files.exists(path)) {
			List<String> list =  Files.lines(path)
					.map(s -> {
						if(s.trim().startsWith("#") || s.indexOf('=') < 0)
							return s;

						String[] keyValue = parseKeyValue(s);
						String key = keyValue[0];
						String value = keyValue[1];
						
						if(!has(key))
							return null;
						
						String newValue = get(key);
						remove(key);
						
						if(Objects.equals(value, newValue))
							return s;
						
						return escapeKey(key)+"="+escapeString(newValue);
					})
					.filter(s -> s != null)
					.collect(Collectors.toList());

			
			if(!list.isEmpty() && list.get(0).startsWith("# --TIME -> "))
				list.set(0, date);
			else
				list.add(0, date);

			store.forEach((key, value) -> list.add(escapeKey(key)+"="+escapeString(value)));
			Files.write(path, list, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		}
		else {
			StringBuilder sb = new StringBuilder(date).append('\n');
			store.forEach((key,value) -> sb.append(escapeKey(key)).append('=').append(escapeString(value)).append('\n'));
			Files.write(path, sb.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		}
	}
}
