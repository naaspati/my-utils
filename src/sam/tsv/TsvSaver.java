package sam.tsv;

import static sam.io.IOConstants.defaultOnMalformedInput;
import static sam.io.IOConstants.defaultOnUnmappableCharacter;
import static sam.tsv.TsvUtils.escape;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;

import sam.myutils.Checker;

public class TsvSaver {
	private final CodingErrorAction onMalformedInput;
	private final CodingErrorAction onUnmappableCharacter;

	public TsvSaver(CodingErrorAction onMalformedInput, CodingErrorAction onUnmappableCharacter) {
		this.onMalformedInput = onMalformedInput;
		this.onUnmappableCharacter = onUnmappableCharacter;
	}
	public TsvSaver() {
		this.onMalformedInput = defaultOnMalformedInput();
		this.onUnmappableCharacter = defaultOnUnmappableCharacter();
	}

	private final StringBuilder sb = TsvUtils.wsb.poll();
	private Iterator<Row> rows;

	void save(Row columnsNamesRow, Path path, Charset charset, Iterator<Row> rows) throws IOException {
		Objects.requireNonNull(columnsNamesRow);
		Objects.requireNonNull(path);
		Objects.requireNonNull(charset);
		this.rows = rows;
		sb.setLength(0);

		append(columnsNamesRow);

		while (rows.hasNext())
			append(rows.next());

		TsvUtils.save(sb, path, charset, onMalformedInput, onUnmappableCharacter);
		sb.setLength(0);
		TsvUtils.wsb.offer(sb);
	}
	private void append(Row row) {
		String line = row.getLine();
		if(line != null)
			sb.append(line);
		else {
			boolean tabbed = false;
			for (String s : row.values()) {
				append(s);
				tabbed = true;
			}
			if(tabbed)
				sb.setLength(sb.length() - 1);
		}
		if(rows.hasNext())
			sb.append('\n');
	}
	private void append(String string) {
		CharSequence s = escape(string);
		if(Checker.isNotEmpty(s))
			sb.append(s);
		sb.append('\t');
	}
}
