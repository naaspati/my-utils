package sam.books;



public interface BooksMeta {
	String PATH_TABLE_NAME = "Paths";

    String PATH_ID = "path_id";    // path_id 	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE
    String PATH = "_path";    // _path 	TEXT NOT NULL UNIQUE
    String MARKER = "marker";    // marker 	TEXT


    String CHANGE_LOG_TABLE_NAME = "change_log";

    String LOG_NUMBER = "log_number";    // nth 	integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE
    String DML_TYPE = "dml_type";    // _type 	text NOT NULL
    String TABLENAME = "table_name";    // _table 	text NOT NULL
    String ID = "_id";    // _id 	integer NOT NULL


    String BOOK_TABLE_NAME = "Books";

    String BOOK_ID = "_id";    // _id 	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE
    String NAME = "name";    // name 	TEXT UNIQUE
    String FILE_NAME = "file_name";    // file_name 	TEXT NOT NULL UNIQUE
    // String PATH_ID = "path_id";    // path_id 	INTEGER NOT NULL
    String AUTHOR = "author";    // author 	TEXT
    String ISBN = "isbn";    // isbn 	TEXT UNIQUE
    String PAGE_COUNT = "page_count";    // page_count 	INTEGER
    String YEAR = "year";    // year 	TEXT
    String DESCRIPTION = "description";    // description 	TEXT
    String STATUS = "status";    // status 	TEXT DEFAULT 'NONE'
    String URL = "url";    // url 	TEXT
    String CREATED_ON = "created_on";    // created_on 	INTEGER DEFAULT 0

}