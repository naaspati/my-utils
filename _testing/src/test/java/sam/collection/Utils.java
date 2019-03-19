package sam.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.Collection;
import java.util.Iterator;

public interface Utils {
	static void check(IntListBase list, Collection<Integer> arraylist) {
		assertEquals(list.size(), arraylist.size());
		assertIterableEquals(arraylist, new Iterable<Integer>() {
			@Override
			public Iterator<Integer> iterator() {
				return list.iterator();
			}
		});
	}


}
