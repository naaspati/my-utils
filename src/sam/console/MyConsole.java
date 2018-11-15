package sam.console;

import java.util.Scanner;
import java.util.function.Predicate;

public interface MyConsole {
	static final Predicate<String> DEFAULT_FILTER = s -> !(s == null || s.isEmpty() || s.trim().isEmpty()); 
	
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

}
