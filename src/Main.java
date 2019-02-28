import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Random;

import sam.io.infile.DataMeta;
import sam.io.infile.InFile;

public class Main {

	public static void main(String[] args) throws Exception {
	}
	
	protected static ByteBuffer fill(ByteBuffer buffer, Random random) {
		while(buffer.hasRemaining())
			buffer.put((byte)random.nextInt(Byte.MAX_VALUE));
		return buffer;
	}
}
