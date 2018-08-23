package sam.manga.newsamrock.chapters;

import static sam.manga.newsamrock.chapters.ChaptersMeta.MANGA_ID;
import static sam.manga.newsamrock.chapters.ChaptersMeta.NUMBER;
import static sam.manga.newsamrock.chapters.ChaptersMeta.TABLE_NAME;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.DoubleStream;

import sam.manga.newsamrock.SamrockDB;
import sam.sql.querymaker.QueryMaker;
import sam.sql.querymaker.Where;

public class ChapterNumbers {
    private final SamrockDB db;
    
    ChapterNumbers(SamrockDB db) {
        this.db = db;
    }
    public Map<Integer, double[]> where(UnaryOperator<Where> wo) throws SQLException{
        return result(QueryMaker.getInstance().select(MANGA_ID, NUMBER).from(TABLE_NAME).where(w -> wo.apply(w)).build());
    }
    public Map<Integer, double[]> byMangaIds(Iterator<Integer> mangaIds) throws SQLException{
        return result(QueryMaker.getInstance().select(MANGA_ID, NUMBER).from(TABLE_NAME).where(w -> w.in(MANGA_ID, mangaIds, false)).build());
    }
    public Map<Integer, double[]> byMangaIds(Iterable<Integer> mangaIds) throws SQLException{
        return byMangaIds(mangaIds.iterator());
    }
    private Map<Integer, double[]> result(String sql) throws SQLException{
        Map<Integer, DoubleStream.Builder> map = new HashMap<>();
        Function<Integer, DoubleStream.Builder> func = id -> DoubleStream.builder();
        
        db.iterate(sql, rs -> map.computeIfAbsent(rs.getInt(MANGA_ID), func).accept(rs.getDouble(NUMBER)));
        
        Map<Integer, double[]> map2 = new HashMap<>();
        map.forEach((s,t) -> map2.put(s, t.build().toArray()));    
        return map2;
    }
}
