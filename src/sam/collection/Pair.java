package sam.collection;

import java.util.Objects;

public class Pair<K, V> {
	public final K key;
	public final V value;
	
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	@Override
	public String toString() {
		return "Pair [key=" + key + ", value=" + value + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Pair other = (Pair) obj;
		return Objects.equals(key, other.key) && Objects.equals(value, other.value);
	}
}
