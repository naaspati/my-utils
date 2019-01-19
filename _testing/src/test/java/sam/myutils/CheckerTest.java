package sam.myutils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CheckerTest {

	@Test
	public void testMustBeTrueBooleanString() {
		assertThrows(IllegalArgumentException.class, () -> Checker.mustBeTrue(false, "msg"));
	}
	
	@Test
	public void testRequireNull() {
		Throwable t = assertThrows(NullPointerException.class, () -> Checker.requireNonNull("a b c", "a", null, "c"));
		assertEquals(t.getMessage(), "b");
	}
	@Test
	public void testRequireNull2() {
		Checker.requireNonNull("a b c", "a", "b", "c");
	}
}
