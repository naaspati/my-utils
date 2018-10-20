package sam.string;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import sam.myutils.MyUtilsCheck;

/**
 * a bare minimum Formatter
 * @author Sameer
 *
 */
public class MessageFormat2 {
	public static void main(String[] args) {
		
		String[] formats = {
				"{} anime {} gone wild ",
				" anime {} gone wild {}",
				"{0} anime {1} gone wild ",
				" anime {0} gone wild { 1 }",
				"{} anime {} gone wild {}",
				"{0} anime {1} gone wild {2}",
				"{} anime {} gone wild \\{\\}",
				"{0} anime {1} gone wild {2}"
		};
		
		String[] formats2 = {
				"{0} anime {1} gone wild ",
				"{0} anime {1} gone wild {2}",
				"{0} anime {1} gone wild {2}"
		};
		
		
		for (String s : formats) {
			MessageFormat2 m = new MessageFormat2(s) ;
			System.out.println(s);
			String s2 = Arrays.stream(m.entries).map(e -> e.string != null ? e.string : e.pointer == -1 ? "{}" : "{"+e.pointer+"}")
					.collect(Collectors.joining(""));
			System.out.println(s2);
			System.out.println(s.equals(s2));
			System.out.println(m.format(1,2,3));
			System.out.println();
		}
		
		System.out.println("----------------------------------");
		
		for (String s : formats2) {
			MessageFormat2 m = new MessageFormat2(s) ;
			System.out.println(s);
			System.out.println(m.format(1,2,3));
			System.out.println(MessageFormat.format(s, 1,2,3));
			System.out.println(m.format(1,2,3).equals(MessageFormat.format(s, 1,2,3)));
			System.out.println();
		}
	}

	private final Entry[] entries;

	private class Entry {
		private final String string;
		private final int pointer;

		public Entry(String string) {
			this.string = string;
			pointer = Integer.MAX_VALUE;
		}
		public Entry(int index, String pointer) {
			this.string = null;
			pointer = pointer.substring(1, pointer.length() - 1).trim();
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
			return "Entry[" +(string == null ? "pointer="+pointer : "string="+string)+"]";
		}
	}

	public MessageFormat2(String format) {
		if(MyUtilsCheck.isEmptyTrimmed(format)) {
			this.entries = new Entry[] {new Entry(format)}; 
		} else {
			ArrayList<Entry> list = new ArrayList<>();

			int n = -1, start = -1, end = -1;

			while(++n < format.length()) {
				if(format.charAt(n) == '{' && (n == 0 || format.charAt(n - 1) != '\\')) {
					start = n;
					if(start != 0)
						list.add(new Entry(format.substring(end < 0 ? 0 : end +1, start)));

				} else if(format.charAt(n) == '}') {
					if(n == 0) throw new IllegalArgumentException("no { found for } at "+0);
					if(format.charAt(n - 1) == '\\' && (n == 1 || format.charAt(n - 2) == '\\')) continue;
					if(start == -1)  throw new IllegalArgumentException("no { found for } at "+n);
					list.add(new Entry(n, format.substring(start, n+1)));
					end = n;
				}
			}
			if(end != format.length() - 1)
				list.add(new Entry(format.substring(end+1)));

			this.entries = list.toArray(new Entry[0]);
		}
	}

	private WeakReference<StringBuilder> wsb = new WeakReference<>(null);

	public String format(Object...args) {
		if(entries.length == 0) return "";
		if(entries.length == 1) {
			if(entries[0].string != null)
				return entries[0].string;

			Objects.requireNonNull(args);
			return arg(args, entries[0].pointer, 0);
		}

		StringBuilder sb = wsb.get();
		if(sb != null) 
			wsb = new WeakReference<>(null);
		else 
			sb = new StringBuilder();

		try {
			sb.setLength(0);
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
			return sb.toString();
		} finally {
			wsb = new WeakReference<StringBuilder>(sb);
		}

	}
	private String arg(Object[] args, int pointer, int index) {
		if(pointer < 0)
			return String.valueOf(args[index]);
		return String.valueOf(args[pointer]);
	}
}
