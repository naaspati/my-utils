package test.manga.newsamrock.urls;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import sam.manga.newsamrock.SamrockDB;
import sam.manga.newsamrock.urls.MangaUrlsUtils.MangaUrl;

class MangaUrlsUtilsTest {

    @Test
    void test() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
        String fox = "http://fanfox.la/manga/amai_akuma_ga_warau";
        String here = "http://www.mangahere.cc/manga/amai_akuma_ga_warau";
        
        try(SamrockDB db = new SamrockDB()) {
            MangaUrl url = db.url().parseMangaUrl(0, fox);
            assertEquals(fox, url.getMangafoxUrl());
            assertNull(url.getMangahereUrl());
            
            url = db.url().parseMangaUrl(0, here);
            assertEquals(here, url.getMangahereUrl());
            assertNull(url.getMangafoxUrl());
            
            url = db.url().parseMangaUrl(0, fox, here);
            assertEquals(fox, url.getMangafoxUrl());
            assertEquals(here, url.getMangahereUrl());
            
            assertEquals(fox, url.getMangafoxUrl());
            assertEquals(here, url.getMangahereUrl());
            
            assertNull(db.url().parseMangaUrl(0, null, null));
        }
    }

}
