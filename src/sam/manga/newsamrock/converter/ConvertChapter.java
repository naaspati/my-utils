package sam.manga.newsamrock.converter;

import static java.lang.String.valueOf;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import sam.tsv.Row;
import sam.tsv.Tsv;
import sam.tsv.TsvException;

public class ConvertChapter  {
    private final int manga_id;
    private final double number;
    private final String name;

    private final Path source;
    private final Path target;

    public static List<ConvertChapter> parse(Tsv tsv) {
        Objects.requireNonNull(tsv, "tsv is null");
        if(tsv.isEmpty())
            throw new TsvException("tsv is empty");
        
        int manga_id_n = tsv.indexOf("manga_id");
        int number_n = tsv.indexOf("number");
        int name_n = tsv.indexOf("name");
        int source_n = tsv.indexOf("source");
        int target_n = tsv.indexOf("target");

        return tsv.stream().map(r -> new ConvertChapter(
                r.getInt(manga_id_n),
                r.getDouble(number_n),
                r.get(name_n),
                Paths.get(r.get(source_n)),
                Paths.get(r.get(target_n))
                )).collect(Collectors.toList());
    }
    
    public static Tsv toTsv(List<ConvertChapter> list) {
        Objects.requireNonNull(list, "list is null");
        if(list.isEmpty())
            throw new IllegalArgumentException("list is empty");
        
        Tsv tsv = new Tsv("manga_id", "number", "name", "source", "target");
        
        list.forEach(c -> tsv.add(valueOf(c.manga_id), valueOf(c.number), valueOf(c.name), valueOf(c.source), valueOf(c.target)));
        return tsv;
    }
    public ConvertChapter(Row row) {
        this.manga_id = row.getInt("manga_id");
        this.number = row.getDouble("number");
        this.name = row.get("name");
        this.source = Paths.get(row.get("source"));
        this.target = Paths.get(row.get("target"));
    }

    public ConvertChapter(int manga_id, double number, String name, Path source, Path target) {
        this.manga_id = manga_id;
        this.number = number;
        this.name = name;
        this.source = source;
        this.target = target;
    }
    public int getMangaId() {
        return manga_id;
    }
    public double getNumber() {
        return number;
    }
    public String getName() {
        return name;
    }
    public Path getSource() {
        return source;
    }
    public Path getTarget() {
        return target;
    }
}
