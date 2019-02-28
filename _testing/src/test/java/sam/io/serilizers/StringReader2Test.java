package sam.io.serilizers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;

import com.thedeanda.lorem.LoremIpsum;

public class StringReader2Test {
	
	@Test
	public void writeTest() throws IOException {
		Path p = Files.createTempFile(null, null);
		try {
			String expected = LoremIpsum.getInstance().getParagraphs(5, 15);
			
			Files.write(p, expected.getBytes("utf-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			String actual = StringReader2.getText(p);
			assertEquals(expected, actual);
			
			Files.write(p, "".getBytes("utf-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			actual = StringReader2.getText(p);
			
			assertEquals("", actual);
			
			assertThrows(NullPointerException.class, () -> StringReader2.getText(p));
		} finally {
			try {
				Files.deleteIfExists(p);
			} catch (Exception e) { }
		}
		
	}

}
