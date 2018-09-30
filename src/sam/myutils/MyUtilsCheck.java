package sam.myutils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public interface MyUtilsCheck {
    /**
     * 
     * @param ifNotTrueThrow if not true, throw new IllegalArgumentException(msg);
     * @param msg
     */
	public static void checkArgument(boolean ifNotTrueThrow, String msg) {
		if(!ifNotTrueThrow)
			throw new IllegalArgumentException(msg);
	}
	/**
     * 
     * @param condition if not true, throw new IllegalArgumentException(msg);
     * @param msg
     */
	public static void checkArgument(boolean ifNotTrueThrow, Supplier<String> msgSupplier) {
		if(!ifNotTrueThrow)
			throw new IllegalArgumentException(msgSupplier.get());
	}
	
	public static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}
	public static boolean isEmptyTrimmed(String s) {
		return isEmpty(s) || s.trim().isEmpty();
	}
	public static boolean isEmpty(Collection<?> s) {
		return s == null || s.isEmpty();
	}
	public static boolean isEmpty(Map<?, ?> s) {
		return s == null || s.isEmpty();
	}
	public static <E> boolean isEmpty(E[] es) {
		return es == null || es.length == 0;
	}
	public static boolean isNull(Object o) {
		return o == null;
	}
	public static boolean isNotNull(Object o) {
		return o != null;
	}
	/**
	 * 
	 * @param value
	 * @param cls
	 * @return value != null && value.getClass() == cls
	 */
	public static boolean isOfType(Object value, @SuppressWarnings("rawtypes") Class cls) {
		return value != null && value.getClass() == cls;
	}
}
