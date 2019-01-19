	package sam.manga.samrock.chapters;

import java.util.function.DoublePredicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import sam.logging.MyLoggerFactory;

public abstract class ChapterFilterBase implements DoublePredicate {
	public final int manga_id;
	private static final Logger LOGGER = MyLoggerFactory.logger(ChapterFilterBase.class);
	private static final boolean logable = LOGGER.isLoggable(Level.FINE);
	private StringBuilder sb;
	
	protected boolean complete;

	public ChapterFilterBase(int manga_id) {
		this.manga_id = manga_id;
		
		if(logable) {
			sb = new StringBuilder(getClass()+"[ ")
					.append("manga_id = ").append(manga_id).append(", ")
					.append("data = [");
		} 
	}
	protected void append(double d) {
		if(logable && check())
			sb.append(d).append(", ");
	}
	protected void append(int d) {
		if(logable && check())
			sb.append(d).append(", ");
	}
	protected void append(String s) {
		if(logable && check())
			sb.append(s).append(", ");
	}
	protected boolean check() {
		if(complete)
			throw new IllegalStateException("closed to modifications");
		return true;
	}
	
	public String setCompleted() {
		if(complete)
			return null;
		
		complete = true;
		if(logable) {
			if(sb.charAt(sb.length() - 2) == ',')
				sb.setLength(sb.length() - 2);
			sb.append("]]");
			String s = sb.toString();
			LOGGER.fine(() -> s);
			sb = null;
			return s;
		}
		return null;
	}
	@Override
	public int hashCode() {
		return manga_id;
	}
}
