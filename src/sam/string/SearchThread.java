package sam.string;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Logger;

import sam.logging.MyLoggerFactory;

class SearchThread implements Runnable  {
	private static final Logger LOGGER = MyLoggerFactory.logger(SearchThread.class.getSimpleName());

	
	private final Thread searchThread ;
	private final int searchDelay ;
	private final AtomicReference<String> searchKey = new AtomicReference<>();
	private final DelayQueue<Delayed> queue = new DelayQueue<>();
	private final Consumer<String> action;
	private final AtomicBoolean reset = new AtomicBoolean(false);
	private volatile boolean stopped;

	public SearchThread(int searchDelay, Consumer<String> action) {
		this.searchDelay = searchDelay;
		this.action = action;
		searchThread = new Thread(this);
		searchThread.setDaemon(true);
	}

	void start()  {
		LOGGER.fine("STARTED");
		searchThread.start();
	}

	@Override
	public void run()  {
		while(true) {
			try {
				queue.take();
			} catch (InterruptedException e) {
				if(reset.get()){
					reset.set(true);
					continue;
				}
				return;
			}
			if(searchKey.get() == null)
				continue;
			
			queue.clear();

			String s = searchKey.getAndSet(null);
			if(s == null) continue;
			action.accept(s);
		}
	}
	
	public void stop(){
		stopped = true;
		searchThread.interrupt();
		LOGGER.fine("STOP");
	}
	public void add(String str) {
		if(stopped) throw new IllegalStateException("adding to a stopped search thread");
		queue.offer(new DL());
		searchKey.set(str);
	}
	private class DL implements Delayed {
		private final long time = System.currentTimeMillis() + searchDelay;

		@Override
		public int compareTo(Delayed o) {
			return Long.compare(((DL)o).time, time);
		}
		@Override
		public long getDelay(TimeUnit unit) {
			long diff = time - System.currentTimeMillis();
			return unit.convert(diff, TimeUnit.MILLISECONDS);
		}
	}
	public void reset() {
		reset.set(true);
		searchKey.set(null);
		searchThread.interrupt();
		queue.clear();
	}

}
