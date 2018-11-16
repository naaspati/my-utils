package sam.reference;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ReferenceList<T>  {
	private final Queue<Reference<T>> list;
    private final Supplier<T> valueGenerator;
	private ReferenceType type;
    
    /**
     * a non-threadsafe
     * @param valueGenerator
     */
    public ReferenceList(ReferenceType type, Supplier<T> valueGenerator) {
    	this(type, false, valueGenerator); 
	}
    
	public ReferenceList(ReferenceType type, boolean threadSafe, Supplier<T> valueGenerator) {
    	this.valueGenerator = valueGenerator == null ? (() -> null) : valueGenerator;
    	this.type = type;
    	
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
        return list.add(type.get(value));
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
    	if(obj == null) return false;
    	
        for (Reference<T> w : list) {
            if(obj.equals(ReferenceUtils.get(w)))
                return true;
        }
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
        if(list.isEmpty()) {
            T v = valueGenerator.get();
            return v;
        }
        
        T t = ReferenceUtils.get(list.poll());
        if(t != null)
            return t;
        
        while(true) {
            t = ReferenceUtils.get(list.poll());
            if(t != null)
                return t;
        }
    }
    public void clear() {
        list.clear();
    }
    
    public Stream<T> stream(){
    	clean();
    	
    	if(list.isEmpty())
    		return Stream.empty();
    	
    	return list.stream().map(ReferenceUtils::get).filter(Objects::nonNull);
    }
    private void clean() {
    	if(list.isEmpty()) return;
    	list.removeIf(w -> ReferenceUtils.get(w) == null);
	}

	/**
     * poll -&gt; consume -&gt; return to store
     * @param consumer
     */
    public void cosume(Consumer<T> consumer) {
        T v = poll();
        consumer.accept(v);
        add(v);
    }
    public <E> E map(Function<T, E> function) {
        T v = poll();
        E e = function.apply(v);
        add(v);
        return e;
    }
    public boolean isEmpty() {
    	clean();
    	if(list.isEmpty()) return true;
    	
    	if(list.stream().allMatch(r -> ReferenceUtils.get(r) == null)) {
    		list.clear();
    		return true;
    	}
    	return false;
    }
	public void forEach(Consumer<T> consumer) {
		clean();
		if(list.isEmpty())
			return;
		
		for (Reference<T> r : list) {
			T t = ReferenceUtils.get(r);
			if(t != null)
				consumer.accept(t);
		}
	}
}
