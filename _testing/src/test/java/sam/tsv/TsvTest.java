package sam.tsv;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.thedeanda.lorem.LoremIpsum;

public class TsvTest {

	@Test
	public void test1() throws IOException {
		tester(lorem -> lorem.getWords(5));
		tester(lorem -> lorem.getParagraphs(0, 5));
		
		StringBuilder sb = new StringBuilder();
		char[] chars = {'\t', '\r', '\n', '\\', ' '};
		Random r = new Random();
		
		tester(lorem -> {
			sb.setLength(0);
			String s = lorem.getWords(5);
			
			for (int i = 0; i < s.length(); i++) {
				char c = chars[r.nextInt(chars.length)];
				sb.append(c == ' ' ? s.charAt(i) : c);
			}
			return sb.toString();
		});
	}
	void tester(Function<LoremIpsum, String> getString) throws IOException {
		String[] columns =  {"1", "2", "3", "4"};
		Tsv tsv = new Tsv(columns);

		StringBuilder expected = new StringBuilder();
		String[] array = new String[columns.length];
		LoremIpsum lorem = LoremIpsum.getInstance();
		
		for (String c : columns) 
			expected.append(c).append('\t');
		
		expected.setCharAt(expected.length() - 1, '\n');

		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < array.length; j++) {
				array[j] = getString.apply(lorem);
				expected.append(array[j].replace("\\", "\\\\").replace("\t", "\\t").replace("\r", "\\r").replace("\n", "\\n"));
				
				if(j < array.length - 1)
					expected.append('\t');
			}
			
			expected.append('\n');
			tsv.addRow(array);
		}
		
		StringBuilder actual = new StringBuilder();
		tsv.save(actual);
		
		assertNotSame(actual, expected);
		assertEquals(actual.length(), expected.length());
		assertEquals(CharBuffer.wrap(actual), CharBuffer.wrap(expected));
		
		
		Tsv t2 = new Tsv(new BufferedReader(new StringReader(expected.toString())));
		
		System.out.println(t2.getColumnNames());
		assertArrayEquals(columns, t2.getColumnNames());
		
		Iterator<Row> t2rows = t2.iterator();
		
		for (Row row : tsv) 
			row.equals(t2rows.next());
	} 

}
