package sam.books;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BookBase {
    public final int book_id ;
    public final String name ;
    public final String file_name ;
    public final int path_id ;
    public final String parentFolderSubpath; 
    public final String author ;
    public final String isbn ;
    public final String page_count ;
    public final String year ;
    public final String description ;
    public BookBase(
            int book_id ,
            String name ,
            String file_name ,
            int path_id ,
            String parentFolderSubpath, 
            String author ,
            String isbn ,
            String page_count ,
            String year ,
            String description 
            ) {
        this.book_id = book_id;
        this.name = name;
        this.file_name = file_name;
        this.path_id = path_id;
        this.parentFolderSubpath = parentFolderSubpath;
        this.author = author;
        this.isbn = isbn;
        this.page_count = page_count;
        this.year = year;
        this.description = description;
    }
    public Path getSubpath() {
        return Paths.get(parentFolderSubpath, file_name);
    }
    public Path getFullPath() {
        Path p = BookUtils.ROOT.resolve(parentFolderSubpath).resolve(file_name);
        
        Path p2 = BookUtils.findBook(p);
        return p2 != null ? p2 : p;
    }
}
