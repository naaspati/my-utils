package sam.di;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codejargon.feather.Feather;
import org.codejargon.feather.Key;
import org.codejargon.feather.Provides;

@SuppressWarnings({"rawtypes", "unchecked"})
public class FeatherInjector extends Injector {
	
	protected final Feather feather;
	private final Map<Class, Class> di_mapping;
	
	public FeatherInjector(Object... additionalModules) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		this.di_mapping = default_mappings();
		List<Object> modules = prepare_modules(additionalModules);
		modules.add(this);
		
		this.feather = Feather.with(modules);
	}
	
	public static List<Object> prepare_modules(Object... additionalModules) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		List<Object> modules = default_modules();
		modules.addAll(Arrays.asList(additionalModules));
		return modules;
	}
	
	public static List<Object> default_modules() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		return Injector.linesToObject(ClassLoader.getSystemResourceAsStream("di.producers.properties"));
	}
	public static Map<Class, Class> default_mappings() throws ClassNotFoundException, IOException {
		return Injector.mapping(ClassLoader.getSystemResourceAsStream("di.mapping.properties"));
	}

	public FeatherInjector(Map<Class, Class> di_mapping, List<Object> modules) {
		this.di_mapping = di_mapping;
		this.feather = Feather.with(modules);
	}
	
	private Class map(Class type) {
		return di_mapping.getOrDefault(type, type);
	}
	@Override
	public <E> E instance(Class<E> type) {
		return (E) feather.instance(map(type));
	}
	@Override
	public <E, A extends Annotation> E instance(Class<E> type, Class<A> qualifier) {
		return (E) feather.instance(Key.of(map(type), qualifier));
	}
	@Override
	public <E> E instance(Class<E> type, String name) {
		return (E) feather.instance(Key.of(map(type), name));
	}
	
	@Provides
	public FeatherInjector self() {
		return this;
	}
	@Provides
	public Injector self2() {
		return this;
	}
}
