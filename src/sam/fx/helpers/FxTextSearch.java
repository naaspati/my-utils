package sam.fx.helpers;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.application.Platform;
import sam.string.TextSearch;
import sam.string.TextSearchPredicate;
import sam.thread.DelayedQueueThread;


/**
 * search values must be in lowerCased <br>
 * search key will be converted to lowerCase 
 * @author Sameer
 *
 * @param <E>
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class FxTextSearch<E> {
	public static final Predicate TRUE_ALL = TextSearchPredicate.TRUE_ALL;
	public static final Predicate FALSE_ALL = TextSearchPredicate.FALSE_ALL;

	public static <E> Predicate<E> trueAll() { return TRUE_ALL; }
	public static <E> Predicate<E> falseAll(){ return FALSE_ALL; }

	private final int searchDelay ;
	private DelayedQueueThread<String> searchThread;
	private TextSearch<E> search;
	private Runnable onChange;
	private final boolean lowerCaseSearchKey;
	private static final String PRE_FILTER_CHANGE = new String(); 

	public FxTextSearch(Function<E, String> mapper, int searchDelay, boolean lowerCaseSearchKey) {
		if(searchDelay < 0)
			throw new IllegalArgumentException("searchDelay < 0");

		this.searchDelay = searchDelay ;
		this.search = new TextSearch<>(mapper);
		this.lowerCaseSearchKey = lowerCaseSearchKey;
	}
	public void setOnChange(Runnable onChange) {
		clear();
		this.onChange = onChange;
	}
	public void set(Predicate<E> preFilter) {
		search.set(preFilter);
		addSearch(PRE_FILTER_CHANGE);
	}
	public void setAllData(Collection<E> allData) {
		search.setAllData(allData);
		Platform.runLater(this::notifyChange);
	}
	public Collection<E> getAllData() {
		return search.getAllData();
	}
	public Collection<E> applyFilter(Collection<E> list) {
		return search.applyFilter(list);
	}
	public void addSearch(String searchKeyword) {
		if(searchDelay == 0) {
			search.set(searchKeyword);
			notifyChange();
		} else {
			searchThread().add(searchKeyword);
		}
	}

	private DelayedQueueThread<String> searchThread() {
		if(searchThread != null) return searchThread;

		searchThread = new DelayedQueueThread<>(searchDelay, this::apply);
		searchThread.start();
		return searchThread;
	}
	private void apply(String key) {
		Platform.runLater(() -> {
			if(key != PRE_FILTER_CHANGE)
				search.set(!lowerCaseSearchKey || key == null ? key : key.toLowerCase());

			notifyChange();
		});
	}
	
	private void notifyChange() {
		if(onChange != null)
			onChange.run();
	}
	public void clear() {
		if(searchThread != null)
			searchThread.clear();
		search.clear();
	}
	public void stop(){
		if(searchThread == null) return;
		searchThread.stop();
		searchThread = null;
	}
	public Predicate<E> getFilter() {
		return search.getFilter();
	}
	
}
