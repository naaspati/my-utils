package sam.reference;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import sam.myutils.System2;

public class ReferenceMap<K, V> {
	private final Map<K, Reference<V>> map;
	private final ReferenceType type;

	public ReferenceMap(ReferenceType type) {
	    this(type, new HashMap<>());
	}
	public ReferenceMap(ReferenceType type, Map<K,Reference<V>> map) {
		this.map = map;
		this.type = type;
	}
	public V put(K key, V value) {
		clean();
		return ReferenceUtils.get(map.put(key, type.get(value)));
	}
	private long time = 0;
	private static final long cleanup_delay = Optional.ofNullable(System2.lookup("CLEANUP_DELAY")).map(String::trim).map(Integer::parseInt).orElse(60000);
	
	private void clean() {
		if(System.currentTimeMillis() - time >= cleanup_delay) {
			time = System.currentTimeMillis();
			map.values().removeIf(w -> ReferenceUtils.get(w) == null);
		}
	}
	public V get(K key) {
		clean();
		return ReferenceUtils.get(map.get(key));
	}
	public Set<K> keySet() {
		return map.keySet();
	}
	public void remove(K k) {
		map.remove(k);
	}
}
