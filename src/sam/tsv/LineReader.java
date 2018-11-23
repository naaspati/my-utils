package sam.tsv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.function.Consumer;


@FunctionalInterface
interface LineReader extends Consumer<String> {
	default void parse(InputStream is, Charset charset) throws IOException {
		int lineNumber[] = {0};

		try(InputStreamReader isr = new InputStreamReader(is, charset);
				BufferedReader reader = new BufferedReader(isr)) {
			reader.lines().peek(s -> {lineNumber[0]++;}).forEach(this);
		} catch (Exception e) {
			throw new ParsingException("line: "+lineNumber[0], e);
		}
	}
	class ParsingException extends IOException {
		private static final long serialVersionUID = 1L;

		public ParsingException(String string, Exception e) {
			super(string, e);
		}
	}
	void accept(String line);
}
