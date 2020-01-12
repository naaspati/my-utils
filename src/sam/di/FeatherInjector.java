package sam.di;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Provider;

import org.codejargon.feather.Feather;
import org.codejargon.feather.Key;
import org.codejargon.feather.Provides;

public class FeatherInjector extends Injector {

	protected final Feather feather;

	public FeatherInjector(Object... modules) {
		this(Arrays.asList(modules));
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public FeatherInjector(List modules) {
		modules = new ArrayList(modules);
		modules.add(this);

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
	public FeatherInjector selfFeatherInjector() {
		return this;
	}
}
