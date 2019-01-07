package test.sam.myutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import sam.myutils.Checker;

public class CheckerTest {

	@Test(expected = IllegalArgumentException.class)
	public void testMustBeTrueBooleanString() {
		Checker.mustBeTrue(false, "msg");
	}
	
	@Test
	public void testRequireNull() {
		try {
			Checker.requireNonNull("a b c", "a", null, "c");
		} catch (NullPointerException e) {
			assertEquals(e.getClass(), NullPointerException.class);
			assertEquals(e.getMessage(), "b");
		}
	}
	@Test
	public void testRequireNull2() {
		Checker.requireNonNull("a b c", "a", "b", "c");
	}
}
