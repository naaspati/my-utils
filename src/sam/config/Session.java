package sam.config;

import static sam.myutils.MyUtilsException.noError;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import sam.logging.MyLoggerFactory;
import sam.myutils.System2;


// VERSION = 0.12;
public class Session {
	private static final Logger LOGGER = MyLoggerFactory.logger(Session.class.getSimpleName());

	private static Properties properties;
	private static boolean modified = false;

	private static Properties props() {
		init0();
		return properties;
	}

	private static void init0() {
		if(properties != null) return;
		
		if(SESSION_FILE == null) {
			properties = new Properties();
			return;
		}

		try {
			properties = new Properties();
			if(Files.notExists(SESSION_FILE))
				return;
			properties.load(Files.newInputStream(SESSION_FILE));
			LOGGER.config(() -> "session_file: "+SESSION_FILE);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "failed to load session_file: "+SESSION_FILE, e);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Path path2 = null;
			try {
				save(SESSION_FILE);
			} catch (IOException | URISyntaxException e) {
				LOGGER.log(Level.SEVERE, "failed to session_file: "+path2, e);
			}
		}) );
	}

	public static final Path SESSION_FILE = noError(() -> path_0());

	private static Path path_0() throws URISyntaxException {
		String s = System2.lookupAny("session_file", "session.file", "SESSION_FILE", "SESSION.FILE");

		Path p = null;

		if(s == null) {
			LOGGER.warning("session_file variable not set");
			return null;
		} else 
			p = Paths.get(s);

		if(p != null && Files.isDirectory(p))
			return p.resolve("session.properties");
		return p;
	}
	private static String toKey(Class<?> cls, String key) {
		if(key == null || key.trim().isEmpty())
			throw new IllegalArgumentException("in valid key: '"+key+"'");

		return cls.getCanonicalName()+"."+key;
	}

	public static String getProperty(Class<?> cls, String key) {
		String key2 = toKey(cls, key);
		String s =  props().getProperty(key2);
		LOGGER.config(() -> "GET: "+key2+"="+s);
		return s;
	}
	public static String getProperty(Class<?> cls, String key, String defaultValue) {
		String s = getProperty(cls, key);
		return s == null ? defaultValue : s;
	}
	@SuppressWarnings("unchecked")
	public static <E> E get(Object key) {
		Objects.requireNonNull(key);
		E obj = (E)props().get(key);
		LOGGER.config(() -> "GET: "+key+"="+obj);
		return obj;
	}
	@SuppressWarnings("unchecked")
	public static <E> E get(Object key, E defaultValue) {
		Object s = get(key);
		return s == null ? defaultValue : (E)s;
	}
	public static <R> R getProperty(Class<?> cls, String key, R defaultValue, Function<String, R> parser) {
		String s = getProperty(cls, key);
		return s == null ? defaultValue : parser.apply(s); 
	}
	public static boolean has(Class<?> cls, String key) {
		return props().containsKey(toKey(cls, key));
	}
	public static void remove(Class<?> cls, String key) {
		if(key == null)
			return;
		modified = true;
		props().remove(toKey(cls, key));
		LOGGER.config(() -> "REMOVE: "+toKey(cls, key));
	}
	public static void remove(Object key) {
		if(key == null)
			return;
		modified = true;
		props().remove(key);
		LOGGER.config(() -> "REMOVE: "+key);
	}

	public static void put(Class<?> ownerClass, String key, Object value) {
		put(toKey(ownerClass, key), value);
	}

	public static void putAsString(Class<?> ownerClass, Object key, Object value) {
		put(toKey(ownerClass, key.toString()), value.toString());
	}
	@SuppressWarnings("unchecked")
	public static <E> E get(Class<?> ownerClass, String key) {
		return (E)get(toKey(ownerClass, key));
	}
	public static void put(Object key, Object value) {
		Objects.requireNonNull(key, "key cannot be a null");

		Object oldValue = props().put(key, value);
		if(key.getClass() == String.class && (value == null || value.getClass() == String.class)) {
			modified = modified || !Objects.equals(oldValue, value);
			LOGGER.config(() -> "PUT: "+key+"="+value);			
		}
	}
	public static void putIfAbsent(Object key, Object value) {
		LOGGER.config(() -> "PUTIFABSENT: "+key+"="+value);
		props().putIfAbsent(key, value);
	}
	public static void putIfAbsent(Class<?> cls, String key, Object value) {
		LOGGER.config(() -> "PUTIFABSENT: "+key+"="+value);
		props().putIfAbsent(toKey(cls, key), value);
	}
	public static void forEach(BiConsumer<Object, Object> action) {
		props().forEach(action);
	}
	private static void save(Path path) throws IOException, URISyntaxException {
		if(!modified)
			return;

		modified = false;
		properties.keySet().removeIf(k -> k == null || k.getClass() != String.class);
		properties.values().removeIf(k -> k == null || k.getClass() != String.class);
		properties.store(Files.newOutputStream(SESSION_FILE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING), LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));

		LOGGER.config(() -> "SESSION SAVED ");
	}
}
