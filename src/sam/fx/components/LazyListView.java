package sam.fx.components;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class LazyListView<T, U> extends ListView<Object> implements Callback<ListView<Object>, ListCell<Object>>, AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(LazyListView.class);
	private final AtomicInteger loops = new AtomicInteger();
	private final AtomicInteger loopsEffective = new AtomicInteger();

	private final Map<ListCell<?>, T> loadQueue;
	private Map<T, U> loaded;
	private final Semaphore lock = new Semaphore(0);
	private final AtomicInteger mod = new AtomicInteger();
	private Class<T> keyType;

	private final boolean async;
	private volatile Thread thread;
	private Function<Stream<T>, Map<T, U>> dataLoader;
	
	public LazyListView() {
		this(false);
	}
	
	public LazyListView(boolean async) {
		this.async = async;
		this.loaded = async ? new ConcurrentHashMap<>() : new HashMap<>();
		this.loadQueue = async ? Collections.synchronizedMap(new IdentityHashMap<>()) : new IdentityHashMap<>(); 
	}

	protected void loadData(int mod) {
		loopsEffective.incrementAndGet();
		if (mod != this.mod.get() || loadQueue.isEmpty() || loadQueue.values().stream().allMatch(Objects::isNull)) 
			return;

		Map<T, U> loaded = dataLoader.apply(
				this.loadQueue.values()
				.stream()
				.filter(Objects::nonNull)
				.filter(s -> !this.loaded.containsKey(s))
				);
		
		if (!async && mod != this.mod.get())
			throw new ConcurrentModificationException();

		if(loaded.isEmpty())
			return;

		this.loaded.putAll(loaded);
		Platform.runLater(() -> {
			getItems().replaceAll(item -> {
				if(keyType.isInstance(item)) {
					U sm = this.loaded.get(item); 
					return sm == null ? item : sm;
				}
				return item;
			});

			this.loadQueue.values().removeAll(loaded.keySet());
			this.refresh();
		});
	}

	public void setLoadedStore(Map<T, U> store) {
		this.loaded = store;
	}

	public boolean isRunning() {
		return thread != null; 
	}

	public void start(Class<T> keyType, Function<Stream<T>, Map<T, U>> dataLoader) {
		if(thread != null) 
			throw new IllegalStateException("already started");
		
		this.dataLoader = Objects.requireNonNull(dataLoader);
		this.keyType = Objects.requireNonNull(keyType);

		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						loops.incrementAndGet();
						lock.acquire();
						int n = mod.incrementAndGet();
						Thread.sleep(200);
						if(mod.get() != n)
							continue;
						if(async) 
							loadData(n);
						else 
							Platform.runLater(() -> loadData(n));
					}
				} catch (InterruptedException e) { }
			}
		});
		thread.setDaemon(true);
		thread.start();
		setCellFactory(this);
		refresh();
	}

	@Override
	public void close() throws Exception {
		if(thread != null) {
			loadQueue.clear();
			Thread t = thread;
			thread = null;
			t.interrupt();
			LOGGER.debug("loaded: {}, loops: {}, effective-loops: {}", loaded.size(), loops.get(), loopsEffective.get());
		}
	}

	@Override
	public ListCell<Object> call(ListView<Object> param) {
		return new ListCell<Object>() {
			@SuppressWarnings("unchecked")
			@Override
			protected void updateItem(Object item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					loadQueue.put(this, null);
				} else {
					U sm;
					if(keyType.isInstance(item)) {
						sm = loaded.get(item);
					} else {
						sm = (U)item;
					}
					loadQueue.put(this, sm == null ? (T)item : null);
					setText((sm == null ? item : sm).toString());
				}
				if(lock.availablePermits() == 0) 
					lock.release();
			}
		};
	}

	public void queue(ListCell<?> cell, T item) {
		loadQueue.put(Objects.requireNonNull(cell), item);
		if(item != null && lock.availablePermits() == 0) 
			lock.release();
	}
}
