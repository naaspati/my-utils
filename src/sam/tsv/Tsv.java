package sam.tsv;

import static sam.io.IOConstants.defaultCharset;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class Tsv  implements Iterable<Row>, Rows, Columns {
	public static Builder builder() {
		return new Builder();
	}
	void addFromBuilder(Row row){
		rows.add(row);
	}
	public Row.Rowbuilder rowBuilder() {
		return new Row.Rowbuilder(columns.size(), this);
	}

	/**
	 * this is standard parser, read data from file, 
	 * stores in {@link ArrayList} 
	 * first row is column names
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Tsv parse(Path path) throws IOException {
		return builder().parse(path);
	}

	Charset charset;
	Path path;
	final Collection<Row> rows;
	final Map<String, Column> columns = new HashMap<>();
	
	public Tsv(String...columnNames) {
		this(new ArrayList<>(),defaultCharset(), columnNames);
	}
	Tsv(Collection<Row> sink, String...columnNames) {
		this(sink, defaultCharset(), columnNames);
	}
	Tsv(Collection<Row> sink, Charset charset, String...columnNames) {
		Objects.requireNonNull(sink);
		Objects.requireNonNull(charset);
		
		addColumns(columnNames);

		this.rows = sink;
		this.charset = charset;
	}
	/**
	 * read a file and replace any existing data 
	 * 
	 * @return
	 * @throws IOException
	 * 
	 * 
	 */  
	public Tsv(Collection<Row> sink, Charset charset, InputStream is) throws IOException {
		boolean firstLine[] = {true};
		this.rows = Objects.requireNonNull(sink);
		this.charset = Objects.requireNonNull(charset);
		
		new LineReader() {
			public void accept(String line) {
				Row row =  new Row(line, Tsv.this);
				if(firstLine[0]) {
					addColumns(row.values());
					firstLine[0] = false;
				} else 
					rows.add(row);
			};
		}
		.parse(is, charset);
		
		if(firstLine[0])
			throw new IOException(new TsvException("columnNames not found"));
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
		.map(r -> r.values())
		.map(s -> new Row(Arrays.copyOf(s, s.length), this))
		.forEach(rows::add);
	}
	@Override
	public Iterator<Row> iterator() {
		return rows.iterator();
	}
	public void setCharset(Charset charset) {
		this.charset = charset;
	}
	public void setPath(Path path) {
		this.path = path;
	}
	public Path getPath() {
		return path;
	}
	
	public void save() throws IOException {
		save(path, charset);
	}
	public void save(Path path) throws IOException {
		save(path, charset);
	}
	/**
	 * if file does not exits than CREATE a new file, otherwise if file exists, than TRUNCATE the existing file
	 * <br> null values are saved as empty string, and later will be accessed as empty string
	 * <br> tab
	 * @param path where file will be saved
	 * @param charset used to encode data
	 * @throws IOException 
	 */
	public void save(Path path, Charset charset) throws IOException {
		save(path, charset, new TsvSaver());
	}
	public void save(Path path, Charset charset, TsvSaver tsvSaver) throws IOException {
		this.path = path;
		this.charset = charset;

		Iterator<Row> rows =  iterator();
		tsvSaver.save(new Row(getColumnNames(), this), path, charset, new Iterator<Row>() {
			@Override public boolean hasNext() { 
				return rows.hasNext(); 
			}
			@Override public Row next() {
				return rows.next();
			}
		});
	}
}
