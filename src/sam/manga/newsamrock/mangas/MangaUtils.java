package sam.manga.newsamrock.mangas;

import static sam.manga.newsamrock.mangas.MangasMeta.MANGA_ID;
import static sam.manga.newsamrock.mangas.MangasMeta.TABLE_NAME;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import sam.fileutils.RemoveInValidCharFromString;
import sam.manga.newsamrock.SamrockDB;
import sam.sql.SqlConsumer;
import sam.sql.SqlFunction;
import sam.sql.querymaker.QueryMaker;

public class MangaUtils {
    private final SamrockDB db;
    public MangaUtils(SamrockDB db) {
        this.db = db;
    }
    private QueryMaker qm() {
        return QueryMaker.getInstance();
    }
    
    private static RemoveInValidCharFromString remover;
    /*
     * why manual work instead of regex? because i have time :]
     */
    public static String toDirName(String mangaName) {
        if (mangaName == null)
            throw new NullPointerException("mangaName ='" + mangaName + "'");

        char[] chars = mangaName.toCharArray();

        // remove html entities (&{7chars};)
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '&') {
                loop: for (int j = i + 1; j < i + 7 && j < chars.length; j++) { // remove &\\w{1,7};
                    if (chars[j] == ';') {//
                        for (; i <= j; i++)
                            chars[i] = ' ';
                        break loop;
                    }
                }
            continue;
            }
            if (chars[i] == '_' || chars[i] == '%') // % and _ have special meaning in text search of SQL
                chars[i] = '\0';
        }
        
        if(remover == null)
            remover = new RemoveInValidCharFromString();

        remover.replaceWindowReservedChars(chars);
        remover.removeInvalidSpaceChars(chars);
        remover.multipleSpacesToNullChars(chars);
        remover.arrangeChars(chars);
        String str = remover.trimAndCreate(chars);

        if (str.isEmpty())
            throw new NullPointerException(
                    "at start mangaName: " + mangaName + " and after formatting mangaName is empty a string");

        return str;

        /*
         * same as
         * 
         * private String formatDirName(String mangaName) { if(mangaName != null &&
         * !mangaName.trim().isEmpty()){ mangaName = mangaName
         * .replaceAll("[\\Q%_<>:\\\"/*|?\\E]", " ")//% and _ are SQL keyChars rest
         * window reserved keyChars .replaceAll("&\\w{1,4};", " ") .replaceAll("\\s+",
         * " ") //remove all space characters except normal single space (" ") .trim()
         * .replaceFirst("\\.+$", ""); //replace dot char at the end of name, as if it
         * is left, then in naming folder or file windows removes it, and file path
         * which contains the dot at the end will give error; } return mangaName == null
         * || mangaName.isEmpty() ? null : mangaName; }
         * 
         */
    }
    
    /**
     * select mangas with given manga_id(s) and with given columns, and iterate with consumer
     *  
     * @param consumer
     * @param mangasMeta
     * @throws SQLException
     */
    public void select(Collection<Integer> mangaIds, SqlConsumer<ResultSet> consumer, String...mangasMeta) throws SQLException {
        String sql = qm().select(mangasMeta).from(TABLE_NAME).where(w -> w.in(MANGA_ID, mangaIds, false)).build();
        db.iterate(sql.toString(), consumer);
    }
    public void select(int[] mangaIds, SqlConsumer<ResultSet> consumer, String...mangasMeta) throws SQLException {
        String sql = qm().select(mangasMeta).from(TABLE_NAME).where(w -> w.in(MANGA_ID, mangaIds)).build();
        db.iterate(sql.toString(), consumer);
    }
    /**
     * select all mangas with given columns, and iterate with consumer
     * @param consumer
     * @param mangasMeta
     * @throws SQLException
     */
    public void selectAll(SqlConsumer<ResultSet> consumer, String...mangasMeta) throws SQLException {
        db.iterate(qm().select(mangasMeta).from(TABLE_NAME).build(), consumer);
    }
    public <E> E select(int mangaId, SqlFunction<ResultSet, E> mapper, String...mangasMeta) throws SQLException {
        return db.executeQuery(qm().select(mangasMeta).from(TABLE_NAME).where(w -> w.eq(MANGA_ID, mangaId)).build(), mapper);
    }
}
