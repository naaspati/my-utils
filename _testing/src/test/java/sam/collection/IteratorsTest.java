package sam.collection;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sam.collection.Iterators.join;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import sam.logging.Logger;

public class IteratorsTest {
	private static final Logger LOGGER = Logger.getLogger(IteratorsTest.class);

	@Test
	public void join_test_must_throw_nullpointerexcetion() {
		assertThrows(NullPointerException.class, () -> join((Iterator<Object>[])null));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void join_test_must_return_zero_iterator() {
		assertSame(Collections.emptyIterator(), join(new Iterator[0]));
	}
	@Test
	public void join_test_must_return_iterator_at_zero() {
		Iterator<Integer> iter = Iterators.of(new int[]{1,2,3,5});
		assertSame(iter, join(iter));
	}
	@SuppressWarnings("unchecked")
	@Test
	public void join_test_must_joined_iter() {
		Random r = new Random();
		int[][] iterators = ArraysUtils.fill(new int[10][], i -> IntStream.generate(() -> r.nextInt()).limit(20 + r.nextInt(20)).toArray());
		
		StringBuilder sb = new StringBuilder("arrays");
		for (int[] is : iterators) {
			sb.append('(').append(is.length).append(") [");
			for (int n : is) 
				sb.append(n).append(',');
			sb.setCharAt(sb.length() - 1, ']');
			sb.append('\n');
		}
		
		LOGGER.info(sb.toString());
		sb = null;
		
		Iterator<Integer> actual = Iterators.join(Arrays.stream(iterators).map(Iterators::of).toArray(Iterator[]::new));
		
		for (int[] expected : iterators) {
			for (int n : expected) 
				assertEquals(n, actual.next().intValue());
		}
	}
}