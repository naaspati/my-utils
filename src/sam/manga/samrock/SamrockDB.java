package sam.manga.samrock;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import sam.config.MyConfig;
import sam.sql.sqlite.SQLiteManeger;

public class SamrockDB implements AutoCloseable {

    /**
     * create instance with MyConfig.SAMROCK_DB 
     * @throws SQLException 
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public SamrockDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        this(true);
    }
    public SamrockDB(boolean createDefaultStatement) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        this(MyConfig.SAMROCK_DB_OLD);
    }
    private final SQLiteManeger maneger;

    public SamrockDB(String dbPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        maneger = new SQLiteManeger(dbPath);
    }
    public SQLiteManeger getDBManeger() {
        return maneger;
    }
    public void commit() throws SQLException {
        maneger.commit();
    }
    @Override
    public void close() throws Exception {
        maneger.close();
    }
    public Chapter[] getChapters(int mangaId) throws SQLException {
        return maneger.executeQuery(join("SELECT ", " FROM MangaData WHERE manga_id = "+mangaId, MangaColumn.CHAPTERS), rs -> {
            if(rs.next())
                return Chapter.bytesToChapters(rs.getBytes(MangaColumn.CHAPTERS.getColumnName()));
            return null;
        });
    }
    public Map<Integer, Chapter[]> getChapters(Collection<Integer> mangaIds) throws SQLException {
        Objects.requireNonNull(mangaIds);

        Map<Integer, Chapter[]> map = new LinkedHashMap<>();

        if(mangaIds.isEmpty())
            return map;

        maneger.iterate(join("SELECT ", mangaIds.stream().map(String::valueOf).collect(Collectors.joining(",", " FROM MangaData WHERE manga_id IN (", ")")), MangaColumn.ID, MangaColumn.CHAPTERS), 
                rs -> map.put(rs.getInt(MangaColumn.ID.getColumnName()), Chapter.bytesToChapters(rs.getBytes(MangaColumn.CHAPTERS.getColumnName()))));

        return map;
    }

    public String join(String prefix, String suffix, MangaColumn... columns) {
        if(columns.length == 0)
            return "";
        if(columns.length == 1)
            return prefix + columns[0].getColumnName() + suffix;

        return Stream.of(columns).map(MangaColumn::getColumnName).collect(Collectors.joining(", ", prefix, suffix));
    }
}
