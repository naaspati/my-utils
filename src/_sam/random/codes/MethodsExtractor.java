package _sam.random.codes;

import static sam.string.StringUtils.contains;
import static sam.string.StringUtils.split;
import static sam.string.StringUtils.splitStream;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import sam.myutils.MyUtilsCheck;
import sam.myutils.MyUtilsExtra;

public class MethodsExtractor {

	public static void main(String[] args) throws IOException {

		for (Class<?> cls : Arrays.asList(MyUtilsCheck.class)) {
			new MethodsExtractor(cls, null, null);
			System.out.println();
		}
	}

	final Class<?> cls;
	final Path file;
	final String prefix;

	public MethodsExtractor(Class<?> cls, String rootPath, String prefix) throws IOException {
		file = Paths.get(rootPath == null ? "src" : rootPath).resolve(cls.getPackage().getName().replace('.', '/')).resolve(cls.getSimpleName()+".java");
		this.cls = cls;
		this.prefix = prefix != null ? prefix : cls.getSimpleName();

		if(Files.notExists(file)) {
			System.out.println("not found: "+file);
			System.exit(0);
		}

		Set<String> methods = Arrays.stream(cls.getDeclaredMethods()).map(Method::getName).collect(Collectors.toSet());
		List<String> list = new ArrayList<>(); 

		Files.lines(file)
		.map(String::trim)
		.forEach(s -> {
			if(!list.isEmpty()) {

				if(s.indexOf('{') >= 0) {
					list.add(s.substring(0, s.indexOf('{')+1));
					process(String.join("", list));
					list.clear();
				} else 
					list.add(s);
				return;
			}
			if(s.startsWith("public") && contains(s, '(') && methods.contains(methodName(s))) {
				if(contains(s, '{'))
					process(s.substring(0, s.indexOf('{')+1));
				else
					list.add(s);
			}
		});
	}

	private Object methodName(String s) {
		int index = s.indexOf('(');
		return s.substring(s.lastIndexOf(' ', index) + 1, index);
	}

	StringBuilder params = new StringBuilder(); 
	StringBuilder names = new StringBuilder();

	private void process(final String data) {
		try {
			String paramNames = data.replaceAll("\r?\n", "").trim();
			paramNames = paramNames.substring(paramNames.indexOf('(') + 1, paramNames.indexOf(')'));

			params.setLength(0);
			names.setLength(0);

			splitStream(paramNames.trim(), ',')
			.map(ss ->split(ss.trim(), ' '))
			.forEach(ss -> {
				params.append(ss[0]).append(',');
				names.append(ss[1]).append(',');
			});

			params.setLength(params.length() - 1);
			names.setLength(names.length() - 1);

			System.out.printf("/** {@link %s#%s(%s)} */\n %s %s %s.%s(%s); }\n", prefix, methodName(data),params.toString().replaceAll("<.+>", ""), data, data.contains("void") ? "" : "return", prefix, methodName(data), names.toString());
		} catch (Exception e) {
			System.out.println("\nerror with: \n"+data+"\n");
		}
	}
}
