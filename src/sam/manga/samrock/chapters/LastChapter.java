package sam.manga.samrock.chapters;


import static sam.manga.samrock.chapters.ChaptersMeta.CHAPTERS_TABLE_NAME;
import static sam.manga.samrock.chapters.ChaptersMeta.CHAPTER_ID;
import static sam.manga.samrock.chapters.ChaptersMeta.MANGA_ID;
import static sam.manga.samrock.chapters.ChaptersMeta.NUMBER;
import static sam.sql.querymaker.QueryMaker.qm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import sam.sql.SqlFunction;
import sam.sql.querymaker.Select;
import sam.sql.sqlite.SQLiteDB;

public class LastChapter  {
	private final SQLiteDB db;
	private final SqlFunction<ResultSet, Chapter> mapper;
	
    public LastChapter(SQLiteDB db, SqlFunction<ResultSet, Chapter> mapper) {
		this.db = db;
		this.mapper = mapper;
	}
	public Map<Integer, Chapter> all() throws SQLException {
        return byMangaId(null);
    }
    public Map<Integer, Chapter> byMangaId(Collection<Integer> mangaIds) throws SQLException {
    	// select *, max(number) from Chapters where manga_id in(?,?...) group by manga_id 
    	
        Select select = qm()
                .select("*", "max("+NUMBER+")").from(CHAPTERS_TABLE_NAME);
        
        if(mangaIds != null)
            select.where(w -> w.in(MANGA_ID, mangaIds));
        
        String sql = select.append("group by").append(MANGA_ID).build();
        System.out.println(sql);
        
        return db.collectToMap(sql, rs -> rs.getInt(MANGA_ID), mapper);
    }
    public Chapter byMangaId(int mangaId) throws SQLException {
        return db.executeQuery(qm().select(CHAPTER_ID, NUMBER).from(CHAPTERS_TABLE_NAME).where(w -> w.eq(MANGA_ID, mangaId)).build(), rs -> {
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
            return db.executeQuery(qm().selectAll().from(CHAPTERS_TABLE_NAME).where(w -> w.eq(CHAPTER_ID, id2)).build(), mapper);
        });
    }

}
