package sam.manga.samrock.chapters;

import java.util.Objects;
import java.util.function.DoublePredicate;

public interface ChapterFilterUtils {
	
	DoublePredicate ALL_ACCEPT_FILTER = new DoublePredicate() {
		@Override
		public boolean test(double value) {
			return true;
		}
		@Override
		public String toString() {
			return "[ALL]";
		}
	};
	
	public static DoublePredicate invertFilter(String title, ChapterFilter filter) {
		Objects.requireNonNull(filter);
		
		String s = filter.toString();
		int n = s.indexOf('[');
		String s2 = n < 0 ? s : title.concat(s.substring(n));
		
		return new DoublePredicate() {
			@Override
			public boolean test(double value) {
				return !filter.test(value);
			}
			@Override
			public String toString() {
				return s2;
			}
		};
		
	}
	

}
