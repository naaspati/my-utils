package sam.nopkg;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class AutoCloseableWrapperTest {
	
	@Test
	public void testManualClose() {
		String value = String.valueOf(System.currentTimeMillis());
		int[] called = {0};
		AutoCloseableWrapper<String> s = new AutoCloseableWrapper<>(() -> {
			called[0]++;
			return value;
		}, t -> assertSame(value, t));
		
		assertSame(s.get(), value);
		assertSame(s.get(), s.get());
		assertSame(s.get(), value);
		
		assertDoesNotThrow(s::close);
		assertThrows(IllegalStateException.class, s::get);
		assertEquals(called[0], 1);
	}
	
	@Test
	public void testAutoClose() {
		String value = String.valueOf(System.currentTimeMillis());
		int[] called = {0};
		
		AutoCloseableWrapper<String> temp = null; 
		
		try(AutoCloseableWrapper<String> s = new AutoCloseableWrapper<>(() -> {
			called[0]++;
			return value;
		}, t -> assertSame(value, t));) {
			
			temp = s;
			
			assertSame(s.get(), value);
			assertSame(s.get(), s.get());
			assertSame(s.get(), value);
			
		} catch (Exception e) {
			fail();
		}
		
		assertDoesNotThrow(temp::close);
		assertThrows(IllegalStateException.class, temp::get);
		assertEquals(called[0], 1);
	}
}
