package sam.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface EnvHelper {
    public static Properties2 read(Class<?> target, String filename) {
        try (InputStream is = target.getResourceAsStream(filename)) {
            Properties2 c = new Properties2(is);
            c.setSystemLookup(true, true);
            return c;
        } catch (IOException e) {
            throw new RuntimeException("failed to load config for: " + target + ", with resource_name: " + filename, e);
        }
    }

    public static void printMissing(Class<?> target) {
        String s = Stream.of(target.getDeclaredFields()).map(f -> {
            try {
                return new Object[] { f.get(null), f };
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException("failed to read field: " + f.getName(), e);
            }
        }).filter(f -> f[0] == null).map(f -> ((Field) f[1]).getName()).collect(Collectors.joining(", "));

        if (!s.isEmpty())
            throw new RuntimeException("not values set for field(s):[" + s + "]");
    }

    public static void listAll(Class<?> target, BiConsumer<Field, Object> consumer) {
        Stream.of(target.getDeclaredFields())
        .forEach(f -> {
            try {
                consumer.accept(f, f.get(null));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException("failed to read field: " + f.getName(), e);
            }
        });
    }

}
