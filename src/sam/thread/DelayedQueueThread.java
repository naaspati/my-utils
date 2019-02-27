package sam.thread;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import sam.logging.Logger;

public class DelayedQueueThread<E>  {
	private static final Logger LOGGER = Logger.getLogger(DelayedQueueThread.class);

	private static final Object CONTINUE = new Object();
	private static final Object STOP = new Object();

	private final Thread thread ;
	private final int delay ;
	private final DelayQueue<DL> queue = new DelayQueue<>();
	private final Consumer<E> action;
	private final AtomicReference<Object> ACTION = new AtomicReference<>();
	private final AtomicBoolean stopped = new AtomicBoolean();
	private final Comparator<DL> comparator; 

	/**
	 * order newest -&gt; oldest
	 * @param delay
	 * @param action
	 */
	public DelayedQueueThread(int delay, Consumer<E> action) {
		this(delay, true, (d, f) -> Long.compare(f.time, d.time), action);
	}
	public DelayedQueueThread(int searchDelay, boolean deamonThread, Comparator<DL> comparator, Consumer<E> action) {
		this.delay = searchDelay;
		this.action = action;
		this.comparator = comparator;
		thread = new Thread(new LOOP());
		thread.setDaemon(deamonThread);
	}

	public void start()  {
		LOGGER.debug(() -> "STARTED: "+getClass().getName());
		thread.start();
	}

	private class LOOP implements Runnable {

		private List<DL> list = new ArrayList<>();

		@SuppressWarnings("unchecked")
		@Override
		public void run()  {
			while(true) {
				Object data; 
				try {
					queue.drainTo(list); //safe clearing the queue
					list.clear();
					data = queue.take().data;
				} catch (InterruptedException e) {
					data = ACTION.get();
					if(data == null)
						throw new RuntimeException(e);
				}
				if(data == CONTINUE) continue;
				if(data == STOP) {
					stopped.set(true);
					return;
				}
				action.accept((E)data);
			}
		}
	}
	private void interrupt(Object action) {
		ACTION.set(action);
		queue.add(new DL(action));
		thread.interrupt();
	}
	public void stop(){
		interrupt(STOP);
	}
	public void clear() {
		interrupt(CONTINUE);
	}
	public void add(E data) {
		if(stopped.get()) throw new IllegalStateException("adding to a stopped search thread");
		queue.offer(new DL(data));
	}
	public class DL implements Delayed {
		final long time = System.currentTimeMillis() + delay;
		final Object data;

		public DL(Object data) {
			super();
			this.data = data;
		}
		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(Delayed o) {
			return comparator.compare(this, (DL)o);
		}
		@Override
		public long getDelay(TimeUnit unit) {
			long diff = time - System.currentTimeMillis();
			return unit.convert(diff, TimeUnit.MILLISECONDS);
		}
	}
}
