package sam.tsv;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sam.myutils.Checker;
import sam.reference.ReferenceUtils;

public class Tsv  implements Iterable<Row>, Rows, Columns {
	void addFromBuilder(Row row){
		rows.add(row);
	}
	public static Tsv parse(Path path) throws IOException {
		try(BufferedReader b = Files.newBufferedReader(path)) {
			return new Tsv(b);	
		}
	} 

	final Collection<Row> rows = rowCollectionImpl();
	final Map<String, Column> columns = new HashMap<>();

	public Tsv(String...columnNames) {
		addColumns(columnNames);
	}
	protected Collection<Row> rowCollectionImpl() {
		return new ArrayList<>();
	}
	/**
	 * read a file and replace any existing data 
	 * 
	 * @return
	 * @throws IOException
	 * 
	 * 
	 */  
	public Tsv(BufferedReader source) throws IOException {
		load(source);
	}
	public void load(BufferedReader source) throws IOException {
		rows.clear();

		TsvParser parser = new TsvParser();
		boolean first = true;
		List<String> list = new ArrayList<>();

		while(true) {
			String line = source.readLine();

			if(first && line == null)
				throw new IOException("columnames not found");  // empty file

			Iterator<String> itr = line == null ? null : parser.iterator(line);

			if(first && (itr == null || !itr.hasNext()))
				throw new IOException("columnames not found");  // empty file

			if(line == null)
				break;

			list.clear();
			itr.forEachRemaining(list::add);
			String[] values = list.isEmpty() ? Row.EMPTY_ARRAY : list.toArray(new String[list.size()]);

			if(first)
				addColumns(values);
			else
				rows.add(new RowImpl(line, values));
			first = false;
		}
	}
	Row newRow(String[] values) {
		return new RowImpl(values);
	}

	protected class RowImpl extends Row {
		private WeakReference<String> line;

		RowImpl(String[] values) {
			super(values);
		}
		RowImpl(String line, String[] values) {
			super(values);
			this.line = Checker.isEmpty(values) ? null : new WeakReference<String>(line);
		}
		@Override
		protected void onModified() {
			line = null;
		}
		public RowImpl(Row s) {
			super(s);

			if(s instanceof RowImpl)
				this.line = ((RowImpl) s).line;
		}
		@Override
		Tsv getParent() {
			return Tsv.this;
		}
	}

	/**
	 * merge two different tsv tables
	 * @param tsv2 other tsv 
	 * @param strict if false two tsv will be merged, regardless both have same column names or not.<br> if true two tsv is merged only if both have same columnNames else throw error   
	 */
	public void merge(Tsv tsv2, boolean strict) {
		if(tsv2 == this)
			throw new TsvException("tsv cannot be merged with itself");

		if(strict) {
			String[] a1 = this.getColumnNames();
			String[] a2 = tsv2.getColumnNames();

			if(strict && !Arrays.equals(a1, a2)) 
				throw new TsvException("merge between incompatible tsv(s), this:" +Arrays.toString(a1)+"  tsv2: "+Arrays.toString(a2));
		}

		tsv2.stream()
		.map(s -> new RowImpl(s))
		.forEach(rows::add);
	}
	@Override
	public Iterator<Row> iterator() {
		return rows.iterator();
	}
	public void save(Path path) throws IOException {
		save(Files.newBufferedWriter(path, WRITE, TRUNCATE_EXISTING, CREATE));
	}
	public synchronized void save(Appendable target) throws IOException {
		TsvSaver t = new TsvSaver();
		t.append(getColumnNames(), target);

		for (Row row : rows) {
			if(row instanceof RowImpl) {
				String line = ReferenceUtils.get(((RowImpl) row).line);
				if(line != null) {
					target.append(line).append('\n');
					continue;
				}
			}
			t.append(row.values(), target);
		}
	}
}
