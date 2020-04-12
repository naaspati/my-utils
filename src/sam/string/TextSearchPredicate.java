package sam.string;

import static sam.config.Constants.TRUE_ALWAYS;
import static sam.myutils.Checker.isEmpty;
import static sam.myutils.Checker.isEmptyTrimmed;
import static sam.string.StringUtils.containsAny;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;


/**
 * highly recommended <br>
 *   - search-key to be lowercased <br>
 *   - data   to be lowercased <br>
 * @author Sameer
 *
 * @param <E>
 */
@SuppressWarnings({"unchecked"})
public class TextSearchPredicate<E> {
	private final Function<E, String> mapper;
	private String currentSearchedText;
	private Predicate<E> _filter = TRUE_ALWAYS;
	
	public TextSearchPredicate(Function<E, String> mapper) {
		this.mapper = mapper;
	}
	
	private String get(E e) {
		return mapper.apply(e);
	}
	public Predicate<E> createFilter(String searchKeyword){
		return _filter = filter0(searchKeyword);
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
			return (Predicate<E>) TRUE_ALWAYS;
		} else {
			if(isEmptyTrimmed(searchKeyword))
				return (d -> get(d).contains(searchKeyword));
			else {
				if(!containsAny(searchKeyword.trim(), ' ', '\n', '\t'))
					return (d -> get(d).contains(searchKeyword));
				else {
					 Predicate<String> filter = pattern.splitAsStream(searchKeyword)
					 .<Predicate<String>>map(s -> source -> source.contains(s))
					 .reduce(Predicate::and).get(); 
					 
					 return (d -> filter.test(get(d)));
				}
			}
		}
	}
	public void clear() {
		_filter = TRUE_ALWAYS;
		currentSearchedText = null;
	}

}
