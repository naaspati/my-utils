package sam.manga.samrock.urls.nnew;


import static sam.manga.samrock.urls.nnew.UrlsMeta.MANGA_ID;
import static sam.manga.samrock.urls.nnew.UrlsMeta.URLSUFFIX_TABLE_NAME;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import sam.manga.samrock.SamrockDB;
import sam.sql.JDBCHelper;

final class MangaUrlsUtils {
    private final SamrockDB db; 
    private final Map<String, UrlsPrefixImpl> prefixes;

    public MangaUrlsUtils(SamrockDB db) throws SQLException {
    	this.db = db;
    	this.prefixes = UrlsPrefixImpl.getAll(db); 
    }
    
    /**
     * @param mangaIds
     * @param db
     * @param mangaUrlsMeta either {@link IColumnName.MangaUrls#MANGAFOX} or {@link IColumnName.MangaUrls#MANGAHERE} 
     * @return
     * @throws SQLException
     */
    public Map<Integer, String> getUrls(Collection<Integer> mangaIds, String urlColumn) throws SQLException{
    	return _getUrls(mangaIds, urlColumn);
    }
    public Map<Integer, String> getAllUrls(String urlColumn) throws SQLException{
    	return _getUrls(LOAD_ALL, urlColumn);
    }
    private static final Object LOAD_ALL = new Object();
    private Map<Integer, String> _getUrls(Object arg0, String urlColumn) throws SQLException {
    	Objects.requireNonNull(urlColumn);
    	Objects.requireNonNull(arg0);
    	
    	UrlsPrefixImpl prefix = prefixes.get(urlColumn);
    	if(prefix == null)
    		throw new IllegalArgumentException("unknown urlColumn: "+urlColumn);
    	
    	StringBuilder sql = JDBCHelper.selectSQL(URLSUFFIX_TABLE_NAME, MANGA_ID, urlColumn);
        
    	if(arg0 != LOAD_ALL) {
    		@SuppressWarnings("unchecked")
			Collection<Integer> mangaIds = (Collection<Integer>) arg0;
    		
        	if(mangaIds.isEmpty())
        		return Collections.emptyMap();

            sql.append(" WHERE ").append(MANGA_ID).append(" IN(");
            mangaIds.forEach(s -> sql.append(s).append(','));
            sql.setCharAt(sql.length() - 1, ')');
    	}
    	
    	sql.append(';');
        return db.collectToMap(sql.toString(), rs -> rs.getInt(MANGA_ID), rs -> prefix.resolve(rs.getString(urlColumn)));
	}
}
