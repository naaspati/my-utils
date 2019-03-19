package sam.thread;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import javafx.application.Platform;
import sam.logging.Logger;

public class DelayedActionThread<E> {
	private static final Logger LOGGER = Logger.getLogger(DelayedActionThread.class);
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
		//printlocation();

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

	private void printlocation() {
		StackTraceElement[] e = Thread.currentThread().getStackTrace();
		print(3, e);
		print(4, e);
		print(5, e);
		print(6, e);
	}

	private void print(int n, StackTraceElement[] e) {
		if(e.length > n)
			System.out.println(e[n]);
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
