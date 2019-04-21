package sam.di;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.jupiter.api.Test;

class InjectorTest {

	@Test
	void test() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		FeatherInjector injector = new FeatherInjector();
		Injector.init(injector);
		
		assertThrows(IllegalStateException.class, () -> Injector.init(injector));
		assertSame(injector, Injector.getInstance());
		
		A a = injector.instance(A.class);
		
		assertSame(injector, a.injector);
		assertSame(injector, a.provider.get());
		assertSame(injector, a.injector2);
		assertSame(injector, a.provider2.get());
	}

	public static class A {
		final Injector injector;
		final Provider<Injector> provider;
		final FeatherInjector injector2;
		final Provider<FeatherInjector> provider2;
		
		@Inject
		public A(Injector injector, Provider<Injector> provider, FeatherInjector injector2, Provider<FeatherInjector> provider2) {
			this.injector = injector;
			this.provider = provider;
			this.injector2 = injector2;
			this.provider2 = provider2;
		}
	}
}
