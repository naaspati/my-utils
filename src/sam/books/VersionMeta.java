package sam.books;



public interface VersionMeta {
	String TABLE_NAME = "Version";

	String BOOK_ID = "book_id";    // book_id 	integer UNIQUE
	String VERSION = "version";    // version 	integer NOT NULL DEFAULT 0


	String CREATE_TABLE_SQL = "CREATE TABLE `Version` (\n"+
			"	`book_id`	integer UNIQUE,\n"+
			"	`version`	integer NOT NULL DEFAULT 0,\n"+
			"	FOREIGN KEY(`book_id`) REFERENCES `Books`(`_id`) on delete cascade,\n"+
			"	PRIMARY KEY(`book_id`)\n"+
			");\n";

}