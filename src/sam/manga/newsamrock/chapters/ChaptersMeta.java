package sam.manga.newsamrock.chapters;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ChaptersMeta {
    String CHAPTER_ID = "chapter_id";
    String MANGA_ID = "manga_id";
    String NAME = "name";
    String NUMBER = "number";
    String READ = "read";

    String TABLE_NAME = "Chapters";
    
    public static  int getChapterId(ResultSet rs) throws SQLException {
        return rs.getInt(CHAPTER_ID);
    }
    public static  int getMangaId(ResultSet rs) throws SQLException {
        return rs.getInt(MANGA_ID);
    }
    public static String getName(ResultSet rs) throws SQLException {
        return rs.getString(NAME);
    }
    public static double getNumber(ResultSet rs) throws SQLException {
        return rs.getDouble(NUMBER);
    }
    public static boolean getRead(ResultSet rs) throws SQLException {
        return rs.getBoolean(READ);
    }
}