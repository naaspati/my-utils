package sam.manga.newsamrock.chapters;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChapterWithMangaId extends Chapter {
    private final int manga_id; 

    public ChapterWithMangaId(int manga_id, double chapterNumber, String fileName) {
        super(chapterNumber, fileName);
        this.manga_id = manga_id;
    }
    
    public ChapterWithMangaId(ResultSet rs) throws SQLException {
        super(rs);
        this.manga_id = rs.getInt(ChaptersMeta.MANGA_ID);
    }
    public int getMangaId() {
        return manga_id;
    }

}
