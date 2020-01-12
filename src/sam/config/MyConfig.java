package sam.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//VERSION = 1.2
//REQUIRED = 1509527940769-my-config.properties

public final class MyConfig {
	

	static {
		try(InputStream is = MyConfig.class.getResourceAsStream("1509527940769-my-config.properties")) {
			Properties2 c = new Properties2(is);
			c.setSystemLookup(true, true);

			COMMONS_DIR = c.get("COMMONS_DIR");
			MANGA_DIR = c.get("MANGA_DIR");
			MANGA_DATA_DIR = c.get("MANGA_DATA_DIR");
			MANGAROCK_INPUT_DB = c.get("MANGAROCK_INPUT_DB");
			MANGAROCK_INPUT_DIR = c.get("MANGAROCK_INPUT_DIR");
			MANGAROCK_DB_BACKUP = c.get("MANGAROCK_DB_BACKUP"); 
			SAMROCK_DB_OLD = c.get("SAMROCK_DB_OLD");
			SAMROCK_DB = c.get("SAMROCK_DB");
			SAMROCK_THUMBS_DIR = c.get("SAMROCK_THUMBS_DIR");
			NEW_MANGAS_TSV_FILE = c.get("NEW_MANGAS_TSV_FILE");
			UPDATED_MANGAS_TSV_FILE = c.get("UPDATED_MANGAS_TSV_FILE");
			MISSING_CHAPTERS_FILE = c.get("MISSING_CHAPTERS_FILE");

			BOOKLIST_ROOT = c.get("BOOKLIST_ROOT");
			BOOKLIST_APP_DIR = c.get("BOOKLIST_APP_DIR");
			BOOKLIST_DB = c.get("BOOKLIST_DB");
		} catch (IOException e) {
			throw new RuntimeException("MyConfig loading failed", e);
		}

		String s = Stream.of(MyConfig.class.getDeclaredFields())
				.map(f -> {
					try {
						return new Object[] {f.get(null), f};
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new RuntimeException("failed to read field: "+f.getName(), e);
					}
				})
				.filter(f -> f[0] == null)
				.map(f -> ((Field)f[1]).getName())
				.collect(Collectors.joining(", "));

		if(!s.isEmpty())
			throw new RuntimeException("not values set for field(s):["+s+"]");
	}

	public static final String 

	COMMONS_DIR,

	MANGA_DIR,
	MANGA_DATA_DIR,
	MANGAROCK_INPUT_DB,
	MANGAROCK_INPUT_DIR,

	SAMROCK_DB_OLD,
	SAMROCK_DB,
	SAMROCK_THUMBS_DIR,

	NEW_MANGAS_TSV_FILE,
	UPDATED_MANGAS_TSV_FILE,
	MISSING_CHAPTERS_FILE,

	BOOKLIST_ROOT,
	BOOKLIST_APP_DIR,
	BOOKLIST_DB,
	MANGAROCK_DB_BACKUP;
	
	public static String get(String key) {
		switch (key) {
			case "COMMONS_DIR": return COMMONS_DIR;
			case "MANGA_DIR": return MANGA_DIR;
			case "MANGA_DATA_DIR": return MANGA_DATA_DIR;
			case "MANGAROCK_INPUT_DB": return MANGAROCK_INPUT_DB;
			case "MANGAROCK_INPUT_DIR": return MANGAROCK_INPUT_DIR;
			case "MANGAROCK_DB_BACKUP": return MANGAROCK_DB_BACKUP;
			case "SAMROCK_DB_OLD": return SAMROCK_DB_OLD;
			case "SAMROCK_DB": return SAMROCK_DB;
			case "SAMROCK_THUMBS_DIR": return SAMROCK_THUMBS_DIR;
			case "NEW_MANGAS_TSV_FILE": return NEW_MANGAS_TSV_FILE;
			case "UPDATED_MANGAS_TSV_FILE": return UPDATED_MANGAS_TSV_FILE;
			case "MISSING_CHAPTERS_FILE": return MISSING_CHAPTERS_FILE;
			case "BOOKLIST_ROOT": return BOOKLIST_ROOT;
			case "BOOKLIST_APP_DIR": return BOOKLIST_APP_DIR;
			case "BOOKLIST_DB": return BOOKLIST_DB;
			default: throw new IllegalArgumentException("unnknown key: \""+key+"\"");
		}
	}
}
