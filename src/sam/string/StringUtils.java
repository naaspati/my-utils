package sam.string;

import static sam.myutils.Checker.isEmpty;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//VERSION = 0.004;
public class StringUtils {


	public static boolean contains(String s, char c) {
		return s.indexOf(c) >= 0;	
	}
	public static boolean containsAny(String s, char...cs) {
		for (char c : cs) {
			if(s.indexOf(c) >= 0)
				return true;
		}
		return false;
	}
	public static boolean containsAll(String s, char...cs) {
		for (char c : cs) {
			if(s.indexOf(c) < 0)
				return false;
		}
		return true;
	}

	public static String[] split(String string, char c) {
		return split(string, c, Integer.MAX_VALUE);
	}
	public static String[] split(String string, char c, int limit) {
		Objects.requireNonNull(string);
		if(isEmpty(string))
			return new String[0];

		return splitStream(string, c, limit).toArray(String[]::new);
	}
	public static Stream<String> splitStream(String string, char c) {
		return splitStream(string, c, Integer.MAX_VALUE);
	}
	public static String[] splitAtNewline(String string) {
		return splitAtNewline(string, Integer.MAX_VALUE);
	}
	public static String[] splitAtNewline(String string, int limit) {
		return splitAtNewlineStream(string).toArray(String[]::new);
	}

	public static Stream<String> splitAtNewlineStream(String string) {
		return splitAtNewlineStream(string, Integer.MAX_VALUE);
	}
	public static Stream<String> splitAtNewlineStream(String string, int limit) {
		return splitStream(string, '\n', limit).map(s -> s.isEmpty() || s.charAt(s.length() - 1) != '\r' ? s : s.substring(0, s.length() - 1));
	}
	public static Stream<String> splitStream(String string, char c, int limit) {
		Objects.requireNonNull(string);
		if(isEmpty(string))
			return Stream.empty();

		Spliterator<String> sp = Spliterators.spliteratorUnknownSize(new StringSplitIterator(string, c, limit), Spliterator.IMMUTABLE);
		return StreamSupport.stream(sp, false);
	}

	public static class StringSplitIterator implements Iterator<String> {
		String current;
		int start = 0;
		int count = 0;
		private final String string;
		private final char c;
		private final int limit;

		public StringSplitIterator(String string, char c, int limit) {
			this.string = string;
			this.c = c;
			this.limit = limit - 1;
			current = next0();
		}

		private String next0() {
			if(count < limit) {
				for (int end = start; end < string.length(); end++) {
					if(string.charAt(end) == c) {
						String s = string.substring(start, end); 
						start = end + 1;
						count++;
						return s;
					}
				}        			
			}
			if(start < string.length()) {
				String s = string.substring(start, string.length());
				start = string.length();
				count++;
				return s;
			}
			return null;
		}

		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public String next() {
			String ss = current;
			this.current = next0();
			return ss;
		}
	}
	/**
	 * joinIfNotEndsWithSeparator("anime", "sanam", "/") -> anime/sanam
	 * joinIfNotEndsWithSeparator("anime/", "sanam", "/") -> anime/sanam
	 * 
	 * @param prefix
	 * @param suffix
	 * @param separator
	 * @return
	 */
	public static String joinIfNotEndsWithSeparator(String prefix, String suffix, String separator) {
		return prefix.endsWith(separator) ? prefix.concat(suffix) : (prefix.concat(separator).concat(suffix));
	}
	public static String camelCaseToSpacedString(String s) {
		if(s.isEmpty())
			return s;

		if(s.chars().allMatch(i -> !Character.isUpperCase(i))) 
			return s;

		StringBuilder sb = new StringBuilder(s.length()+16);
		int i = 0;
		while(!Character.isUpperCase(s.charAt(i++))) 
			sb.append(s.charAt(i-1));

		if(sb.length() != 0 && Character.isLowerCase(sb.charAt(sb.length() - 1)))
			sb.append(' ');
		sb.append(s.charAt(i - 1));

		for (; i < s.length(); i++) {
			char c = s.charAt(i);
			if(Character.isUpperCase(c) && i < s.length() - 1 && Character.isLowerCase(s.charAt(i + 1)))
				sb.append(' ');

			sb.append(c);
		}
		return sb.toString();
	}
	public static String join(Object...data) {
		if(data == null || data.length == 0) return null;
		if(data.length == 1) return String.valueOf(data[0]);
		if(data.length == 2) return data[0] != null && data[1] != null ? String.valueOf(data[0]).concat(String.valueOf(data[1])) : String.valueOf(data[0]) + data[1];

		StringBuilder sb = new StringBuilder();
		for (Object s : data) sb.append(s);
		return sb.toString();
	}

	public static String joinWithSeparator(String separator, Object...data) {
		if(data == null || data.length == 0) return null;
		if(data.length == 1) return String.valueOf(data[0]);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			sb.append(data[i]);
			if(i != data.length - 1)
				sb.append(separator);
		}
		return sb.toString();
	}


	public static String doubleToString(double d) {
		return doubleToString(d, Integer.MAX_VALUE);
	}
	public static String doubleToString(double d, int trimToPlaces) {
		if(d == (int)d)
			return String.valueOf((int)d);
		else {
			String s = String.valueOf(d);
			if(trimToPlaces == Integer.MAX_VALUE)
				return s;
			int index = s.indexOf('.') + trimToPlaces + 1;
			return s.substring(0, Math.min(index, s.length()));
		}
	}
	/**
	 * times can be 0 or more <br>
	 * in case of times = 0, returned value is a empty string.
	 * 
	 * @param s
	 * @param times 
	 * @return
	 */
	public static String repeat(String s, int times) {
		Objects.requireNonNull(s);

		if(times == 0 || s.isEmpty())
			return "";
		if(times == 1)
			return s;
		if(times == 2)
			return s.concat(s);
		if(s.length() == 1)
			return String.valueOf(repeat(s.charAt(0), times));

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++) 
			sb.append(s);

		return sb.toString();
	}

	private static char[] repeat(char c, int times) {
		if(times == 0)
			return new char[0];
		if(times == 1)
			return new char[] {c};

		char[] cs = new char[times];
		Arrays.fill(cs, c);

		return cs;
	}

	public static StringBuilder repeat(CharSequence s, int times, StringBuilder sink) {
		Objects.requireNonNull(s);

		for (int i = 0; i < times; i++) 
			sink.append(s);

		return sink;
	}
	public static String remove(String from, char charater) {
		if(from.isEmpty())
			return from;
		if(!contains(from, charater))
			return from;

		char[] chars = from.toCharArray();
		int n = 0;
		for (char c : chars) {
			if(c != charater)
				chars[n++] = c;
		}
		return String.valueOf(chars, 0, n);
	}
	public static StringBuilder joinToStringBuilder(Object...values) {
		if(values == null)
			return null;
		StringBuilder sb = new StringBuilder();
		if(values.length == 0)
			return sb;

		for (Object o : values)
			sb.append(o);

		return sb;
	}

	public static String trimLeft(String s) {
		if(s.isEmpty()) return s;

		int n = 0;
		while(s.charAt(n++) == ' ') {}
		if(n == 0) return s;
		return s.substring(n-1);
	}
	public static String trimRight(String s) {
		if(s.isEmpty()) return s;

		int n = s.length();
		while(s.charAt(--n) == ' ') {}
		if(n == 0) return s;
		return s.substring(0, n+1);
	}
}
