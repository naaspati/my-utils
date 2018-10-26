package sam.string;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.MissingFormatArgumentException;
import java.util.Objects;

import sam.myutils.MyUtilsCheck;

/**
 * a bare minimum Formatter
 * @author Sameer
 *
 */
public class BasicFormat {
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

	public BasicFormat(String format) {
		if(MyUtilsCheck.isEmptyTrimmed(format)) {
			this.entries = new Entry[] {new Entry(format)}; 
		} else {
			ArrayList<Entry> list = new ArrayList<>();
			boolean previousSlash = false;
			int previous = -1; 
			StringBuilder sb = sb();
			
			for (int i = 0; i < format.length(); i++) {
				char c = format.charAt(i);
				if(!previousSlash && (c == '{' || c == '}')) {
					
					if((previous == -1 && c == '}') || (previous != -1 && c == format.charAt(previous)))
						throw new MissingFormatArgumentException((c == '{' ? "no } found for { at" : "no { found for } at")+previous) ;

					if(c == '}')
						list.add(new Entry(i, sb.toString()));
					else
						list.add(new Entry(sb.toString()));
					
					sb.setLength(0);
					previous = i;
					continue;
				} 
				
				if(previousSlash)
					sb.setCharAt(sb.length() - 1, c);
				else
					sb.append(c);
				
				previousSlash =  c == '\\' && !previousSlash;
			}
			
			if(previous != format.length() - 1){
				if(format.charAt(previous) == '{')
					throw new MissingFormatArgumentException("no } found for { at" +previous) ;
				list.add(new Entry(sb.toString()));
			}
			this.entries = list.toArray(new Entry[0]);
			weakSB = new WeakReference<StringBuilder>(sb);
		}
	}

	private WeakReference<StringBuilder> weakSB = new WeakReference<>(null);
	
	private synchronized StringBuilder sb(){
		StringBuilder sb = weakSB.get();
		if(sb != null) 
			weakSB = new WeakReference<>(null);
		else 
			sb = new StringBuilder();
		
		return sb;
	}
	

	public String format(Object...args) {
		if(entries.length == 0) return "";
		if(entries.length == 1) {
			if(entries[0].string != null)
				return entries[0].string;

			Objects.requireNonNull(args);
			return arg(args, entries[0].pointer, 0);
		}
		
		StringBuilder sb = sb();

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
			weakSB = new WeakReference<StringBuilder>(sb);
		}

	}
	private String arg(Object[] args, int pointer, int index) {
		if(pointer < 0)
			return String.valueOf(args[index]);
		return String.valueOf(args[pointer]);
	}
}
