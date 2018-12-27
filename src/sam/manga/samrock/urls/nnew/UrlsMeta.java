package sam.manga.samrock.urls.nnew;



public interface UrlsMeta {
    String URLSPREFIX_TABLE_NAME = "UrlsPrefix";

    String COLUMN_NAME = "column_name";    // column_name 	TEXT NOT NULL UNIQUE
    String PREFIX = "_prefix";    // _prefix 	TEXT NOT NULL UNIQUE
    String PROTOCOL = "protocol";    // protocol 	TEXT NOT NULL DEFAULT 'http'
    
    String URLSUFFIX_TABLE_NAME = "UrlSuffix";
    String MANGA_ID = "manga_id";
}