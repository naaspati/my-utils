package sam.manga.samrock.chapters;

import static sam.manga.samrock.chapters.ChaptersMeta.CHAPTER_ID;
import static sam.manga.samrock.chapters.ChaptersMeta.MANGA_ID;
import static sam.manga.samrock.chapters.ChaptersMeta.NAME;
import static sam.manga.samrock.chapters.ChaptersMeta.NUMBER;
import static sam.manga.samrock.chapters.ChaptersMeta.READ;
import static sam.manga.samrock.chapters.ChaptersMeta.TABLE_NAME;
import static sam.manga.samrock.chapters.Modification.DELETE;
import static sam.manga.samrock.chapters.Modification.MODIFIED;
import static sam.manga.samrock.chapters.Modification.NEW;
import static sam.manga.samrock.chapters.Modification.NO_CHANGE;
import static sam.manga.samrock.chapters.Modification.READ_MODIFIED;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import sam.collection.Iterables;
import sam.config.MyConfig;
import sam.fileutils.FileNameSanitizer;
import sam.manga.samrock.SamrockDB;
import sam.manga.samrock.mangas.MangaUtils;
import sam.manga.samrock.mangas.MangasMeta;
import sam.sql.SqlConsumer;
import sam.sql.SqlFunction;
import sam.sql.querymaker.QueryMaker;
import sam.string.StringUtils;

public class ChapterUtils {
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
		String sql = qm().select(chaptersMeta).from(TABLE_NAME).where(w -> w.in(MANGA_ID, mangaIds)).build();
		db.iterate(sql.toString(), consumer);
	}
	public LastChapter lastChapter() {
		return new LastChapter(db);
	}

	/**
	 * 
	 * @param newChaptersData 
	 * @param mangaIds used when chapterData is not present and manga needs to be updated
	 * @param logger
	 * @throws SQLException
	 * @throws IOException
	 */
	public  void updateChaptersInDB(List<ChapterWithMangaId> newChapterData, List<Integer> mangaIds, StringBuilder logger) throws SQLException, IOException {
		if(newChapterData.isEmpty() && mangaIds.isEmpty())
			throw new IllegalArgumentException("invalid mangaIdChaptersMap"+newChapterData +" and mangaIDs: "+mangaIds);

		LinkedList<ChapterWithMangaId> newChaptersData = new LinkedList<>(newChapterData);
		
		//manga_id -> dir_name
		Map<Integer, String> mangaMap = new HashMap<>();
		Set<Integer> mids = new HashSet<>(mangaIds);
		for (ChapterWithMangaId c : newChaptersData)
			mids.add(c.getMangaId());
		
		new MangaUtils(db).select(mids, rs -> mangaMap.put(rs.getInt(MangasMeta.MANGA_ID), rs.getString(MangasMeta.DIR_NAME)), MangasMeta.MANGA_ID, MangasMeta.DIR_NAME);
		mids = null;

		//manga_id -> chapters
		LinkedList<ChapterWithMangaId> oldChaptersData = new LinkedList<>(); 
		db.iterate(qm().selectAll().from(TABLE_NAME).where(w -> w.in(MANGA_ID, mangaMap.keySet())).build(),
				rs -> oldChaptersData.add(new ChapterWithMangaId(rs)));

		ArrayList<String> sink = new ArrayList<>();
		Map<String, ChapterWithMangaId> newMap = new HashMap<>();
		Map<String, ChapterWithMangaId> oldMap = new HashMap<>();


		List<Chapter> delete = new ArrayList<>();
		List<Chapter> neww = new ArrayList<>();

		Pattern pattern =  Pattern.compile(" - \\d\\.jpe?g");
		Set<String> chapCountPc = new HashSet<>();
		int readCount = 0;
		int unreadCount = 0;

		String sql = qm().update(MangasMeta.TABLE_NAME)
				.placeholders(
						MangasMeta.READ_COUNT, 
						MangasMeta.UNREAD_COUNT, 
						MangasMeta.CHAP_COUNT_PC, 
						MangasMeta.LAST_UPDATE_TIME)
				.where(w -> w.eqPlaceholder(MangasMeta.MANGA_ID))
				.build();

		Path root = Paths.get(MyConfig.MANGA_DIR);

		try(PreparedStatement ps =  db.prepareStatement(sql)){
			for (Entry<Integer, String> e : mangaMap.entrySet()) {
				int manga_id = e.getKey();
				String dir_name = e.getValue();

				boolean[] b = {false};
				BiConsumer<String, String> append = (s1, s2) -> {
					if(!b[0]) {
						logger.append(manga_id).append("  ").append(dir_name).append('\n');
						b[0] = true;
					}
					logger.append("   ").append(s1);
					if(s2 != null)
						logger.append("  (").append(s2).append(')');
					logger.append('\n');
				};

				sink.clear();
				newMap.clear();
				oldMap.clear();
				chapCountPc.clear();

				readCount = 0;
				unreadCount = 0;

				Path dir = root.resolve(dir_name);
				list(dir, sink);

				fill(newChaptersData, newMap, manga_id);
				fill(oldChaptersData, oldMap, manga_id);

				for (String fileName : sink) {
					ChapterWithMangaId newwChap = newMap.remove(fileName);
					ChapterWithMangaId old = oldMap.remove(fileName);
					Chapter c = null;

					chapCountPc.add(fileName.indexOf('-') < 0 ? fileName : pattern.matcher(fileName).replaceFirst(""));

					if(newwChap == null && old == null) {
						Double d = Chapter.parseChapterNumber(fileName);
						if(d == null) 
							append.accept(fileName, "Bad Chapter");
						else {
							neww.add(c = new ChapterWithMangaId(manga_id, d, fileName));
							append.accept(fileName, "new (filename parsed)");
						}
					} else if(newwChap != null) {
						if(old != null) {
							delete.add(old);
							newwChap.setRead(old.isRead());
						} 
						c = newwChap;
						neww.add(newwChap);
						append.accept(fileName, "new (data loaded)");
					} else if(old != null)
						c = old;

					if(c != null) {
						if(c.isRead())
							readCount++;
						else
							unreadCount++;
					}
				}
				if(!oldMap.isEmpty()) {
					delete.addAll(oldMap.values());
					oldMap.keySet().forEach(fileName -> append.accept(fileName, "deleted"));
				}
				append.accept("\nread: "+readCount+" |  unread: "+unreadCount+" | chap_count_pc: "+chapCountPc.size(), null);

				/*
				 * MangasMeta.READ_COUNT, 
				 * MangasMeta.UNREAD_COUNT, 
				 * MangasMeta.CHAP_COUNT_PC, 
				 * MangasMeta.LAST_UPDATE_TIME
				 * MangasMeta.MANGA_ID
				 */

				ps.setInt(1, readCount);
				ps.setInt(2, unreadCount);
				ps.setInt(3, chapCountPc.size());
				ps.setLong(4, dir.toFile().lastModified());
				ps.setInt(5, manga_id);
				ps.addBatch();
			}
			logger.append("manga updates: ").append(ps.executeBatch().length).append('\n');
		}
		if(!delete.isEmpty()) 
			logger.append("deleted: ").append(deleteChapters(delete, null)).append('\n');

		if(!neww.isEmpty())
			logger.append("new added: ").append(insertNewChapters(-1, neww, null)).append('\n');
	}
	private void fill(LinkedList<ChapterWithMangaId> source, Map<String, ChapterWithMangaId> sink, int manga_id) {
		if(source.isEmpty())
			return;

		Iterator<ChapterWithMangaId> itr = source.iterator();
		while (itr.hasNext()) {
			ChapterWithMangaId c = itr.next();
			if(c.getMangaId() == manga_id) {
				itr.remove();
				sink.put(c.getFileName(), c);
			}
		}
	}
	public Map<Integer, List<Chapter>> getChapters(Iterable<Integer> mangaIds) throws SQLException {
		Map<Integer, List<Chapter>> map = new HashMap<>();

		String sql = qm().selectAll().from(TABLE_NAME).where(w -> w.in(MANGA_ID, mangaIds)).build();
		Function<Integer, List<Chapter>> computer = i -> new ArrayList<>();

		db.iterate(sql, rs -> map.computeIfAbsent(rs.getInt(MANGA_ID), computer).add(new Chapter(rs)));
		return map;
	}

	private EnumMap<Modification, List<Chapter>> groupChapters(Iterable<Chapter> chapters, EnumMap<Modification, List<Chapter>> sink){
		return Iterables.stream(chapters)
				.collect(Collectors.groupingBy(c -> {
					if(c.isDeleted())
						return DELETE;
					if(c.isChapterNotInDb())
						return NEW;
					if(c.isReadModified() && !c.isNameModified() && !c.isNumberModified())
						return READ_MODIFIED;
					if(c.isNameModified() || c.isNumberModified() || c.isReadModified())
						return MODIFIED;
					return NO_CHANGE;
				}, () -> sink, Collectors.toList())); 
	}

	public StringBuilder commitChaptersChanges(int mangaId, Iterable<Chapter> chapters) throws SQLException {
		StringBuilder sb = new StringBuilder();
		commitChaptersChanges(mangaId, chapters, sb);
		return sb;
	}
	public void commitChaptersChanges(int mangaId, Iterable<Chapter> chapters, StringBuilder logger) throws SQLException {
		EnumMap<Modification, List<Chapter>> grouped = groupChapters(chapters, new EnumMap<>(Modification.class));

		List<Chapter> list = grouped.get(DELETE);

		if(list != null) 
			deleteChapters(list,logger);

		list = grouped.get(NEW);

		if(list != null)
			insertNewChapters(mangaId, list, logger);

		list = grouped.get(READ_MODIFIED);

		if(list != null)
			setReadModifiedChapters(list);

		list = grouped.get(MODIFIED);

		if(list != null)
			updateChapters(list);
	}
	private void setReadModifiedChapters(Collection<Chapter> chapters) throws SQLException {
		if(chapters.isEmpty())
			return;
		int[] ids = chapters.stream().filter(Chapter::isRead).mapToInt(Chapter::getId).toArray();

		if(ids.length != 0)
			db.executeUpdate(qm().update(TABLE_NAME).set(READ, 1).where(w -> w.in(CHAPTER_ID, ids)).build());

		if(ids.length == chapters.size())
			return;

		int[] ids2 = chapters.stream().filter(c -> !c.isRead()).mapToInt(Chapter::getId).toArray();
		db.executeUpdate(qm().update(TABLE_NAME).set(READ, 0).where(w -> w.in(CHAPTER_ID, ids2)).build());
	}
	private int deleteChapters(Iterable<Chapter> chapters, StringBuilder logger) throws SQLException {
		Iterator<Chapter> itr = chapters.iterator(); 
		if(!itr.hasNext())
			return 0 ;

		String sql = qm().deleteFrom(TABLE_NAME).where(w -> w.in(CHAPTER_ID, itr, Chapter::getId, false)).build();
		int executes = db.executeUpdate(sql);

		if(logger != null) {
			logger.append("chapters deleted: \n");
			for (Chapter c : Iterables.of(itr)) logger.append("   ").append(c.getFileName()).append('\n');
			logger.append(" executes: ").append(executes).append('\n');
		}
		return executes;
	}
	public int deleteChapters(Iterable<Integer> chapterIds) throws SQLException {
		Objects.requireNonNull(chapterIds);
		Iterator<Integer> itr = chapterIds.iterator();
		Objects.requireNonNull(itr);

		if(!itr.hasNext())
			throw new IllegalArgumentException("chapterIds empty: "+chapterIds);

		String sql = qm().deleteFrom(TABLE_NAME).where(w -> w.in(CHAPTER_ID, itr, false)).build();
		return db.executeUpdate(sql);
	}
	private int updateChapters(Iterable<Chapter> chapters) throws SQLException {
		return db.prepareStatementBlock(qm().update(TABLE_NAME).placeholders(
				NUMBER,
				NAME,
				READ
				).where(w -> w.eqPlaceholder(CHAPTER_ID)).build(),

				ps -> {
					for (Chapter ch : chapters) {
						ps.setDouble(1, ch.getNumber());
						ps.setString(2, ch.getFileName());
						ps.setBoolean(3, ch.isRead());
						ps.setInt(4, ch.getId());
						ps.addBatch();
					}
					return ps.executeBatch().length;
				});
	}

	private int insertNewChapters(int mangaId, Iterable<Chapter> chapters, StringBuilder logger) throws SQLException {
		Iterator<Chapter> itr = chapters.iterator();

		if(!itr.hasNext())
			return 0;

		if(logger != null)
			logger.append("new chapters: \n");

		return db.prepareStatementBlock(qm().insertInto(TABLE_NAME).placeholders(
				MANGA_ID,
				NAME,
				NUMBER,
				READ),

				ps -> {
					for (Chapter ch : Iterables.of(itr)) {
						if(logger != null)
							logger.append("   ").append(ch.getFileName()).append('\n');

						int id = mangaId;
						if(ch instanceof ChapterWithMangaId)
							id = ((ChapterWithMangaId)ch).getMangaId();

						ps.setInt(1, id);
						ps.setString(2, ch.getFileName());
						ps.setDouble(3, ch.getNumber());
						ps.setBoolean(4, ch.isRead());
						ps.addBatch();
					}
					int ex = ps.executeBatch().length;
					if(logger != null)
						logger.append(" executes: ").append(ex).append('\n');
					return ex;
				});
	}
	public <E extends Chapter> List<E> getChapters(int mangaId,  SqlFunction<ResultSet, E> chapterMapper) throws SQLException {
		return db.collectToList(qm().select(CHAPTER_ID, NAME, NUMBER, READ).from(TABLE_NAME).where(w -> w.eq(MANGA_ID, mangaId)).build(), chapterMapper);
	}
	public static <E extends Chapter> List<E> reloadChapters(Path mangaFolder, Iterable<E> chapters, BiFunction<Double, String, E> chapterMaker) throws IOException {
		Map<String, Double> map = list(mangaFolder, new ArrayList<>()).stream().collect(Collectors.toMap(s -> s, Chapter::parseChapterNumber));

		if(map.values().stream().anyMatch(Objects::isNull)) {
			System.out.println("bad files in "+mangaFolder.getName(mangaFolder.getNameCount() - 2));
			map.keySet().forEach(s -> System.out.println("  "+s));
			map.values().removeIf(Objects::isNull);
		}

		if(map.isEmpty()) 
			throw new IOException("no chapters found");

		for (Chapter c : chapters) {
			Double number = map.remove(c.getFileName());
			if(number == null) {
				System.out.println("  delete: "+c);
				c.setDeleted(true);
			} else
				c.setNumber(number);
		}

		List<E> list = chapters instanceof ArrayList || chapters instanceof LinkedList ? (List<E>) chapters : Iterables.stream(chapters).collect(Collectors.toList());
		if(!map.isEmpty())
			map.forEach((s,t) -> chapterMaker.apply(t, s)); 
		return list;
	}

	private static List<String> list(Path path, List<String> sink) throws IOException{
		try (DirectoryStream<Path> strm = Files.newDirectoryStream(path)){
			for (Path p : strm) {
				if(Files.isRegularFile(p) && !Files.isHidden(p))
					sink.add(p.getFileName().toString());
			}
		}
		return sink;
	}

}
