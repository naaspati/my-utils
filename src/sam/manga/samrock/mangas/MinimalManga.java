package sam.manga.samrock.mangas;

import java.nio.file.Path;

import sam.manga.samrock.chapters.MinimalChapter;

public interface MinimalManga {
	public int getMangaId();
    public String getDirName();
    public Path getDirPath();
    public String getMangaName();
    public Iterable<? extends MinimalChapter> getChapterIterable();
}
