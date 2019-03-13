package sam.tsv;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import sam.myutils.Checker;
import sam.string.StringSplitIterator;

class TsvParser {
	private final StringBuilder rawCol = new StringBuilder();
	private final StringBuilder unEscaped = new StringBuilder();
	
	public Iterator<String> iterator(String line) {
		return Checker.isEmpty(line) ? Collections.emptyIterator() : new UnEscapedSplitter(line);
	}

	private class UnEscapedSplitter extends StringSplitIterator {
		public UnEscapedSplitter(CharSequence string) {
			super(string, '\t');
		}
		@Override
		protected String substring(CharSequence string, int start, int end) {
			rawCol.setLength(0);
			rawCol.append(string, start, end);
			return "";
		}
		@Override
		public String next() {
			if(!hasNext())
				throw new NoSuchElementException();
			
			unEscaped.setLength(0);
			TsvUtils.unescape(rawCol, unEscaped);
			String s = unEscaped.toString();
			super.next();
			
			return s;
		}
	}
}
