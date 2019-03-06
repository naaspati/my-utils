import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import sam.io.infile.DataMeta;
import sam.io.infile.InFile;
import sam.string.StringSplitIterator;

public class Main {

	public static void main(String[] args) throws Exception {
		System.out.println(new Random().nextLong());
	}
}
