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
			String s = LoremIpsum.getInstance().getParagraphs(5, 15);
			System.out.println("write text length: "+s.length());
			
			StringWriter2.setText(p, s);
			
			assertEquals(s, new String(Files.readAllBytes(p), "utf-8"));
			
			StringWriter2.setText(p, "");
			assertEquals(0, Files.size(p));
			
			assertThrows(NullPointerException.class, () -> StringWriter2.setText(p, null));
		} finally {
			Files.deleteIfExists(p);
		}
		
	}

}
