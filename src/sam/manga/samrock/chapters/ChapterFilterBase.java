package sam.manga.samrock.chapters;

import java.util.function.DoublePredicate;

import sam.console.ANSI;

public abstract class ChapterFilterBase implements DoublePredicate {
	public final int manga_id;
	
	private String asString;
	protected StringBuilder _sb;
	protected static final String separator = ANSI.yellow(", ");
	
	protected boolean complete;

	public ChapterFilterBase(int manga_id, String title) {
		this.manga_id = manga_id;
		
		if(title != null) 
			_sb = new StringBuilder(title).append(" [");
	}
	protected void check() {
		if(complete)
			throw new IllegalStateException("closed to modifications");
	}
	
	public void setCompleted() {
		if(complete)
			return;
		
		complete = true;
		asString = _sb == null ? super.toString() : _sb.append("]").toString();
	}
	public String toString() {
		if(!complete)
			throw new IllegalStateException("not completed");
		return asString;
	}
	@Override
	public int hashCode() {
		return manga_id;
	}
}
