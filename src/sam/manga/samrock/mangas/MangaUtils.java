package sam.manga.samrock.mangas;

import static sam.config.MyConfig.MANGA_DIR;
import static sam.manga.samrock.mangas.MangasMeta.MANGAS_TABLE_NAME;
import static sam.manga.samrock.mangas.MangasMeta.MANGA_ID;
import static sam.sql.querymaker.QueryMaker.qm;

import java.io.File;
import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;

import sam.myutils.Checker;
import sam.sql.SqlConsumer;
import sam.sql.SqlFunction;
import sam.sql.sqlite.SQLiteDB;
import sam.string.StringUtils;
public class MangaUtils {
    private final SQLiteDB db;
    public MangaUtils(SQLiteDB db) {
        this.db = db;
    }
    /**
	 * tags to sorted tags int array <br>
	 * <pre> ".1..2..3..5..7..8..18..19..24." -> [1, 2, 3, 5, 7, 8, 18, 19, 24] </pre> 
	 * @param tags
	 * @return
	 */
	public static int[] tagsToIntArray(String tags) {
		if(Checker.isEmptyTrimmed(tags))
			return new int[0];
		
		int[] n = tagsToIntStream(tags).toArray();
		Arrays.sort(n);
		return n;
	}
	public static IntStream tagsToIntStream(String tags) {
		if(Checker.isEmptyTrimmed(tags))
			return IntStream.empty();
		
		return StringUtils.splitStream(tags, '.').map(String::trim).filter(Checker::isNotEmpty).mapToInt(Integer::parseInt);
	} 
    
    /**
     * select mangas with given manga_id(s) and with given columns, and iterate with consumer
     *  
     * @param consumer
     * @param mangasMeta
     * @throws SQLException
     */
    public void select(Collection<Integer> mangaIds, SqlConsumer<ResultSet> consumer, String...mangasMeta) throws SQLException {
        String sql = qm().select(mangasMeta).from(MANGAS_TABLE_NAME).where(w -> w.in(MANGA_ID, mangaIds, false)).build();
        db.iterate(sql.toString(), consumer);
    }
    public void select(int[] mangaIds, SqlConsumer<ResultSet> consumer, String...mangasMeta) throws SQLException {
        String sql = qm().select(mangasMeta).from(MANGAS_TABLE_NAME).where(w -> w.in(MANGA_ID, mangaIds)).build();
        db.iterate(sql.toString(), consumer);
    }
    /**
     * select all mangas with given columns, and iterate with consumer
     * @param consumer
     * @param mangasMeta
     * @throws SQLException
     */
    public void selectAll(SqlConsumer<ResultSet> consumer, String...mangasMeta) throws SQLException {
        db.iterate(qm().select(mangasMeta).from(MANGAS_TABLE_NAME).build(), consumer);
    }
    public <E> E select(int mangaId, SqlFunction<ResultSet, E> mapper, String...mangasMeta) throws SQLException {
        return db.executeQuery(qm().select(mangasMeta).from(MANGAS_TABLE_NAME).where(w -> w.eq(MANGA_ID, mangaId)).build(), mapper);
    }
    
    private static WeakReference<String[]> dirList = new WeakReference<String[]>(null);
    private static long dirListTime = 0;
    
    /**
     * 
     * @return new File(MANGA_DIR).list()
     */
    public static String[] dirList() {
    	File f = new File(MANGA_DIR);
    	String[] list = dirList.get();
    	long t = f.lastModified();
    	if(list != null && t == dirListTime)
    		return list;
    	
    	dirListTime = t;
    	list = new File(MANGA_DIR).list();
    	dirList = new WeakReference<String[]>(list);
    	return list;
    }
}
