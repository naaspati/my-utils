package sam.tsv;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Path;
import java.util.ArrayList;

import sam.io.serilizers.StringWriter2;
import sam.reference.WeakQueue;

final class TsvUtils {
	static final WeakQueue<StringBuilder> wsb = new WeakQueue<>(StringBuilder::new);
	static final WeakQueue<ArrayList<String>> wlist = new WeakQueue<>(ArrayList::new);

	private TsvUtils() {}

	static String[] split(String line) {
		if(line == null || line.isEmpty())
			return new String[0];

		StringBuilder sb = wsb.poll();
		ArrayList<String> list = wlist.poll();
		sb.setLength(0);
		list.clear();
		

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if(c == '\t') {
				list.add(unescape(sb).toString());
				sb.setLength(0);
			} else {
				sb.append(c);
			}
		}
		
		if(sb.length() != 0)
			list.add(unescape(sb).toString());
		
		String[] array = list.toArray(new String[list.size()]);
		list.clear();
		wlist.offer(list);
		offer(sb);
		return array;
	}
	static CharSequence escape(CharSequence string) {
		if(string == null || string.length() == 0)
			return string;

		StringBuilder sb = get(string.length());

		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			switch (c) {
				case '\\':
					sb.append('\\').append('\\');
					break;
				case '\t':
					sb.append('\\').append('t');
					break;
				case '\n':
					sb.append('\\').append('n');
					break;
				case '\r':
					sb.append('\\').append('r');
					break;
				default:
					sb.append(c);
					break;
			}
		}
		return createReturn(string, sb);
	}
	private static void offer(StringBuilder sb) {
		sb.setLength(0);
		wsb.offer(sb);
	}
	private static String toStringAndOffer(StringBuilder sb) {
		String s = sb.toString();
		offer(sb);
		return s;
	}
	private static StringBuilder get(int len) {
		StringBuilder sb = wsb.poll(); 
		sb.setLength(0);
		sb.ensureCapacity(len+1);
		return sb;
	}
	static CharSequence unescape(CharSequence string) {
		if(string == null || string.length() == 1)
			return string;
		
		StringBuilder sink = get(string.length());

		boolean slash = false;
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);

			if(slash) {
				slash = false;
				switch (c) {
					case '\\':
						sink.append('\\');
						break;
					case 'n':
						sink.append('\n');
						break;
					case 't':
						sink.append('\t');
						break;
					case 'r':
						sink.append('\r');
						break;
					default:
						throw new TsvException("uknown escape sequence: \\"+c+", in-string: "+string);
				}
			} else {
				if(c == '\\')
					slash = true;
				else
					sink.append(c);
			}
		}
		return createReturn(string , sink );
	}

	private static CharSequence createReturn(CharSequence string, StringBuilder sink) {
		if(sink.length() == string.length()) {
			offer(sink);
			return string;
		} else {
			return toStringAndOffer(sink);
		}
	}

	static void save(StringBuilder data, Path path, Charset charset, CodingErrorAction onMalformedInput, CodingErrorAction onUnmappableCharacter) throws IOException {
		StringWriter2.writer()
		.charset(charset)
		.target(path, false)
		.onMalformedInput(onMalformedInput)
		.onUnmappableCharacter(onUnmappableCharacter)
		.write(data);
	}
}
