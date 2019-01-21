package sam.myutils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Checker {
	/**
	 * 
	 * @param condition if not true, throw new IllegalArgumentException(msg);
	 * @param msg
	 */
	public static void mustBeTrue(boolean condition, String msg) {
		if(!condition)
			throw new IllegalArgumentException(msg);
	}
	/**
	 * 
	 * @param condition if not true, throw new IllegalArgumentException(msg);
	 * @param msgSupplier
	 */
	public static void mustBeTrue(boolean condition, Supplier<String> msgSupplier) {
		if(!condition)
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
	public static <E> boolean isEmpty(int[] es) {
		return es == null || es.length == 0;
	}
	public static <E> boolean isEmpty(double[] es) {
		return es == null || es.length == 0;
	}
	public static <E> boolean isEmpty(long[] es) {
		return es == null || es.length == 0;
	}
	public static <E> boolean isEmpty(char[] es) {
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
	 * @return value != null &amp;&amp; value.getClass() == cls
	 */
	public static boolean isOfType(Object value, @SuppressWarnings("rawtypes") Class cls) {
		return value != null && value.getClass() == cls;
	}
	public static boolean exists(File file) {
		return file != null && file.exists();
	}
	public static boolean notExists(File file) {
		return file == null || !file.exists();
	}
	public static boolean exists(Path file) {
		return file != null && Files.exists(file);
	}
	public static boolean notExists(Path file) {
		return file == null || Files.notExists(file);
	}
	@SafeVarargs
	public static <E> boolean anyMatch(Predicate<E> tester, E...es) {
		for (E e : es) {
			if(tester.test(e))
				return true;
		}
		return false;
	}
	/**
	 * 
	 * @param variableNames variableNames in a single string separator by space
	 * @param variables
	 */
	public static void requireNonNull(String variableNames, Object...variables) {
		mustBeTrue(isNotEmpty(variables), "args no speficied");
		
		int n = 0;
		while( n < variables.length && variables[n++] != null) {}

		if(n == variables.length)
			return;

		String[] s = variableNames.split("\\s+");
		StringBuilder sb = new StringBuilder();

		n--;
		while(n < variables.length) {
			if(variables[n] == null)
				sb.append(s[n]).append(", ");
			n++;
		}
		throw new NullPointerException(sb.substring(0, sb.length() - 2));
	}
	public static boolean isInteger(String s) {
		if(s.trim().isEmpty())
			return false;
		
		int index = 0;
		if(s.charAt(0) == '-')
			index = 1;
		
		if(s.length() == index)
			return false;
		
		while(index < s.length()){
			char c = s.charAt(index++);
			if(c < '0' || c > '9')
				return false;
		}
		
		return true;
	}
}
