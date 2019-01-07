package sam.logging;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Filter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogFilter implements Filter {
	private final Set<String> allowed_names;
	private final Predicate<String> allowed_patterns;

	public LogFilter() {
		String cname = getClass().getName();
		LogManager lm = LogManager.getLogManager();
		allowed_names = set(lm.getProperty(cname+".allowed.names"));
		
		allowed_patterns = set(lm.getProperty(cname+".allowed.patterns")).stream().reduce(p -> false, (predicate, pattern) -> {
			Pattern p = Pattern.compile(pattern);
			return predicate.or(s -> p.matcher(s).matches());
		}, Predicate::or);
	}
	private Set<String> set(String s) {
		if(s == null)
			return Collections.emptySet();
		else if(s.indexOf(';') < 0)
			return Collections.singleton(s.trim());
		else 
			return Pattern.compile(";").splitAsStream(s).map(String::trim).collect(Collectors.toSet());
	}

	@Override
	public boolean isLoggable(LogRecord record) {
		if(allowed_names.contains(record.getLoggerName()) || allowed_patterns.test(record.getLoggerName()))
			return true;
		return false;
	}
}
