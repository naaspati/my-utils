package sam.manga.samrock;

public enum MangaColumn {
    ID("manga_id"),
    DIR_NAME("dir_name"),
    MANGA_NAME("manga_name"),
    AUTHOR("author"),
    CHAPTERS("chapters"),
    READ_COUNT("read_count"),
    UNREAD_COUNT("unread_count"),
    DESCRIPTION("description"),
    CHAP_COUNT_MANGAROCK("chap_count_mangarock"),
    CHAP_COUNT_PC("chap_count_pc"),
    CATEGORIES("categories"),
    IS_FAVORITE("isFavorite"),
    STATUS("status"),
    RANK("rank"),
    LAST_READ_TIME("last_read_time"),
    LAST_UPDATE_TIME("last_update_time"),
    STARTUP_VIEW("startup_view"),
    CHAPTER_ORDERING("chapter_ordering"),
    BU_ID("bu_id");
    
    private final String columnName;
    private MangaColumn(String value) {
        columnName = value;
    }
    public String getColumnName() {
        return columnName;
    }
    @Override
    public String toString() {
        return columnName;
    }
}
