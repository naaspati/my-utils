package sam.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.stream.Collectors;

public class PrintEnvironment {
	public static String print() throws UnsupportedEncodingException {
		Set<String> ignore = new BufferedReader(new InputStreamReader(PrintEnvironment.class.getResourceAsStream("1542051261555-ignore"), "utf-8"))
				.lines()
				.map(String::trim)
				.filter(s -> !s.isEmpty() && s.charAt(0) != '#')
				.collect(Collectors.toSet());
		
		StringBuilder sb = new StringBuilder();
		sb.append(ANSI.createUnColoredBanner("SYSTEM_PROPERTIES"));
		sb.append('\n');
		
		System.getProperties().forEach((s,t)  -> {
			if(!ignore.contains(s))
				sb.append(s).append('=').append(t).append('\n');
		});
		sb.append("\n\n");
		
		sb.append(ANSI.createUnColoredBanner("SYSTEM_ENVIROMENT"));
		sb.append('\n');
		System.getenv().forEach((s,t)  -> {
			if(!ignore.contains(s))
				sb.append(s).append('=').append(t).append('\n');
		});
		sb.append('\n');
		
		return sb.toString();
	} 

}
