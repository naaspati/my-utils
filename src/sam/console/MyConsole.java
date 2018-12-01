package sam.console;

import java.util.Scanner;
import java.util.function.Predicate;

public interface MyConsole {
	static final Predicate<String> DEFAULT_FILTER = s -> !(s == null || s.isEmpty() || s.trim().isEmpty());
	public static final Predicate<String> YES_NO_FILTER = s -> {
		switch (s.toLowerCase()) {
			case "y": return true;
			case "n": return true;
			case "yes": return true;
			case "no": return true;
			default: return false;
		}
	};
	
	public static String getResponse(String msg) {
		return getResponse(msg, null, DEFAULT_FILTER);
	}
	@SuppressWarnings("resource")
	public static String getResponse(String msg, String invalidInputMessage, Predicate<String> responseFilter) {
		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.print(msg);
			String s = sc.nextLine();
			if(responseFilter.test(s))
				return s;
			else if(invalidInputMessage != null)
				System.out.println(invalidInputMessage);
		}
	}
	public static boolean confirmation(String msg, String invalidInputMessage) {
		String s = getResponse(msg, invalidInputMessage, YES_NO_FILTER);
		return s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes");
	}

}
