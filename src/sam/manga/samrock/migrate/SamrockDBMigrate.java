package sam.manga.samrock.migrate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import sam.config.MyConfig;
import sam.manga.samrock.Chapter;
import sam.sql.sqlite.SQLiteManeger;

public class SamrockDBMigrate {
    private final SQLiteManeger samrock;
    public SamrockDBMigrate() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
        Files.copy(Paths.get(MyConfig.SAMROCK_DB_OLD), Paths.get(MyConfig.SAMROCK_DB), StandardCopyOption.REPLACE_EXISTING);
        
        samrock = new SQLiteManeger(MyConfig.SAMROCK_DB);
        
        modifyChapters();
        modifyMangas();
        modifyUrls();
        modifyTags();
        samrock.commit();
        samrock.close();
    }
    private void modifyMangas() throws SQLException {
        samrock.executeUpdate("CREATE TABLE `Mangas` ("+
                "  `manga_id`  INTEGER NOT NULL UNIQUE,"+
                "  `dir_name`  TEXT NOT NULL UNIQUE,"+
                "  `manga_name`  TEXT NOT NULL UNIQUE,"+
                "  `author`  TEXT,"+
                "  `read_count`  INTEGER NOT NULL DEFAULT 0,"+
                "  `unread_count`  INTEGER NOT NULL DEFAULT 0,"+
                "  `description`  TEXT,"+
                "  `chap_count_mangarock`  INTEGER NOT NULL DEFAULT 0,"+
                "  `chap_count_pc`  INTEGER NOT NULL DEFAULT 0,"+
                "  `categories`  TEXT NOT NULL,"+
                "  `is_favorite`  BOOLEAN NOT NULL DEFAULT 0,"+
                "  `status`  BOOLEAN NOT NULL DEFAULT 0,"+
                "  `rank`  INTEGER NOT NULL DEFAULT 99999,"+
                "  `last_read_time`  INTEGER NOT NULL DEFAULT 0,"+
                "  `last_update_time`  INTEGER NOT NULL DEFAULT 0,"+
                "  `startup_view`  INTEGER NOT NULL DEFAULT 0,"+
                "  `chapter_ordering`  BOOLEAN NOT NULL DEFAULT 1,"+
                "  `bu_id`  INTEGER NOT NULL  DEFAULT -1,"+
                "  PRIMARY KEY(`manga_id`,`dir_name`)"+
                ");");
        
        samrock.commit();
        
        samrock.executeUpdate("INSERT INTO Mangas(manga_id,dir_name,manga_name,author,read_count,unread_count,description,chap_count_mangarock,chap_count_pc,categories,is_favorite,status,rank,last_read_time,last_update_time,startup_view,chapter_ordering,bu_id) SELECT manga_id,dir_name,manga_name,author,read_count,unread_count,description,chap_count_mangarock,chap_count_pc,categories,isFavorite,status,rank,last_read_time,last_update_time,startup_view,chapter_ordering,bu_id FROM MangaData");
        samrock.commit();
        samrock.executeUpdate("UPDATE Mangas SET startup_view = 3 WHERE startup_view = 'VIEWELEMENTS_VIEW'");
        samrock.executeUpdate("UPDATE Mangas SET startup_view = 1 WHERE startup_view = 'CHAPTERS_LIST_VIEW'"); 
        samrock.executeUpdate("UPDATE Mangas SET startup_view = 0 WHERE startup_view = 'DATA_VIEW'"); 
        samrock.executeUpdate("UPDATE Mangas SET startup_view = 2 WHERE startup_view = 'CHAPTERS_EDIT_VIEW'");
        samrock.executeUpdate("UPDATE Mangas SET startup_view = 4 WHERE startup_view = 'NOTHING_FOUND_VIEW'");
        samrock.executeUpdate("UPDATE Mangas SET bu_id = -1 WHERE bu_id < 0 OR bu_id > "+Integer.MAX_VALUE);
        samrock.executeUpdate("DROP TABLE MangaData");
        samrock.commit();
    }
    private void rename(String oldName, String newName) throws SQLException {
        samrock.executeUpdate("ALTER TABLE "+oldName+" RENAME TO "+newName);
    }
    private void modifyUrls() throws SQLException {
        samrock.executeUpdate("CREATE TABLE `MangaUrlsBase` ('column_name' TEXT NOT NULL, 'base' TEXT NOT NULL)" );
        samrock.commit();
        
        samrock.executeUpdate("INSERT INTO MangaUrlsBase VALUES ('mangafox', 'http://fanfox.la/manga/')");
        samrock.executeUpdate("INSERT INTO MangaUrlsBase VALUES ('mangahere', 'http://www.mangahere.cc/manga/')");
        
        samrock.executeUpdate("CREATE TABLE `MangaUrls2` (" + 
                "    `manga_id`  INTEGER NOT NULL UNIQUE," + 
                "    `mangafox`  TEXT," + 
                "    `mangahere` TEXT," + 
                "    PRIMARY KEY(`manga_id`)" + 
                ");" );
        
        samrock.commit();

        PreparedStatement ps = samrock.prepareStatement("INSERT INTO MangaUrls2 VALUES(?,?,?)");

        samrock.iterate("SELECT manga_id, mangafox, mangahere FROM MangaUrls", rs -> {
            ps.setString(1, rs.getString("manga_id"));
            ps.setString(2, mangaNameFromMangaUrl(rs.getString("mangafox")));
            ps.setString(3, mangaNameFromMangaUrl(rs.getString("mangahere")));
            ps.addBatch();
        });

        ps.executeBatch();
        ps.close();

        samrock.executeUpdate("DROP TABLE MangaUrls");
        samrock.commit();
        rename("MangaUrls2", "MangaUrls");
        samrock.commit();
    }
    private void modifyChapters() throws SQLException {
        samrock.executeUpdate("DROP TABLE LastChap");
        samrock.executeUpdate("CREATE TABLE `Chapters` (" + 
                "    `chapter_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,"+
                "    `manga_id`  INTEGER NOT NULL," + 
                "    `name`  TEXT," + 
                "    `number`    REAL NOT NULL," + 
                "    `read`  BOOLEAN NOT NULL" + 
                ");");
        
        samrock.commit();
        
        PreparedStatement ps = samrock.prepareStatement("INSERT INTO Chapters(manga_id, name, number, read) VALUES(?,?,?,?)");

        samrock.iterate("SELECT manga_id, chapters FROM MangaData", rs -> {
            Chapter[] ch = Chapter.bytesToChapters(rs.getBytes("chapters"));
            int id = rs.getInt("manga_id");

            for (Chapter c : ch) {
                ps.setInt(1, id);
                ps.setString(2, c.getName());
                ps.setDouble(3, c.getNumber());
                ps.setBoolean(4, c.isRead());
                ps.addBatch();
            } 
        });
        ps.executeBatch();
        ps.close();
        samrock.commit();
    }
    private void modifyTags() throws SQLException {
        samrock.executeUpdate("CREATE TABLE `Tags2` (" + 
                "    `name`  VARCHAR NOT NULL UNIQUE," + 
                "    `id`    SMALLINT NOT NULL UNIQUE," + 
                "    `active`    BOOLEAN NOT NULL," + 
                "    PRIMARY KEY(`id`)" + 
                ");");
        
        samrock.commit();
        samrock.executeUpdate("INSERT INTO Tags2(name, id, active) SELECT name, id, add_to_list FROM Tags");
        samrock.commit();
        samrock.executeUpdate("DROP TABLE Tags");
        samrock.commit();
        rename("Tags2","Tags");
        samrock.commit();
    }
    public static String mangaNameFromMangaUrl(String url) {
        if(url == null || (url = url.trim()).isEmpty()) 
            return null;

        boolean b = url.charAt(url.length() - 1) == '/';
        int l = url.length();
        return url.substring(url.lastIndexOf('/', l - (b ? 2 : 1)) + 1, l - (b ? 1 : 0));
    }
}
