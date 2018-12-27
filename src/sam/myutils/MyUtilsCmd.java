package sam.myutils;

import java.util.Arrays;
import java.util.Formatter;

import sam.console.ANSI;

public interface MyUtilsCmd {
	public static void beep(int count) {
		char[] chars = new char[count];
		Arrays.fill(chars, '\007');
		System.out.println(new String(chars));
	}
	public static StringBuilder helpString(String[][] array, StringBuilder sb) {
		int max = Arrays.stream(array)
				.map(s -> s[0])
				.mapToInt(String::length)
				.max()
				.orElse(5) + 5;

		Formatter formatter = new Formatter(sb);
		String format = ANSI.yellow("%-"+max+"s")+"%s\n";

		for (String[] s : array)
			formatter.format(format, (Object[])s);

		formatter.close();
		return sb;
	}

}
