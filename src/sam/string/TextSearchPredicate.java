package sam.string;

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
@SuppressWarnings({"rawtypes", "unchecked"})
public class TextSearchPredicate<E> {
	public static final Predicate TRUE_ALL = s -> true;
	public static final Predicate FALSE_ALL = s -> false;
	
	public static <E> Predicate<E> trueAll() { return TRUE_ALL; }
	public static <E> Predicate<E> falseAll(){ return FALSE_ALL; }
	
	private final Function<E, String> mapper;
	private String currentSearchedText;
	private Predicate<E> _filter = TRUE_ALL;
	
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
			return (Predicate<E>) TRUE_ALL;
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
		_filter = TRUE_ALL;
		currentSearchedText = null;
	}

}
