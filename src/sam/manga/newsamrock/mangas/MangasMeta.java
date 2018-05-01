package sam.manga.newsamrock.mangas;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface MangasMeta {
    String MANGA_ID = "manga_id";
    String DIR_NAME = "dir_name";
    String MANGA_NAME = "manga_name";
    String AUTHOR = "author";
    String READ_COUNT = "read_count";
    String UNREAD_COUNT = "unread_count";
    String DESCRIPTION = "description";
    String CHAP_COUNT_MANGAROCK = "chap_count_mangarock";
    String CHAP_COUNT_PC = "chap_count_pc";
    String CATEGORIES = "categories";
    String IS_FAVORITE = "is_favorite";
    String STATUS = "status";
    String RANK = "rank";
    String LAST_READ_TIME = "last_read_time";
    String LAST_UPDATE_TIME = "last_update_time";
    String STARTUP_VIEW = "startup_view";
    String CHAPTER_ORDERING = "chapter_ordering";
    String BU_ID = "bu_id";

    String TABLE_NAME = "Mangas";

    public static  int getMangaId(ResultSet rs) throws SQLException { 
        return rs.getInt(MANGA_ID);
    }
    public static  String getDirName(ResultSet rs) throws SQLException { 
        return rs.getString(DIR_NAME);
    }
    public static  String getMangaName(ResultSet rs) throws SQLException { 
        return rs.getString(MANGA_NAME);
    }
    public static  String getAuthor(ResultSet rs) throws SQLException { 
        return rs.getString(AUTHOR);
    }
    public static  int getReadCount(ResultSet rs) throws SQLException { 
        return rs.getInt(READ_COUNT);
    }
    public static  int getUnreadCount(ResultSet rs) throws SQLException { 
        return rs.getInt(UNREAD_COUNT);
    }
    public static  String getDescription(ResultSet rs) throws SQLException { 
        return rs.getString(DESCRIPTION);
    }
    public static  int getchapCountMangarock(ResultSet rs) throws SQLException { 
        return rs.getInt(CHAP_COUNT_MANGAROCK);
    }
    public static  int getChapCountPc(ResultSet rs) throws SQLException { 
        return rs.getInt(CHAP_COUNT_PC);
    }
    public static  String getCategories(ResultSet rs) throws SQLException { 
        return rs.getString(CATEGORIES);
    }
    public static  boolean getIsFavorite(ResultSet rs) throws SQLException { 
        return rs.getBoolean(IS_FAVORITE);
    }
    public static  boolean getStatus(ResultSet rs) throws SQLException { 
        return rs.getBoolean(STATUS);
    }
    public static  int getRank(ResultSet rs) throws SQLException { 
        return rs.getInt(RANK);
    }
    public static  long getLastReadTime(ResultSet rs) throws SQLException { 
        return rs.getLong(LAST_READ_TIME);
    }
    public static  long getLastUpdateTime(ResultSet rs) throws SQLException { 
        return rs.getLong(LAST_UPDATE_TIME);
    }
    public static  int getStartupView(ResultSet rs) throws SQLException { 
        return rs.getInt(STARTUP_VIEW);
    }
    public static  boolean getChapterOrdering(ResultSet rs) throws SQLException { 
        return rs.getBoolean(CHAPTER_ORDERING);
    }
    public static  int getBuID(ResultSet rs) throws SQLException { 
        return rs.getInt(BU_ID);
    }
}