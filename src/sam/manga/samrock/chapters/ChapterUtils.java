package sam.manga.samrock.chapters;

import static sam.manga.samrock.mangas.MangasMeta.*;
import static sam.sql.querymaker.QueryMaker.qm;
import static sam.manga.samrock.chapters.ChapterUpdate.DELETE;
import static sam.manga.samrock.chapters.ChapterUpdate.NEW_FROM_DATA;
import static sam.manga.samrock.chapters.ChapterUpdate.NEW_PARSING_FILE;
import static sam.manga.samrock.chapters.ChapterUpdate.NO_UPDATE;
import static sam.manga.samrock.chapters.ChaptersMeta.CHAPTERS_TABLE_NAME;
import static sam.manga.samrock.chapters.ChaptersMeta.CHAPTER_ID;
import static sam.manga.samrock.chapters.ChaptersMeta.MANGA_ID;
import static sam.manga.samrock.chapters.ChaptersMeta.NAME;
import static sam.manga.samrock.chapters.ChaptersMeta.NUMBER;
import static sam.manga.samrock.chapters.ChaptersMeta.READ;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import sam.collection.Iterables;
import sam.io.fileutils.FileNameSanitizer;
import sam.logging.MyLoggerFactory;
import sam.manga.samrock.SamrockDB;
import sam.manga.samrock.mangas.MinimalManga;
import sam.myutils.System2;
import sam.sql.SqlConsumer;
import sam.sql.SqlFunction;
import sam.sql.querymaker.QueryMaker;
import sam.string.BasicFormat;
import sam.string.StringUtils;
public class ChapterUtils {
	private static final Logger LOGGER = MyLoggerFactory.logger(ChapterUtils.class);

	private final SamrockDB db;
	private static FileNameSanitizer remover;

	public ChapterUtils(SamrockDB db) {
		this.db = db;
	}

	private QueryMaker qm() {
		return QueryMaker.getInstance();
	}
	public ChapterNumbers chapterNumbers() throws SQLException{
		return new ChapterNumbers(db);
	}
	public static String makeChapterFileName(double number, String chapterFileName, String mangaName) {
		if (chapterFileName == null)
			throw new NullPointerException("chapterName: " + chapterFileName);

		final String numS = StringUtils.doubleToString(number);
		if(chapterFileName == null || chapterFileName.trim().isEmpty())
			return numS;

		chapterFileName = Pattern.compile(mangaName.replaceFirst("(?i)Manh(?:w|u)a", ""), Pattern.LITERAL | Pattern.CASE_INSENSITIVE)
				.matcher(chapterFileName)
				.replaceFirst("")
				.replace(numS, "");

		char[] chars = (numS +" "+chapterFileName.trim()).toCharArray();

		if ((chars[0] == 'C' || chars[0] == 'c') && (chars[1] == 'h' || chars[1] == 'H')
				&& (chars[2] == 'a' || chars[2] == 'A') && (chars[3] == 'p' || chars[3] == 'P')
				&& (chars[4] == 't' || chars[4] == 'T') && (chars[5] == 'e' || chars[5] == 'E')
				&& (chars[6] == 'r' || chars[6] == 'R')) {
			chars[0] = '\0';
			chars[1] = '\0';
			chars[2] = '\0';
			chars[3] = '\0';
			chars[4] = '\0';
			chars[5] = '\0';
			chars[6] = '\0';
		}
		if(remover == null)
			remover = new FileNameSanitizer();

		remover.removeUnmappableChars(chars);
		remover.replaceWindowReservedChars(chars);
		remover.remove_non_space_white_spaces(chars);
		remover.removeNullChars(chars);
		String str = remover.trimAndCreate(chars);

		return str;
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
		final int manga_id;
		final MinimalChapter chapter;
		
		public TempInsert(int manga_id, MinimalChapter chapter) {
			this.manga_id = manga_id;
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

		HashMap<Integer, Map<String, MinimalChapter>> newData = new HashMap<>();
		HashMap<Integer, MinimalManga> mangas = new HashMap<>();

		/**
		 * eliminate duplicate mangas
		 */
		for (MinimalManga m : mangasToUpdate) {
			Map<String, MinimalChapter> chapters = newData.computeIfAbsent(m.getMangaId(), i -> new HashMap<>());
			mangas.put(m.getMangaId(), m);

			for (MinimalChapter c : m.getChapterIterable()) 
				chapters.put(c.getFileName(), c);
		}

		Map<Integer, List<Chapter>> _db0 = getChapters(newData.keySet());
		HashMap<Integer, Map<String, Chapter>> database = new HashMap<>();
		_db0.forEach((id, list) -> database.put(id, list.stream().collect(Collectors.toMap(MinimalChapter::getFileName, c -> c))));

		Map<Integer, List<ChapterFile>> walked = new HashMap<>();
		for (Entry<Integer, MinimalManga> m : mangas.entrySet()) 
			walked.put(m.getKey(), chapterFileNames(m.getValue()).map(ChapterFile::new).collect(Collectors.toList()));	
		
		ArrayList<MangaLog> logs = new ArrayList<>();
		final HashSet<String> chapCountPc = new HashSet<>();
		Pattern pattern =  Pattern.compile(" - \\d\\.jpe?g");
		int[] total = {0};
		int[] readCount = {0};
		List<Integer> delete = new ArrayList<>();
		List<TempInsert> insert = new ArrayList<>();

		walked.forEach((id, chapters) -> {
			total[0] = 0;
			readCount[0] = 0;
			chapCountPc.clear();

			MinimalManga manga = mangas.get(id);
			List<ChapterLog> chapLogs = new ArrayList<>(chapters.size()+1);

			Map<String, MinimalChapter> newDataMap = newData.get(id);
			Map<String, Chapter> dbMap = database.get(id);
			if(newDataMap == null)
				newDataMap = Collections.emptyMap();
			if(dbMap == null)
				dbMap = Collections.emptyMap();

			for (ChapterFile c : chapters) {
				total[0]++;
				String fileName = c.getFileName();
				MinimalChapter dataC = newDataMap.get(fileName);
				Chapter dbC = dbMap.remove(fileName);
				chapCountPc.add(fileName.lastIndexOf('-') < 0 ? fileName : pattern.matcher(fileName).replaceFirst(""));

				if(dataC == null && dbC == null) {
					chapLogs.add(new ChapterLog(c, NEW_PARSING_FILE));
					insert.add(new TempInsert(id, c));
				} else if(dbC != null) {
					chapLogs.add(new ChapterLog(dbC, NO_UPDATE));
					if(dbC.isRead())
						readCount[0]++;
				} else {
					chapLogs.add(new ChapterLog(dataC, NEW_FROM_DATA));
					insert.add(new TempInsert(id, c));
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
		
		StringBuilder sb = new StringBuilder();
		for (MangaLog m : logs)
			format.format(sb, m.read, m.unread, m.chapCountPc, m.manga.getMangaId());
		
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
					p.setInt(1, t.manga_id);
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
			if(array.length == 0)
				return Stream.empty();

			return Arrays.stream(array)
					.filter(s -> s.endsWith(".jpeg") || s.endsWith(".jpg"));
		}

		try (DirectoryStream<Path> strm = Files.newDirectoryStream(path)){
			Stream.Builder<String> build = Stream.builder();

			for (Path p : strm) {
				if(Files.isRegularFile(p) && !Files.isHidden(p)) {
					LOGGER.fine(() -> {
						try {
							return String.format("Skipping File {regular-file:%s, is-hidden:%s, file-path:%s}", Files.isRegularFile(p) , Files.isHidden(p), p);
						} catch (IOException e) {
							return String.format("Skipping File {regular-file:%s, is-hidden:%s, file-path:%s}", Files.isRegularFile(p), e.toString(), p);
						}
					});
					build.add(p.getFileName().toString());
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

}
