package sam.manga.newsamrock;

import java.sql.SQLException;

import sam.config.MyConfig;
import sam.manga.newsamrock.chapters.ChapterUtils;
import sam.manga.newsamrock.mangas.MangaUtils;
import sam.manga.newsamrock.urls.MangaUrlsUtils;
import sam.sql.sqlite.SQLiteManeger;

public class SamrockDB extends SQLiteManeger implements AutoCloseable {
    
    private MangaUrlsUtils mangaUrlsUtils;
    private MangaUtils mangaUtils;
    private ChapterUtils chapterUtils;
    
    public ChapterUtils chapter() {
        if(chapterUtils == null)
            chapterUtils = new ChapterUtils(this);
        return chapterUtils;
    }
    public MangaUtils manga() {
        if(mangaUtils == null)
            mangaUtils = new MangaUtils(this);
        return mangaUtils;
    }
    public MangaUrlsUtils url() throws SQLException {
        if(mangaUrlsUtils == null)
            mangaUrlsUtils = new MangaUrlsUtils(this);
        return mangaUrlsUtils;
    }
    

    /**
     * create instance with MyConfig.SAMROCK_DB 
     * @throws SQLException 
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public SamrockDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        this(MyConfig.SAMROCK_DB);
    }
    public SamrockDB(String dbPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        super(dbPath);
    }
    
    @Override
    public void close() throws SQLException {
        super.close();
        mangaUrlsUtils = null;
        mangaUtils = null;
    }
}
