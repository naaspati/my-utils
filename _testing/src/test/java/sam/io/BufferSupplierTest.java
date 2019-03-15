package sam.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;

class BufferSupplierTest {

	@Test
	void ofInputStreamTest() throws IOException {
		Path p = Files.createTempFile(null, null);
		
		
		try {
			byte[] bytes = new byte[10000];
			Random r = new Random();
			r.nextBytes(bytes);
			
			Files.write(p, bytes);
			
			try(InputStream is = Files.newInputStream(p, StandardOpenOption.READ)) {
				BufferSupplier b = BufferSupplier.of(is, ByteBuffer.allocate(200));
				int n = 0;
				
				while(!b.isEndOfInput()) {
					ByteBuffer buf = b.next();
					while(buf.hasRemaining()) {
						assertEquals(bytes[n++], buf.get());
					}
				}
				assertEquals(bytes.length, n);
			}
		} finally {
			try {
				Files.deleteIfExists(p);
			} catch (Exception e) { }
		}
		
	}

}
