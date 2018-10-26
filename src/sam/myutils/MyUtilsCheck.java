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

	public static boolean isEmpty(CharSequence s) {
		return s == null || s.length() == 0;
	}
	public static boolean isNotEmpty(CharSequence s) {
		return !isEmpty(s);
	}
	public static boolean isEmptyTrimmed(CharSequence s) {
		if(isEmpty(s)) return true;

		int n = s.length();
		for (int i = 0; i < n; i++) { 
			char c = s.charAt(i);
			if(!(c == ' ' || c == '\t' || c == '\n'))
				return false;
		}
		return true;
	}
	public static boolean isEmpty(Collection<?> s) {
		return s == null || s.isEmpty();
	}
	public static boolean isNotEmpty(Collection<?> s) {
		return !isEmpty(s);
	}
	public static boolean isEmpty(Map<?, ?> s) {
		return s == null || s.isEmpty();
	}
	public static boolean isNotEmpty(Map<?, ?> s) {
		return !isEmpty(s);
	}
	public static <E> boolean isEmpty(E[] es) {
		return es == null || es.length == 0;
	}
	public static <E> boolean isNotEmpty(E[] es) {
		return !isEmpty(es);
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
