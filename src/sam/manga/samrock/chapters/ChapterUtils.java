package sam.manga.samrock.chapters;

import static sam.manga.samrock.chapters.ChapterUpdate.DELETE;
import static sam.manga.samrock.chapters.ChapterUpdate.NEW_FROM_DATA;
import static sam.manga.samrock.chapters.ChapterUpdate.NEW_PARSING_FILE;
import static sam.manga.samrock.chapters.ChaptersMeta.CHAPTERS_TABLE_NAME;
import static sam.manga.samrock.chapters.ChaptersMeta.CHAPTER_ID;
import static sam.manga.samrock.chapters.ChaptersMeta.MANGA_ID;
import static sam.manga.samrock.chapters.ChaptersMeta.NAME;
import static sam.manga.samrock.chapters.ChaptersMeta.NUMBER;
import static sam.manga.samrock.chapters.ChaptersMeta.READ;
import static sam.manga.samrock.mangas.MangasMeta.CHAP_COUNT_PC;
import static sam.manga.samrock.mangas.MangasMeta.LAST_UPDATE_TIME;
import static sam.manga.samrock.mangas.MangasMeta.MANGAS_TABLE_NAME;
import static sam.manga.samrock.mangas.MangasMeta.READ_COUNT;
import static sam.manga.samrock.mangas.MangasMeta.UNREAD_COUNT;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import sam.collection.Iterables;
import sam.logging.MyLoggerFactory;
import sam.manga.samrock.Renamer;
import sam.manga.samrock.SamrockDB;
import sam.manga.samrock.mangas.MinimalManga;
import sam.myutils.Checker;
import sam.myutils.MyUtilsException;
import sam.myutils.System2;
import sam.sql.SqlConsumer;
import sam.sql.SqlFunction;
import sam.sql.querymaker.QueryMaker;
import sam.string.BasicFormat;
public class ChapterUtils {
	private static final Logger LOGGER = MyLoggerFactory.logger(ChapterUtils.class);
	private final SamrockDB db;

	public ChapterUtils(SamrockDB db) {
		this.db = db;
	}

	private QueryMaker qm() {
		return QueryMaker.getInstance();
	}
	public void selectByMangaId(Collection<Integer> mangaIds, SqlConsumer<ResultSet> consumer, String...chaptersMeta) throws SQLException {
		String sql = qm().select(chaptersMeta).from(CHAPTERS_TABLE_NAME).where(w -> w.in(ChaptersMeta.MANGA_ID, mangaIds)).build();
		db.iterate(sql.toString(), consumer);
	}
	public LastChapter lastChapter() {
		return new LastChapter(db);
	}

	public static class MangaLog {
		public final MinimalManga manga;
		public final List<ChapterLog> chapters;
		public final int currentTotal, read, unread, delete, chapCountPc;

		public MangaLog(MinimalManga manga, List<ChapterLog> chapters, int currentTotal, int read, int unread, int delete, int chapCountPc) {
			this.manga = manga;
			this.chapters = chapters;
			this.currentTotal = currentTotal;
			this.read = read;
			this.unread = unread;
			this.delete = delete;
			this.chapCountPc = chapCountPc;
		}
	}
	public static class ChapterLog {
		public final MinimalChapter chapter;
		public final ChapterUpdate update;


		public ChapterLog(MinimalChapter chapter, ChapterUpdate update) {
			this.chapter = chapter;
			this.update = update;
		}
	}
	public static class ChapterFile implements MinimalChapter {
		private double number = -10;
		private String title = null;
		private final String filename;

		public ChapterFile(String filename) {
			this.filename = filename;
		}
		@Override
		public double getNumber() {
			if(number == -10)
				number = MinimalChapter.parseChapterNumber(filename).orElse(-1);
			return number;
		}
		@Override
		public String getTitle() {
			if(title == null)
				title = MinimalChapter.getTitleFromFileName(filename);
			return title;
		}
		@Override
		public String getFileName() {
			return filename;
		}
	}
	private static class TempInsert {
		final MinimalManga manga;
		final MinimalChapter chapter;

		public TempInsert(MinimalManga manga, MinimalChapter chapter) {
			this.manga = manga;
			this.chapter = chapter;
		}
	}

	/**
	 * does not commit the changes, just executes them
	 * @param mangasToUpdate mangas may contains chapters (helpful when paring chapter filename)
	 * @param logger
	 * @throws SQLException
	 * @throws IOException
	 */
	public List<MangaLog> completeUpdate(List<MinimalManga> mangasToUpdate) throws SQLException, IOException {
		if(mangasToUpdate.isEmpty())
			throw new IllegalArgumentException("invalid state: mangasToUpdate is empty");

		/** FIXME Converter not supplying proper data
		 * eliminate duplicate mangas
		 */
		IdentityHashMap<MinimalManga, Map<String, MinimalChapter>> suppliedData = mangasToUpdate.stream()
				.collect(Collectors.toMap(m -> m, m -> {
					Map<String, MinimalChapter> chapters = new HashMap<>();
					for (MinimalChapter c : m.getChapterIterable()) {
						String s =  c.getFileName();
						if(s == null)
							s = Renamer.makeChapterFileName(c.getNumber(), c.getTitle(), m.getMangaName());
						if(s != null)
							chapters.put(s.concat(".jpeg"), c);
					} 
					return chapters;
				}, (o, n) -> n, IdentityHashMap::new));

		IdentityHashMap<MinimalManga, List<ChapterFile>> files = suppliedData.keySet().stream().collect(Collectors.toMap(m -> m, m -> MyUtilsException.noError(() -> chapterFileNames(m)).map(ChapterFile::new).collect(Collectors.toList()), (p, o) -> {throw new IllegalStateException();}, IdentityHashMap::new));
		IdentityHashMap<MinimalManga, List<Chapter>> dbloaded = new IdentityHashMap<>();

		{
			Map<Integer, MinimalManga> map = suppliedData.keySet().stream().collect(Collectors.toMap(MinimalManga::getMangaId, m -> m, (p,o) -> p));
			Map<Integer, List<Chapter>> chapters = getChapters(map.keySet());
			chapters.forEach((id,chaps) -> dbloaded.put(map.get(id), chaps));
		}

		ArrayList<MangaLog> logs = new ArrayList<>();
		final HashSet<String> chapCountPc = new HashSet<>();
		Pattern pattern =  Pattern.compile(" - \\d\\.jpe?g");
		int[] total = {0};
		int[] readCount = {0};
		List<Integer> delete = new ArrayList<>();
		List<TempInsert> insert = new ArrayList<>();
		HashMap<String, Chapter> dbMap = new HashMap<>();

		files.forEach((manga, chapters) -> {
			total[0] = 0;
			readCount[0] = 0;
			chapCountPc.clear();

			List<ChapterLog> chapLogs = new ArrayList<>(chapters.size()+1);

			Map<String, MinimalChapter> supplied = suppliedData.getOrDefault(manga, Collections.emptyMap());
			dbMap.clear();
			List<Chapter> temp = dbloaded.get(manga);
			if(temp != null)
				temp.forEach(c -> dbMap.put(c.getFileName(), c));

			for (ChapterFile c : chapters) {
				total[0]++;
				String fileName = c.getFileName();
				MinimalChapter dataC = supplied.get(fileName);
				Chapter dbC = dbMap.remove(fileName);
				chapCountPc.add(fileName.lastIndexOf('-') < 0 ? fileName : pattern.matcher(fileName).replaceFirst(""));

				if(dataC == null && dbC == null) {
					chapLogs.add(new ChapterLog(c, NEW_PARSING_FILE));
					insert.add(new TempInsert(manga, c));
				} else if(dbC != null) {
					// chapLogs.add(new ChapterLog(dbC, NO_UPDATE));
					if(dbC.isRead())
						readCount[0]++;
				} else {
					chapLogs.add(new ChapterLog(dataC, NEW_FROM_DATA));
					insert.add(new TempInsert(manga, c));
				} 
			}
			logs.add(new MangaLog(manga, Collections.unmodifiableList(chapLogs), total[0], readCount[0], total[0] - readCount[0], dbMap.size(), chapCountPc.size()));

			if(!dbMap.isEmpty()) {
				dbMap.values().forEach(c -> {
					chapLogs.add(new ChapterLog(c, DELETE));
					delete.add(c.getChapterId());
				});
			}
		});
		String s = qm().update(MANGAS_TABLE_NAME)
				.placeholders(READ_COUNT, UNREAD_COUNT, CHAP_COUNT_PC)
				.set(LAST_UPDATE_TIME, System.currentTimeMillis())
				.where(w -> w.eqPlaceholder(MANGA_ID))
				.build();
		s = s.replace("?", "{}");
		BasicFormat format = new BasicFormat(s.concat("\n"));
		logs.removeIf(m -> m.chapters.isEmpty());

		StringBuilder sb = new StringBuilder();
		for (MangaLog m : logs) 
			format.format(sb, m.read, m.unread, m.chapCountPc, m.manga.getMangaId());

		if(sb.length() != 0)
			db.executeUpdate(sb.toString());

		if(!delete.isEmpty()) {
			String sql = qm().deleteFrom(CHAPTERS_TABLE_NAME)
					.where(w -> w.in(MANGA_ID, delete))
					.build();			
			db.executeUpdate(sql);
		}

		if(!insert.isEmpty()) {
			String sql = "INSERT INTO "+CHAPTERS_TABLE_NAME+"("+String.join(",", MANGA_ID, NAME, NUMBER, READ)+") VALUES(?,?,?,0)";

			try(PreparedStatement p = db.prepareStatement(sql)) {
				for (TempInsert t : insert) {
					p.setInt(1, t.manga.getMangaId());
					p.setString(2, t.chapter.getFileName());
					p.setDouble(3, t.chapter.getNumber());
					p.addBatch();	
				}
				p.executeBatch();
			}			
		}
		return logs;
	}
	public Map<Integer, List<Chapter>> getChapters(Iterable<Integer> mangaIds) throws SQLException {
		Map<Integer, List<Chapter>> map = new HashMap<>();

		String sql = qm().selectAll().from(CHAPTERS_TABLE_NAME).where(w -> w.in(MANGA_ID, mangaIds)).build();
		Function<Integer, List<Chapter>> computer = i -> new ArrayList<>();

		db.iterate(sql, rs -> map.computeIfAbsent(rs.getInt(MANGA_ID), computer).add(new Chapter(rs)));
		return map;
	}
	public <E extends Chapter> List<E> getChapters(int mangaId,  SqlFunction<ResultSet, E> chapterMapper) throws SQLException {
		return db.collectToList(qm().select(CHAPTER_ID, NAME, NUMBER, READ).from(CHAPTERS_TABLE_NAME).where(w -> w.eq(MANGA_ID, mangaId)).build(), chapterMapper);
	}
	public static <E extends Chapter> List<E> reloadChapters(MinimalManga manga, Iterable<E> chapters, BiFunction<OptionalDouble, String, E> chapterMaker) throws IOException {
		Map<String, OptionalDouble> map = chapterFileNames(manga).collect(Collectors.toMap(s -> s, MinimalChapter::parseChapterNumber));

		if(map.values().stream().anyMatch(d -> !d.isPresent())) {
			Path p = manga.getDirPath();
			System.out.println("bad files in "+p.getFileName());
			map.keySet().forEach(s -> System.out.println("  "+s));
			map.values().removeIf(Objects::isNull);
		}

		if(map.isEmpty()) 
			throw new IOException("no chapters found");

		for (Chapter c : chapters) {
			OptionalDouble number = map.get(c.getFileName());
			if(!number.isPresent()) {
				System.out.println("  delete: "+c);
				c.setDeleted(true);
			} else
				c.setNumber(number.getAsDouble());
		}

		List<E> list = chapters instanceof ArrayList || chapters instanceof LinkedList ? (List<E>) chapters : Iterables.stream(chapters).collect(Collectors.toList());
		if(!map.isEmpty())
			map.forEach((s,t) -> chapterMaker.apply(t, s)); 
		return list;
	}
	private static Stream<String> chapterFileNames(MinimalManga manga, boolean walkSimple) throws IOException{
		Path path = manga.getDirPath();

		if(Files.notExists(path))
			throw new FileNotFoundException(path.toString());

		if(walkSimple) {
			String[] array = path.toFile().list();
			if(Checker.isEmpty(array))
				return Stream.empty();

			return Arrays.stream(array)
					.filter(s -> s.endsWith(".jpeg") || s.endsWith(".jpg"));
		}

		try (DirectoryStream<Path> strm = Files.newDirectoryStream(path)){
			Stream.Builder<String> build = Stream.builder();

			for (Path p : strm) {
				if(Files.isRegularFile(p) && !Files.isHidden(p)) {
					build.add(p.getFileName().toString());
				} else {
					LOGGER.fine(() -> {
						try {
							return String.format("Skipping File {regular-file:%s, is-hidden:%s, file-path:%s}", Files.isRegularFile(p) , Files.isHidden(p), p);
						} catch (IOException e) {
							return String.format("Skipping File {regular-file:%s, is-hidden:%s, file-path:%s}", Files.isRegularFile(p), e.toString(), p);
						}
					});
				}
			}
			return build.build();
		}
	}

	private static String WALK_TYPE;
	private static boolean simplewalk;

	private static Stream<String> chapterFileNames(MinimalManga manga) throws IOException{
		if(WALK_TYPE == null) {
			WALK_TYPE = System2.lookup("MANGA_DIR_WALK_TYPE", "simple");
			simplewalk = WALK_TYPE.equalsIgnoreCase("simple");
			if(!simplewalk && !WALK_TYPE.equalsIgnoreCase("heavy"))
				throw new IllegalStateException("unknown value: MANGA_DIR_WALK_TYPE="+WALK_TYPE);
		}

		return chapterFileNames(manga, simplewalk);
	}

	public Map<Integer, ChapterFilter> getChapterFilters(Collection<Integer> mangaIds, String filterTitle) throws SQLException {
		return getChapterFilters(mangaIds, filterTitle, ChapterFilter::new);
	}

	public <E extends ChapterFilter> Map<Integer, E> getChapterFilters(Collection<Integer> mangaIds, String filterTitle, BiFunction<Integer, String, E> newInstance) throws SQLException {
		if(mangaIds.isEmpty())
			return Collections.emptyMap();

		Map<Integer, E> map = new HashMap<>();
		Function<Integer, E> mapper = manga_id -> newInstance.apply(manga_id, filterTitle);

		db.iterate(QueryMaker.qm().select(MANGA_ID, NUMBER).from(CHAPTERS_TABLE_NAME).where(w -> w.in(MANGA_ID, mangaIds)).build(), rs -> {
			map.computeIfAbsent(rs.getInt(MANGA_ID), mapper)
			.add(rs.getDouble(NUMBER));
		});

		map.forEach((s,t) -> t.setCompleted());
		return map;
	}
}
