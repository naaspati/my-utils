package sam.di;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

public interface Injector {
	public <E> E instance(Class<E> type);
	public <E, A extends Annotation> E instance(Class<E> type, Class<A> qualifier);
	
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
		}
		
		if(map == null)
			map = Collections.emptyMap();
		
		return map;
	}
	
	public static List<Object> linesToObject(InputStream is) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
		if(is == null)
			Collections.emptyList();
		
		List<Object> list = new ArrayList<>();
		
		try(BufferedReader b = new BufferedReader(new InputStreamReader(is))) {
			Iterator<String> itr = b.lines().map(String::trim).filter(s -> !s.isEmpty() && s.charAt(0) != '#').iterator();
			while (itr.hasNext()) {
				list.add(Class.forName(itr.next()).newInstance());
			}
		}
		
		return list;
	}
	
}

