package sam.anime.entities;

import java.sql.ResultSet;
import java.sql.SQLException;

import sam.anime.meta.AnimesMeta;

public class MinimalAnime {
	private final int mal_id;
	private final String title;
	
	public MinimalAnime(ResultSet rs) throws SQLException {
		this.mal_id = rs.getInt(AnimesMeta.MAL_ID);
		this.title = rs.getString(AnimesMeta.TITLE);
	}

	MinimalAnime(int mal_id, String title) {
		this.mal_id = mal_id;
		this.title = title;
	}

	public int getMalId() { return mal_id; }
	public String getTitle() { return title; }
}
