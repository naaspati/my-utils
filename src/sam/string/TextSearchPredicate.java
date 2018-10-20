package sam.string;

import static sam.string.StringUtils.containsAny;
import static sam.string.StringUtils.isEmpty;
import static sam.string.StringUtils.isEmptyTrimmed;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import sam.logging.MyLoggerFactory;

public class TextSearchPredicate<E> {
	private final Function<E, String> mapper;
	final boolean isFieldLowerCased;
	private String currentSearchedText;
	public final Predicate<E> TRUE_ALL = s -> true;
	public final Predicate<E> FALSE_ALL = s -> false;
	private Predicate<E> _filter = TRUE_ALL;
	private static final Logger LOGGER = MyLoggerFactory.logger(TextSearchPredicate.class.getSimpleName());
	
	public TextSearchPredicate(Function<E, String> mapper, boolean isFieldLowerCased) {
		this.mapper = mapper;
		this.isFieldLowerCased = isFieldLowerCased;
	}
	
	private String get(E e) {
		return mapper.apply(e);
	}
	public Predicate<E> createFilter(String searchKeyword){
		_filter = filter0(searchKeyword);
		LOGGER.fine(() -> "Filter created for: "+searchKeyword);
		return _filter; 
	}
	public Predicate<E> getFilter() {
		return _filter;
	}
	private static final Pattern pattern = Pattern.compile("\\s+");
	
	public String getCurrentSearchedText() {
		return currentSearchedText;
	}
	private Predicate<E> filter0(String searchKeyword){
		currentSearchedText = searchKeyword;
		if(isEmpty(searchKeyword)) {
			return (Predicate<E>) TRUE_ALL;
		} else {
			if(isEmptyTrimmed(searchKeyword))
				return (d -> get(d).contains(searchKeyword));
			else {
				final String str = currentSearchedText = isFieldLowerCased ? searchKeyword.toLowerCase() : searchKeyword;
				if(!containsAny(str.trim(), ' ', '\n', '\t'))
					return (d -> get(d).contains(str));
				else {
					 Predicate<String> filter = pattern.splitAsStream(str)
					 .<Predicate<String>>map(s -> source -> source.contains(s))
					 .reduce(Predicate::and).orElse(null); 
					 
					 return (d -> filter.test(get(d)));
				}
			}
		}
	}
	public void reset() {
		_filter = TRUE_ALL;
		currentSearchedText = null;
	}

}
