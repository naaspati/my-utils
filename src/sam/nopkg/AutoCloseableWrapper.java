package sam.nopkg;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AutoCloseableWrapper<T> implements AutoCloseable {
	private final Supplier<T> getter;
	private final Consumer<T> closer;
	private boolean getInvoked = false;
	private boolean closed = false;
	private T t;

	public AutoCloseableWrapper(Supplier<T> getter, Consumer<T> closer) {
		this.getter = getter;
		this.closer = closer;
	}

	
	public T get() {
		checkClosed();
		
		if(getInvoked)
			return t;

		getInvoked = true;
		return t = getter.get();
	}
	
	private void checkClosed() {
		if(closed)
			throw new IllegalStateException("closed");
	}

	@Override
	public void close() throws Exception {
		if(closed)
			return;
		
		closed = true;
		if(getInvoked)
			closer.accept(t);
	}


}
