package sam.string;

import static sam.string.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TextSearch<E> extends TextSearchPredicate<E>{
	private String oldSearch = "";
	private List<E> allData;
	private List<E> currentResult;
	
	public TextSearch(Function<E, String> mapper, boolean isFieldLowerCased) {
		super(mapper, isFieldLowerCased);
	}
	
	public void setAllData(List<E> allData) {
		reset();
		this.allData = allData;
	}
	public Collection<E> getAllData() {
		return allData;
	}
	public List<E> getCurrentResult() {
		return currentResult == null ? null : Collections.unmodifiableList(currentResult);
	}
	private void setCurrentResult(List<E> list) {
		if(this.currentResult == null)
			currentResult = new ArrayList<>(list);
		else {
			currentResult.clear();
			currentResult.addAll(list);
		}
	}
	public void reset() {
		oldSearch = "";
		currentResult = null;
	}
	public List<E> filter(String searchKeyword){
		if(isEmpty(searchKeyword)) {
			oldSearch = "";
			setCurrentResult(allData);
			return getCurrentResult();
		} else {
			final String str = isFieldLowerCased ? searchKeyword.toLowerCase() : searchKeyword;
			Predicate<E> filter = createFilter(str);
			
			if(currentResult == null)
				setCurrentResult(allData);
			if(str.contains(oldSearch))  
				currentResult.removeIf(filter.negate());
			else 
				setCurrentResult(allData.stream().filter(filter).collect(Collectors.toList()));
			oldSearch = str;
		}
		return getCurrentResult();  
	} 
	
	

}
