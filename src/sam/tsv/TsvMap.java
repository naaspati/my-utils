package sam.tsv;


import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import sam.myutils.Checker;

public final class TsvMap {
	private TsvMap() { }
	
	public static Map<String, String> parse(BufferedReader reader) throws IOException {
		return parse(reader, s -> s);
	}
	public static Map<String, String> parse(Path path) throws IOException {
		return parse(path, s -> s);
	}
	
	public static <E> Map<E, E> parse(BufferedReader reader, Function<String, E> converter) throws IOException {
		return parse(reader, converter, converter);
	}
	public static <K, V> Map<K, V> parse(BufferedReader reader, Function<String, K> keyConverter,Function<String, V> valueConverter) throws IOException {
		Map<K, V> map = new LinkedHashMap<K,V>();
		load(reader, map, keyConverter, valueConverter);
		return map;
	}
	public static <E> Map<E, E> parse(Path path, Function<String, E> converter) throws IOException {
		return parse(path, converter, converter);
	}
	public static <K, V> Map<K, V> parse(Path path, Function<String, K> keyConverter,Function<String, V> valueConverter) throws IOException {
		try(BufferedReader reader = Files.newBufferedReader(path)) {
			return parse(reader, keyConverter, valueConverter);
		}
	}
	public static <K,V> void load(BufferedReader source, Map<K, V> sink, Function<String, K> keyConverter,Function<String, V> valueConverter) throws IOException {
		StringBuilder s1 = new StringBuilder();
		StringBuilder s2 = new StringBuilder();

		String line;
		while((line = source.readLine()) != null) {
			if(line.isEmpty())
				continue;
			
			int n = line.indexOf('\t');
			if(n < 0) 
				sink.put(unescape(keyConverter, line,0, line.length(), s1, s2), null);
			else {
				sink.put(
						unescape(keyConverter, line, 0, n, s1, s2), 
						unescape(valueConverter, line, n+1, line.length(), s1, s2)
						);
			}
		}
	}
	
	private static <K> K unescape(Function<String, K> converter, String string, int start, int end, StringBuilder s1, StringBuilder s2) {
		if(!string.isEmpty() && start != end) {
			s1.setLength(0);
			s1.append(string, start, end);
			
			if(s1.length() != 0) {
				s2.setLength(0);
				TsvUtils.unescape(s1, s2);
				string = s2.toString();
			}
		}
		return converter.apply(string);
	}

	public static <E> void save(Path path, Map<E, E> map, Function<E, String> converter) throws IOException {
		save(path, map, converter, converter);
	}
	public static <K, V> void save(Path path, Map<K, V> map, Function<K, String> keyConverter, Function<V, String> valueConverter) throws IOException {
		Checker.requireNonNull("path, map, keyConverter, valueConverter", path, map, keyConverter, valueConverter);
		
		try(BufferedWriter w = Files.newBufferedWriter(path, CREATE, TRUNCATE_EXISTING, WRITE)) {
			save(w, map, keyConverter, valueConverter);
		}
	}
	public static void save(Path path, Map<String, String> map) throws IOException {
		save(path, map, s -> s);
	}
	public static void save(Appendable target, Map<String, String> map) throws IOException {
		save(target, map, s -> s);
	}
	public static <E> void save(Appendable target, Map<E, E> map, Function<E, String> converter) throws IOException {
		save(target, map, converter, converter);
	}
	public static <K, V> void save(Appendable target, Map<K, V> map, Function<K, String> keyConverter, Function<V, String> valueConverter) throws IOException {
		Checker.requireNonNull("target, map, keyConverter, valueConverter", target, map, keyConverter, valueConverter);
		
		if(map.isEmpty())
			return;
		
		TsvSaver saver = new TsvSaver();

		for (Entry<K, V> e : map.entrySet()) 
			saver.append(keyConverter.apply(e.getKey()), valueConverter.apply(e.getValue()), target);
	}
}