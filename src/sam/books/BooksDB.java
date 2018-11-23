package sam.books;

import static sam.books.BookStatus.NONE;
import static sam.books.BookStatus.READ;
import static sam.books.BookStatus.SKIPPED;
import static sam.books.BookStatus.valueOf;
import static sam.books.BooksMeta.BOOK_ID;
import static sam.books.BooksMeta.FILE_NAME;
import static sam.books.BooksMeta.STATUS;
import static sam.books.BooksMeta.TABLE_NAME;
import static sam.sql.querymaker.QueryMaker.qm;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import sam.logging.MyLoggerFactory;
import sam.myutils.MyUtilsPath;
import sam.string.BasicFormat;
import sam.string.StringUtils;
import sam.string.BasicFormat.EscapeType;

public class BooksDB extends BooksDBMinimal {

	public BooksDB() throws  SQLException {
		super(DB);
	}
	public BooksDB(Path dbpath) throws  SQLException {
		super(dbpath);
	}
	public int changeBookStatus(List<BookImpl> book, BookStatus newStatus) throws Exception {
		return changeBookStatus(book.stream().collect(Collectors.toMap(BookImpl::getBookId, BookImpl::getFullPath, (o,n) -> n)), newStatus);
	}
	public int changeBookStatus(Map<Integer, Path> bookIdPathMap, final BookStatus newStatus) throws Exception {
		Objects.requireNonNull(newStatus);
		Objects.requireNonNull(bookIdPathMap);
		
		if(bookIdPathMap.isEmpty())
			return 0;
		
		int n = 0;
		Exception exception = null;
		
		ArrayList<Path[]> moved = new ArrayList<>();
		Map<Integer, String> bookIdFileNameMap = collectToMap(qm().select(BOOK_ID,FILE_NAME).from(TABLE_NAME).where(w -> w.in(BOOK_ID, bookIdPathMap.keySet(), false)).build(), rs -> rs.getInt(BOOK_ID), rs -> rs.getString(FILE_NAME));
		
		try {
			Logger logger = MyLoggerFactory.logger(getClass()); 
			
			for (Entry<Integer, Path> entry : bookIdPathMap.entrySet()) {
				Path path = Objects.requireNonNull(entry.getValue());
				int book_id = Objects.requireNonNull(entry.getKey());
			
				if(Files.notExists(path))
					throw new FileNotFoundException(path.toString());
				
				String s = bookIdFileNameMap.get(book_id);
				
				if(s == null)
					throw new SQLException("no data found for book_id: "+book_id);
				
				if(!path.getFileName().toString().equals(s))
					throw new SQLException("file_name mismatch: expected_name:"+path.getFileName()+"  found_name:"+s);
				
				BookStatus status = getStatusFromFile(path);
				Path target = path;
				if(status != NONE)
					target = path.getParent();
				
				target = target.resolveSibling(newStatus.getPathName()).resolve(path.getFileName());
				
				 if(Files.notExists(target) || !Files.isSameFile(path, target)) {
					Files.createDirectories(target.getParent());
					Files.move(path, target, StandardCopyOption.REPLACE_EXISTING);
					Path p = target;
					moved.add(new Path[] {path, target});
					logger.info(() -> "moved: "+ MyUtilsPath.subpath(path, ROOT)+" -> " + MyUtilsPath.subpath(p, ROOT));
				}
			} 
			 n = executeUpdate(qm().update(TABLE_NAME).set(STATUS, newStatus, true).where(w -> w.in(BOOK_ID, bookIdFileNameMap.keySet(), false)).build());
			 commit();
		} catch (Exception e) {
			exception = e;
		} finally {
			if(exception != null) {
				for (Path[] p : moved) {
					try {
						Files.move(p[1], p[0], StandardCopyOption.REPLACE_EXISTING);
					} catch (Exception e) {}
				}
			}
		}
		
		if(exception != null)
			throw exception;
		
		return n;
	}

	public static Path findBook(Path expectedPath) {
		if(Files.exists(expectedPath))
			return expectedPath;
		Path path = expectedPath.resolveSibling("_read_").resolve(expectedPath.getFileName());

		if(Files.exists(path))
			return path;

		File[] dirs = expectedPath.getParent().toFile().listFiles(f -> f.isDirectory());

		if(dirs == null || dirs.length == 0)
			return null;

		String name = expectedPath.getFileName().toString();

		for (File file : dirs) {
			File f = new File(file, name);
			if(f.exists())
				return f.toPath();
		}
		return null;
	}
	public static BookStatus getStatusFromDir(Path dir) {
		Path name = dir.getFileName();
		if(name.equals(READ.getPathName()))
			return READ;
		if(name.equals(SKIPPED.getPathName()))
			return SKIPPED;

		String s = name.toString();
		if(s.charAt(0) == '_' && s.charAt(s.length() - 1) == '_')
			return valueOf(s.substring(1, s.length() - 1).toUpperCase());

		return NONE;
	}
	public static BookStatus getStatusFromFile(Path p) {
		return getStatusFromDir(p.getParent()); 
	} 
	private static WeakReference<Pattern> pattern = new WeakReference<Pattern>(null); 
	public static String createDirname(int book_id, String file_name) {
		StringBuilder sb = StringUtils.joinToStringBuilder(book_id,"-",file_name);
		Pattern p = pattern.get();
		if(p == null)
			pattern = new WeakReference<Pattern>(p = Pattern.compile("\\W+"));
		if(sb.length() > 4 && sb.substring(sb.length() - 4).equalsIgnoreCase(".pdf"))
			sb.setLength(sb.length() - 4);
		return p.matcher(sb).replaceAll("-");
	}
	private static BasicFormat bookJson;
	public static String toJson(int book_id, String isbn, String name) {
		if(bookJson == null) 
			bookJson = new BasicFormat("\\{\"book_name\":\"{}\",\"id\":{}, \"isbn\":\"{}\"\\}") ;	
		return bookJson.format(name,book_id, isbn);
	}
}
