package sam.config;

import java.util.function.Predicate;

@SuppressWarnings({"rawtypes", "unchecked"})
public interface Constants {
	Predicate TRUE_ALWAYS = s -> true;
	Predicate FALSE_ALWAYS = s -> false;
	
	public static <E> Predicate<E> trueAlways() { return TRUE_ALWAYS; }
	public static <E> Predicate<E> falseAlways(){ return FALSE_ALWAYS; }
}
