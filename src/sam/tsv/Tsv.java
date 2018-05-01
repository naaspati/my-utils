package sam.tsv;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

public class Tsv  implements Iterable<Row>, Rows, Columns {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Collection<Row> collection = new LinkedList<>();
        private String[] columnNames;
        private Charset charset = Charset.defaultCharset();
        private String nullReplaceMent;

        private Builder() {}

        public <C extends Collection<Row>> Builder rowStore(C collection) {
            this.collection = collection;
            return this;
        }
        public Builder columnNames(String...columnNames) {
            this.columnNames = columnNames;
            return this;
        }
        public Builder charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder nullReplaceMent(String nullReplaceMent) {
            this.nullReplaceMent = nullReplaceMent;
            return this;
        }

        public Tsv build() {
            return new Tsv(collection, columnNames, charset, nullReplaceMent);
        }
        public Tsv parse(Path path, boolean firstRowIsColumnNames) throws IOException {
            Objects.requireNonNull(path);

            try(InputStream is = Files.newInputStream(path)) {
                Tsv tsv = build().parse(is, firstRowIsColumnNames);

                tsv.setPath(path);
                return tsv;
            }
        }
        public Tsv parse(InputStream is, boolean firstRowIsColumnNames) throws IOException {
            Objects.requireNonNull(is);
            return build().parse(is, firstRowIsColumnNames);
        }
    }
    
    void addFromBuilder(Row row){
        rows.add(row);
    }
    public Row.Rowbuilder rowBuilder() {
        return new Row.Rowbuilder(columnNames.size(), this);
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
        return builder().parse(path, true);
    }
    public static Tsv parse(InputStream is) throws IOException {
        return builder().parse(is, true);
    }

    Charset charset;
    Path path;
    String nullReplacement;
    HashMap<String, Integer> columnNames;
    final Collection<Row> rows;

    public Tsv(String...columnNames) {
        this(new ArrayList<>(), columnNames, Charset.defaultCharset(), null);
    }
    private Tsv(Collection<Row> collection, String[] columnNames, Charset charset, String nullReplacement) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(charset);
        this.nullReplacement = nullReplacement;

        this.rows = collection;
        this.charset = charset;

        if(columnNames != null && columnNames.length != 0)
            setColumns(columnNames);
    }
    /**
     * read a file and replace any existing data 
     * 
     * @param path
     * @param firstRowIsColumnNames
     * @param options
     * @return
     * @throws IOException
     * 
     * 
     */  
    public Tsv parse(InputStream is, boolean firstRowIsColumnNames) throws IOException {
        new Parser() {

            @Override
            public void setColumnsNames(String[] values) {
                setColumns(values);
            }

            @Override
            public void addRow(String[] row) {
                add(new Row(row, Tsv.this));
            }
        }.parse(is, firstRowIsColumnNames, charset, nullReplacement);
        return this;
    }
    /**
     * merge two different tsv tables
     * @param tsv2 other tsv 
     * @param strict if false two tsv will be merged, regardless both have same column names or not.<br> if true two tsv is merged only if both have same columnNames else throw error   
     */
    public void merge(Tsv tsv2, boolean strict) {
        if(tsv2 == this)
            throw new TsvException("tsv cannot be merged with itself");

        String[] a1, a2;
        if(strict && !Arrays.equals(a1 = columnNames.values().toArray(new String[0]), a2 = tsv2.columnNames.values().toArray(new String[0]))) 
            throw new TsvException("merge between incompatible tsv(s), this:" +Arrays.toString(a1)+"  tsv2: "+Arrays.toString(a2));

        tsv2.stream()
        .map(r -> r.values)
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
    public void setNullReplacement(String nullReplacement) {
        this.nullReplacement = nullReplacement;
    }
    public void save() throws IOException {
        save(path, charset, nullReplacement);
    }
    public void save(Path path) throws IOException {
        save(path, charset, nullReplacement);
    }
    /**
     * if file does not exits than CREATE a new file, otherwise if file exists, than TRUNCATE the existing file
     * <br> null values are saved as empty string, and later will be accessed as empty string
     * <br> tab
     * @param path where file will be saved
     * @param charset used to encode data
     * @throws IOException 
     */
    public void save(Path path, Charset charset, String nullReplacement) throws IOException {
        save(path, charset, nullReplacement, new TsvSaver());
    }
    public void save(Path path, Charset charset, String nullReplacement, TsvSaver tsvSaver) throws IOException {
        this.path = path;
        this.charset = charset;
        this.nullReplacement = nullReplacement;

        Iterator<Row> rows =  iterator();
        tsvSaver.save(getColumnNames(), path, charset, nullReplacement, new Iterator<String[]>() {
            @Override public boolean hasNext() { 
                return rows.hasNext(); 
            }
            @Override public String[] next() {
                return rows.next().values;
            }
        });
    }
}
