package sam.anime.meta;

public interface AnimeDirsMeta {
	String TABLE_NAME = "DIRS",
			MAL_ID = "mal_id", 
			ID = "_id",
			PATH = "_path",
			LAST_MODIFIED = "last_modified";

	public static String[] allColumns() {
		return new String[] {ID,/** myanimelist_id */MAL_ID, PATH, LAST_MODIFIED };
	}

}
