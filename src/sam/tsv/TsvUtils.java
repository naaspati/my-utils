package sam.tsv;

import java.io.IOException;

import sam.myutils.Checker;

final class TsvUtils {
	
	public static void escape(CharSequence string, Appendable sink) throws IOException {
		if(Checker.isEmpty(string))
			return;

		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			switch (c) {
				case '\\':
					sink.append('\\').append('\\');
					break;
				case '\t':
					sink.append('\\').append('t');
					break;
				case '\n':
					sink.append('\\').append('n');
					break;
				case '\r':
					sink.append('\\').append('r');
					break;
				default:
					sink.append(c);
					break;
			}
		}
	}
	public static void unescape(CharSequence source, StringBuilder target) {
		if(Checker.isEmpty(source))
			return;
		
		if(source.length() == 1) {
			target.append(source);
			return;
		}

		boolean slash = false;
		
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);

			if(slash) {
				slash = false;
				switch (c) {
					case '\\':
						target.append('\\');
						break;
					case 'n':
						target.append('\n');
						break;
					case 't':
						target.append('\t');
						break;
					case 'r':
						target.append('\r');
						break;
					default:
						throw new TsvException("uknown escape sequence: \\"+c+", in-string: "+source);
				}
			} else {
				if(c == '\\')
					slash = true;
				else
					target.append(c);
			}
		}
	}
}
