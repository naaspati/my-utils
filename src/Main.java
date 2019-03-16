import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import sam.io.IOUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		Random r = new Random();
		Path p = Files.createTempFile(null, null);
		
		for (int i = 0; i < 1000; i++) {
		}
	}
}
