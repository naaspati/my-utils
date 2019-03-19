package sam.fx.textsearch;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.application.Platform;
import sam.string.TextSearch;
import sam.thread.DelayedActionThread;


/**
 * search values must be in lowerCased <br>
 * search key will be converted to lowerCase 
 * @author Sameer
 *
 * @param <E>
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class FxTextSearch<E> {
	public static final Predicate TRUE_ALL = TextSearch.TRUE_ALL;
	public static final Predicate FALSE_ALL = TextSearch.FALSE_ALL;

	public static <E> Predicate<E> trueAll() { return TRUE_ALL; }
	public static <E> Predicate<E> falseAll(){ return FALSE_ALL; }

	private final DelayedActionThread<String> handler;
	private TextSearch<E> search;
	private Runnable onChange;
	private static final String JUST_NOTIFY = new String();
	private final boolean lowercaseKeyword;
	private boolean disable = false;

	public FxTextSearch(Function<E, String> mapper, int searchDelay, boolean lowercaseKeyword) {
		if(searchDelay < 0)
			throw new IllegalArgumentException("searchDelay < 0");

		this.lowercaseKeyword = lowercaseKeyword;
		this.search = new TextSearch<>(mapper);
		this.handler = searchDelay == 0 ? null : new DelayedActionThread<>(searchDelay, this::apply);
	}
	public void enable() {
		this.disable = false;
	}
	public void disable() {
		this.disable = true;
	}
	public boolean isDisabled() {
		return disable;
	}
	public void setOnChange(Runnable onChange) {
		this.onChange = onChange;
	}
	public void set(Predicate<E> preFilter) {
		search.set(preFilter);
		addSearch(JUST_NOTIFY);
	}
	public void set(String searchKey) {
		addSearch(searchKey);
	}
	public void set(Predicate<E> preFilter, String searchKey) {
		search.set(preFilter);
		addSearch(searchKey);
	}
	public void setAllData(Collection<E> allData) {
		search.setAllData(allData);
		addSearch(JUST_NOTIFY);
	}
	public Collection<E> getAllData() {
		return search.getAllData();
	}
	public Collection<E> applyFilter(Collection<E> list) {
		return search.applyFilter(list);
	}
	public void addSearch(String searchKeyword) {
		if(disable)
			return;
		
		if(handler == null) {
			search.set(searchKeyword);
			notifyChange();
		} else {
			handler.queue(searchKeyword);
		}
	}
	private void apply(String key) {
		Platform.runLater(() -> {
			if(disable)
				return;
			
			if(key != JUST_NOTIFY)
				search.set(!lowercaseKeyword || key == null ? key : key.toLowerCase());

			notifyChange();	
		});	
	}
	
	private void notifyChange() {
		if(onChange != null)
			onChange.run();
	}
	public void stop(){
		handler.stop();
	}
	public Predicate<E> getFilter() {
		return search.getFilter();
	}
	public void applyFilter(Collection<E> col, Predicate<E> filter) {
		search.applyFilter(col, filter);
	}
}
