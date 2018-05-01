package sam.books;

import static sam.console.ANSI.createBanner;

import java.sql.SQLException;

import sam.sql.sqlite.SQLiteManeger;

public class BooksDB extends SQLiteManeger {
    public BooksDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        super(BookUtils.DB);
    }

    private void createDB() throws SQLException{
        System.out.println(createBanner("creating :")+BookUtils.DB);

        executeUpdate("CREATE TABLE `Books` ("+
                "   `_id`   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,"+
                "   `name`  TEXT NOT NULL,"+
                "   `file_name` TEXT NOT NULL,"+
                "   `path_id`   INTEGER NOT NULL,"+
                "   `author`    TEXT,"+
                "   `isbn`  TEXT,"+
                "   `page_count`    INTEGER,"+
                "   `year`  TEXT,"+
                "   `description`   TEXT"+
                ");");

        executeUpdate("CREATE TABLE `Paths` ("+
                "   `path_id`   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,"+
                "   `_path` TEXT NOT NULL UNIQUE"+
                ");");
    }
}
