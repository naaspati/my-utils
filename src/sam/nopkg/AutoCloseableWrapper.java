package sam.nopkg;

import java.io.IOException;

import sam.functions.IOExceptionConsumer;
import sam.functions.IOExceptionSupplier;

public class AutoCloseableWrapper<T> implements AutoCloseable {
	private final IOExceptionSupplier<T> getter;
	private final IOExceptionConsumer<T> closer;
	private boolean getInvoked = false;
	private boolean closed = false;
	private T t;

	public AutoCloseableWrapper(IOExceptionSupplier<T> getter, IOExceptionConsumer<T> closer) {
		this.getter = getter;
		this.closer = closer;
	}
	
	public T get() throws IOException {
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
	public void close() throws IOException {
		if(closed)
			return;
		
		closed = true;
		if(getInvoked)
			closer.accept(t);
	}


}
