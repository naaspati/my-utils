package sam.myutils;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class System2 {
	private static final PrintStream sink;
	private static final boolean printLoc;

	static {
		Function<String, String> find = suffix -> System2Helper.lookup("sam.System2.dump".concat(suffix == null ? "" : ".".concat(suffix)), null);
		PrintStream w = null;
		String enable = Optional.ofNullable(find.apply(null)).orElseGet(() -> find.apply("enable"));

		if (enable != null) {
			enable = enable.trim().toLowerCase();
			if (enable.equals("true")) {
				try {
					String p = Optional.ofNullable(find.apply("file")).map(String::trim).orElse("system.out");
					w = p.equalsIgnoreCase("system.out") ? System.out
							: new PrintStream(newOutputStream(Paths.get(p), CREATE, APPEND), true);
					if (w != System.out) {
						w.println("\n-----------" + LocalDateTime.now() + "\n-----------");
						System.err.println("sam.System2.dump enabled, saved in: " + p);
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else if (!enable.equals("false")) {
				System.err.println("bad value for DUMP_LOOKUP: \"" + enable + "\", possible values are[true,false]");
			}
		}

		printLoc = Boolean.valueOf(find.apply("printloc"));
		sink = w;
		if (sink != null && sink != System.out) {
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				sink.flush();
				sink.close();
			}));
		}
	}

	public static String lookup(Class<?> cls, String fieldName, String defaultValue) {
		return lookup(cls.getCanonicalName() + "." + fieldName, defaultValue);
	}

	public static String lookup(String key, String defaultValue) {
		String value = System2Helper.lookup(key, defaultValue);
		log(() -> key.concat(value == null ? "=" : "=".concat(value)));
		return value;
	}

	private static void log(Supplier<String> msg) {
		if (sink == null)
			return;

		String s = msg.get();

		if (printLoc) {
			sink.print(s);
			sink.print('\t');
			sink.println(Arrays.stream(Thread.currentThread().getStackTrace()).skip(1)
					.filter(f -> !f.getClassName().contains("System2")).findFirst().orElse(null));
		} else {
			sink.println(s);
		}
	}

	public static boolean lookupBoolean(String key) {
		return lookupBoolean(key, false);
	}

	public static boolean lookupBoolean(String key, boolean defaultValue) {
		String value = lookup(key, null);
		return parseBoolean(value, defaultValue);
	}

	public static boolean parseBoolean(String booleanString, boolean defaultValue) {
		if (booleanString == null)
			return defaultValue;

		String s = booleanString.trim().toLowerCase();
		if (s.isEmpty())
			return defaultValue;

		switch (booleanString.trim().toLowerCase()) {
			case "true":
				return true;
			case "false":
				return false;

			case "yes":
				return true;
			case "no":
				return false;

			case "on":
				return true;
			case "off":
				return false;

			default:
				System.err.println("Unknown boolean value: " + booleanString);
				return defaultValue;
		}
	}

	public static String lookup(String key) {
		return lookup(key, null);
	}

	public static int lookupInt(String key, int default_value) {
		String s = lookup(key, null);
		return s == null ? default_value : Integer.parseInt(s.trim());
	}

	public static double lookupDouble(String key, double default_value) {
		String s = lookup(key, null);
		return s == null ? default_value : Double.parseDouble(s.trim());
	}

	public static String lookupAny(String... keys) {
		String s = null;
		String k = null;

		try {
			for (String key : keys) {
				k = key;
				s = getProperty(key);
				if (s != null)
					return s;
			}

			for (String key : keys) {
				k = key;
				s = getenv(key);
				if (s != null)
					return s;
			}

		} finally {
			if (s == null)
				log(() -> "NO VALUE found for any: " + Arrays.toString(keys));
			else {
				String s2 = s, k2 = k;
				log(() -> Arrays.toString(keys) + ", found: " + k2 + "=" + s2);
			}
		}
		return null;
	}

}
