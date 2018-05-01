package sam.manga.newsamrock.chapters;

import static sam.manga.newsamrock.chapters.ChaptersMeta.CHAPTER_ID;
import static sam.manga.newsamrock.chapters.ChaptersMeta.MANGA_ID;
import static sam.manga.newsamrock.chapters.ChaptersMeta.NUMBER;
import static sam.manga.newsamrock.chapters.ChaptersMeta.TABLE_NAME;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import sam.manga.newsamrock.SamrockDB;
import sam.sql.querymaker.Select;

public class LastChapter extends Common {
    LastChapter(SamrockDB db) {
        super(db);
    }
    
    private final class TEMP {
        private int id;
        private double number;

        private void set(ResultSet rs) throws SQLException {
            double n = rs.getDouble(NUMBER);
            if(n > number) {
                id = rs.getInt(CHAPTER_ID);
                number = n;
            }
        } 
    }
    public Map<Integer, Chapter> all() throws SQLException {
        return byMangaId(null);
    }
    public Map<Integer, Chapter> byMangaId(Collection<Integer> mangaIds) throws SQLException {
        HashMap<Integer, TEMP> map = new HashMap<>();

        Select select = qm()
                .select(CHAPTER_ID, MANGA_ID, NUMBER)
                .from(TABLE_NAME);
        
        if(mangaIds != null)
            select.where(w -> w.in(MANGA_ID, mangaIds));
        String sql = select.build();

        db.iterate(sql, 
                rs -> {
                    int id = rs.getInt(MANGA_ID);
                    TEMP t = map.get(id);
                    if(t == null)
                        map.put(id, t = new TEMP());
                    t.set(rs);
                });

        HashMap<Integer, Chapter> map2 = new HashMap<>();

        sql = qm().selectAll().from(TABLE_NAME).where(w -> w.in(CHAPTER_ID, map.values(), t -> t.id, false)).build();
        db.iterate(sql, rs -> map2.put(rs.getInt(MANGA_ID), new Chapter(rs)));

        return map2;
    }
    public Chapter byMangaId(int mangaId) throws SQLException {
        return db.executeQuery(qm().select(CHAPTER_ID, NUMBER).from(TABLE_NAME).where(w -> w.eq(MANGA_ID, mangaId)).build(), rs -> {
            int id = 0;
            double number = 0;
            while(rs.next()) {
                double n = rs.getDouble(NUMBER);
                if(n > number) {
                    id = rs.getInt(CHAPTER_ID);
                    number = n;
                }    
            }
            if(id == 0)
                return null;

            int id2 = id;
            return db.executeQuery(qm().selectAll().from(TABLE_NAME).where(w -> w.eq(CHAPTER_ID, id2)).build(), Chapter::new);
        });
    }

}
