package sam.books;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;

import sam.config.MyConfig;
import sam.sql.querymaker.QueryMaker;
import sam.sql.sqlite.SQLiteManeger;

public final class BookUtils {
    public static final Path ROOT;
    public static final Path APP_FOLDER;
    public static final Path BACKUP_FOLDER;
    public static final Path DB;

    static {
        ROOT = Paths.get(MyConfig.BOOKLIST_ROOT);
        APP_FOLDER = Paths.get(MyConfig.BOOKLIST_APP_DIR);
        DB = Paths.get(MyConfig.BOOKLIST_DB);
        BACKUP_FOLDER = APP_FOLDER.resolve("backups");
    }
    
    public static Path findBook(Path expectedPath) {
        if(Files.exists(expectedPath))
            return expectedPath;
        Path path = expectedPath.resolveSibling("_read_").resolve(expectedPath.getFileName());

        if(Files.exists(path))
            return path;
        
        File[] dirs = expectedPath.getParent().toFile().listFiles(f -> f.isDirectory());

        if(dirs == null || dirs.length == 0)
            return null;
        
        String name = expectedPath.getFileName().toString();
        
        for (File file : dirs) {
            File f = new File(file, name);
            if(f.exists())
                return f.toPath();
        }
        return null;
    }
    public static Path resolveToReadDir(Path path) {
    	return path.resolveSibling("_read_").resolve(path.getFileName());
    } 
    /**
     * a folder starting and ending with underscore (_) is called is called invisible folder, as it's content should be resolved as content of it's parent folder 
     * e.g.
     *   _read_ is a invisible folder
     *   
     * @param p
     * @return
     */
    public static boolean isInvisibleFolder(Path p){
        String s = p.getFileName().toString(); 
        return s.charAt(0) == '_' && s.charAt(s.length() - 1) == '_';
    }
    public static HashMap<Integer, String> pathsMap(SQLiteManeger db) throws SQLException {
        String pathSql = QueryMaker.getInstance().selectAllFrom(BookPathMeta.TABLE_NAME).build();
        
        HashMap<Integer, String> paths = new HashMap<>();
        db.iterate(pathSql, rs -> paths.put(rs.getInt(BookPathMeta.PATH_ID), rs.getString(BookPathMeta.PATH)));
        
        return paths;
    }
}
