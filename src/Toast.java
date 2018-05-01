import java.io.IOException;

import sam.collection.Iterables;
import sam.manga.newsamrock.SamrockDB;
import sam.manga.newsamrock.urls.MangaUrlsUtils.MangaUrl;

public class Toast {
    public static void main(String[] args) throws IOException {
        for (Object s : Iterables.empty()) {
            System.out.println(s);
        }
        System.out.println("DONE");
    }
}
