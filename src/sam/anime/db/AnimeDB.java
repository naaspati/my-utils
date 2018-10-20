package sam.anime.db;

import java.sql.SQLException;

import sam.myutils.System2;
import sam.sql.sqlite.SQLiteDB;

public class AnimeDB extends SQLiteDB {

	public AnimeDB() throws  SQLException {
		super(dbPath());
	}
	public static String dbPath() {
		return System2.lookup("ANIME_DB");
	}
}
