package sam.di;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public interface InjectorProvider {
	public static List<InjectorProvider> detect() {
		List<InjectorProvider> list = new ArrayList<>();
		ServiceLoader.load(InjectorProvider.class).forEach(list::add);
		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Object> detectAndAdd(Object...toAdd) {
		List list = detect();
		for (Object o : toAdd) 
			list.add(o);
		return list;
	}
}
