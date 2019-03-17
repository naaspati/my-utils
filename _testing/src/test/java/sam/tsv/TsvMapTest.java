package sam.tsv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

class TsvMapTest {
	
	@Test
	void test2() throws IOException {
		/**
		 * 1	10
		 * 2	11
		 * 3	12
		 * 4	13
		 * 5	14
		 */
		
		
		Map<Long, Long> expectedMap = new HashMap<>();
		StringBuilder expected = new StringBuilder();
		Random r = new Random();
		
		for (int i = 0; i < 10; i++) {
			long l = r.nextLong();
			long l2 = r.nextLong();
			
			expectedMap.put(l, l2);
			expected.append(l).append('\t').append(l2).append('\n');
		}
		
		expectedMap = Collections.unmodifiableMap(expectedMap);
		
		StringReader reader = new StringReader(expected.toString());
		Map<Long, Long> map = TsvMap.parse(new BufferedReader(reader), Long::parseLong);
		assertEquals(expectedMap, map);
		
		StringBuilder actual = new StringBuilder();
		TsvMap.save(actual, map, String::valueOf);
		
		assertNotSame(actual, expected);
		assertEquals(actual.length(), expected.length());
		assertEquals(CharBuffer.wrap(actual), CharBuffer.wrap(expected));
		
		expected = null;
		map = null;
		
		map = TsvMap.parse(new BufferedReader(new StringReader(actual.toString())), Long::parseLong);
		assertEquals(expectedMap, map);
	}
}
