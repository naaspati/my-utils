package sam.reference;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import sam.console.ANSI;
import sam.myutils.System2;

public class ReferencePool<T>  {
	private static final boolean DUMP_POOL_GENERATED_COUNT = System2.lookupBoolean("DUMP_POOL_GENERATED_COUNT", false);
	private static final Map<String, AtomicInteger> counts = DUMP_POOL_GENERATED_COUNT ? Collections.synchronizedMap(new IdentityHashMap<>()) : null;
	
	static {
		if(DUMP_POOL_GENERATED_COUNT) {
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				StringBuilder sb = new StringBuilder();
				sb.append("\n\n");
				ANSI.createBanner("DUMP_POOL_GENERATED_COUNT", sb);
				sb.append('\n');
				synchronized (counts) {
					counts.forEach((s,t) -> sb.append(s).append(": ").append(t).append('\n'));	
				}
				System.out.println(sb);
			}));
		}
	}
	
	private final Deque<Reference<T>> queue;
	private final Supplier<T> valueGenerator;
	private final ReferenceType type;
	private final AtomicInteger count;
	private final AtomicBoolean put;

	/**
	 * a non-threadsafe
	 * @param valueGenerator
	 */
	public ReferencePool(ReferenceType type, Supplier<T> valueGenerator) {
		this(type, false, valueGenerator); 
	}

	public ReferencePool(ReferenceType type, boolean threadSafe, Supplier<T> valueGenerator) {
		if(valueGenerator == null) {
			this.valueGenerator = () -> null;
			this.count = null;
			this.put = null;
		} else if(DUMP_POOL_GENERATED_COUNT) {
			this.count = new AtomicInteger(0);
			this.put = new AtomicBoolean(false);
			this.valueGenerator = wrap(valueGenerator);
		} else {
			this.put = null;
			this.count = null;
			this.valueGenerator = valueGenerator;
		}
		
		this.type = type;
		queue = threadSafe ? new ConcurrentLinkedDeque<>() : new LinkedList<>();
	}
	
	private Supplier<T> wrap(Supplier<T> valueGenerator) {
		return () -> {
			count.incrementAndGet();
			T t =  valueGenerator.get();
			if(t != null && put.compareAndSet(false, true)) {
				synchronized (counts) {
					counts.put(getClass().getName()+"("+t.getClass().getName()+")", count);
				}
			}
			return t;
		};
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
		return queue.add(type.get(value));
	}
	public boolean addIfAbsent(T value) {
		if(value == null || contains(value))
			return false;
		return add(value);
	}
	private boolean contains(Object obj) {
		if(obj == null || queue.isEmpty()) 
			return false;

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
		queue.clear();
	}
	private void clean() {
		if(queue.isEmpty()) return;
		queue.removeIf(w -> ReferenceUtils.get(w) == null);
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
		if(queue.isEmpty())
			return;

		for (Reference<T> r : queue) {
			T t = ReferenceUtils.get(r);
			if(t != null)
				consumer.accept(t);
		}	
	}
}
