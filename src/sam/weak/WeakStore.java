package sam.weak;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class WeakStore<T> {
	private static final Logger LOGGER = Logger.getLogger("WeakStore");
	
    private final Queue<WeakReference<T>> list;
    private final Supplier<T> valueGenerator;
    
    public WeakStore() {
    	this(null);
	}
    /**
     * a non-threadsafe Weakstore
     * @param valueGenerator
     */
    public WeakStore(Supplier<T> valueGenerator) {
    	this(valueGenerator, false); 
	}
	public WeakStore(Supplier<T> valueGenerator, boolean threadSafe) {
    	this.valueGenerator = valueGenerator;
    	
    	if(threadSafe)
    		list = new ConcurrentLinkedQueue<>();
    	else
    		list  = new LinkedList<>();
	}
    
	/**
	 * @param value if null, values is not added, return false
	 * @return
	 */
    public boolean add(T value) {
    	if(value == null)
    		return false;
        return list.add(new WeakReference<T>(value));
    }
    public boolean offer(T value) {
       return add(value);
    }
    public boolean addIfAbsent(T value) {
        if(value == null || contains(value))
            return false;
        return add(value);
    }
    public boolean contains(Object obj) {
        for (WeakReference<T> w : list)
            if(w.get() == obj)
                return true;
        
        return false;
    }
    @SuppressWarnings("unchecked")
    public void addAll(T... values) {
        for (T t : values)
            add(t);
    }
    public void addAll(Collection<T> values) {
        for (T t : values)
            add(t);
    }
    /**
     * <pre>
     * if (found any non null value) 
     *    remove and return
     * else if(valueGenerator != null)
     *   return valueGenerator.get()
     * else
     *   return null
     *</pre>
     * @return 
     */
    public T poll() {
        while(true) {
            if(list.isEmpty()) {
            	if(valueGenerator == null)
            		return null;
            	
            	T v = valueGenerator.get();
            	LOGGER.config(() -> "new value created: "+ v);
                return v;
            }
            T t = list.poll().get();
            if(t != null)
                return t; 
        }
    }
    public void clear() {
        list.clear();
    }
    
    public Stream<T> stream(){
    	if(list.isEmpty())
    		return Stream.empty();
    	
    	return list.stream().map(WeakReference::get).filter(Objects::nonNull);
    }
    /**
     * poll -> consume -> return to store
     * @param consumer
     */
    public void pollWrap(Consumer<T> consumer) {
        T v = poll();
        consumer.accept(v);
        add(v);
    }
    public <E> E pollWrap(Function<T, E> function) {
        T v = poll();
        E e = function.apply(v);
        add(v);
        return e;
    }
}
