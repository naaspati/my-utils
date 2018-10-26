package sam.config;

import static sam.myutils.MyUtilsPath.resolveToClassLoaderPath;
import static sam.myutils.System2.lookup;

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


public class Session {
	private static final Logger LOGGER = MyLoggerFactory.logger(Session.class.getSimpleName());
	public static final double VERSION = 0.1;

	private static Properties properties;
	private static boolean modified = false;

	private static Properties props() {
		init0();
		return properties;
	}

	private static void init0() {
		if(properties != null) return;

		Path path = null;
		try {
			path = path();
			properties = new Properties();
			if(Files.notExists(path))
				return;
			properties.load(Files.newInputStream(path));
			Path p = path;
			LOGGER.config(() -> "session_file: "+p);
		} catch (IOException | URISyntaxException e) {
			LOGGER.log(Level.SEVERE, "failed to load session_file: "+path, e);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Path path2 = null;
			try {
				path2 = path();
				save(path2);
			} catch (IOException | URISyntaxException e) {
				LOGGER.log(Level.SEVERE, "failed to session_file: "+path2, e);
			}
		}) );
	}

	private static Path sessionFile;

	private static Path path() throws URISyntaxException {
		if(sessionFile != null) return sessionFile;

		String s = lookup("session_file");
		Path p = null;

		if(s == null)
			LOGGER.warning("session_file variable not set");
		else 
			p = Paths.get(s);

		if(p != null) {
			if(Files.isDirectory(p))
				return sessionFile = p.resolve("session.properties");

			return sessionFile = p;
		}

		return sessionFile = resolveToClassLoaderPath("session.properties");
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
		properties.store(Files.newOutputStream(path(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING), LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
		
		LOGGER.config(() -> "SESSION SAVED ");
	}
}
