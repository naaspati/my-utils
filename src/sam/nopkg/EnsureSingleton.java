package sam.nopkg;

import java.util.concurrent.atomic.AtomicBoolean;

public class EnsureSingleton {
	private final AtomicBoolean init = new AtomicBoolean(false);
	private final Runnable throwHandler;
	
	public EnsureSingleton(Runnable throwHandler) {
		this.throwHandler = throwHandler;
	}
	public EnsureSingleton() {
		this.throwHandler = () -> {throw new IllegalStateException("already initialized");};
	}
	
	public boolean initialized() {
		return init.get();
	}
	public void init() {
		if(!init.compareAndSet(false, true))
			throwHandler.run();
	}
}
