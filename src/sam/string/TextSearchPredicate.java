package sam.string;

import static sam.string.StringUtils.containsAny;
import static sam.string.StringUtils.isEmpty;
import static sam.string.StringUtils.isEmptyTrimmed;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class TextSearchPredicate<E> {
	private final Function<E, String> mapper;
	final boolean isFieldLowerCased;
	public final Predicate<E> TRUE_ALL = s -> true;
	public final Predicate<E> FALSE_ALL = s -> false;
	
	public TextSearchPredicate(Function<E, String> mapper, boolean isFieldLowerCased) {
		this.mapper = mapper;
		this.isFieldLowerCased = isFieldLowerCased;
	}
	
	private String get(E e) {
		return mapper.apply(e);
	}
	private Predicate<E> _filter;
	public Predicate<E> createFilter(String searchKeyword){
		_filter = filter0(searchKeyword);
		return _filter; 
	}
	public Predicate<E> getFilter() {
		return _filter;
	}
	private static final Pattern pattern = Pattern.compile("\\s+");
	
	private Predicate<E> filter0(String searchKeyword){
		if(isEmpty(searchKeyword)) {
			return TRUE_ALL;
		} else {
			if(isEmptyTrimmed(searchKeyword))
				return (d -> get(d).contains(searchKeyword));
			else {
				final String str = isFieldLowerCased ? searchKeyword.toLowerCase() : searchKeyword;
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

}
