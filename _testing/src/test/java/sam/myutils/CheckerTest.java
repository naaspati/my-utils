package sam.myutils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sam.myutils.Checker.*;
import static sam.myutils.Checker.isInteger;
import static sam.myutils.Checker.requireNonNull;

import java.nio.CharBuffer;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class CheckerTest {

	@Test
	public void testMustBeTrueBooleanString() {
		assertThrows(IllegalArgumentException.class, () -> assertTrue(false, "msg"));
	}
	
	@Test
	void isEmptyTrimmedTest() {
		StringBuilder sb = new StringBuilder();
		try {
			char[] chars = new char[100];
			char[] chars2 = {'\n', ' ','\t', '\r'};
			Random random = new Random();
			
			for (int i = 0; i < 20; i++) {
				for (int j = 0; j < i; j++) {
					chars[j] = chars2[random.nextInt(chars2.length)];	
				}
				isEmptyTrimmed2(true, sb, new String(chars, 0, i));
			}
			
			isEmptyTrimmed2(true, sb, "");

			for (int i = 2; i < 50; i+=2) {
				int j = 0;
				while(j < i) {
					chars[j++] = (char) ('A' + random.nextInt('Z'));
					chars[j++] = chars2[random.nextInt(chars2.length)];
				}
				isEmptyTrimmed2(false, sb, new String(chars, 0, i));
			}
			
			isEmptyTrimmed2(false, sb, "a");
			isEmptyTrimmed2(false, sb, "abc");
			isEmptyTrimmed2(false, sb, " a ");
			isEmptyTrimmed2(false, sb, " a");
			isEmptyTrimmed2(false, sb, "a ");
			isEmptyTrimmed2(false, sb, "   a");
			isEmptyTrimmed2(false, sb, "a   ");
			isEmptyTrimmed2(false, sb, " a   ");
		} finally {
			System.out.println(sb);
		}
	}

	private void isEmptyTrimmed2(boolean expected, StringBuilder sb, String s) {
		sb.append('\'');
		s.chars()
		.forEach(e -> {
			switch (e) {
				case '\n':
					sb.append("\\n");
					break;
				case '\t':
					sb.append("\\t");
					break;
				case '\r':
					sb.append("\\r");
					break;
				default:
					sb.append((char)e);
					break;
			}
		});
		sb.append('\'');
		sb.append('\n');
		
		if(expected)
			assertTrue(isEmptyTrimmed(s), s);
		else
			assertFalse(isEmptyTrimmed(s), s);
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
	
	@Test
	public void testIsEqual() {
		String s = "anime";
		String s2 = "jotan";
		
		assertTrue(isEqual(null, null));
		assertTrue(isEqual("", ""));
		assertTrue(isEqual("", new String()));
		assertTrue(isEqual("", new StringBuilder()));
		assertTrue(isEqual("", CharBuffer.allocate(0)));
		assertTrue(isEqual(new String(), ""));
		assertTrue(isEqual(new StringBuilder(), ""));
		assertTrue(isEqual(CharBuffer.allocate(0), ""));
		assertTrue(isEqual(s, s));
		assertTrue(isEqual(s, new StringBuilder(s)));
		assertTrue(isEqual(s, CharBuffer.wrap(s)));
		assertTrue(isEqual(new StringBuilder(s), s));
		assertTrue(isEqual(CharBuffer.wrap(s), s));
		
		assertFalse(isEqual(null, s));
		assertFalse(isEqual(s, null));
		assertFalse(isEqual("", "a"));
		assertFalse(isEqual(s, new StringBuilder(s2)));
		assertFalse(isEqual(s, CharBuffer.wrap(s2)));
		assertFalse(isEqual(new StringBuilder(s2), s));
		assertFalse(isEqual(CharBuffer.wrap(s2), s));
	}

	private void append(StringBuilder sb, String string, int start) {
		sb.append(string);
		
		if(string.charAt(0) != '-')
			sb.append(' ');
		
		while((sb.length() - start)%14 != 0)
			sb.append(' ');
	}
}
