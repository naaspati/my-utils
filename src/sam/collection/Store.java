package sam.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.carrotsearch.hppc.IntObjectScatterMap;
import com.carrotsearch.hppc.procedures.IntObjectProcedure;

import sam.myutils.Checker;

/**
 * TODO think a better name
 * @author sameer
 *
 * @param <E>
 */
public class Store<E> {
	private final Logger logger = Logger.getLogger(getClass().getSimpleName());
	
	private final int fixedSize;
	private final Object[] array;
	private final IntObjectScatterMap<E> map = new IntObjectScatterMap<>();
	private int loaded;

	public Store(int fixedSize) {
		array = new Object[fixedSize];
		this.fixedSize = fixedSize;
		logger.fine(() -> "store created: " + fixedSize);
	}

	@SuppressWarnings("unchecked")
	public E get(int index) {
		if (index < fixedSize)
			return (E) array[index];
		else
			return map.get(index);
	}

	public void put(int index, E data) {
		loaded = loaded + (array == null ? -1 : 1);

		if (index < fixedSize)
			array[index] = data;
		else
			map.put(index, data);
	}

	public int size() {
		return loaded;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<E> values() {
		List list = new ArrayList(array.length + map.size() + 1);
		list.addAll(Arrays.asList(array));
		mapForEach((key, value) -> list.add(value));
		return list;
	}

	private void mapForEach(IntObjectProcedure<E> action) {
		map.forEach(action);
	}

	@SuppressWarnings("unchecked")
	public void forEach(Consumer<E> action) {
		if(Checker.isNotEmpty(array)) {
			for (Object object : array) {
				if(object != null)
				action.accept((E)object);
			} 
				
		}
		if(!map.isEmpty()) {
			mapForEach((key, value) -> {
				if(value != null)
				action.accept(value);
			});
		}
	}
}
