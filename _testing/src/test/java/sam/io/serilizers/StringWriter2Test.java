package sam.io.serilizers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.thedeanda.lorem.LoremIpsum;

public class StringWriter2Test {
	
	@Test
	public void writeTest() throws IOException {
		Path p = Files.createTempFile(null, null);
		try {
			String expexted = LoremIpsum.getInstance().getParagraphs(5, 15);
			System.out.println("write text length: "+expexted.length());
			
			new StringWriter2().write(expexted, p);
			
			String actual = new String(Files.readAllBytes(p), "utf-8");
			assertEquals(expexted.length(), actual.length());
			assertEquals(expexted, actual);
			
			new StringWriter2().write("", p);
			assertEquals(0, Files.size(p));
			
			assertThrows(NullPointerException.class, () -> new StringWriter2().write(null, p));
		} finally {
			Files.deleteIfExists(p);
		}
		
	}

}
