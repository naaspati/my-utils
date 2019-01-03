package test.sam.collection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import sam.collection.ArraysUtils;

public class ArraysUtilsTest {

	@Test
	public void removeIfTest() {
		List<Integer> list = IntStream.range(1, 10).boxed().collect(Collectors.toList());
		Integer[] array = IntStream.range(1, 10).boxed().toArray(Integer[]::new);
		Integer[] array2 = ArraysUtils.removeIf(array, e -> e > 10);
		
		assertSame(array, array2);
		
		list.removeIf(e -> e%2 == 0);
		array2 = ArraysUtils.removeIf(array2, e -> e%2 == 0);
		
		assertArrayEquals(list.toArray(new Integer[list.size()]), array2);
	}
}
