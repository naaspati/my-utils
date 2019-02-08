package sam.nopkg;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

public class AutoCloseableWrapperTest {
	
	@Test
	public void testManualClose() {
		AutoCloseableWrapper<Long> s = instance();
		
		assertEquals(s.get().longValue(), ATOMIC_LONG.get());
		assertEquals(s.get().longValue(), ATOMIC_LONG.get());
		assertSame(s.get(), s.get());
		
		assertDoesNotThrow(s::close);
		
		assertThrows(IllegalStateException.class, s::get);
	}
	
	@Test
	public void testAutoClose() {
		AutoCloseableWrapper<Long> temp = instance();
		
		try(AutoCloseableWrapper<Long> s = temp) {
			assertEquals(s.get().longValue(), ATOMIC_LONG.get());
			assertEquals(s.get().longValue(), ATOMIC_LONG.get());
			assertSame(s.get(), s.get());	
		} catch (Exception e) {
			fail();
		}
		assertThrows(IllegalStateException.class, temp::get);
	}
	
	private static final AtomicLong ATOMIC_LONG = new AtomicLong();

	private AutoCloseableWrapper<Long> instance() {
		return new AutoCloseableWrapper<>(ATOMIC_LONG::incrementAndGet, t -> System.out.println("closed: "+t));
	}
	
}
