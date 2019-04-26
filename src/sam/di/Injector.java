package sam.di;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import javax.inject.Provider;

import sam.logging.Logger;
import sam.myutils.Checker;
import sam.nopkg.EnsureSingleton;

public abstract class Injector {
	static final Logger logger = Logger.getLogger(Injector.class);
	
	private static final EnsureSingleton singleton = new EnsureSingleton();
	private static volatile Injector instance;

	public static Injector init(Injector impl) {
		Objects.requireNonNull(impl);
		singleton.init();
		instance = impl;
		
		logger.debug(() -> "Injector INIT with: " + impl);
		
		return instance; 
	}

	public static Injector getInstance() {
		return instance;
	}

	public abstract  <E> E instance(Class<E> type);
	public abstract <E, A extends Annotation> E instance(Class<E> type, Class<A> qualifier);
	public abstract <E> E instance(Class<E> type, String name);
	
	public abstract  <E> Provider<E> provider(Class<E> type);
	public abstract <E, A extends Annotation> Provider<E> provider(Class<E> type, Class<A> qualifier);
	public abstract <E> Provider<E> provider(Class<E> type, String name);

	@SuppressWarnings("rawtypes")
	public static Map<Class, Class> mapping(InputStream is) throws ClassNotFoundException, IOException {
		if(is == null)
			return Collections.emptyMap();

		Map<Class, Class> map = null;

		if(is != null) {
			Properties p = new Properties();
			p.load(is);

			if(!p.isEmpty()) {
				map = new HashMap<>();
				for (Entry<Object, Object> e : p.entrySet()) {
					map.put(Class.forName(e.getKey().toString()), Class.forName(e.getValue().toString()));
				}
			}
			if(!map.isEmpty() && logger.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder("\ndi.mapping found\n");
				map.forEach((s,t) -> sb.append("  ").append(s).append(" -> ").append(t).append('\n'));
				logger.debug(() -> sb.toString());
			}
		}

		if(Checker.isEmpty(map))
			map = Collections.emptyMap();

		return map;
	}

	public static List<Object> linesToObject(InputStream is) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
		List<Object> list = new ArrayList<>();

		if(is == null)
			return list;

		try(BufferedReader b = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = b.readLine()) != null) {
				line = line.trim();
				if(!(line.isEmpty() || line.charAt(0) =='#'))
					list.add(Class.forName(line).newInstance());
			} 
		}

		return list;
	}

}

