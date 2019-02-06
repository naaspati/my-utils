package sam.myutils;

public interface UrlUtils {
	public static String extractName(String url) {
		if(Checker.isEmptyTrimmed(url) || (url.length() == 1 && (url.charAt(0) == '?' || url.charAt(0) == '/' )))
			throw new IllegalArgumentException("empty url: \'"+url+"\'");

		int end = url.indexOf('?');
		if(end > 0)
			return url.substring(url.lastIndexOf('/', end - 1) + 1, end);
		else
			end = url.length();

		int start = url.lastIndexOf('/', end - 1);
		if(start == end - 1) {
			end = start;
			start = url.lastIndexOf('/', end - 1);
		}

		start++;

		return url.substring(start, end); 
	}

}
