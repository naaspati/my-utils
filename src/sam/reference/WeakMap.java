package sam.reference;

import java.lang.ref.Reference;
import java.util.Map;

/**
 * stored
 * @author Sameer
 *
 * @param <K>
 * @param <V>
 */
public class WeakMap<K, V> extends ReferenceMap<K, V> {

	public WeakMap(Map<K, Reference<V>> map) {
		super(ReferenceType.WEAK, map);
	}

	public WeakMap() {
		super(ReferenceType.WEAK);
	}
}
