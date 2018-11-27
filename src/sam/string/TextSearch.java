package sam.string;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import sam.logging.MyLoggerFactory;

/**
 * for javafx app user TextSearchFx</br>
 * 
 * highly recommended <br>
 *   - search-key to be lowercased <br>
 *   - data  to be lowercased <br>
 * @author Sameer
 *
 * @param <E>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class TextSearch<E> {
	private static final Logger LOGGER = MyLoggerFactory.logger(TextSearch.class);

	public static final Predicate TRUE_ALL = TextSearchPredicate.TRUE_ALL;
	public static final Predicate FALSE_ALL = TextSearchPredicate.FALSE_ALL;

	public static <E> Predicate<E> trueAll() { return TRUE_ALL; }
	public static <E> Predicate<E> falseAll(){ return FALSE_ALL; }

	private String oldSearch = "";
	private String currentSearch;
	private Collection<E> allData, searchBackup;
	private Predicate<E> preFilter = TRUE_ALL, textFilter = TRUE_ALL;
	private boolean preFilterChanged, allDataChanged, newSearchContainsOldSearch;
	private final TextSearchPredicate<E> tsp;

	public TextSearch(Function<E, String> mapper) {
		this.tsp = new TextSearchPredicate<>(mapper);
	}
	public void set(Predicate<E> preFilter) {
		allFilter = null;
		this.preFilter = preFilter;
		this.preFilterChanged = true;
	}
	public void set(String searchKeyword) {
		allFilter = null;
		this.oldSearch = this.currentSearch;
		this.currentSearch = searchKeyword;
		this.newSearchContainsOldSearch = searchKeyword != null && (oldSearch == null || searchKeyword.contains(oldSearch));
		textFilter = tsp.createFilter(searchKeyword);
	}
	public String getCurrentSearchKeyword() {
		return currentSearch;
	}
	public void set(Predicate<E> prefilter, String searchString) {
		set(prefilter);
		set(searchString);
	}
	public void setAllData(Collection<E> allData) {
		this.allData = allData;
		searchBackup = null;
		allDataChanged = true;
	}
	public Collection<E> getAllData() {
		return allData;
	}
	public void clear() {
		allDataChanged = false;
		preFilterChanged = false;
		oldSearch = "";
		searchBackup = null;
		allFilter = null;
		preFilter = null;
		textFilter = null;
		tsp.clear();
	}
	public Collection<E> getFilterData() {
		return searchBackup = applyFilter(searchBackup);
	}
	public Collection<E> applyFilter(Collection<E> list){
		if(allData == null) {
			if(list != null)
				list.removeIf(getFilter().negate());
			return list;
		};

		Predicate<E> filter = getFilter(); 

		if(filter == FALSE_ALL) {
			if(list == null)
				return new ArrayList<>();
			list.clear();
			LOGGER.fine(() -> "filter == FALSE_ALL");
			return list;
		} else if(filter == TRUE_ALL) {
			oldSearch = "";
			LOGGER.fine(() -> "filter == TRUE_ALL");
			return setAll(list, allData);
		} else {
			if(allDataChanged || preFilterChanged || list == null || !newSearchContainsOldSearch) {
				LOGGER.fine(() -> "FULL FILTER: searchKey: "+wrap(currentSearch)+", "+ string("allDataChanged", allDataChanged)+ string("preFilterChanged", preFilterChanged)+  string("list == null", list == null)+  string("!newSearchContainsOldSearch", !newSearchContainsOldSearch));
				preFilterChanged = false;
				allDataChanged = false;
				return setAll(list, allData.stream().filter(filter).collect(Collectors.toList()));
			} else {
				int len = list.size();
				list.removeIf(filter.negate());
				LOGGER.fine(() -> wrap(currentSearch)+".contains("+wrap(oldSearch)+")  ("+len+" -> "+list.size()+")");
				return list;
			}
		}
	} 
	private String wrap(String s) {
		return s == null ? null : "\""+s+"\"";
	}
	private String string(String s, boolean b) {
		return b ? s+", " : "";
	}

	private Collection<E> setAll(Collection<E> list, Collection<E> allData) {
		if(list == null)
			return new ArrayList<>(allData);
		if(list instanceof ObservableList)
			((ObservableList<E>)list).setAll(allData);
		return list;
	}
	private Predicate<E> allFilter;

	public Predicate<E> getFilter() {
		if(allFilter != null)
			return allFilter;

		Predicate<E> x = preFilter;
		Predicate<E> y = textFilter;

		if(x == y)
			return allFilter = x == null ? TRUE_ALL : x;

		if(x == null || y == null)
			return allFilter = x == null ? y : x;

		return allFilter = x.and(y);
	}
}
