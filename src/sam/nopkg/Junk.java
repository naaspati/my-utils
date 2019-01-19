package sam.nopkg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import sam.console.ANSI;
import sam.myutils.MyUtilsBytes;

public interface Junk {
	public static String systemInfo() throws UnsupportedEncodingException {
		Set<String> ignore = new BufferedReader(new InputStreamReader(Junk.class.getResourceAsStream("1542051261555-ignore"), "utf-8"))
				.lines()
				.map(String::trim)
				.filter(s -> !s.isEmpty() && s.charAt(0) != '#')
				.collect(Collectors.toSet());

		System.out.println("working dir: "+Paths.get(".").toAbsolutePath().normalize());

		StringBuilder sb = new StringBuilder();
		sb.append(ANSI.createUnColoredBanner("SYSTEM_PROPERTIES"));
		sb.append('\n');

		System.getProperties().forEach((s,t)  -> {
			if(!ignore.contains(s))
				sb.append(s).append('=').append(t).append('\n');
		});
		sb.append("\n\n");

		sb.append(ANSI.createUnColoredBanner("SYSTEM_ENVIROMENT"));
		sb.append('\n');
		System.getenv().forEach((s,t)  -> {
			if(!ignore.contains(s))
				sb.append(s).append('=').append(t).append('\n');
		});
		sb.append('\n');

		return sb.toString();
	} 
	public static String memoryUsage() {
		Runtime r = Runtime.getRuntime();
		StringBuilder sb = new StringBuilder();
		Function<Long, String> f = l -> MyUtilsBytes.bytesToHumanReadableUnits(l, false);
		sb.append("Total Memory: ").append(f.apply(r.totalMemory())).append('\n')
		.append(" Free Memory: ").append(f.apply(r.freeMemory())).append('\n')
		.append("  Max Memory: ").append(f.apply(r.maxMemory())).append('\n')
		.append(" used Memory: ").append(f.apply(r.totalMemory() - r.freeMemory())).append('\n')
		;
		return sb.toString();
	}
	public static Stream<Method> getters(Class cls) {
		return Stream.of(cls.getDeclaredMethods())
				.filter(m -> m.getName().startsWith("get") && 
						Modifier.isPublic(m.getModifiers()) && 
						!Modifier.isStatic(m.getModifiers()) &&
						m.getParameterCount() == 0
						);
	}
	public static void invokeGetters(Object object, Appendable sink) throws IOException {
		Iterator<Method> mds = getters(object.getClass()).iterator();

		while (mds.hasNext()) {
			Method m = mds.next();

			String s = m.getName();
			sink.append(s, 3, s.length());
			
			sink.append(": ");
			try {
				sink.append(String.valueOf(m.invoke(object))).append('\n');
			} catch (Exception e) {
				sink.append("thrown: ").append(e.toString()).append('\n');
			}
		}


	}
	public static <E> E notYetImplemented() throws IllegalAccessError {
		throw new IllegalAccessError("NOT YET IMPLEMENTED");
	}
}
