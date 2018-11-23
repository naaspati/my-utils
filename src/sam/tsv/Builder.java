package sam.tsv;

import static sam.io.IOConstants.defaultCharset;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

public class Builder {
    private Collection<Row> collection = new LinkedList<>();
    private String[] columnNames;
    private Charset charset = defaultCharset();

    Builder() {}

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
    public Tsv build() {
        return new Tsv(collection, charset, columnNames);
    }
    public Tsv parse(Path path) throws IOException {
        Objects.requireNonNull(path);

        try(InputStream is = Files.newInputStream(path)) {
            Tsv tsv = new Tsv(collection, charset, is);

            tsv.setPath(path);
            return tsv;
        }
    }
    public Tsv parse(InputStream is) throws IOException {
        Objects.requireNonNull(is);
        return new Tsv(collection, charset, is);
    }
}