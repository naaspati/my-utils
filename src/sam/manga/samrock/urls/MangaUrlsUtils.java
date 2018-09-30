package sam.manga.samrock.urls;

import static sam.manga.samrock.urls.MangaUrlsMeta.MANGAFOX;
import static sam.manga.samrock.urls.MangaUrlsMeta.MANGAHERE;
import static sam.manga.samrock.urls.MangaUrlsMeta.MANGA_ID;
import static sam.manga.samrock.urls.MangaUrlsMeta.TABLE_NAME;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import sam.manga.samrock.SamrockDB;
import sam.sql.querymaker.QueryMaker;
import sam.string.StringUtils;

public final class MangaUrlsUtils {
    private final String mangafoxBase;
    private final String mangahereBase;
    private final SamrockDB db; 

    public MangaUrlsUtils(SamrockDB db) throws SQLException {
        Map<String, String> baseMap = new HashMap<>();
        this.db = db;

        try(ResultSet rs = db.executeQuery(StringUtils.join("SELECT * FROM ",MangaUrlsBaseMeta.TABLE_NAME));
                ) {
            UnaryOperator<String> map = temp -> temp.endsWith("/") ? temp : temp.concat("/");

            while(rs.next())
                baseMap.put(rs.getString(MangaUrlsBaseMeta.COLUMN_NAME), map.apply(rs.getString(MangaUrlsBaseMeta.BASE)));
        }
        mangafoxBase = baseMap.get(MANGAFOX);
        mangahereBase = baseMap.get(MANGAHERE);
    }

    private static QueryMaker qm() {
        return QueryMaker.getInstance();
    }
    /**
     * 
     * @param mangaIds
     * @param db
     * @param mangaUrlsMeta either {@link IColumnName.MangaUrls#MANGAFOX} or {@link IColumnName.MangaUrls#MANGAHERE} 
     * @return
     * @throws SQLException
     */
    public Map<Integer, String> getUrls(Collection<Integer> mangaIds, String mangaUrlsMeta) throws SQLException{
        String base = MangaUrlsMeta.MANGAFOX.equals(mangaUrlsMeta) ? mangafoxBase : MangaUrlsMeta.MANGAHERE.equals(mangaUrlsMeta) ? mangahereBase : null;
        
        if(base == null)
            throw new SQLException("column name not found in db: "+mangaUrlsMeta);

        UnaryOperator<String> join = name -> name == null ? null : base + name;

        HashMap<Integer, String> map = new HashMap<>();
        String sql = qm().select(MANGA_ID, mangaUrlsMeta).from(TABLE_NAME).where(w -> w.in(MANGA_ID, mangaIds)).build();

        db.iterate(sql, rs -> map.put(rs.getInt(MANGA_ID), join.apply(rs.getString(mangaUrlsMeta))));
        return map;
    }
    public List<MangaUrl> getMangaUrls(Collection<Integer> mangaIds) throws SQLException{
        List<MangaUrl> map = new ArrayList<MangaUrl>();
        db.iterate(qm().selectAllFrom(TABLE_NAME).where(w -> w.in(MANGA_ID, mangaIds)).build(), 
                rs -> map.add(new MangaUrl(rs.getInt(MANGA_ID), rs.getString(MANGAFOX), rs.getString(MANGAHERE))));

        return map;
    }
    public List<MangaUrl> getMangaUrls(int[] mangaIds) throws SQLException{
        List<MangaUrl> map = new ArrayList<MangaUrl>();
        db.iterate(qm().selectAllFrom(TABLE_NAME).where(w -> w.in(MANGA_ID, mangaIds)).build(), 
                rs -> map.add(new MangaUrl(rs.getInt(MANGA_ID), rs.getString(MANGAFOX), rs.getString(MANGAHERE))));

        return map;
    }
    public MangaUrl getMangaUrl(int mangaId) throws SQLException {
        return db.executeQuery(qm().selectAllFrom(TABLE_NAME).where(w -> w.eq(MANGA_ID, mangaId)).build(), 
                rs -> !rs.next() ? null : new MangaUrl(rs.getInt(MANGA_ID), rs.getString(MANGAFOX), rs.getString(MANGAHERE)));
    }
    public MangaUrl parseMangaUrl(int mangaId, String url) throws SQLException{
        return parseMangaUrl(mangaId, url, null);
    }    
    public MangaUrl parseMangaUrl(int mangaId, String url1, String url2) throws SQLException {
        String[] s1 = split(url1);
        String[] s2 = split(url2);

        if(s1 == null && s2 == null)
            return null;

        String fox = null, here = null;

        if(!(s1 == null || s2 == null) && Objects.equals(s1[0], s2[0]) && !Objects.equals(s1[1], s2[1]))
            throw new SQLException("colliding urls: "+s1[1] +",  "+ s2[1]);
        
        if(s1 != null) {
            fox = s1[0] == mangafoxBase ? s1[1] : null;
            here = s1[0] == mangahereBase ? s1[1] : null;
        }
        if(s2 != null) {
            fox = s2[0] == mangafoxBase ? s2[1] : fox;
            here = s2[0] == mangahereBase ? s2[1] : here;
        }
        return new MangaUrl(mangaId, fox, here);
    }
    private String[] split(String url) {
        if(url == null)
            return null;

        int start = url.lastIndexOf('/');
        if(start < 0)
            return null;

        int end = url.length() - 1;

        if(start == end)
            start = url.lastIndexOf('/', --end);

        if(start < 0 || start >= end)
            return null;

        String base = url.substring(0, start + 1);
        String name = url.substring(start + 1, end + 1);

        if(base.equals(mangafoxBase))
            return new String[] {mangafoxBase, name};
        if(base.equals(mangahereBase))
            return new String[] {mangahereBase, name};

        return null;
    }
    public int commitMangaUrls(List<MangaUrl> urls) throws SQLException {
        Map<Integer, MangaUrl> map = getMangaUrls(urls.stream().mapToInt(MangaUrl::getMangaId).toArray()).stream().collect(Collectors.toMap(MangaUrl::getMangaId, m -> m));

        try(PreparedStatement insert = db.prepareStatement(qm().insertInto(TABLE_NAME).placeholders(MANGA_ID, MANGAFOX, MANGAHERE));
                PreparedStatement set = db.prepareStatement(qm().update(TABLE_NAME).placeholders(MANGAFOX, MANGAHERE).where(w ->w.eqPlaceholder(MANGA_ID)).build());
                ) {
            boolean insertB = false, setB = false; 
            for (MangaUrl _new : urls) {
                MangaUrl old = map.get(_new.mangaId);
                if(old == null) {
                    insertB = true;
                    insert.setInt(1, _new.mangaId);
                    insert.setString(2, _new.getMangafoxName());
                    insert.setString(3, _new.getMangahereName());
                    insert.addBatch();
                } else {
                    setB = true;
                    set.setString(1, _new.getMangafoxName() == null ? old.getMangafoxName() : _new.getMangafoxName());
                    set.setString(2, _new.getMangahereName() == null ? old.getMangahereName() : _new.getMangahereName());
                    set.setInt(3, _new.mangaId);
                    set.addBatch();
                }
            }
            int i = 0;
            if(insertB)
                i += insert.executeBatch().length;
            if(setB)
                i += set.executeBatch().length;

            return  i;
        }
    }
    public class MangaUrl {
        final int mangaId;
        private final String mangafox, mangahere;

        MangaUrl(int id, String mangafox, String mangahere) {
            this.mangaId = id;
            this.mangafox = mangafox;
            this.mangahere = mangahere;
        }
        public int getMangaId() {
            return mangaId;
        }
        public String getMangafoxUrl() {
            return mangafox == null ? null : mangafoxBase + mangafox;
        }
        public String getMangahereUrl() {
            return mangahere == null ? null : mangahereBase + mangahere;
        }
        public String getMangafoxName() {
            return mangafox;
        }
        public String getMangahereName() {
            return mangahere;
        }
        public String getName(String MangaUrlsMeta) {
            switch (MangaUrlsMeta) {
                case MANGAFOX:
                    return getMangafoxName();
                case MANGAHERE:
                    return getMangahereName();
            }
            return null;
        }
        public String getUrl(String MangaUrlsMeta) {
            switch (MangaUrlsMeta) {
                case MANGAFOX:
                    return getMangafoxUrl();
                case MANGAHERE:
                    return getMangahereUrl();
            }
            return null;
        }
        @Override
        public String toString() {
            return "MangaUrl [id=" + mangaId + ", mangafox=" + getMangafoxUrl() + ", mangahere=" + getMangahereUrl() + "]";
        }
    }
}
