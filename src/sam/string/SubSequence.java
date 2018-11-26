package sam.string;

public class SubSequence implements CharSequence{
	private final CharSequence source; 
	private final int from, to;
	
	public SubSequence(CharSequence source, int from, int to) {
		super();
		this.source = source;
		this.from = from;
		this.to = to;
	}

	@Override
	public int length() {
		return to - from ;
	}

	@Override
	public char charAt(int index) {
		return source.charAt(from+index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return source.subSequence(start+from, end+from);
	}
	@Override
	public String toString() {
		return source.subSequence(from, to).toString();
	}
}
