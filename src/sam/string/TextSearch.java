package sam.string;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import sam.logging.Logger;

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
	private static final Logger LOGGER = Logger.getLogger(TextSearch.class);
	private static final boolean DEBUG = LOGGER.isDebugEnabled();

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

	private WeakReference<List<E>> wsink = new WeakReference<List<E>>(new ArrayList<>());

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
			LOGGER.debug("filter == FALSE_ALL");
			return list;
		} else if(filter == TRUE_ALL) {
			oldSearch = "";
			LOGGER.debug("filter == TRUE_ALL");
			return setAll(list, allData);
		} else {
			Collection<E> source;

			if(allDataChanged || preFilterChanged || list == null || !newSearchContainsOldSearch) {
				preFilterChanged = false;
				allDataChanged = false;
				source = allData;
			} else {
				source = list;
			}

			List<E> filteredSource = sink(); 
			source.forEach(e -> {
				if(filter.test(e))
					filteredSource.add(e);
			});

			int len = source.size();
			Collection<E> result = setAll(list, filteredSource);
			filteredSource.clear();

			if(DEBUG) {
				if(source == allData)
					LOGGER.debug(() -> "FULL FILTER: searchKey: "+wrap(currentSearch)+", "+ string("allDataChanged", allDataChanged)+ string("preFilterChanged", preFilterChanged)+  string("list == null", list == null)+  string("!newSearchContainsOldSearch", !newSearchContainsOldSearch)+", size: "+len+" -> "+result.size());
				else 
					LOGGER.debug(() -> wrap(currentSearch)+".contains("+wrap(oldSearch)+")  ("+len+" -> "+list.size()+")");
			}
			return result;
		}
	}

	public void applyFilter(Collection<E> col, Predicate<E> filter) {
		Objects.requireNonNull(col);
		Objects.requireNonNull(filter);

		List<E> list = sink();
		list.clear();

		col.forEach(e -> {
			if(filter.test(e))
				list.add(e);
		});

		setAll(col, list);
		list.clear();
	} 

	private List<E> sink() {
		List<E> sink = wsink.get();

		if(sink == null)
			wsink = new WeakReference<List<E>>(sink = new ArrayList<>());

		return sink;
	}
	private String wrap(String s) {
		return s == null ? null : "\""+s+"\"";
	}
	private String string(String s, boolean b) {
		return b ? s+", " : "";
	}

	private static <E> Collection<E> setAll(Collection<E> sink, Collection<E> source) {
		if(sink == null)
			return new ArrayList<>(source);
		
		if(sink instanceof ObservableList) 
			((ObservableList)sink).setAll(source);
		else if(sink instanceof ObservableSet) {
			sink.clear();
			sink.addAll(source);
		} else {
			sink.clear();
			source.forEach(sink::add);
		}
		return sink;
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
