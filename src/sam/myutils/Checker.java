package sam.myutils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Checker {
	private Checker() {}
	
	@FunctionalInterface
	public static interface MessageSupplier {
		String get();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void assertIsNull(Object obj) {
		assertTrue(obj == null, (Supplier)null);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void assertTrue(boolean condition) {
		assertTrue(condition, (Supplier)null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void assertTrue(boolean condition, MessageSupplier messageSupplier) {
		assertTrue(condition, (Supplier)() -> new AssertionError(messageSupplier.get()));
	}
	
	public static void assertTrue(boolean condition, Supplier<RuntimeException> exceptionSupplier) {
		if(!condition) {
			if(exceptionSupplier == null)
				throw new AssertionError();
			else
				throw exceptionSupplier.get();
		}
	}

	public static boolean isEmpty(CharSequence s) {
		return s == null || s.length() == 0;
	}
	public static boolean isNotEmpty(CharSequence s) {
		return !isEmpty(s);
	}
	public static boolean isNotEmptyTrimmed(CharSequence s) {
		return !isEmptyTrimmed(s);
	}
	public static boolean isEmptyTrimmed(CharSequence s) {
		if(isEmpty(s)) return true;

		int len = s.length();
		int i = len/2;
		int j = len/2;
		int x = 0;
		int y = s.length() - 1;


		while(i >= 0 || j < s.length()) {
			if(i >= x)
				if(!isSpace(s, i) || !isSpace(s,x))
					return false;
			if(j <= y)
				if(!isSpace(s,j) || !isSpace(s,y))
					return false;
			i--;
			x++;
			j++;
			y--;
		}
		return true;
	}

	private static boolean isSpace(CharSequence s, int index) {
		char c = s.charAt(index);
		return c == ' ' || c == '\t' || c == '\r' || c == '\n';
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
	@SafeVarargs
	public static <E> boolean allMatch(Predicate<E> tester, E...es) {
		for (E e : es) {
			if(!tester.test(e))
				return false;
		}
		return true;
	}
	/**
	 * 
	 * @param variableNames variableNames in a single string separator by space
	 * @param variables
	 */
	public static void requireNonNull(String variableNames, Object...variables) {
		assertTrue(isNotEmpty(variables), () -> new IllegalArgumentException("args not speficied"));

		if(variables.length == 1) {
			if(variables[0] == null)
				throw new NullPointerException(variableNames);
			return;
		}

		int n = 0;
		while( n < variables.length && variables[n++] != null) {}

		if(n == variables.length)
			return;

		String[] s = variableNames.split("\\s+|(?:\\s*,\\s*)");
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
	public static boolean isEqual(CharSequence s1, CharSequence s2) {
		if(s1 == s2)
			return true;
		if(s1 == null || s2 == null)
			return false;
		
		if(s1.length() != s2.length())
			return false;
		
		if(s1.length() == 0 && s2.length() == 0)
			return true;
		
		for (int i = 0; i < s1.length(); i++) {
			if(s1.charAt(i) != s2.charAt(i))
				return false;
		}
		
		return true;
	}
}
