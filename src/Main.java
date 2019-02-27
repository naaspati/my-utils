import java.lang.ref.WeakReference;

import sam.logging.Logger;

public class Main {

	public static void main(String[] args) throws Exception {
		Logger.getLogger(Main.class).info("anime: {}", 1);
	}
	
	private static WeakReference<StringBuilder> wsb = new WeakReference<StringBuilder>(null);
	private static final Object LOCK = new Object();
	
	private String convert(String s) {
		int end = s.indexOf('{');
		int start = 0;
		if(end < 0)
			return s;

		synchronized (LOCK) {
			int k = 0;
			StringBuilder sb = null;
			if(wsb == null || (sb = wsb.get()) == null)
				wsb = new WeakReference<>(sb = new StringBuilder());

			int found = 0;
			while(end > 0) {
				sb.append(s, start, end + 1);
				if(s.length() > end + 1 && s.charAt(end + 1) == '}') {
					sb.append(k++);
					found++;
				}

				start = end + 1;
				end = s.indexOf('{', start);
				if(end < 0) {
					sb.append(s, start, s.length());
					break;
				}
			}

			if(found == 0) {
				sb.setLength(0);
				return s;
			} else {
				s = sb.toString();
				sb.setLength(0);
				return s;
			}
		}
	}
}
