package sam.di;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Provider;

import org.codejargon.feather.Feather;
import org.codejargon.feather.Key;
import org.codejargon.feather.Provides;

import sam.myutils.Checker;

@SuppressWarnings({"rawtypes"})
public class FeatherInjector extends Injector {

	protected final Feather feather;

	public FeatherInjector(Object... additionalModules) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		List<Object> modules = prepare_modules(additionalModules);
		modules.add(this);

		this.feather = Feather.with(default_mappings(), modules);
	}

	public static List<Object> prepare_modules(Object... additionalModules) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		List<Object> modules = default_modules();
		if(Checker.isNotEmpty(additionalModules))
			modules.addAll(Arrays.asList(additionalModules));

		if(!modules.isEmpty() && logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder("\ndi.producers found\n");
			modules.forEach((s) -> sb.append("  ").append(s).append('\n'));
			logger.debug(() -> sb.toString());
		}

		return modules;
	}

	public static List<Object> default_modules() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		return Injector.linesToObject(ClassLoader.getSystemResourceAsStream("di.producers"));
	}
	public static Map<Class, Class> default_mappings() throws ClassNotFoundException, IOException {
		return Injector.mapping(ClassLoader.getSystemResourceAsStream("di.mapping.properties"));
	}

	public FeatherInjector(Map<Class, Class> di_mapping, List<Object> modules) {
		this.feather = Feather.with(modules);
	}
	@Override
	public <E> E instance(Class<E> type) {
		return feather.instance(type);
	}
	@Override
	public <E, A extends Annotation> E instance(Class<E> type, Class<A> qualifier) {
		return feather.instance(Key.of(type, qualifier));
	}
	@Override
	public <E> E instance(Class<E> type, String name) {
		return (E) feather.instance(Key.of(type, name));
	}
	@Override
	public <E> Provider<E> provider(Class<E> type) {
		return feather.provider(type);
	}
	@Override
	public <E, A extends Annotation> Provider<E> provider(Class<E> type, Class<A> qualifier) {
		return feather.provider(Key.of(type, qualifier));
	}
	@Override
	public <E> Provider<E> provider(Class<E> type, String name) {
		return feather.provider(Key.of(type, name));
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
