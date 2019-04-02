package sam.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class IndexedMapTest {

	@Test
	void test() {
		assertThrows(NullPointerException.class, () -> new IndexedMap<>(null, c -> fail()));
		assertThrows(NullPointerException.class, () -> new IndexedMap<>(new Integer[0], null));

		Random r = new Random();

		test(shuffledList(0, 100, r));

		List<Integer> list = shuffledList(0, 50, r);

		while(list.size() != 100) {
			Integer n = r.nextInt(200);
			if(!list.contains(n)) 
				list.add(n);
		}

		test(list);

		list = new ArrayList<>(100);
		for (int i = 0; i < 100; i++) {
			Integer n = Math.abs(r.nextInt());
			
			if(!list.contains(n))
				list.add(n);
		}
		
		test(list);
	}

	private void test(List<Integer> integers) {
		IndexedMap<Integer> map = map(integers);
		System.out.println(integers);
		integers.sort(Comparator.naturalOrder());
		System.out.println(integers);

		assertIterableEquals(integers, map);

		for (int i = 0; i < integers.size(); i++) 
			assertEquals(integers.get(i), map.get(integers.get(i)));

		System.out.println("---------------------------------------------------");
	}

	private List<Integer> shuffledList(int min, int max, Random r) {
		List<Integer> integers =  IntStream.range(min, max).boxed().collect(Collectors.toList());
		Collections.shuffle(integers, r);

		return integers;
	}

	private IndexedMap<Integer> map(List<Integer> list) {
		return map(list.toArray(new Integer[list.size()]));
	}
	private IndexedMap<Integer> map(Integer[] array) {
		return new IndexedMap<Integer>(array, i -> i);
	}

}
