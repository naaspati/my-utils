package sam.tsv;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;

import sam.fileutils.StringToFileWriter;

public class TsvSaver extends Escaper {
    private final boolean append;
    private final CodingErrorAction onMalformedInput;
    private final CodingErrorAction onUnmappableCharacter;

    public TsvSaver(boolean append, CodingErrorAction onMalformedInput, CodingErrorAction onUnmappableCharacter) {
        this.append = append;
        this.onMalformedInput = onMalformedInput;
        this.onUnmappableCharacter = onUnmappableCharacter;
    }

    public TsvSaver() {
        this.append = false;
        this.onMalformedInput = CodingErrorAction.REPORT;
        this.onUnmappableCharacter = CodingErrorAction.REPORT;
    }

    void save(String[] columnsNames, Path path, Charset charset, String nullReplacement, Iterator<String[]> rows) throws IOException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(charset);

        final String nr = nullReplacement == null ? "" : nullReplacement; 

        StringBuilder b = new StringBuilder();
        String newline = System.lineSeparator();

        if(columnsNames != null){
            for (String s : columnsNames) b.append(escape(s)).append('\t');
            b.deleteCharAt(b.length() - 1);
            b.append(newline);
        }

        while (rows.hasNext()) {
            String[] str = rows.next();

            if(str.length != 0) {
                for (String s : escape(str)) b.append(s == null ? nr : s).append('\t');
                b.deleteCharAt(b.length() - 1);
            }

            if(rows.hasNext())
                b.append(newline);
        }
        save(b, path, charset);
    }
    void save(StringBuilder data, Path path, Charset charset) throws IOException {
        new StringToFileWriter(2 << 10).write(path, data, charset, append, onMalformedInput, onUnmappableCharacter);
    }
}
