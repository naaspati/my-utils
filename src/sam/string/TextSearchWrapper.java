package sam.string;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import sam.myutils.MyUtilsCheck;

public class TextSearchWrapper<T> {
	public final String lowercased;
	public final T owner;
	
	public static <E> List<TextSearchWrapper<E>> map(List<E> list, Function<E, String> mapper){
		return MyUtilsCheck.isEmpty(list) ? new ArrayList<>() : list.stream().map(t -> new TextSearchWrapper<>(t, mapper)).collect(Collectors.toList());
	} 
	public TextSearchWrapper(T owner, Function<T, String> mapper) {
		this.lowercased = mapper.apply(owner).toLowerCase();
		this.owner = owner;
	}
	public String getLowercased() {
		return lowercased;
	}
	public T getOwner() {
		return owner;
	}
	

}
