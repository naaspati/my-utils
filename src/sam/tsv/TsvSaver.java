package sam.tsv;

import static sam.tsv.TsvUtils.escape;

import java.io.IOException;
import java.util.Iterator;

import sam.myutils.Checker;

class TsvSaver {
	public void append(Iterator<String> row, Appendable sink) throws IOException {
		while (row.hasNext()) {
			String s = row.next();
			append(s, sink, row.hasNext());
		}
		sink.append('\n');
	}
	public void append(String[] row, Appendable sink) throws IOException {
		if(Checker.isNotEmpty(row)) {
			for (int i = 0; i < row.length - 1; i++) 
				append(row[i], sink, true);

			append(row[row.length - 1], sink, false);
		}	
		sink.append('\n');
	}
	public void append(String s1, String s2, Appendable sink) throws IOException {
		append(s1, sink, true);
		append(s2, sink, false);
		sink.append('\n');
	}

	public void append(String string, Appendable sink, boolean tab) throws IOException {
		if(Checker.isNotEmpty(string))
			escape(string, sink);
		if(tab)
			sink.append('\t');
	}
}
