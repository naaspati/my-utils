package sam.string;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class StringSplitIterator implements Iterator<String> {
	String current;
	int start = 0;
	int count = 0;
	protected final CharSequence string;
	protected final char c;
	protected final int limit;

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
					String s = substring(string, start, end); 
					start = end + 1;
					count++;
					return s;
				}
			}        			
		}
		if(start < string.length()) {
			String s = substring(string, start, string.length());
			start = string.length();
			count++;
			return s;
		}
		
		return null;
	}

	protected String substring(CharSequence string, int start, int end) {
		return string.subSequence(start, end).toString();
	}
	@Override
	public boolean hasNext() {
		return current != null;
	}

	@Override
	public String next() {
		if(!hasNext())
			throw new NoSuchElementException();
		
		String ss = current;
		this.current = next0();
		return ss;
	}
}
