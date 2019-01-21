package sam.myutils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import static sam.myutils.Checker.*;

import java.util.Formatter;
import java.util.Random;

public class CheckerTest {

	@Test
	public void testMustBeTrueBooleanString() {
		assertThrows(IllegalArgumentException.class, () -> mustBeTrue(false, "msg"));
	}

	@Test
	public void testRequireNull() {
		Throwable t = assertThrows(NullPointerException.class, () -> requireNonNull("a b c", "a", null, "c"));
		assertEquals(t.getMessage(), "b");
	}
	@Test
	public void testRequireNull2() {
		requireNonNull("a b c", "a", "b", "c");
	}

	@Test
	public void checkIsInteger() {
		Random r = new Random();
		StringBuilder sb = new StringBuilder();

		append(sb, "ASSERT_TRUE: ", 0);
		sb.append('\n');
		int n = sb.length();

		for (int i = 0; i < 1000; i++)  {
			String s = String.valueOf(r.nextInt());
			assertTrue(isInteger(s));
			append(sb, s, n);

			if(i != 0 && i%10 == 0) {
				sb.append('\n');
				n = sb.length();
			}
		}
		
		sb.append("\n\nASSERT_FALSE: ");
		for (String s : new String[]{" ","   ",""," 1 2 "," 1 ","1 "," 1","a","abc"}) {
			assertFalse(isInteger(s));	
			sb.append('\'').append(s).append('\'').append('\n');
		}
		
		System.out.println(sb);

	}

	private void append(StringBuilder sb, String string, int start) {
		sb.append(string);
		
		if(string.charAt(0) != '-')
			sb.append(' ');
		
		while((sb.length() - start)%14 != 0)
			sb.append(' ');
	}
}
