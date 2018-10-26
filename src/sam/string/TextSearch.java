package sam.string;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import sam.logging.MyLoggerFactory;

import static sam.myutils.MyUtilsCheck.*;

public class TextSearch<E> extends TextSearchPredicate<E>{
	private static final Logger LOGGER = MyLoggerFactory.logger(TextSearch.class.getSimpleName());

	private String oldSearch = "";
	private Collection<E> allData, searchBackup;
	private Runnable onChange;
	private Predicate<E> _preFilter = TRUE_ALL;
	private boolean preFilterChanged;

	public TextSearch(Function<E, String> mapper, boolean isFieldLowerCased, int searchDelay) {
		super(mapper, isFieldLowerCased);
		this.searchDelay = searchDelay;
	}

	private Thread searchThread;
	private int searchDelay;
	private AtomicReference<String> searchKey;
	private DelayQueue<Delayed> queue;

	public void setOnChange(Runnable onChange) {
		this.onChange = onChange;
	}
	
	private void setPreFilter0(Predicate<E> preFilter) {
		this._preFilter = preFilter;
		this.preFilterChanged = true;
		allFilter = null;
	} 
	
	public void search(Predicate<E> prefilter, String searchString) {
		setPreFilter0(prefilter);
		search(searchString);
	}
	public void setPreFilter(Predicate<E> preFilter) {
		setPreFilter0(preFilter);
		notifyChange();
	}
	public void search(String str) {
		if(searchDelay <= 0 || isEmpty(str)) {
			if(isEmpty(str))
				reset();
			else
				changeFilter(str);
			return;
		}

		if(searchThread == null) {
			queue = new DelayQueue<>();
			searchKey = new AtomicReference<String>(null);

			searchThread = new Thread(() -> {
				try {
					while(true) {
						queue.take();
						if(searchKey.get() == null && !queue.isEmpty())
							continue;

						String s = searchKey.getAndSet(null);
						if(s == null) continue;
						changeFilter(s);
					}
				} catch (Exception e) {
					searchThread = null;
				} 
			});
			searchThread.setDaemon(true);
			searchThread.start();
		}
		searchKey.set(str);
		queue.offer(new DL());
	}

	private class DL implements Delayed {
		private final long time = System.currentTimeMillis() + searchDelay;

		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(Delayed o) {
			return Long.compare(((DL)o).time, time);
		}
		@Override
		public long getDelay(TimeUnit unit) {
			long diff = time - System.currentTimeMillis();
			return unit.convert(diff, TimeUnit.MILLISECONDS);
		}
	}

	public void setAllData(Collection<E> allData) {
		this.allData = allData;
		reset();
	}
	public Collection<E> getAllData() {
		return allData;
	}
	public void reset() {
		if(queue != null) {
			searchKey.set(null);
			queue.clear();
		}
		
		oldSearch = "";
		searchBackup = null;
		allFilter = null;
		super.reset();
		changeFilter(null);
	}
	private void changeFilter(String s){
		createFilter(s);
		notifyChange();
	}
	private void notifyChange() {
		if(onChange != null)
			onChange.run();
	}
	public Collection<E> getFilterData() {
		return searchBackup = process(searchBackup);
	}
	public Collection<E> process(Collection<E> list){
		if(allData == null) {
			if(list != null)
				list.removeIf(getFilter().negate());
			return list;
		};

		Predicate<E> filter = getFilter(); 

		if(filter == TRUE_ALL) {
			oldSearch = "";
			LOGGER.fine(() -> "filter == TRUE_ALL");
			return setAll(list, allData);
		} else {
			String str = getCurrentSearchedText();
			String os = oldSearch;
			oldSearch = str;

			if(!preFilterChanged && list != null && os != null && str != null && str.contains(os)) {
				list.removeIf(filter.negate());
				LOGGER.fine(() -> String.format("\"%s\".contains(\"%s\")", str, os));
				return list;
			} else {
				LOGGER.fine(() -> "ALL DATA FILTER");
				preFilterChanged = false;
				return setAll(list, allData.stream().filter(filter).collect(Collectors.toList()));
			}
		}
	} 

	private Collection<E> setAll(Collection<E> list, Collection<E> allData) {
		if(list == null)
			return new ArrayList<>(allData);
		if(list instanceof ObservableList)
			((ObservableList<E>)list).setAll(allData);
		return list;
	}
	@Override
	public Predicate<E> createFilter(String searchKeyword) {
		allFilter = null;
		return super.createFilter(searchKeyword);
	}
	private Predicate<E> allFilter;
	@Override
	public Predicate<E> getFilter() {
		if(allFilter != null)
			return allFilter;

		Predicate<E> f = super.getFilter();

		if(_preFilter == TRUE_ALL && f == TRUE_ALL)
			return TRUE_ALL;
		if(_preFilter == TRUE_ALL)
			return allFilter = f;
		if(f == TRUE_ALL)
			return allFilter = _preFilter;

		return allFilter = _preFilter.and(f);
	}
}
