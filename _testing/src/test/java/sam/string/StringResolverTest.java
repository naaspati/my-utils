package sam.string;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

public class StringResolverTest {
	@Test
	void test1() {
		StringBuilder sb = new StringBuilder();
		
		UnaryOperator<String> fail = s -> {fail(); return null;};
		StringResolver.resolve("", '%', sb, fail);
		assertEquals(0, sb.length());
		
		sb.setLength(0);
		StringResolver.resolve("anime", '%', sb, fail);
		assertEquals("anime", "anime");

		sb.setLength(0);
		StringResolver.resolve("%1%", '%', sb, s -> String.valueOf(Integer.parseInt(s)+1));
		assertEquals("2", sb.toString());
		
		sb.setLength(0);
		StringResolver.resolve("anime", '%', sb, fail);
		assertEquals("anime", sb.toString());
		
		sb.setLength(0);
		StringResolver.resolve("%1%%2%", '%', sb, s -> String.valueOf(Integer.parseInt(s)+1));
		assertEquals("23", sb.toString());
		
		sb.setLength(0);
		StringResolver.resolve("%1%anime%2%", '%', sb, s -> String.valueOf(Integer.parseInt(s)+1));
		assertEquals("2anime3", sb.toString());
		
		sb.setLength(0);
		StringResolver.resolve("anime%1%anime%2%anime", '%', sb, s -> String.valueOf(Integer.parseInt(s)+1));
		assertEquals("anime2anime3anime", sb.toString());
		
		sb.setLength(0);
		StringResolver.resolve("anime%1%anime%2%", '%', sb, s -> String.valueOf(Integer.parseInt(s)+1));
		assertEquals("anime2anime3", sb.toString());
		
		sb.setLength(0);
		StringResolver.resolve("%1%anime%2%anime", '%', sb, s -> String.valueOf(Integer.parseInt(s)+1));
		assertEquals("2anime3anime", sb.toString());
		
		StringResolver.resolve("%1%anime%2%anime", '%', sb, s -> String.valueOf(Integer.parseInt(s)+1));
		assertEquals("2anime3anime"+"2anime3anime", sb.toString());
		
		assertThrows(IllegalArgumentException.class, () -> StringResolver.resolve("%anie", '%', sb, fail));
		
	}

}
