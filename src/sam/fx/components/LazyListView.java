package sam.fx.components;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
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

    private final IdentityHashMap<ListCell<?>, T> loadQueue = new IdentityHashMap<>();
    private Map<T, U> loaded = new HashMap<>();
    private final Semaphore lock = new Semaphore(0);
    private final AtomicInteger mod = new AtomicInteger();
    private Class<T> keyType;
    
    private volatile Thread thread;
    private Function<Stream<T>, Map<T, U>> dataLoader;
    
    public void setDataLoader(Function<Stream<T>, Map<T, U>> dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    protected void loadData(int mod) {
        loopsEffective.incrementAndGet();
        if (mod != this.mod.get() || loadQueue.isEmpty() || loadQueue.values().stream().allMatch(Objects::isNull)) 
            return;
        
        Map<T, U> loaded = dataLoader.apply(this.loadQueue.values().stream().filter(Objects::nonNull));
        if (mod != this.mod.get())
            throw new ConcurrentModificationException();
        
        if(loaded.isEmpty())
            return;
        
        this.loaded.putAll(loaded);
        
        getItems().replaceAll(item -> {
            if(keyType.isInstance(item)) {
                U sm = this.loaded.get(item); 
                return sm == null ? item : sm;
            }
            return item;
        });
        
        this.loadQueue.clear();
        this.refresh();
    }
    
    public void setLoadedStore(Map<T, U> store) {
        this.loaded = store;
    }
    
    public boolean isRunning() {
        return thread != null; 
    }
    
    public void start(Class<T> keyType) {
        if(thread != null) 
            throw new IllegalStateException("already started");
        
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
                        Platform.runLater(() -> loadData(n));
                    }
                } catch (InterruptedException e) { }
            }
        });
        thread.setDaemon(true);
        thread.start();
        this.keyType = keyType;
        setCellFactory(this);
        refresh();
    }
    
    @Override
    public void close() throws Exception {
        if(thread != null) {
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
}
