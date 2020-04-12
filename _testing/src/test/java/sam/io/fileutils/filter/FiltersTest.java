package sam.io.fileutils.filter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class FiltersTest {

	@Test
	void test() throws JsonMappingException, JsonProcessingException {
		String json = "{\n" + 
				"        \"endsWith\": [\"Converter/app_data/cache\", \"Samrock app7/cache\"],\n" + 
				"        \"filename\": [\"bin\", \"lib\"]\n" + 
				"      }";
		
		String[] values = {"Converter/app_data/cache", "Samrock app7/cache", "bin", "lib"};
		
		Filters filters = new ObjectMapper().readValue(json, Filters.class);
		@SuppressWarnings("rawtypes")
		Predicate[] prdicates = {filters.endsWith, filters.endsWith, filters.filename, filters.filename};
		
		Arrays.asList(filters.path,filters.startsWith,filters.glob,filters.regex).forEach(s -> assertSame(s, Filters.ALWAYS_FALSE));
		assertNull(filters.invert);
		assertArrayEquals(values(filters.endsWith), Arrays.copyOf(values, 2));
		assertArrayEquals(values(filters.filename), Arrays.copyOfRange(values, 2, 4));
		
		for (int i = 0; i < prdicates.length; i++) {
			Path p = Paths.get(values[i]);
			assertTrue(filters.test(p));
			assertSame(prdicates[i], filters.matchedAt(p));
			assertSame(prdicates[i], filters.matchedAt(Paths.get("anime/"+values[i])));
			
			assertFalse(filters.test(Paths.get(values[i]+"/anime")));
			assertNull(filters.matchedAt(Paths.get(values[i]+"/anime")));
		}
		System.out.println(new ObjectMapper().writeValueAsString(filters));
	}

	@SuppressWarnings("rawtypes")
	private String[] values(Predicate<Path> endsWith) {
		return ((Filter)endsWith).values();
	}
}
