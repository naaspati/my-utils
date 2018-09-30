package sam.anime.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import sam.anime.entities.Anime;
import sam.anime.meta.AnimesMeta;
import sam.myutils.MyUtilsException;
import sam.myutils.MyUtilsSystem;
import sam.sql.SqlFunction;
import sam.sql.sqlite.SQLiteDB;

public class AnimeDB extends SQLiteDB {

	public AnimeDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		super(MyUtilsSystem.lookup("ANIME_DB"));
	}
	
	private static final String ANIME_SQL = "SELECT * FROM "+AnimesMeta.TABLE_NAME+" WHERE "+AnimesMeta./** myanimelist_id */MAL_ID+"= ";  
	
	public Anime getAnime(int id) throws SQLException {
		return executeQuery(ANIME_SQL+id, rs -> rs.next() ? new Anime(rs, this) : null);
	}
	public <E> void loadList(int mal_id, String[] columnNames, String tableName, SqlFunction<ResultSet, E> mapper, List<E> sink) {
		MyUtilsException.noError(() -> collect("SELECT "+(columnNames.length < 2 ? columnNames[0] : String.join(",", columnNames))+" FROM "+tableName+" WHERE mal_id="+mal_id, sink, mapper));
	}
	
}
