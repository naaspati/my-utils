package sam.myutils;

import java.util.Map;

public interface MyUtilsDebug {
	public static void print(Map<?, ?> map) {
		print(map, "%s\t%s%n");
	}
	public static void print(Map<?, ?> map, String format) {
		map.forEach((s,t) -> System.out.printf(format, s,t));
	}
}
