package sam.io.serilizers;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.thedeanda.lorem.LoremIpsum;

import sam.io.infile.InFileTest;
import sam.test.commons.TempDir;
public class StringReader2Test {
	private static final Logger LOGGER = Logger.getLogger(StringReader2Test.class.getSimpleName());
	private static TempDir tempdir;

	@BeforeAll
	public static void setup() throws IOException {
		tempdir = new TempDir(InFileTest.class.getSimpleName()) {
			@Override
			protected Logger logger() {
				return LOGGER;
			}
		};
	}

	@AfterAll
	public static void cleanup() throws IOException {
		tempdir.close();
		tempdir = null;
	}

	@Test
	public void readTest() throws IOException {
		Path p = tempdir.nextPath();
		try {
			String expected = LoremIpsum.getInstance().getParagraphs(5, 15);

			Files.write(p, expected.getBytes("utf-8"), CREATE, TRUNCATE_EXISTING);
			String actual = getText(p);
			assertEquals(expected, actual);

			Files.write(p, "".getBytes("utf-8"), CREATE, TRUNCATE_EXISTING);
			actual = getText(p);

			assertEquals("", actual);
		} finally {
			tempdir.deleteQuietly(p);
		}
	}
	
	@Test
	public void readTest2() throws IOException {
		Path p = tempdir.nextPath();
		try {
			String expected = LoremIpsum.getInstance().getParagraphs(5, 15);

			Files.write(p, expected.getBytes("utf-8"), CREATE, TRUNCATE_EXISTING);
			StringBuilder actual = getText0(p);
			assertEquals(expected, actual.toString());

			Files.write(p, "".getBytes("utf-8"), CREATE, TRUNCATE_EXISTING);
			actual = getText0(p);

			assertEquals("", actual.toString());
		} finally {
			tempdir.deleteQuietly(p);
		}
	}

	private String getText(Path p) throws IOException {
		return getText0(p).toString(); 
	}
	private StringBuilder getText0(Path p) throws IOException {
		return new StringReader2().getText(p);
	}

}
