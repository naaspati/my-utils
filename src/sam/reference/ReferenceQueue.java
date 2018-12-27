package sam.reference;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ReferenceQueue<T>  {
	private final LinkedList<Reference<T>> queue = new LinkedList<>();
	private final Supplier<T> valueGenerator;
	private ReferenceType type;
	private final boolean threadSafe;

	/**
	 * a non-threadsafe
	 * @param valueGenerator
	 */
	public ReferenceQueue(ReferenceType type, Supplier<T> valueGenerator) {
		this(type, false, valueGenerator); 
	}

	public ReferenceQueue(ReferenceType type, boolean threadSafe, Supplier<T> valueGenerator) {
		this.valueGenerator = valueGenerator == null ? (() -> null) : valueGenerator;
		this.type = type;
		this.threadSafe = threadSafe;
	}

	public boolean offer(T value) {
		return add(value);
	}

	/**
	 * @param value if null, values is not added, return false
	 * @return
	 */
	public boolean add(T value) {
		if(value == null)
			return false;
		if(threadSafe) 
			synchronized (queue) { return queue.add(type.get(value)); }
		else
			return queue.add(type.get(value));
	}
	public boolean addIfAbsent(T value) {
		if(value == null || contains(value))
			return false;
		return add(value);
	}
	public boolean contains(Object obj) {
		if(threadSafe) {
			synchronized (queue) { return contains0(obj); }
		} else
			return contains0(obj);
	}
	private boolean contains0(Object obj) {
		if(obj == null || queue.isEmpty()) return false;

		for (Reference<T> w : queue) {
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
		if(threadSafe) {
			synchronized (queue) { return poll0(); }
		} else
			return poll0();
	}
	private T poll0() {
		if(queue.isEmpty()) 
			return valueGenerator.get();

		T t = ReferenceUtils.get(queue.pollLast());
		if(t != null) 
			return t;

		while(!queue.isEmpty()) {
			t = ReferenceUtils.get(queue.pollLast());
			if(t != null) 
				return t;
		}
		return valueGenerator.get();
	}

	public void clear() {
		if(threadSafe) {
			synchronized (queue) { queue.clear(); }
		} else
			queue.clear();

	}

	private void clean() {
		synchronized (queue) {
			if(queue.isEmpty()) return;
			queue.removeIf(w -> ReferenceUtils.get(w) == null);	
		}
	}

	/**
	 * poll -&gt; consume -&gt; return to store
	 * @param consumer
	 */
	public void consume(Consumer<T> consumer) {
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
		return queue.isEmpty();
	}
	public void forEach(Consumer<T> consumer) {
		clean();
		synchronized (queue) {
			if(queue.isEmpty())
				return;

			for (Reference<T> r : queue) {
				T t = ReferenceUtils.get(r);
				if(t != null)
					consumer.accept(t);
			}	
		}

	}
}
