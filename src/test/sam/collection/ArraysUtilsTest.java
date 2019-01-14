package test.sam.collection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;
import static sam.collection.ArraysUtils.intRange;
import static sam.collection.ArraysUtils.join;
import static sam.collection.ArraysUtils.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

public class ArraysUtilsTest {

	@Test
	public void intRangeTest () {
		int[] singleArray = intRange(0, 100);
		assertArrayEquals(singleArray, IntStream.range(0, 100).toArray());
	}

	@Test
	public void joinTest() {
		int[] array = intRange(0, 20);
		assertSame(array, join(array));

		Random random = new Random();
		int[][] arrays = Stream.generate(() -> {
			int start = random.nextInt();
			int size =  20 + random.nextInt(30);
			int[] ar = intRange(start, start + size + 1);
			return ar;
		})
				.limit(20)
				.toArray(int[][]::new);

		array = join(arrays);
		int[] expected = Arrays.stream(arrays).flatMapToInt(Arrays::stream).toArray();

		assertArrayEquals(expected, array);
	}
	@Test
	public void joinObjectArrayTest() {
		Random random = new Random();

		Integer[][] arrays = Stream.generate(() -> {
			int start = random.nextInt();
			int size =  20 + random.nextInt(30);
			Integer[] ar = IntStream.range(start, start + size + 1).boxed().toArray(Integer[]::new);
			return ar;
		})
				.limit(20)
				.toArray(Integer[][]::new);

		Integer[] array = join(arrays);
		Integer[] expected = Arrays.stream(arrays).flatMap(Arrays::stream).toArray(Integer[]::new);

		assertArrayEquals(expected, array);
	}

	@Test
	public void removeIfTest() {
		List<Integer> list = IntStream.range(1, 10).boxed().collect(Collectors.toList());
		Integer[] array = IntStream.range(1, 10).boxed().toArray(Integer[]::new);
		Integer[] array2 = removeIf(array, e -> e > 10);

		assertSame(array, array2);

		list.removeIf(e -> e%2 == 0);
		array2 = removeIf(array2, e -> e%2 == 0);

		assertArrayEquals(list.toArray(new Integer[list.size()]), array2);
	}
}
