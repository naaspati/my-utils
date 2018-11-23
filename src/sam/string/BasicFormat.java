package sam.string;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.MissingFormatArgumentException;

/**
 * a bare minimum Formatter
 * @author Sameer
 *
 */
public class BasicFormat {
	private final Entry[] entries;
	private final String rawText;
	private final String format;

	public static enum EscapeType {
		// BRACKET,
		SLASH
	}

	private class Entry {
		private final String string;
		private final int pointer;

		public Entry(String string) {
			this.string = string;
			pointer = Integer.MAX_VALUE;
		}
		public Entry(int index, String pointer) {
			this.string = null;
			if(pointer.isEmpty())
				this.pointer = -1;
			else {
				this.pointer = Integer.parseInt(pointer);
				if(this.pointer < 0)
					throw new IllegalArgumentException("invalid index: "+this.pointer+" near: "+index);
			}
		}
		@Override
		public String toString() {
			if(string == null)
				return "Entry[pointer="+pointer+"]";
			else
				return "Entry[string="+string+"]";
		}
	}

// 	public BasicFormat(String format, EscapeType type) {
	public BasicFormat(String format) {
		this.format = format;

		int n = format.indexOf('{');
		if(n < 0) {
			n = format.indexOf('}');
			if(n >= 0)
				throw new MissingFormatArgumentException("missing opening bracket for }, at "+n) ;

			this.entries = null;
			this.rawText = format;
		} else {
			Object o = slashScaped(format);// Objects.requireNonNull(type) == EscapeType.SLASH ? slashScaped(format) : bracketScaped(format);

			if(o.getClass() ==String.class) {
				rawText = (String) o;
				entries = null;
			} else {
				rawText = null;
				entries = (Entry[]) o;
			}
		}
	}

	@SuppressWarnings("unused")
	private Entry[] bracketScaped(String format) {
		return null;
	}
	private Object slashScaped(String format) {
		ArrayList<Entry> list = new ArrayList<>();
		boolean escape = false;
		int previous = -1; 
		StringBuilder sb = sb();

		for (int i = 0; i < format.length(); i++) {
			char c = format.charAt(i);
			if(!escape && (c == '{' || c == '}')) {

				if((previous == -1 && c == '}') || (previous != -1 && c == format.charAt(previous)))
					throw new MissingFormatArgumentException((c == '{' ? "no } found for { at" : "no { found for } at")+previous+"  format: "+format) ;

				if(c == '}')
					list.add(new Entry(i, sb.toString()));
				else
					list.add(new Entry(sb.toString()));

				sb.setLength(0);
				previous = i;
				continue;
			} 

			if(escape)
				sb.setCharAt(sb.length() - 1, c);
			else
				sb.append(c);

			escape =  c == '\\' && !escape;
		}

		if(previous != -1 && previous < format.length()){
			if(format.charAt(previous) == '{')
				throw new MissingFormatArgumentException("no } found for { at" +previous) ;
			list.add(new Entry(sb.toString()));
		}
		weakSB = new WeakReference<StringBuilder>(sb);

		if(list.isEmpty())
			return format;
		return list.toArray(new Entry[list.size()]);

	}

	public static String format(String format, Object...args){
		return new BasicFormat(format).format(args);
	}

	private WeakReference<StringBuilder> weakSB = new WeakReference<>(null);

	private StringBuilder sb(){
		StringBuilder sb = weakSB.get();
		if(sb != null) 
			weakSB = new WeakReference<>(null);
		else 
			sb = new StringBuilder();

		return sb;
	}
	public StringBuilder format(StringBuilder sb, Object...args) {
		if(rawText != null) {
			sb.append(rawText);
			return sb;
		}
		int index = 0;
		Entry ee = null;
		try {
			for (Entry e : entries) {
				ee = e;
				if(e.pointer < 0)
					sb.append(arg(args, e.pointer, index++));
				else if(e.pointer != Integer.MAX_VALUE)
					sb.append(arg(args, e.pointer, Integer.MAX_VALUE));
				else
					sb.append(e.string);
			}
		} catch (IndexOutOfBoundsException e2) {
			throw new RuntimeException("index: "+ (ee.pointer == -1 ? (index-1) : ee.pointer), e2);  
		}
		return sb;
	}
	public String format(Object...args) {
		if(rawText != null) return rawText;

		StringBuilder sb = sb();
		sb.setLength(0);
		String s = format(sb, args).toString();
		weakSB = new WeakReference<StringBuilder>(sb);
		return s;
	}
	private String arg(Object[] args, int pointer, int index) {
		if(pointer < 0)
			return String.valueOf(args[index]);
		return String.valueOf(args[pointer]);
	}
	@Override
	public String toString() {
		return format;
	}


}
