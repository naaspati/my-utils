package sam.string;

import java.util.Iterator;

public class StringSplitIterator implements Iterator<String> {
	String current;
	int start = 0;
	int count = 0;
	private final CharSequence string;
	private final char c;
	private final int limit;

	public StringSplitIterator(CharSequence string, char c) {
		this(string, c, Integer.MAX_VALUE);
	}
	public StringSplitIterator(CharSequence string, char c, int limit) {
		this.string = string;
		this.c = c;
		this.limit = limit - 1;
		current = next0();
	}

	private String next0() {
		if(count < limit) {
			for (int end = start; end < string.length(); end++) {
				if(string.charAt(end) == c) {
					String s = string.subSequence(start, end).toString(); 
					start = end + 1;
					count++;
					return s;
				}
			}        			
		}
		if(start < string.length()) {
			String s = string.subSequence(start, string.length()).toString();
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
