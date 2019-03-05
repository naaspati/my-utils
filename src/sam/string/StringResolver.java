package sam.string;

import java.util.Objects;
import java.util.function.UnaryOperator;

public class StringResolver {
	
	public static void resolve(String source, char delimeter, StringBuilder sink, UnaryOperator<String> getter) {
		if(source.isEmpty())
			return;
		
		int start = 0;
		int i = 0;
		while(i < source.length()) {
			if(source.charAt(i++) == delimeter) {
				sink.append(source, start, i - 1);
				int s = i;
				int end = -1;
				
				while(i < source.length()) {
					if(source.charAt(i++) == delimeter) {
						end = i;
						break;
					}
				}
				if(end == -1)
					throw new IllegalArgumentException(String.format("no closing '%s' found for '%s' at %s", delimeter, delimeter, s));
				String key = source.substring(s, end - 1);
				sink.append(Objects.requireNonNull(getter.apply(key)));
				start = i;
			}
		}
		
		if(start < source.length())
			sink.append(source, start, source.length());
	}
}
