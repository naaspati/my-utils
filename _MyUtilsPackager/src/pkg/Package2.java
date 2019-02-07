package pkg;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

class Package2 {
	public static final Map<String, List<String>> founded = new LinkedHashMap<>();

	final Path dir;
	final String pkgName;
	final List<Class2> classes = new ArrayList<>();
	List<String> files;
	private int count;

	public Package2(Main myUtilsPackager, Path path, int nameCount) {
		this.dir = path;
		this.pkgName = path.subpath(nameCount, path.getNameCount()).toString().replace('\\', '.');
	}
	public boolean anyFound() {
		for (Class2 c : classes) 
			if(c.isFound())
				return true;

		return false;
	}
	public int getCount() {
		return count;
	}
	public void addFile(Path file) {
		if(files == null)
			files = new ArrayList<>();
		files.add(file.getFileName().toString());
	}
	public Class2 add(Path file) {
		count++;
		Class2 c = new Class2(file);
		classes.add(c);
		return c;
	}
	public class Class2 {
		final Path file;
		final String className;
		private  boolean found2;
		private    String text;

		public Class2(Path file) {
			this.file = file;
			String t = file.getFileName().toString();
			this.className = t.substring(0, t.lastIndexOf('.'));
		}
		
		private String[] required;
		
		public String[] getRequired() {
			if(files == null || required != null)
				return required;
			
			getText();
			return required = files.stream().filter(s -> text.contains(s)).toArray(String[]::new);
		}
		public boolean isFound() { return found2; }
		public void setFound(Object where) {
			founded.computeIfAbsent(where instanceof String ? (String)where : ((Class2)where).getCanonicalName(), k -> new ArrayList<>()).add(this.getCanonicalName());
			this.found2 = true;
		}
		public String getText() {
			return text != null ? text : (text = Main.toText(file));
		}
		public String getVersion() {
			int n = getText().indexOf("VERSION");
			if(n < 0) return null;
			n += "VERSION".length(); 
			String s = text.substring(n, text.indexOf('\n', n+1));
			return s.replaceFirst("^\\s*=\\s*", "").replaceFirst("\\s*;\\s*$", "").trim();
		}
		public String getCanonicalName() {
			return pkgName+"."+className;
		}
		Package2 getPackage(){
			return Package2.this;
		}
		Pattern pattern;
		public boolean checkHas(String text) {
			if(!text.contains(className))
				return false;

			if(pattern == null)
				pattern = Pattern.compile("\\b\\Q"+className+"\\E\\b");

			return pattern.matcher(text).find();
		} 
	}
}