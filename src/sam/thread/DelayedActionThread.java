package sam.thread;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;

public class DelayedActionThread<E> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DelayedActionThread.class);
	private final int delay;
	private final Semaphore lock = new Semaphore(0);
	private final AtomicReference<E> string = new AtomicReference<E>();
	private final Consumer<E> consumer;

	public DelayedActionThread(int delay, Consumer<E> consumer) {
		this.delay = delay;
		this.consumer = consumer;
	}

	private int n = 1;
	private volatile Thread t;

	public void  queue(E s) {
		if(!lock.hasQueuedThreads()) {
			if(t != null)
				t.interrupt();

			t = new Thread(new LOOP());
			t.setDaemon(true);
			t.setName("Search-Thread: "+(n++));
			t.start();
		}

		string.set(s);
		lock.release();
	}
	
	private class LOOP implements Runnable {
		@Override
		public void run() {
			LOGGER.debug("START thread: "+Thread.currentThread().getName());

			long last = -1;

			while(true) {
				try {
					if(last != -1) {
						if(!lock.tryAcquire(delay, TimeUnit.MILLISECONDS) && System.currentTimeMillis() - last >= delay) {
							last = -1;
							consumer.accept(string.get());
						}
					} else {
						lock.acquire();
						last = System.currentTimeMillis();
					}
				} catch (InterruptedException e) {
					LOGGER.debug("STOP thread: "+Thread.currentThread().getName());
					break;
				}
			}
		}
	}

	public void stop() {
		if(t != null)
			t.interrupt();
		t = null;
	}
}
