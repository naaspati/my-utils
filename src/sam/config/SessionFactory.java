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
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import sam.logging.MyLoggerFactory;
import sam.myutils.System2;


// VERSION = 0.13;
public class SessionFactory {
	private final Logger LOGGER = MyLoggerFactory.logger(Session.class);

	@SuppressWarnings("rawtypes")
	private HashMap<Class, Session> configs = new HashMap<>();
	private boolean _modified = false;

	private Properties properties;

	@SuppressWarnings("rawtypes")
	public static Session getSession(Class cls) {
		return getInstance().getSession0(cls);
	}
	
	private Session global;
	public static Session sharedSession() {
		return getInstance().global();
	}
	
	private Session global() {
		if(global == null)
			 global = new Session(null);
		return global;
	}
	private Session getSession0(@SuppressWarnings("rawtypes") Class cls) {
		Session s = configs.get(cls);
		if(s == null)
			configs.put(cls, s = new Session(cls));
		return s;
	}

	private static volatile SessionFactory INSTANCE;

	public static SessionFactory getInstance() {
		if (INSTANCE != null)
			return INSTANCE;

		synchronized (SessionFactory.class) {
			if (INSTANCE != null)
				return INSTANCE;

			INSTANCE = new SessionFactory();
			return INSTANCE;
		}
	}

	private SessionFactory() {}

	private void init() {
		if(properties != null) return;

		Path sessionFile = getLocation();
		properties = new Properties();

		if(sessionFile == null || Files.notExists(sessionFile))
			return;
		
		Logger LOGGER0 = MyLoggerFactory.logger(SessionFactory.class);

		try {
			properties.load(Files.newInputStream(sessionFile));
			LOGGER0.config(() -> "session_file: "+sessionFile);
		} catch (IOException e) {
			LOGGER0.log(Level.SEVERE, "failed to load session_file: "+sessionFile, e);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Path path2 = null;
			try {
				save(sessionFile);
			} catch (IOException | URISyntaxException e) {
				LOGGER0.log(Level.SEVERE, "failed to session_file: "+path2, e);
			}
		}) );
	}

	private Path _sessionFile;
	public void setLocation(Path path) {
		_sessionFile = path;
	}

	public Path getLocation() {
		if(_sessionFile != null) return _sessionFile;
		return _sessionFile = path_0();
	}
	private Path path_0() {
		String s = System2.lookupAny("session_file", "session.file", "sessionFile", "SESSION.FILE");
		
		Logger LOGGER0 = MyLoggerFactory.logger(SessionFactory.class);

		if(s == null) {
			LOGGER0.warning("session_file variable not set: Session will not be saved in a File");
			return null;
		} 

		Path p = Paths.get(s);

		if(p != null && Files.isDirectory(p))
			return p.resolve("session.properties");
		return p;
	}

	private void save(Path path) throws IOException, URISyntaxException {
		if(!_modified)
			return;

		_modified = false;
		properties.store(Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING), LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
		MyLoggerFactory.logger(SessionFactory.class).config(() -> "SESSION SAVED: "+path);
	}

	public class Session {
		private final String _prefix;

		/**
		 * stores non-string-key -&gt; non-string-value mappings
		 */
		private IdentityHashMap<Object, Object> objectmap;
		/**
		 * stores string-key -&gt; non-string-value mappings
		 */
		private HashMap<String, Object> stringObjectmap;
		/**
		 * stores string-key -&gt; string-value mappings
		 */
		private HashMap<String, String> stringmap0;

		@SuppressWarnings("rawtypes")
		private Session(Class cls) {
			_prefix = cls == null ? null : cls.getName().concat(".");
		}
		public String key(Object key) {
			return _prefix == null ? (String)key : _prefix.concat((String)key);
		}
		private boolean isString(Object key) {
			return key.getClass() == String.class;
		}
		public <E> E get(Object key) {
			return get(key, null);
		}

		public String getString(Object key) {
			init();
			Object result = null;
			if(stringmap0 != null)
				result = stringmap0.get(key);
			if(result != null)
				return (String) result;

			result = properties.get(key(key));
			if(result != null) {
				if(stringmap0 == null)
					stringmap0 = new HashMap<>();
				stringmap0.put((String)key, (String)result);
			}
			return (String)result;
		}

		@SuppressWarnings("unchecked")
		public <E> E get(Object key, E defaultValue) {
			Objects.requireNonNull(key);
			Object result = null;
			if(isString(key)) {
				if(stringObjectmap != null) 
					result = stringObjectmap.get(key);
				if(result == null) 
					result = getString(key);
				return result == null ? defaultValue : (E)result; 
			}
			if(result == null && objectmap != null)
				objectmap.get(key);

			return result == null ? defaultValue : (E)result;  
		}
		public String getProperty(String key) {
			return getProperty(key, null);
		}
		public <E> E getProperty(String key, E defaultValue, Function<String, E> converter) {
			String s = getProperty(key);
			return s == null ? defaultValue : converter.apply(s);
		}
		public String getProperty(String key, String defaultValue) {
			Objects.requireNonNull(key);
			String v = getString(key);

			LOGGER.config(() -> "GET: "+key(key)+(v == null ? "=" : "="+v));
			return v == null ? defaultValue : v;
		}
		public boolean contains(Object key) {
			Objects.requireNonNull(key);
			if(isString(key))
				return getString(key) != null || (stringObjectmap != null && stringObjectmap.containsKey(key));
			else
				return (objectmap != null && objectmap.containsKey(key));
		}
		public void remove(Object key) {
			if(key == null) return;
			if(isString(key)) {
				init();
				_modified = properties.remove(key(key)) != null || _modified;
				if(stringObjectmap != null)
					stringObjectmap.remove(key);
				LOGGER.config("REMOVE: "+key(key));
			} else if(objectmap != null) 
				objectmap.remove(key);
		}
		public void put(Object key, Object value) {
			Objects.requireNonNull(key);
			if(value == null) {
				remove(key);
				return;
			}
			if(isString(key)) {
				if(isString(value)) {
					init();
					Object prop = stringmap0.put((String)key, (String)value);
					if(prop == null) {
						init();
						prop = properties.put(key(key), value);
					}
					if(Objects.equals(value, prop))
						return;
					
					_modified = true;
					Object v2 = prop;
					if(prop != null)
						LOGGER.config(() -> "CHANGED "+key(key) +"="+v2+" -> "+value);
					else
						LOGGER.config(() -> "PUT "+key(key) +"="+value);
				} else {
					if(stringObjectmap == null)
						stringObjectmap = new HashMap<>();
					stringObjectmap.put((String)key, value);
				}
			} else {
				if(objectmap == null)
					objectmap = new IdentityHashMap<>();
				objectmap.put(key, value);
			}
		}

		public void putIfAbsent(Object key, Object value) {
			Objects.requireNonNull(key);
			if(contains(key))
				return;
			put(key, value);
		}
	}
}
