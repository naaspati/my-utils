import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import sam.collection.ArraysUtils;
import sam.collection2.IntArrayWrappedList;
import sam.console.VT100;
import sun.security.util.ArrayUtil;

public class Main {

	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get("D:\\importents_are_here\\eclipse_workplace\\java\\_MyUtils\\src\\sam\\console\\VT100.java"));
		
		for (int i = 0; i < lines.size(); i++) {
			if(lines.get(i).trim().startsWith("public  static")) {
				System.out.println(lines.get(i).trim().replace('{', ':').replace("public static void", "def").trim());
				System.out.println("    " + lines.get(i - 1).trim().replace("/**", "\"\"\"").replace("*/", "\"\"\""));
				System.out.println("    " + lines.get(i + 1).trim().replace("System.out.", "").replace(";", "").replace(")", ", end='')"));
				System.out.println();
			}
		}
	}
}
