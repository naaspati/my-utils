package sam.books;

public class BookBuilder {
	private int book_id ;
	private String name ;
	private String file_name ;
	private int path_id ;
	private PathsImpl dir; 
	private String author ;
	private String isbn ;
	private int page_count ;
	private String year ;
	private String description ;
	private BookStatus status;
	private String url;

	public BookBuilder bookId(int book_id){ this.book_id=book_id;  return this; }
	public BookBuilder name(String name){ this.name=name;  return this; }
	public BookBuilder fileName(String file_name){ this.file_name=file_name;  return this; }
	public BookBuilder pathId(int path_id){ this.path_id=path_id;  return this; }
	public BookBuilder dir(PathsImpl dir){ this.dir=dir;  return this; }
	public BookBuilder author(String author){ this.author=author;  return this; }
	public BookBuilder isbn(String isbn){ this.isbn=isbn;  return this; }
	public BookBuilder pageCount(int page_count){ this.page_count=page_count;  return this;  }
	public BookBuilder year(String year){ this.year=year;  return this; }
	public BookBuilder description(String description){ this.description=description;  return this; }
	public BookBuilder status(BookStatus status){ this.status=status;  return this; }
	public BookBuilder url(String url){ this.url=url;  return this; }

	BookBuilder() {}

	public BookImpl build() {
		return new BookImpl(book_id, name, file_name, path_id, dir, author, isbn, page_count, year, description, status, url);
	} 
}
