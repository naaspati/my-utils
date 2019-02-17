import java.nio.file.Path;
import java.nio.file.Paths;

import sam.nopkg.SavedAsStringResource;

public class Main {

	public static void main(String[] args) throws Exception {
		Path p = Paths.get("temp");
		// Files.deleteIfExists(p);
		SavedAsStringResource<Path> ss = new SavedAsStringResource<>(p, Paths::get);
		
		System.out.println(p.equals(ss.get()));
		System.out.println(p.toString().equals(ss.get().toString()));
		ss.close();
		
	}
}
